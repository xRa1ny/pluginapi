package me.xra1ny.pluginapi.models.user;

import lombok.Getter;
import lombok.Setter;
import me.xra1ny.pluginapi.RPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RUser {
    /**
     * the player of this user (might be null after player disconnected)
     */
    @Getter(onMethod = @__(@Nullable))
    private Player player;

    /**
     * the date of creation of this user instance
     */
    @Getter(onMethod = @__(@NotNull))
    private final Date creation = Date.from(Instant.now());

    /**
     * the timeout of this user (unregisters this user when 0)
     */
    @Getter
    @Setter
    private long timeout;

    /**
     * the list of all users this user ignores
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<RUser> ignored = new ArrayList<>();

    public RUser(@NotNull Player player) {
        this.player = player;
        this.timeout = RPlugin.getInstance().getUserManager().getUserTimeoutHandler().getUserTimeout();
    }

    @Override
    public String toString() {
        return String.format(
                "RUser(player=%s, creation=%s, timeout=%o)",
                this.player.getName(), this.creation, this.timeout
        );
    }

    /**
     * updates this user
     */
    public void update() {
        if(this.player == null) {
            return;
        }

        this.player = Bukkit.getPlayer(this.player.getUniqueId());
    }
}
