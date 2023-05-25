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
     * the title of this inventory menu
     */
    @NotNull
    String title();

    /**
     * the size of this inventory menu
     */
    int size();

    /**
     * the material used as background of this inventory menu
     */
    @NotNull
    Material background();
}
