package me.xra1ny.pluginapi.models.maintenance;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.models.user.RUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class MaintenanceManager {
    @Getter(onMethod = @__(@NotNull))
    private String message;

    @Getter
    private boolean enabled;

    @Getter(onMethod = @__({ @NotNull, @Unmodifiable}))
    private final List<UUID> ignoredUsers = new ArrayList<>();

    public MaintenanceManager() {
        this.message = RPlugin.getInstance().getConfig().getString("maintenance.message");
        this.enabled = RPlugin.getInstance().getConfig().getBoolean("maintenance.enabled");

        final List<UUID> ignoredUsers = RPlugin.getInstance().getConfig().getStringList("maintenance.ignored").stream().map(UUID::fromString).toList();

        if(ignoredUsers != null) {
            this.ignoredUsers.addAll(ignoredUsers);
        }
    }

    public void setMessage(@NotNull String message) {
        if(this.message != null && this.message.equals(message)) {
            return;
        }

        RPlugin.getInstance().getConfig().set("maintenance.message", message);
        RPlugin.getInstance().saveConfig();

        // TODO: Add filter (Send Message only to permitted Players)
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + "Die Wartungsarbeiten Nachricht wurde angepasst!");
        }

        this.message = message;
    }

    public void setEnabled(boolean enabled) {
        if(this.enabled == enabled) {
            return;
        }

        RPlugin.getInstance().getConfig().set("maintenance.enabled", enabled);
        RPlugin.getInstance().saveConfig();

        // Kick all Users not permitted...
        for(RUser user : RPlugin.getInstance().getUserManager().getUsers()) {
            if(!this.ignoredUsers.contains(user.getPlayer().getUniqueId())) {
                user.getPlayer().kickPlayer(this.message);
            }
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + "Die Wartungen wurden " + String.valueOf(enabled)
                    .replace("true", ChatColor.GREEN + "aktiviert!")
                    .replace("false", ChatColor.RED + "deaktiviert!"));
        }

        this.enabled = enabled;
    }

    private void updateConfig() {
        RPlugin.getInstance().getConfig().set("maintenance.ignored", this.ignoredUsers.stream().map(UUID::toString).toList());
        RPlugin.getInstance().saveConfig();
    }

    public void add(@NotNull UUID uuid) {
        if(this.ignoredUsers.contains(uuid)) {
            return;
        }

        this.ignoredUsers.add(uuid);

        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        // TODO: Add filter (Send Message only to permitted Players)
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + ChatColor.YELLOW + offlinePlayer.getName() + RPlugin.getInstance().getChatColor() + " wurde als Wartungsarbeiten Ausnahme " + ChatColor.GREEN + "hinzugefügt!");
        }

        updateConfig();
    }

    public void remove(@NotNull UUID uuid) {
        if(!this.ignoredUsers.contains(uuid)) {
            return;
        }

        this.ignoredUsers.remove(uuid);

        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        // TODO: Add filter (Send Message only to permitted Players)
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + ChatColor.YELLOW + offlinePlayer.getName() + RPlugin.getInstance().getChatColor() + " wurde als Wartungsarbeiten Ausnahme " + ChatColor.RED + "entfernt!");
        }

        updateConfig();
    }
}
