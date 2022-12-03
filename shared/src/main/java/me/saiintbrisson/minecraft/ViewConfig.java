package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.internal.InitOnly;

// TODO Allow slot inheritance
public interface ViewConfig {

    /**
     * Defines the type of the container.
     * <p>
     * If applied in view scope, it will be the default value for all contexts originated from it.
     *
     * @param type The container type.
     */
    @InitOnly
    void setContainerType(ViewType type);

    /**
     * Defines the title of the container.
     * <p>
     * If applied in view scope, it will be the default value for all contexts originated from it.
     *
     * @param title The container title.
     */
    @InitOnly
    void setContainerTitle(String title);

    /**
     * Defines the size of the container.
     * <p>
     * If applied in view scope, it will be the default value for all contexts originated from it.
     *
     * @param size The container size.
     */
    @InitOnly
    void setContainerSize(int size);
}
