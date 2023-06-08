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
    private Material displayType;

    public Hologram(@NotNull String name, @NotNull Location location, @Nullable Material displayType, @NotNull String... lines) {
        this.name = name;
        this.lines.addAll(List.of(lines));
        this.location = location;
        this.displayType = displayType;

        update();
    }

    /**
     * removes this hologram
     */
    public void remove() {
        for(Entity entity : this.base.getPassengers()) {
            entity.remove();
        }

        for(ArmorStand armorStand : this.baseLines) {
            armorStand.remove();
        }

        this.base.remove();
    }

    /**
     * updates this hologram
     */
    public void update() {
        if(this.base != null) {
            for(Entity entity : this.base.getPassengers()) {
                entity.remove();
            }

            this.base.remove();
        }

        this.base = (ArmorStand) this.location.getWorld().spawnEntity(this.location, EntityType.ARMOR_STAND);
        this.base.setVisible(false);
        this.base.setMarker(true);

        if(this.displayType != null) {
            final Item item = this.location.getWorld().dropItem(this.base.getEyeLocation(), new ItemStack(this.displayType));

            item.setPickupDelay(Integer.MAX_VALUE);

            this.base.addPassenger(item);
        }

        for (ArmorStand armorStand : this.baseLines) {
            armorStand.remove();
        }

        for(int i = this.lines.size() - 1; i > -1; i--) {
            final String line = this.lines.get(i);
            final float height = (float) this.lines.size() - (float) i / 4;
            final Location lineLoc = this.location.clone().add(0, this.lines.size() + .25, 0);
            final ArmorStand armorStand = (ArmorStand) this.location.getWorld().spawnEntity(lineLoc.subtract(0, height, 0), EntityType.ARMOR_STAND);

            armorStand.setVisible(false);
            armorStand.setMarker(true);
            armorStand.setCustomName(line);
            armorStand.setCustomNameVisible(true);

            this.baseLines.add(armorStand);
        }
    }
}
