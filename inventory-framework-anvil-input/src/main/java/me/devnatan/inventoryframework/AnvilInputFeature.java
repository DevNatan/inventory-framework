package me.devnatan.inventoryframework;

import static me.devnatan.inventoryframework.IFViewFrame.FRAME_REGISTERED;

import java.util.Map;
import java.util.UUID;
import java.util.function.UnaryOperator;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.feature.Feature;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class AnvilInputFeature implements Feature<Void, Void, ViewFrame> {

    /**
     * Instance of the Anvil Input feature.
     *
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/anvil-input">Anvil Input on Wiki</a>
     */
    public static final Feature<Void, Void, ViewFrame> AnvilInput = new AnvilInputFeature();

    private PipelineInterceptor frameInterceptor;

    private AnvilInputFeature() {}

    @Override
    public @NotNull String name() {
        return "Anvil Input";
    }

    @Override
    public @NotNull Void install(ViewFrame framework, UnaryOperator<Void> configure) {
        framework.getPipeline().intercept(FRAME_REGISTERED, (frameInterceptor = createFrameworkInterceptor()));
        return null;
    }

    @Override
    public void uninstall(ViewFrame framework) {
        framework.getPipeline().removeInterceptor(FRAME_REGISTERED, frameInterceptor);
    }

    private PipelineInterceptor createFrameworkInterceptor() {
        return (PipelineInterceptor<IFViewFrame>) (pipeline, subject) -> {
            final Map<UUID, PlatformView> views = subject.getRegisteredViews();

            for (final PlatformView view : views.values()) {
                handleOpen(view);
            }
        };
    }

    private void handleOpen(PlatformView view) {
        view.getPipeline().intercept(StandardPipelinePhases.OPEN, (pipeline, subject) -> {
            if (!(subject instanceof IFOpenContext)) return;

            System.out.println("after open");

            final OpenContext context = (OpenContext) subject;
            if (context.getConfig().getType() != ViewType.ANVIL) return;

            final boolean hasAnvilInput =
                    context.getConfig().getModifiers().stream().anyMatch(modifier -> modifier instanceof AnvilInput);

            if (!hasAnvilInput) return;

            final Inventory inventory =
                    AnvilInputNMS.open(context.getPlayer(), context.getConfig().getTitle());
            final ViewContainer container = new BukkitViewContainer(inventory, false, ViewType.ANVIL);

            System.out.println("setContainer");
            context.setContainer(container);
        });
    }
}
