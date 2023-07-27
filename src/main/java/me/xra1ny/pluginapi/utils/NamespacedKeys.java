package me.xra1ny.pluginapi.utils;

import me.xra1ny.pluginapi.RPlugin;
import org.bukkit.NamespacedKey;

public interface NamespacedKeys {
    NamespacedKey ITEM_UUID = new NamespacedKey(RPlugin.getInstance(), "itemUuid");
}
