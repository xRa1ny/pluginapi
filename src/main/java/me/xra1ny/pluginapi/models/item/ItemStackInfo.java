package me.xra1ny.pluginapi.models.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ItemStackInfo {
    @NotNull
    String name();

    @NotNull
    String[] lore() default {};

    int amount() default 1;

    @NotNull
    Material type();

    @NotNull
    ItemFlag[] itemFlags() default {};

    int cooldown() default 0;

    boolean localised() default false;

    boolean enchanted() default false;
}
