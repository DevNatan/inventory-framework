package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.component.Component;

public interface IFComponentContext extends IFContext {

	IFContext getTopLevelContext();

    Component getComponent();
}
