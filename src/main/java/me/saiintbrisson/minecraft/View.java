package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public class View extends VirtualView implements InventoryHolder, Closeable {

    public static final int INVENTORY_ROW_SIZE = 9;

    private ViewFrame frame;
    private final String title;
    private final int rows;
    final Map<Player, ViewContext> contexts;
    private boolean cancelOnClick;
    private boolean cancelOnPickup;
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
    public ViewItem slot(int slot) {
        ViewItem item = super.slot(slot);
        item.setCancelOnClick(cancelOnClick);
        return item;
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

    protected ViewContext createContext(View view, Player player, Inventory inventory) {
        return new ViewContext(view, player, inventory);
    }

    public void open(Player player) {
        open(player, null);
    }

    public void open(Player player, Map<String, Object> data) {
        if (contexts.containsKey(player))
            return;

        final OpenViewContext preOpenContext = new OpenViewContext(this, player);
        if (data != null) setData(player, data);

        onOpen(preOpenContext);
        if (preOpenContext.isCancelled()) {
            if (data != null) clearData(player);
            return;
        }

        final Inventory inventory = getInventory(preOpenContext.getInventoryTitle());
        final ViewContext context = createContext(this, player, inventory);
        context.setData(preOpenContext.data());
        contexts.put(player, context);
        onRender(context);
        render(context);
        player.openInventory(inventory);
    }

    @Override
    public void update(ViewContext context) {
        onUpdate(context);
        super.update(context);
    }

    void remove(final Player player) {
        if (!contexts.containsKey(player))
            return;

        clearData(player);
        final ViewContext context = contexts.remove(player);
        if (context != null)
            context.invalidate();
    }

    public void close() {
        for (Player player : contexts.keySet()) {
            player.closeInventory();
        }
    }

    public boolean isCancelOnClick() {
        return cancelOnClick;
    }

    public void setCancelOnClick(boolean cancelOnClick) {
        this.cancelOnClick = cancelOnClick;
    }

    public boolean isCancelOnPickup() {
        return cancelOnPickup;
    }

    public void setCancelOnPickup(boolean cancelOnPickup) {
        this.cancelOnPickup = cancelOnPickup;
    }

    @Override
    public Inventory getInventory() {
        return getInventory(title);
    }

    private Inventory getInventory(String title) {
        return Bukkit.createInventory(this, getLastSlot() + 1, title == null ? this.title : title);
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
    public <T> T getData(Player player, String key, Supplier<T> defaultValue) {
        if (!data.containsKey(player) || !data.get(player).containsKey(key))
            return defaultValue.get();

        return (T) data.get(player).get(key);
    }

    public void setData(Player player, Map<String, Object> data) {
        this.data.put(player, data);
    }

    public void setData(Player player, String key, Object value) {
        data.computeIfAbsent(player, $ -> new HashMap<>()).put(key, value);
    }

    public boolean hasData(Player player, String key) {
        if (!data.containsKey(player))
            return false;

        return data.get(player).containsKey(key);
    }

    protected void onOpen(OpenViewContext context) {
    }

    protected void onRender(ViewContext context) {
    }

    protected void onClose(ViewContext context) {
    }

    protected void onClick(ViewSlotContext context) {
    }

    protected void onUpdate(ViewContext context) {
    }

}