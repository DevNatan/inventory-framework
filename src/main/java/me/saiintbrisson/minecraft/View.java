package me.saiintbrisson.minecraft;

import com.google.common.base.Preconditions;
import me.saiintbrisson.minecraft.pagination.PaginatedView;
import me.saiintbrisson.minecraft.pagination.PaginatedViewContext;
import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class View implements InventoryHolder, Closeable {

    public static final int INVENTORY_ROW_SIZE = 9;

    private ViewFrame frame;
    private final String title;
    private final int rows;
    final Map<Player, Inventory> nodes;
    private boolean cancelOnClick = true;
    private final ViewItem[] items;
    private final Map<Player, Map<String, Object>> data;

    public View(int rows, String title) {
        this(null, rows, title);
    }

    public View(ViewFrame frame, int rows, String title) {
        this.items = new ViewItem[INVENTORY_ROW_SIZE * rows];
        this.rows = rows;
        this.frame = frame;
        this.title = title;
        nodes = new WeakHashMap<>();
        data = new WeakHashMap<>();

        // self registration, must be singleton
        if ((frame != null) && frame.isSelfRegister())
            frame.addView(this);
    }

    public ViewItem getItem(int slot) {
        return items[slot];
    }

    public ViewItem slot(int slot) {
        int max = INVENTORY_ROW_SIZE * rows;
        if (slot > max)
            throw new IllegalArgumentException("Slot exceeds the inventory limit (expected: < " + max + ", given: " + slot + ")");

        return items[slot] = new ViewItem(slot);
    }

    public ViewItem slot(int row, int column) {
        return slot((Math.max((row - 1), 0) * 9) + Math.max((column - 1), 0));
    }

    public ViewItem firstSlot() {
        return slot(0);
    }

    public ViewItem lastSlot() {
        return slot(getLastSlot());
    }

    public int getLastSlot() {
        return INVENTORY_ROW_SIZE * rows - 1;
    }

    public ViewFrame getFrame() {
        return frame;
    }

    void setFrame(ViewFrame frame) {
        this.frame = frame;
    }

    public int getRows() {
        return rows;
    }

    public String getTitle() {
        return title;
    }

    public void open(Player player) {
        open(player, null);
    }

    public void open(Player player, Map<String, Object> data) {
        if (nodes.containsKey(player))
            throw new IllegalStateException("Inventory already opened");

        Inventory inventory = getInventory();
        ViewContext context = new ViewContext(this, player, inventory);
        onOpen(context);
        if (context.isCancelled())
            return;

        if (data != null) {
            for (Map.Entry<String, Object> entry : data.entrySet())
                setData(player, entry.getKey(), entry.getValue());
        }

        nodes.put(player, inventory);
        onRender(context);
        render(context);
        player.openInventory(inventory);
    }

    protected void renderSlot(ViewContext context, ViewItem item, int slot) {
        ItemStack result = item.getItem();
        if (item.getRenderHandler() != null) {
            ViewSlotContext slotContext = new ViewSlotContext(this, context.getPlayer(), context.getInventory(), slot, result);
            item.getRenderHandler().handle(slotContext, null);
            if (!slotContext.hasChanged())
                return;

            result = slotContext.getItem();
        } else if (result != null)
            result = result.clone();

        context.getInventory().setItem(slot, result);
    }

    protected void renderSlot(ViewContext context, int slot) {
        ViewItem item = items[slot];
        if (item == null)
            return;

        renderSlot(context, item, slot);
    }

    private void render(ViewContext context) {
        for (int i = 0; i < items.length; i++) {
            renderSlot(context, i);
        }

        if (this instanceof PaginatedView) {
            PaginatedView<?> paginated = (PaginatedView<?>) this;
            if (paginated.getPaginationSource().isEmpty())
                return;

            Paginator<?> paginator = new Paginator<>(paginated.getLimit() - paginated.getOffset(), paginated.getPaginationSource());
            paginated.setPaginator(paginator);

            PaginatedViewContext viewContext = new PaginatedViewContext(paginated, context.getPlayer(), context.getInventory(), 0, paginator);
            paginated.updateNavigation(viewContext);
            viewContext.switchTo(0);
        }

    }

    public void updateSlot(Player player) {
        Inventory inventory = nodes.get(player);
        Preconditions.checkNotNull(inventory, "Player inventory cannot be null");

        for (int i = 0; i < items.length; i++) {
            updateSlot(player, inventory, i);
        }
    }

    public void updateSlot(Player player, int slot) {
        Inventory inventory = nodes.get(player);
        Preconditions.checkNotNull(inventory, "Player inventory cannot be null");

        updateSlot(player, inventory, slot);
    }

    public void updateSlot(Player player, Inventory inventory, int slot) {
        ViewItem item = items[slot];
        if (item == null) {
            return;
        }

        ViewSlotContext context = new ViewSlotContext(this, player, inventory, slot, inventory.getItem(slot));
        if (item.getUpdateHandler() != null) {
            item.getUpdateHandler().handle(context, null);
            inventory.setItem(slot, context.getItem());
        } else
            renderSlot(context, item, slot);
    }

    public void close(Player player) {
        close0(player, remove(player));
    }

    private void close0(Player player, Inventory inventory) {
        player.closeInventory();
        onClose(new ViewContext(this, player, inventory));
    }

    Inventory remove(Player player) {
        if (!nodes.containsKey(player))
            throw new IllegalStateException("Inventory not yet opened");

        clearData(player);
        return nodes.remove(player);
    }

    public void close() {
        for (Map.Entry<Player, Inventory> playerInventoryEntry : nodes.entrySet()) {
            playerInventoryEntry.getKey().closeInventory();
        }
    }

    public void setCancelOnClick(boolean cancelOnClick) {
        this.cancelOnClick = cancelOnClick;
    }

    public boolean isCancelOnClick() {
        return cancelOnClick;
    }

    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(this, INVENTORY_ROW_SIZE * rows, title);
    }

    public void clearData(Player player) {
        data.remove(player);
    }

    public Map<String, Object> getData(Player player) {
        return data.get(player);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(Player player, String key) {
        if (!data.containsKey(player))
            return null;
        return (T) data.get(player).get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(Player player, String key, T defaultValue) {
        if (!data.containsKey(player))
            return defaultValue;
        return (T) data.get(player).getOrDefault(key, defaultValue);
    }

    public void setData(Player player, String key, Object value) {
        data.computeIfAbsent(player, $ -> new HashMap<>()).put(key, value);
    }

    public boolean hasData(Player player, String key) {
        return data.containsKey(player) && data.get(player).containsKey(key);
    }

    protected void onRender(ViewContext context) {
    }

    protected void onOpen(ViewContext context) {
    }

    protected void onClose(ViewContext context) {
    }

    protected void onClick(ViewContext context, InventoryClickEvent event) {
    }

}