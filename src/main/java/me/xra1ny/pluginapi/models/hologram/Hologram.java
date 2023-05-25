package me.xra1ny.pluginapi.models.hologram;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public final class Hologram {
    /**
     * the id of this hologram
     */
    @Getter
    private final int id;

    /**
     * the name of this hologram
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(onParam = @__(@NotNull))
    private String name;

    /**
     * the base armor stand of this hologram
     */
    @Getter(onMethod = @__(@NotNull))
    private ArmorStand base;

    /**
     * the armor stand lines of this hologram
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(onParam = @__(@NotNull))
    private List<ArmorStand> baseLines = new ArrayList<>();

    /**
     * the lines of this hologram
     */
    @Getter(onMethod = @__({@NotNull, @Unmodifiable}))
    @Setter(onParam = @__(@NotNull))
    private List<String> lines = new ArrayList<>();

    /**
     * the location of this hologram
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(onParam = @__(@NotNull))
    private Location location;

    /**
     * the item this hologram displays
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(onParam = @__(@NotNull))
    private Material displayItem;

    Hologram(@NotNull String name, @NotNull List<String> lines, @NotNull Location location, @Nullable Material displayItem) {
        this.id = hashCode();
        this.name = name;
        this.lines.addAll(lines);
        this.location = location;
        this.displayItem = displayItem;

        update();
    }

    Hologram(int id, @NotNull String name, @NotNull List<String> lines, @NotNull Location location, @Nullable Material displayItem) {
        this.name = name;
        this.id = id;
        this.lines.addAll(lines);
        this.location = location;
        this.displayItem = displayItem;

        update();
    }

    /**
     * removes this hologram
     */
    public void remove() {
        for (Entity entity : this.base.getPassengers()) {
            entity.remove();
        }

        for (ArmorStand armorStand : this.baseLines) {
            armorStand.remove();
        }

        this.base.remove();
    }

    /**
     * updates this hologram
     */
    public void update() {
        if (this.base != null) {
            for (Entity entity : this.base.getPassengers()) {
                entity.remove();
            }

            this.base.remove();
        }

        this.base = (ArmorStand) this.location.getWorld().spawnEntity(this.location, EntityType.ARMOR_STAND);
        this.base.setVisible(false);
        this.base.setMarker(true);

        if (this.displayItem != null) {
            final Item item = this.location.getWorld().dropItem(this.base.getEyeLocation(), new ItemStack(this.displayItem));
            item.setPickupDelay(Integer.MAX_VALUE);
            this.base.addPassenger(item);
        }

        for (ArmorStand armorStand : this.baseLines) {
            armorStand.remove();
        }

        for (String line : this.lines) {
            final float height = (float) this.lines.size() - (float) this.lines.indexOf(line) / 4;
            final Location lineLoc = this.location.clone().subtract(0, 2, 0);
            final ArmorStand armorStand = (ArmorStand) this.location.getWorld().spawnEntity(lineLoc.add(0, height, 0), EntityType.ARMOR_STAND);

            armorStand.setVisible(false);
            armorStand.setMarker(true);
            armorStand.setCustomName(line);
            armorStand.setCustomNameVisible(true);
            this.baseLines.add(armorStand);
        }
    }
}
