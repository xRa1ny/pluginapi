package me.xra1ny.pluginapi.exceptions;

import me.xra1ny.pluginapi.models.exception.RPluginException;
import me.xra1ny.pluginapi.models.item.RItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackNotRegisteredException extends RPluginException {
    public ItemStackNotRegisteredException(@NotNull RItemStack itemStack) {
        super("itemstack " + itemStack + " is not yet registered!");
    }
}
