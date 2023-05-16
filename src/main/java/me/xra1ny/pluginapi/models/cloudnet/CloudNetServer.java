package me.xra1ny.pluginapi.models.cloudnet;

import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.PluginInfo;
import de.dytanic.cloudnet.ext.bridge.player.ServicePlayer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public final class CloudNetServer {
    @Getter(onMethod = @__(@NotNull))
    private final ServiceInfoSnapshot serviceInfoSnapshot;

    CloudNetServer(@NotNull ServiceInfoSnapshot serviceInfoSnapshot) {
        this.serviceInfoSnapshot = serviceInfoSnapshot;
    }

    public void start() {
        this.serviceInfoSnapshot.provider().start();
    }

    public void stop() {
        this.serviceInfoSnapshot.provider().stop();
    }

    public boolean isStarting() {
        return this.serviceInfoSnapshot.getProperty(BridgeServiceProperty.IS_STARTING).orElse(false);
    }

    public void setStarting(boolean starting) {
        this.serviceInfoSnapshot.setProperty(BridgeServiceProperty.IS_STARTING, starting);
    }

    public boolean isOnline() {
        return this.serviceInfoSnapshot.getProperty(BridgeServiceProperty.IS_ONLINE).orElse(false);
    }

    public void setOnline(boolean online) {
        this.serviceInfoSnapshot.setProperty(BridgeServiceProperty.IS_ONLINE, online);
    }

    public boolean isFull() {
        return this.serviceInfoSnapshot.getProperty(BridgeServiceProperty.IS_FULL).orElse(false);
    }

    public void setFull(boolean full) {
        this.serviceInfoSnapshot.setProperty(BridgeServiceProperty.IS_FULL, full);
    }

    public boolean isEmpty() {
        return this.serviceInfoSnapshot.getProperty(BridgeServiceProperty.IS_EMPTY).orElse(false);
    }

    public void setEmpty(boolean empty) {
        this.serviceInfoSnapshot.setProperty(BridgeServiceProperty.IS_EMPTY, empty);
    }

    public boolean isIngame() {
        return this.serviceInfoSnapshot.getProperty(BridgeServiceProperty.IS_IN_GAME).orElse(false);
    }

    public void setIngame(boolean ingame) {
        this.serviceInfoSnapshot.setProperty(BridgeServiceProperty.IS_IN_GAME, ingame);
    }

    public int getPlayerCount() {
        return this.serviceInfoSnapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0);
    }

    public void setPlayerCount(int playerCount) {
        this.serviceInfoSnapshot.setProperty(BridgeServiceProperty.ONLINE_COUNT, playerCount);
    }

    public int getMaxPlayers() {
        return this.serviceInfoSnapshot.getProperty(BridgeServiceProperty.MAX_PLAYERS).orElse(0);
    }

    public void setMaxPlayers(int maxPlayers) {
        this.serviceInfoSnapshot.setProperty(BridgeServiceProperty.MAX_PLAYERS, maxPlayers);
    }

    @NotNull
    public List<ServicePlayer> getPlayers() {
        return this.serviceInfoSnapshot.getProperty(BridgeServiceProperty.PLAYERS).orElse(List.of()).stream().toList();
    }

    public void setPlayers(@NotNull Collection<ServicePlayer> players) {
        this.serviceInfoSnapshot.setProperty(BridgeServiceProperty.PLAYERS, players);
    }

    @Nullable
    public String getMotd() {
        return this.serviceInfoSnapshot.getProperty(BridgeServiceProperty.MOTD).orElse(null);
    }

    public void setMotd(@NotNull String motd) {
        this.serviceInfoSnapshot.setProperty(BridgeServiceProperty.MOTD, motd);
    }

    @NotNull
    public List<PluginInfo> getPlugins() {
        return this.serviceInfoSnapshot.getProperty(BridgeServiceProperty.PLUGINS).orElse(List.of()).stream().toList();
    }

    public void setPlugins(@NotNull Collection<PluginInfo> plugins) {
        this.serviceInfoSnapshot.setProperty(BridgeServiceProperty.PLUGINS, plugins);
    }

    @Nullable
    public String getState() {
        return this.serviceInfoSnapshot.getProperty(BridgeServiceProperty.STATE).orElse(null);
    }

    public void setState(@NotNull String state) {
        this.serviceInfoSnapshot.setProperty(BridgeServiceProperty.STATE, state);
    }

    @Nullable
    public String getVersion() {
        return this.serviceInfoSnapshot.getProperty(BridgeServiceProperty.VERSION).orElse(null);
    }

    public void setVersion(@NotNull String version) {
        this.serviceInfoSnapshot.setProperty(BridgeServiceProperty.VERSION, version);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CloudNetServer && ((CloudNetServer) obj).getServiceInfoSnapshot().equals(this.serviceInfoSnapshot);
    }
}
