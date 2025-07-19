package me.devnatan.inventoryframework.runtime.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.runtime.view.AnvilInputSample;
import me.devnatan.inventoryframework.runtime.view.AutoUpdate;
import me.devnatan.inventoryframework.runtime.view.Failing;
import me.devnatan.inventoryframework.runtime.view.SimplePagination;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IFExampleCommandExecutor implements CommandExecutor, TabCompleter {

    private static final Map<String, Class<? extends View>> views = Map.of(
            "anvil", AnvilInputSample.class,
            "failing", Failing.class,
            "simple-pagination", SimplePagination.class,
            "auto-update", AutoUpdate.class);

    private final ViewFrame viewFrame;

    public IFExampleCommandExecutor(ViewFrame viewFrame) {
        this.viewFrame = viewFrame;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can only be executed by players.");
            return false;
        }

        Player player = (Player) commandSender;

        if (strings.length == 0) {
            commandSender.sendMessage("Usage: /ifexample <view>");
            commandSender.sendMessage("Available views: anvil, failing, simple-pagination, auto-update");
            return false;
        }

        Class<? extends View> viewClass = views.get(strings[0].toLowerCase());
        if (viewClass != null) {
            viewFrame.open(viewClass, player);
            return true;
        }

        commandSender.sendMessage("Unknown view: " + strings[0]);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] strings) {
        return new ArrayList<>(views.keySet());
    }
}
