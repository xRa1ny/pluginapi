package me.xra1ny.pluginapi.models.cloudnet;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import me.xra1ny.pluginapi.exceptions.cloudnet.ServiceInfoSnapshotNotFoundException;
import me.xra1ny.pluginapi.exceptions.cloudnet.ServiceTaskNotFoundException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class CloudNetManager {
    @Nullable
    public CloudNetServer startNewCloudServer(@NotNull String server) throws ServiceTaskNotFoundException, ServiceInfoSnapshotNotFoundException {
        try {
            getCloudServer(server);
        }catch (ServiceInfoSnapshotNotFoundException e) {
            final ServiceTask serviceTask = CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(server);

            if(serviceTask == null) {
                throw new ServiceTaskNotFoundException(server);
            }

            final ServiceInfoSnapshot serviceInfoSnapshot = CloudNetDriver.getInstance().getCloudServiceFactory().createCloudService(serviceTask);

            if(serviceInfoSnapshot == null) {
                throw new ServiceInfoSnapshotNotFoundException(serviceTask);
            }

            serviceInfoSnapshot.provider().start();

            return new CloudNetServer(serviceInfoSnapshot);
        }

        return null;
    }

    @NotNull
    public CloudNetServer getCloudServer(@NotNull String server) throws ServiceInfoSnapshotNotFoundException {
        final ServiceInfoSnapshot serviceInfoSnapshot = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServiceByName(server);

        if(serviceInfoSnapshot == null) {
            throw new ServiceInfoSnapshotNotFoundException(server);
        }

        return new CloudNetServer(serviceInfoSnapshot);
    }

    public int getCloudServersPlayerCount(@NotNull String task) {
        int playerCount = 0;

        for(ServiceInfoSnapshot server : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices("")) {
            playerCount+=server.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0);
        }

        return playerCount;
    }

    public List<CloudNetServer> getCloudServers() {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices().stream()
                .map(CloudNetServer::new)
                .toList();
    }

    public List<CloudNetServer> getCloudServers(@NotNull String task) {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices(task).stream()
                .map(CloudNetServer::new)
                .toList();
    }

    @Nullable
    public CloudNetServer getCloudServer(@NotNull Player player) {
        return getCloudServers().stream()
                .filter(cloudServer -> cloudServer.getPlayers().contains(player))
                .findFirst().orElse(null);
    }
}
