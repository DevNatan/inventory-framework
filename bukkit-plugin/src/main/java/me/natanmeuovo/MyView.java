package me.natanmeuovo;

import me.devnatan.inventoryframework.config.ViewConfig;
import me.saiintbrisson.minecraft.View;

class MyView extends View {

    @Override
    protected void onInit(ViewConfig config) {
        super.onInit(config);
        System.out.println("aa");
    }
}
