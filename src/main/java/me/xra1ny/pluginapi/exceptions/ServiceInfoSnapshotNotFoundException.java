package me.xra1ny.pluginapi.exceptions;

import de.dytanic.cloudnet.driver.service.ServiceTask;
import me.xra1ny.pluginapi.models.exception.RPluginException;
import org.jetbrains.annotations.NotNull;

public class ServiceInfoSnapshotNotFoundException extends RPluginException {
    public ServiceInfoSnapshotNotFoundException(@NotNull String serviceInfoSnapshotName) {
        super("service info snapshot " + serviceInfoSnapshotName + " not found!");
    }

    public ServiceInfoSnapshotNotFoundException(@NotNull ServiceTask serviceTask) {
        super("service info snapshot of service task " + serviceTask + " not found!");
    }
}
