package me.xra1ny.pluginapi.models.hologram;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public final class Hologram {
    @Getter
    private final int id;

    @Getter(onMethod = @__(@NotNull))
    @Setter(onParam = @__(@NotNull))
    private String name;

    @Getter(onMethod = @__(@NotNull))
    private ArmorStand base;

    @Getter(onMethod = @__(@NotNull))
    @Setter(onParam = @__(@NotNull))
    private List<ArmorStand> baseLines = new ArrayList<>();

    @Getter(onMethod = @__({@NotNull, @Unmodifiable}))
    @Setter(onParam = @__(@NotNull))
    private List<String> lines = new ArrayList<>();

    @Getter(onMethod = @__(@NotNull))
    @Setter(onParam = @__(@NotNull))
    private Location location;

    @Getter(onMethod = @__(@NotNull))
    @Setter(onParam = @__(@NotNull))
    private Material displayItem;

    Hologram(@NotNull String name, @NotNull List<String> lines, @NotNull Location location, @Nullable Material displayItem) {
        log.info("Initialising Hologram {}", this);
        this.id = hashCode();
        this.name = name;
        this.lines.addAll(lines);
        this.location = location;
        this.displayItem = displayItem;

        update();
    }

    Hologram(int id, @NotNull String name, @NotNull List<String> lines, @NotNull Location location, @Nullable Material displayItem) {
        log.info("Initialising Hologram {}", this);
        this.name = name;
        this.id = id;
        this.lines.addAll(lines);
        this.location = location;
        this.displayItem = displayItem;

        update();
    }

    public void remove() {
        // Remove all Passengers from Base
        for (Entity entity : base.getPassengers()) {
            entity.remove();
        }

        // Remove all Lines
        for (ArmorStand armorStand : baseLines) {
            armorStand.remove();
        }

        // Remove Base
        base.remove();
    }

    /**
     * Updates this current Holograms Lines, Location and DisplayItem
     */
    public void update() {
        if (base != null) {
            for (Entity entity : base.getPassengers()) {
                entity.remove();
            }
            base.remove();
        }

        base = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        base.setVisible(false);
        base.setMarker(true);

        if (displayItem != null) {
            final Item item = location.getWorld().dropItem(base.getEyeLocation(), new ItemStack(displayItem));
            item.setPickupDelay(Integer.MAX_VALUE);
            base.addPassenger(item);
        }

        for (ArmorStand armorStand : baseLines) {
            armorStand.remove();
        }

        for (String line : lines) {
            final float height = (float) lines.size() - (float) lines.indexOf(line) / 4;
            final Location lineLoc = location.clone().subtract(0, 2, 0);
            final ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(lineLoc.add(0, height, 0), EntityType.ARMOR_STAND);

            armorStand.setVisible(false);
            armorStand.setMarker(true);
            armorStand.setCustomName(line);
            armorStand.setCustomNameVisible(true);
            baseLines.add(armorStand);
        }
    }
}
