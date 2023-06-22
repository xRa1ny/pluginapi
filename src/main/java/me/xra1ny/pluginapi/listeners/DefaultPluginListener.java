package me.xra1ny.pluginapi.listeners;

import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.models.menu.RInventoryMenu;
import me.xra1ny.pluginapi.models.menu.RPagedInventoryMenu;
import me.xra1ny.pluginapi.models.user.RUser;
import me.xra1ny.pluginapi.models.user.UserInputWindow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Level;

@Slf4j
public final class DefaultPluginListener implements Listener {
    @EventHandler
    public void onPlayerOpenInventory(@NotNull InventoryOpenEvent e) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get((Player) e.getPlayer());

            if (e.getInventory().getHolder() instanceof RInventoryMenu inventoryMenu) {
                inventoryMenu.onOpen(e, user);

                inventoryMenu.setBackground();
                inventoryMenu.setItems(user);

                if (inventoryMenu instanceof RPagedInventoryMenu pagedInventoryMenu) {
                    pagedInventoryMenu.setPage(1, user);
                }
            }
        }catch(Exception ex) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default inventory open event handler!");
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerClickInInventory(@NotNull InventoryClickEvent e) {
        try {
            if(e.getClickedInventory() == null) {
                return;
            }

            final RUser user = RPlugin.getInstance().getUserManager().get((Player) e.getWhoClicked());

            if(e.getClickedInventory().getHolder() instanceof RInventoryMenu inventoryMenu) {

                // If the User clicks outside of Inventory Window, close it
                if(e.getClickedInventory() == null) {
                    user.getPlayer().closeInventory();

                    return;
                }

                inventoryMenu.handleClick(e, user);
                e.setCancelled(true);
            }
        }catch(Exception ex) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default inventory click event handler!");
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerCloseInventory(@NotNull InventoryCloseEvent e) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get((Player) e.getPlayer());

            if (e.getInventory().getHolder() instanceof RInventoryMenu inventoryMenu) {
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

                inventoryMenu.onClose(e, user);
            }
        }catch(Exception ex) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default inventory close event handler!");
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent e) {
        try {
            if (e.getItem() == null) {
                return;
            }

            final RUser user = RPlugin.getInstance().getUserManager().get(e.getPlayer());

            RPlugin.getInstance().getItemStackManager().getItems()
                    .stream()
                    .filter(i -> i.equals(e.getItem()))
                    .findFirst().ifPresent(itemStack -> itemStack.handleInteraction(e, user));
        }catch(Exception ex) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default player interact event handler!");
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerSendChatMessage(AsyncPlayerChatEvent e) {
        try {
            final RUser user = RPlugin.getInstance().getUserManager().get(e.getPlayer());
            final UserInputWindow userInputWindow = RPlugin.getInstance().getUserInputWindowManager().get(user);

            if(userInputWindow == null) {
                return;
            }

            e.setCancelled(true);
            userInputWindow.getInputWindowHandler().onUserSendChatMessage(user, e.getMessage());
        }catch(Exception ex) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default async chat event handler!");
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerPreLogin(@NotNull AsyncPlayerPreLoginEvent e) {
        try {
            // If the Maintenances are enabled on this Server, check whether the current User is allowed to Join or not
            if (RPlugin.getInstance().getServerMaintenanceManager().isEnabled()) {
                if (!RPlugin.getInstance().getServerMaintenanceManager().getIgnoredUsers().stream()
                        .map(UUID::toString).toList().contains(e.getUniqueId().toString())) {
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, RPlugin.getInstance().getServerMaintenanceManager().getMessage());
                } else {
                    e.allow();
                }
            }
        }catch(Exception ex) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default async player pre login event handler!");
            ex.printStackTrace();
        }
    }
}