package me.xra1ny.pluginapi.models.hologram;

import lombok.Getter;
import me.xra1ny.pluginapi.RPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class HologramManager {
    @Getter(onMethod=@__(@NotNull))
    private final File configFile;

    @Getter(onMethod=@__(@NotNull))
    private final FileConfiguration config;

    @Getter(onMethod=@__({@NotNull, @Unmodifiable}))
    private final List<Hologram> holograms = new ArrayList<>();

    public HologramManager() {
        configFile = new File(RPlugin.getInstance().getDataFolder(), "holograms.yml");
//        Save default Hologram Config File to Plugins Directory
        RPlugin.getInstance().saveResource(configFile.getName(), false);
//        Load default Hologram Config
        config = YamlConfiguration.loadConfiguration(configFile);

//        Attempt to load all Holograms from Config
        loadFromConfig();
    }

    /** Attempts to load all Config saved Holograms */
    public void loadFromConfig() {
        final ConfigurationSection holograms = config.getConfigurationSection("");
        for(String key : holograms.getKeys(false)) {
            final ConfigurationSection section = holograms.getConfigurationSection(key);

            if(section == null)
                continue;

            final int id = section.getInt("id", -1);
            final String name = section.getString("name");
            final List<String> lines = (List<String>) section.getList("lines");
            final Material material = Material.valueOf(section.getString("material"));

            final ConfigurationSection location = section.getConfigurationSection("location");

            if(location == null)
                continue;

            final String world = location.getString("world");
            final double x = location.getDouble("x");
            final double y = location.getDouble("y");
            final double z = location.getDouble("z");

//            Skip Hologram Creation if Values are null
            if(name == null || id == -1 || lines == null || world == null || x == 0 || y == 0 || z == 0)
                continue;

            final Location loc = new Location(Bukkit.getWorld(world), x, y, z);

//            Attempt to create the loaded Hologram from Config Information...
            final Hologram hologram = createHologram(id, name, lines, loc, material);
            hologram.update();
        }
    }

    /** Creates a Hologram with the specified Parameters */
    public Hologram createHologram(@NotNull String name, @NotNull List<String> lines, @NotNull Location location, @Nullable Material displayItem) {
        final Hologram hologram = new Hologram(name, lines, location, displayItem);
        holograms.add(hologram);

        return hologram;
    }

    private Hologram createHologram(int id, @NotNull String name, @NotNull List<String> lines, @NotNull Location location, @Nullable Material displayItem) {
        final Hologram hologram = new Hologram(id, name, lines, location, displayItem);
        holograms.add(hologram);

        return hologram;
    }

    /** Removes the specified Hologram */
    public void removeHologram(@NotNull Hologram hologram) {
        holograms.remove(hologram);
        hologram.getBase().remove();
    }

    /** Saves the specified Hologram to Config */
    public void saveToConfig(@NotNull Hologram hologram) {
        final ConfigurationSection section = config.createSection(String.valueOf(hologram.getId()));
        section.set("id", hologram.getId());
        section.set("name", hologram.getName());
        section.set("lines", hologram.getLines());
        section.set("material", hologram.getDisplayItem().toString());

        final ConfigurationSection location = section.createSection("location");
        location.set("world", hologram.getLocation().getWorld().getName());
        location.set("x", hologram.getLocation().getX());
        location.set("y", hologram.getLocation().getY());
        location.set("z", hologram.getLocation().getZ());

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Removes the specified Hologram from Config */
    public void removeFromConfig(@NotNull Hologram hologram) {
        config.set(String.valueOf(hologram.getId()), null);

        try {
            config.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public Hologram getHologram(int id) {
        return holograms.stream().filter(hologram -> hologram.getId() == id).findAny().orElse(null);
    }
}
