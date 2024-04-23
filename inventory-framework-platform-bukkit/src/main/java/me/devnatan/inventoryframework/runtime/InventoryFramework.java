package me.devnatan.inventoryframework.runtime;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.context.CloseContext;
import me.devnatan.inventoryframework.context.OpenContext;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class InventoryFramework extends JavaPlugin {

    public static final String LIBRARY_VERSION = "3.0.9";

    @Override
    public void onEnable() {
        ViewFrame.create(this).enableDebug().with(new A(), new B()).register();
    }
}

class A extends View {

    @Override
    public void onOpen(@NotNull OpenContext open) {
        Bukkit.broadcastMessage("[A] onOpen");
    }

    @Override
    public void onClose(@NotNull CloseContext close) {
        Bukkit.broadcastMessage("[A] onClose");
    }
}

class B extends View {

    @Override
    public void onOpen(@NotNull OpenContext open) {
        Bukkit.broadcastMessage("[B] onOpen");
    }

    @Override
    public void onClose(@NotNull CloseContext close) {
        Bukkit.broadcastMessage("[B] onClose");
    }
}
