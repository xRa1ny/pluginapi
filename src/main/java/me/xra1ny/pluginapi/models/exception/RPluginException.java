package me.xra1ny.pluginapi.models.exception;

import org.jetbrains.annotations.NotNull;

public class RPluginException extends Exception {
    public RPluginException(@NotNull String message) {
        super(message);
    }
}
