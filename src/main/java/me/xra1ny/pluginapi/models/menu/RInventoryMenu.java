package me.xra1ny.pluginapi.models.menu;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.exceptions.ClassNotAnnotatedException;
import me.xra1ny.pluginapi.models.item.ItemBuilder;
import me.xra1ny.pluginapi.models.user.RUser;
import org.bukkit.Bukkit;
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
    @Getter(onMethod = @__(@NotNull))
    private Inventory inventory;

    @Getter(onMethod = @__(@NotNull))
    private final org.bukkit.inventory.ItemStack background;

    @Getter(onMethod = @__(@NotNull))
    private final String title;

    @Getter
    private final int size;

    @Getter(onMethod = @__(@NotNull))
    private final List<RUser> openUsers = new ArrayList<>();

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

    public abstract void setItems(@NotNull RUser user);
    public abstract void onOpen(@NotNull InventoryOpenEvent e, @NotNull RUser user);
    public abstract void onClose(@NotNull InventoryCloseEvent e, @NotNull RUser user);

    public final void handleClick(@NotNull InventoryClickEvent e, @NotNull RUser user) {
        final Player player = (Player) e.getWhoClicked();

        if(e.getClickedInventory() == null)  {
            return;
        }

        final org.bukkit.inventory.ItemStack itemStack = e.getCurrentItem();

        if(!this.background.equals(itemStack)) {
            // Play Sound
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, .3f, 1f);
        }

        onClick(e, user);
    }
    public abstract void onClick(@NotNull InventoryClickEvent e, @NotNull RUser user);

    public void setBackground() {
        for(int i = 0; i < this.inventory.getSize(); i++) {
            if (this.inventory.getItem(i) == null) {
                this.inventory.setItem(i, this.background);
            }
        }
    }

    /** Opens this InventoryMenu for the specified Player */
    public final void open(@NotNull RUser user) {
        if(this.openUsers.contains(user)) {
            return;
        }

        // Open the created Inventory for the specified Player
        user.getPlayer().openInventory(this.inventory);

        this.openUsers.add(user);
    }
}
