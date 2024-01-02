package me.devnatan.inventoryframework;

import static java.util.Objects.requireNonNull;
import static me.devnatan.inventoryframework.AnvilInput.defaultConfig;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.feature.Feature;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public final class AnvilInputFeature implements Feature<AnvilInputConfig, Void, ViewFrame> {

    private static final int INGREDIENT_SLOT = 0;

    /**
     * Instance of the Anvil Input feature.
     *
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/anvil-input">Anvil Input on Wiki</a>
     */
    public static final Feature<AnvilInputConfig, Void, ViewFrame> AnvilInput = new AnvilInputFeature();

    private AnvilInputConfig config;
    private PipelineInterceptor frameInterceptor;

    private AnvilInputFeature() {}

    @Override
    public @NotNull String name() {
        return "Anvil Input";
    }

    @Override
    public @NotNull Void install(ViewFrame framework, UnaryOperator<AnvilInputConfig> configure) {
        config = configure.apply(defaultConfig());
        framework
                .getPipeline()
                .intercept(PipelinePhase.Frame.FRAME_REGISTERED, (frameInterceptor = createFrameworkInterceptor()));
        return null;
    }

    @Override
    public void uninstall(ViewFrame framework) {
        framework.getPipeline().removeInterceptor(PipelinePhase.Frame.FRAME_REGISTERED, frameInterceptor);
    }

    private PipelineInterceptor createFrameworkInterceptor() {
        return (PipelineInterceptor<IFViewFrame>) (pipeline, subject) -> {
            final Map<UUID, PlatformView> views = subject.getRegisteredViews();

            for (final PlatformView view : views.values()) {
                handleOpen(view);
                handleClose(view);
                handleClick(view);
            }
        };
    }

    private AnvilInput getAnvilInput(IFContext context) {
        if (context.getConfig().getType() != ViewType.ANVIL) return null;

        final Optional<ViewConfig.Modifier> optional = context.getConfig().getModifiers().stream()
                .filter(modifier -> modifier instanceof AnvilInput)
                .findFirst();

        //noinspection OptionalIsPresent
        if (!optional.isPresent()) return null;

        return (AnvilInput) optional.get();
    }

    private void updatePhysicalResult(String newText, ViewContainer container) {
        final Inventory inventory = ((BukkitViewContainer) container).getInventory();
        final ItemStack ingredientItem = requireNonNull(inventory.getItem(INGREDIENT_SLOT));
        final ItemMeta ingredientMeta = requireNonNull(ingredientItem.getItemMeta());
        ingredientMeta.setDisplayName(newText);
        ingredientItem.setItemMeta(ingredientMeta);
    }

    private void handleClick(PlatformView view) {
        view.getPipeline().intercept(PipelinePhase.Context.CONTEXT_SLOT_CLICK, (pipeline, subject) -> {
            final SlotClickContext context = (SlotClickContext) subject;
            final AnvilInput anvilInput = getAnvilInput(context);
            if (anvilInput == null) return;

            final int resultSlot = context.getContainer().getType().getResultSlots()[0];
            if (context.getClickedSlot() != resultSlot) return;

            final ItemStack resultItem = context.getItem();
            if (resultItem == null || resultItem.getType() == Material.AIR) return;

            final ItemMeta resultMeta = requireNonNull(resultItem.getItemMeta());
            final String text = resultMeta.getDisplayName();
            final Inventory clickedInventory =
                    requireNonNull(context.getClickOrigin().getClickedInventory(), "Clicked inventory cannot be null");
            final ItemStack ingredientItem = requireNonNull(clickedInventory.getItem(INGREDIENT_SLOT));
            final ItemMeta ingredientMeta = requireNonNull(ingredientItem.getItemMeta());
            ingredientMeta.setDisplayName(text);
            context.updateState(anvilInput.internalId(), text);
            ingredientItem.setItemMeta(ingredientMeta);

            if (config.closeOnSelect) {
                context.closeForPlayer();
            }
        });
    }

    private void handleOpen(PlatformView view) {
        view.getPipeline().intercept(PipelinePhase.Context.CONTEXT_OPEN, (pipeline, subject) -> {
            final OpenContext context = (OpenContext) subject;
            final AnvilInput anvilInput = getAnvilInput(context);
            if (anvilInput == null) return;

            // Forces internal state initialization
            context.getInternalStateValue(anvilInput);

            context.watchState(
                    anvilInput.internalId(),
                    (pipelineContext, diff) -> updatePhysicalResult(
                            (String) diff.getNewValue(), ((IFRenderContext) diff.getHost()).getContainer()));

            final String globalInitialInput = config.initialInput;
            final String scopedInitialInput = anvilInput.get(context);

            final Inventory inventory = AnvilInputNMS.open(
                    context.getPlayer(),
                    context.getConfig().getTitle(),
                    scopedInitialInput.isEmpty() ? globalInitialInput : scopedInitialInput);
            final ViewContainer container = new BukkitViewContainer(inventory, ViewType.ANVIL, false, true);

            context.setContainer(container);
        });
    }

    private void handleClose(PlatformView view) {
        view.getPipeline().intercept(PipelinePhase.Context.CONTEXT_CLOSE, (pipeline, subject) -> {
            final IFCloseContext context = (IFCloseContext) subject;
            final AnvilInput anvilInput = getAnvilInput(context);
            if (anvilInput == null) return;

            final BukkitViewContainer container = (BukkitViewContainer) context.getContainer();
            final int slot = container.getType().getResultSlots()[0];
            final ItemStack item = container.getInventory().getItem(slot);

            if (item == null || item.getType() == Material.AIR) return;

            final String input = requireNonNull(item.getItemMeta()).getDisplayName();
            context.updateState(anvilInput.internalId(), input);
        });
    }
}
