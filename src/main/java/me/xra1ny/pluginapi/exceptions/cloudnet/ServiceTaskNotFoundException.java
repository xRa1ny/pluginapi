package me.xra1ny.pluginapi.exceptions.cloudnet;

import me.xra1ny.pluginapi.models.exception.RPluginException;
import org.jetbrains.annotations.NotNull;

public class ServiceTaskNotFoundException extends RPluginException {
    public ServiceTaskNotFoundException(@NotNull String serviceTaskName) {
        super("service task " + serviceTaskName + " not found!");
    }
}
