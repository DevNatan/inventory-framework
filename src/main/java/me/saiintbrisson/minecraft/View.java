package me.saiintbrisson.minecraft;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class View extends VirtualView implements InventoryHolder, Closeable {

    public static final int INVENTORY_ROW_SIZE = 9;

    private ViewFrame frame;
    private final String title;
    private final int rows;
    final Map<Player, ViewContext> contexts;
    private boolean cancelOnClick = true;
    private final Map<Player, Map<String, Object>> data;

    public View(int rows) {
        this(null, rows, "");
    }

    public View(int rows, String title) {
        this(null, rows, title);
    }

    public View(ViewFrame frame, int rows, String title) {
        super(new ViewItem[INVENTORY_ROW_SIZE * rows]);
        this.rows = rows;
        this.frame = frame;
        this.title = title;
        contexts = new WeakHashMap<>();
        data = new WeakHashMap<>();

        // self registration, must be singleton
        if ((frame != null) && frame.isSelfRegister())
            frame.addView(this);
    }

    @Override
    public int getLastSlot() {
        return INVENTORY_ROW_SIZE * rows - 1;
    }

    public ViewContext getContext(Player player) {
        return contexts.get(player);
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
        if (contexts.containsKey(player))
            throw new IllegalStateException("Inventory already opened");

        PreRenderViewContext preOpenContext = new PreRenderViewContext(this, player);
        if (data != null) setData(player, data);

        onOpen(preOpenContext);
        if (preOpenContext.isCancelled()) {
            if (data != null) clearData(player);
            return;
        }

        Inventory inventory = getInventory(preOpenContext.getInventoryTitle());
        ViewContext context = new ViewContext(this, player, inventory);
        contexts.put(player, context);
        onRender(context);
        render(context);
        context.render(context); // render virtual view
        player.openInventory(inventory);
    }

    public void updateSlot(Player player) {
        Inventory inventory = contexts.get(player).getInventory();
        Preconditions.checkNotNull(inventory, "Player inventory cannot be null");

        for (int i = 0; i < getItems().length; i++) {
            updateSlot(player, inventory, i);
        }
    }

    public void updateSlot(Player player, int slot) {
        Inventory inventory = contexts.get(player).getInventory();
        Preconditions.checkNotNull(inventory, "Player inventory cannot be null");

        updateSlot(player, inventory, slot);
    }

    public void updateSlot(Player player, Inventory inventory, int slot) {
        ViewItem item = getItem(slot);
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
        player.closeInventory();
    }

    void remove(Player player) {
        if (!contexts.containsKey(player))
            throw new IllegalStateException("Inventory not yet opened");

        clearData(player);
        contexts.remove(player);
    }

    public void close() {
        for (Player player : contexts.keySet()) {
            player.closeInventory();
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
        return getInventory(title);
    }

    private Inventory getInventory(String title) {
        return Bukkit.createInventory(this, INVENTORY_ROW_SIZE * rows, title == null ? this.title : title);
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

    public void setData(Player player, Map<String, Object> data) {
        this.data.put(player, data);
    }

    public void setData(Player player, String key, Object value) {
        data.computeIfAbsent(player, $ -> new HashMap<>()).put(key, value);
    }

    public boolean hasData(Player player, String key) {
        return data.containsKey(player) && data.get(player).containsKey(key);
    }

    protected void onRender(ViewContext context) {
    }

    protected void onOpen(PreRenderViewContext context) {
    }

    protected void onClose(ViewContext context) {
    }

    protected void onClick(ViewSlotContext context) {
    }

}