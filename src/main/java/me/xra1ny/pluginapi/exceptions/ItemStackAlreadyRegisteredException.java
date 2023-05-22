package me.xra1ny.pluginapi.exceptions;

import me.xra1ny.pluginapi.models.exception.RPluginException;
import me.xra1ny.pluginapi.models.item.RItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackAlreadyRegisteredException extends RPluginException {
    public ItemStackAlreadyRegisteredException(@NotNull RItemStack itemStack) {
        super("itemstack " + itemStack + " is already registered!");
    }
}
