package me.xra1ny.pluginapi.listeners;

import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.models.scoreboard.GlobalScoreboard;
import me.xra1ny.pluginapi.models.scoreboard.PerPlayerScoreboard;
import me.xra1ny.pluginapi.models.user.RUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

public class DefaultPluginConnectionListener implements Listener {
    @EventHandler
    public void onPlayerJoinServer(@NotNull PlayerJoinEvent e) {
        try {
            RUser user = RPlugin.getInstance().getUserManager().get(e.getPlayer());

            if(user == null) {
                user = RPlugin.getInstance().getUserManager().getUserClass().getDeclaredConstructor(Player.class).newInstance(e.getPlayer());
                RPlugin.getInstance().getUserManager().register(user);
            }else {
                user.setTimeout(RPlugin.getInstance().getUserManager().getUserTimeoutHandler().getUserTimeout());
                user.update();
            }

            e.setJoinMessage(null);
        }catch(Exception ex) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default player join event handler!");
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerLeaveServer(@NotNull PlayerQuitEvent e) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get(e.getPlayer());
            final List<GlobalScoreboard> globalScoreboards = RPlugin.getInstance().getScoreboardManager().getGlobalScoreboards(user);

            if(globalScoreboards.size() != 0) {
                final GlobalScoreboard globalScoreboard = globalScoreboards.get(0);

                if(globalScoreboard != null) {
                    globalScoreboard.remove(user);
                }
            }

            final List<PerPlayerScoreboard> perPlayerScoreboards = RPlugin.getInstance().getScoreboardManager().getPerPlayerScoreboards(user);

            if(perPlayerScoreboards.size() != 0) {
                final PerPlayerScoreboard perPlayerScoreboard = perPlayerScoreboards.get(0);

                if(perPlayerScoreboard != null) {
                    perPlayerScoreboard.remove(user);
                }
            }

            e.setQuitMessage(null);
        }catch(Exception ex) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default player quit event handler!");
            ex.printStackTrace();
        }
    }
}
