package me.xra1ny.pluginapi.models.menu;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.exceptions.ClassNotAnnotatedException;
import me.xra1ny.pluginapi.models.item.ItemBuilder;
import me.xra1ny.pluginapi.models.user.RUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Used to create Interactive Inventories */
@Slf4j
public abstract class RInventoryMenu implements InventoryHolder {
    /**
     * the inventory of this inventory menu
     */
    @Getter(onMethod = @__(@NotNull))
    private Inventory inventory;

    /**
     * the background item of this inventory menu
     */
    @Getter(onMethod = @__(@NotNull))
    private final org.bukkit.inventory.ItemStack background;

    /**
     * the title of this inventory menu
     */
    @Getter(onMethod = @__(@NotNull))
    private final String title;

    /**
     * the size of this inventory menu
     */
    @Getter
    private final int size;

    /**
     * the users this inventory is currently open for
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<RUser> openUsers = new ArrayList<>();

    /**
     * the previous menu of this inventory menu
     */
    @Getter(onMethod = @__(@Nullable))
    private final RInventoryMenu previousMenu;

    public RInventoryMenu(@Nullable RInventoryMenu previousMenu) {
        final InventoryMenuInfo info = getClass().getDeclaredAnnotation(InventoryMenuInfo.class);

        if(info == null) {
            throw new RuntimeException(new ClassNotAnnotatedException(getClass(), InventoryMenuInfo.class));
        }else {
            this.background = ItemBuilder.builder()
                    .name(null)
                    .type(info.background())
                    .build()
                    .toItemStack();
            this.title = info.title();
            this.size = info.size();
            this.inventory = Bukkit.createInventory(this, this.size, this.title);
            this.previousMenu = previousMenu;
        }
    }

    /**
     * called when this inventory menu opens and asks to be filled with items
     * @param user the user
     */
    public abstract void setItems(@NotNull RUser user);

    /**
     * called when this inventory menu opens
     * @param e the inventory open event
     * @param user the user
     */
    public abstract void onOpen(@NotNull InventoryOpenEvent e, @NotNull RUser user);

    /**
     * called when this inventory menu closes
     * @param e the inventory close event
     * @param user the user
     */
    public abstract void onClose(@NotNull InventoryCloseEvent e, @NotNull RUser user);

    public final void handleClick(@NotNull InventoryClickEvent e, @NotNull RUser user) {
        final Player player = user.getPlayer();

        if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && !e.getCurrentItem().equals(this.background)) {
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, .3f, 1f);
        }

        onClick(e, user);
    }

    /**
     * called when a user clicks within this inventory menu
     * @param e the inventory click event
     * @param user the user
     */
    public abstract void onClick(@NotNull InventoryClickEvent e, @NotNull RUser user);

    public void setBackground() {
        for(int i = 0; i < this.inventory.getSize(); i++) {
            if (this.inventory.getItem(i) == null) {
                this.inventory.setItem(i, this.background);
            }
        }
    }

    /**
     * opens this inventory menu for the user specified
     * @param user the user
     */
    public final void open(@NotNull RUser user) {
        if(this.openUsers.contains(user)) {
            return;
        }

        // Open the created Inventory for the specified Player
        user.getPlayer().openInventory(this.inventory);

        this.openUsers.add(user);
    }
}
