package me.xra1ny.pluginapi.listeners;

import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.UserNotRegisteredException;
import me.xra1ny.pluginapi.models.scoreboard.GlobalScoreboard;
import me.xra1ny.pluginapi.models.scoreboard.PerPlayerScoreboard;
import me.xra1ny.pluginapi.models.user.RUser;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

public class DefaultPluginConnectionListener implements Listener {
    @EventHandler
    public void onPlayerJoinServer(@NotNull PlayerJoinEvent event) {
        try {
            RUser user;
            try {
                user = RPlugin.getInstance().getUserManager().get(event.getPlayer());
            }catch (UserNotRegisteredException e) {
                user = RPlugin.getInstance().getUserManager().getUserClass().getDeclaredConstructor(Player.class).newInstance(event.getPlayer());
                RPlugin.getInstance().getUserManager().register(user);
            }

//            user.update(RPlugin.getInstance());
//
//            final User dbUser = RPlugin.getInstance().getDatabaseApiManager().getUserApi().getService(UserService.class).getDao(UserDao.class).getUser(user.getPlayer().getUniqueId());
//
//            if(dbUser == null) {
//                RPlugin.getInstance().getDatabaseApiManager().getUserApi().getUserService().createUser(user.getPlayer().getUniqueId(), user.getPlayer().getName(), RPlugin.getInstance().getDatabaseApiManager().getPermissionApi().getService(PermissionService.class).getDao(PermissionDao.class).getPermissionGroup("player").getId(), Date.from(Instant.now()));
//
//                user.getPlayer().showTitle(Title.title(Component.text(RPlugin.getInstance().CHAT_COLOR + "Willkommen"), Component.text(ChatColor.YELLOW + user.getPlayer().getName())));
//                user.getPlayer().sendMessage(RPlugin.getInstance().PREFIX + RPlugin.getInstance().CHAT_COLOR + "Willkommen auf RainyMC " + ChatColor.YELLOW + user.getPlayer().getName() + "!");
//            }

//            // dbUser will never be null at this point
//            assert dbUser != null;
//            dbUser.setLastOnline(Date.from(Instant.now()));
//            dbUser.setOnline(true);
//            RPlugin.getInstance().getDatabaseApiManager().getUserApi().getUserService().updateUser(dbUser);


            user.getPlayer().setPlayerListHeaderFooter(ChatColor.BOLD + "RAINYMC.DE\n", "\n");

//        Hide all Vanished Players
            for (RUser vanished : RPlugin.getInstance().getUserManager().getInVanishMode()) {
                user.getPlayer().hidePlayer(RPlugin.getInstance(), vanished.getPlayer());
            }

//        Show Effect when joining Server
            user.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 90, Integer.MAX_VALUE));
            user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);
            user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 1f, 1f);
            user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1f, 1f);

            event.setJoinMessage(null);
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default player join event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerLeaveServer(@NotNull PlayerQuitEvent event) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get(event.getPlayer());

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

            // Reset Users Tablist Custom Name
            user.getPlayer().setPlayerListName(user.getPlayer().getName());

//            final User dbUser = RPlugin.getInstance().getDatabaseApiManager().getUserApi().getUserService().getUser(user.getPlayer().getUniqueId());
//            // dbUser will never be null at this Point
//            assert dbUser != null;
//            dbUser.setOnline(false);
//            dbUser.setLastOnline(Date.from(Instant.now()));
//            RPlugin.getInstance().getDatabaseApiManager().getUserApi().getUserService().updateUser(dbUser);

//        Show all Players in Vanish
            for (RUser vanished : RPlugin.getInstance().getUserManager().getInVanishMode()) {
                user.getPlayer().showPlayer(RPlugin.getInstance(), vanished.getPlayer());
            }

            RPlugin.getInstance().getUserManager().unregister(user);
            event.setQuitMessage(null);
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default player quit event handler!");
            e.printStackTrace();
        }
    }
}
