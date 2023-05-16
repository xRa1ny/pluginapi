package me.xra1ny.pluginapi.models.menu;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InventoryMenuInfo {
    /**
     * Specifies the Title of the Inventory Menu
     */
    @NotNull
    String title();

    /**
     * Specifies the Size of the Inventory Menu
     */
    int size();

    /**
     * Specifies the Background this Inventory Menu should have
     */
    @NotNull
    Material background();
}
