package me.xra1ny.pluginapi.models.user;

import lombok.Getter;
import me.xra1ny.pluginapi.RPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RUser {
    /**
     * the player of this user
     */
    @Getter(onMethod = @__(@NotNull))
    private final Player player;

    @Getter(onMethod = @__(@NotNull))
    private final Date creation = Date.from(Instant.now());

    @Getter
    private boolean inFlyMode, inVanishMode, inBuildMode;

    /**
     * the list of all users this user ignores
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<RUser> ignored = new ArrayList<>();

    public RUser(@NotNull Player player) {
        this.player = player;
    }

    public void setInBuildMode(boolean inBuildMode) {
        if(this.inBuildMode == inBuildMode) {
            return;
        }

        if(inBuildMode) {
            this.player.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + "Du hast den Baumodus betreten!");
        }else {
            this.player.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + "Du hast den Baumodus verlassen!");
        }

        this.inBuildMode = inBuildMode;
    }

    public void setInFlyMode(boolean inFlyMode) {
        if(this.inFlyMode == inFlyMode) {
            return;
        }

        if(inFlyMode) {
            this.player.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + "Du hast den Flugmodus betreten!");
        }else {
            this.player.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + "Du hast den Flugmodus verlassen!");
        }

        this.inFlyMode = inFlyMode;
    }

    public void setInVanishMode(boolean inVanishMode) {
        if(this.inVanishMode == inVanishMode) {
            return;
        }

        if(inVanishMode) {
            this.player.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + "Du hast den Vanishmodus betreten!");
        }else {
            this.player.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + "Du hast den Vanishmodus verlassen!");
        }

        this.inVanishMode = inVanishMode;
    }
}
