package me.xra1ny.pluginapi.listeners;

import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.UserNotRegisteredException;
import me.xra1ny.pluginapi.models.menu.RInventoryMenu;
import me.xra1ny.pluginapi.models.menu.RPagedInventoryMenu;
import me.xra1ny.pluginapi.models.scoreboard.GlobalScoreboard;
import me.xra1ny.pluginapi.models.scoreboard.PerPlayerScoreboard;
import me.xra1ny.pluginapi.models.user.RUser;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Level;

@Slf4j
public final class DefaultPluginListener implements Listener {
    @EventHandler
    public void onPlayerPlaceBlock(@NotNull BlockPlaceEvent event) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get(event.getPlayer());

//            for(RUserInteractionListener listener : RPlugin.getInstance().getListenerManager().get(RUserInteractionListener.class)) {
//                listener.onUserPlaceBlock(event, user);
//            }

//        Allow Event if User is in 'Build Mode'
            if(user.isInBuildMode()) {
                event.setCancelled(false);
            }
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default block place event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(@NotNull BlockBreakEvent event) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get(event.getPlayer());

//        Allow Event if User is in 'Build Mode'
            if(user.isInBuildMode()) {
                event.setCancelled(false);
            }
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default block break event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
        try {
            if(event.getEntity() instanceof Player) {
                final RUser user = RPlugin.getInstance().getUserManager().get((Player) event.getEntity());

//        Allow Event if User is in 'Build Mode'
                if(user.isInBuildMode()) {
                    event.setCancelled(false);
                }
            }
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default entity damage event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        try {
            if(event.getDamager() instanceof Player) {
                RUser user = RPlugin.getInstance().getUserManager().get((Player) event.getDamager());
                RUser damager = null;

                if(event.getEntity() instanceof Player) {
                    damager = RPlugin.getInstance().getUserManager().get((Player) event.getEntity());
                }

//        Allow Event if User is in 'Build Mode'
                if(user.isInBuildMode()) {
                    event.setCancelled(false);
                }
            }
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default entity damage by entity event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent event) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get(event.getPlayer());

//        Allow Event if User is in 'Build Mode'
            if(user.isInBuildMode()) {
                event.setCancelled(false);
            }
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default player drop item event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerConsumeItem(@NotNull PlayerItemConsumeEvent event) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get(event.getPlayer());

//        Allow Event if User is in 'Build Mode'
            if(user.isInBuildMode()) {
                event.setCancelled(false);
            }
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default player item consume event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerOpenInventory(@NotNull InventoryOpenEvent event) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get((Player) event.getPlayer());

            if (event.getInventory().getHolder() == null) {
                return;
            }

            if (event.getInventory().getHolder() instanceof RInventoryMenu inventoryMenu) {
                inventoryMenu.onOpen(event, user);

                inventoryMenu.setBackground();
                inventoryMenu.setItems(user);

                if (inventoryMenu instanceof RPagedInventoryMenu pagedInventoryMenu) {
                    pagedInventoryMenu.setPage(1);
                }
            }
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default inventory open event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerClickInInventory(@NotNull InventoryClickEvent event) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get((Player) event.getWhoClicked());

            if (event.getInventory().getHolder() != null) {
                if (event.getInventory().getHolder() instanceof RInventoryMenu inventoryMenu) {

                    // If the User clicks outside of Inventory Window, close it
                    if(event.getClickedInventory() == null) {
                        user.getPlayer().closeInventory();

                        return;
                    }

                    inventoryMenu.handleClick(event, user);
                    event.setCancelled(true);
                }
            }

//        Allow Event if User is in 'Build Mode'
            if(user.isInBuildMode()) {
                event.setCancelled(false);
            }
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default inventory click event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerCloseInventory(@NotNull InventoryCloseEvent event) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get((Player) event.getPlayer());

            if (event.getInventory().getHolder() == null) {
                return;
            }

            if (event.getInventory().getHolder() instanceof RInventoryMenu inventoryMenu) {
                if(inventoryMenu.getOpenUsers().contains(user)) {
                    inventoryMenu.getOpenUsers().remove(user);

                    if(inventoryMenu.getPreviousMenu() != null) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                inventoryMenu.getPreviousMenu().open(user);
                            }
                        }.runTaskLater(RPlugin.getInstance(), 1L);
                    }

                }

                inventoryMenu.onClose(event, user);
            }
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default inventory close event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get(event.getPlayer());

            // Allow Event if User is in 'Build Mode'
            if(user.isInBuildMode()) {
                event.setCancelled(false);
            }

            if (event.getItem() == null) {
                return;
            }

            RPlugin.getInstance().getItemStackManager().getItems()
                    .stream()
                    .filter(i -> i.toString().equals(event.getItem().toString()))
                    .findFirst().ifPresent(itemStack -> itemStack.handleInteraction(event, user));
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default player interact event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerLoginServer(@NotNull PlayerLoginEvent event) {
        try {
            event.getPlayer().setOp(false);
        } catch (Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default player login event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerPickupItem(@NotNull EntityPickupItemEvent event) {
       if(!(event.getEntity() instanceof Player player)) {
           return;
       }

        try {
            final RUser user = RPlugin.getInstance().getUserManager().get(player);

//        Allow Event if User is in 'Build Mode'
            if(user.isInBuildMode()) {
                event.setCancelled(false);
            }
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default player attempt pickup item event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerSendChatMessage(AsyncPlayerChatEvent event) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get(event.getPlayer());

            if(RPlugin.getInstance().getUserInputWindowManager().get(user).size() > 0) {
                RPlugin.getInstance().getUserInputWindowManager().get(user).get(0).getInputWindowHandler().onUserSendChatMessage(user, event.getMessage());

                return;
            }

            if(!event.isCancelled()) {
                event.setCancelled(true);

                for (RUser _user : RPlugin.getInstance().getUserManager().getUsers()) {
                    if (!_user.getIgnored().contains(user)) {
                        _user.getPlayer().sendMessage(user.getPlayer().getDisplayName() +
                                ChatColor.DARK_GRAY + " >> " +
                                RPlugin.getInstance().getChatColor() + event.getMessage());
                    }
                }
            }
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default async chat event handler!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerPreLogin(@NotNull AsyncPlayerPreLoginEvent event) {
        try {
            // If the Maintenances are enabled on this Server, check whether the current User is allowed to Join or not
            if (RPlugin.getInstance().getMaintenanceManager().isEnabled()) {
                if (!RPlugin.getInstance().getMaintenanceManager().getIgnoredUsers().stream()
                        .map(UUID::toString).toList().contains(event.getUniqueId().toString())) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, RPlugin.getInstance().getMaintenanceManager().getMessage());
                } else {
                    event.allow();
                }
            }
        }catch(Exception e) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default async player pre login event handler!");
            e.printStackTrace();
        }
    }
}