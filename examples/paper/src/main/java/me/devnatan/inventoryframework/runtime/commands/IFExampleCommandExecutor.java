package me.devnatan.inventoryframework.runtime.commands;

import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.runtime.view.AnvilInputSample;
import me.devnatan.inventoryframework.runtime.view.Failing;
import me.devnatan.inventoryframework.runtime.view.SimplePagination;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class IFExampleCommandExecutor implements CommandExecutor {

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
            commandSender.sendMessage("Available views: anvil, failing, simple-pagination");
            return false;
        }

        String view = strings[0].toLowerCase();

        if (view.equalsIgnoreCase("anvil")) {
            viewFrame.open(AnvilInputSample.class, player);
            return true;
        }

        if (view.equalsIgnoreCase("failing")) {
            viewFrame.open(Failing.class, player);
            return true;
        }

        if (view.equalsIgnoreCase("simple-pagination")) {
            viewFrame.open(SimplePagination.class, player);
            return true;
        }

        commandSender.sendMessage("Unknown view: " + view);
        return false;
    }
}
