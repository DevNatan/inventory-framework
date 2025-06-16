package me.devnatan.inventoryframework.runtime;

import me.devnatan.inventoryframework.AnvilInputFeature;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.runtime.commands.IFExampleCommandExecutor;
import me.devnatan.inventoryframework.runtime.listener.PigListener;
import me.devnatan.inventoryframework.runtime.view.AnvilInputSample;
import me.devnatan.inventoryframework.runtime.view.AutoUpdate;
import me.devnatan.inventoryframework.runtime.view.Failing;
import me.devnatan.inventoryframework.runtime.view.SimplePagination;
import org.bukkit.plugin.java.JavaPlugin;

public class SamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("ligoo");
        ViewFrame viewFrame = ViewFrame.create(this)
                .install(AnvilInputFeature.AnvilInput)
                .with(new AnvilInputSample(), new Failing(), new SimplePagination(), new AutoUpdate())
                .register();

        getCommand("ifexample").setExecutor(new IFExampleCommandExecutor(viewFrame));
        getServer().getPluginManager().registerEvents(new PigListener(viewFrame), this);
    }
}
