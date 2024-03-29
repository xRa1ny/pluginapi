package me.xra1ny.pluginapi.models.hologram;

import lombok.Getter;
import lombok.SneakyThrows;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.hologram.HologramAlreadyRegisteredException;
import me.xra1ny.pluginapi.exceptions.hologram.HologramNotRegisteredException;
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
import java.util.logging.Level;

public final class HologramManager {
    /**
     * the config file of this hologram manager
     */
    @Getter(onMethod=@__(@NotNull))
    private final File configFile;

    /**
     * the config of this hologram manager
     */
    @Getter(onMethod=@__(@NotNull))
    private final FileConfiguration config;

    /**
     * all currently registered holograms
     */
    @Getter(onMethod=@__({@NotNull, @Unmodifiable}))
    private final List<Hologram> holograms = new ArrayList<>();

    public HologramManager() {
        this.configFile = new File(RPlugin.getInstance().getDataFolder(), "holograms.yml");
        RPlugin.getInstance().saveResource(this.configFile.getName(), false);
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        loadFromConfig();
    }

    public boolean isRegistered(@NotNull Hologram hologram) {
        return this.holograms.contains(hologram);
    }

    public void register(@NotNull Hologram hologram) throws HologramAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register hologram " + hologram + "...");

        if(isRegistered(hologram)) {
            throw new HologramAlreadyRegisteredException(hologram);
        }

        this.holograms.add(hologram);
        RPlugin.getInstance().getLogger().log(Level.INFO, "hologram " + hologram + " registered successfully!");
    }

    public void unregister(@NotNull Hologram hologram) throws HologramNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister hologram " + hologram + "...");

        if(!isRegistered(hologram)) {
            throw new HologramNotRegisteredException(hologram);
        }

        this.holograms.remove(hologram);
        RPlugin.getInstance().getLogger().log(Level.INFO, "hologram " + hologram + " unregistered successfully!");
    }

    public void unregisterAll() throws HologramNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister all holograms...");
        this.holograms.clear();
        RPlugin.getInstance().getLogger().log(Level.INFO, "successfully unregistered all holograms!");
    }

    /**
     * loads all holograms from config
     */
    @SneakyThrows
    public void loadFromConfig() {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to load all holograms from config...");

        for(String key : this.config.getKeys(false)) {
            final ConfigurationSection section = this.config.getConfigurationSection(key);

            if(section == null) {
                RPlugin.getInstance().getLogger().log(Level.INFO, "section is null, continuing...");

                continue;
            }

            final List<String> lines = (List<String>) section.getList("lines");
            final String materialName = section.getString("type");
            final Material material = materialName == null ? null : Material.valueOf(materialName);
            final Location location = section.getLocation("location");

            // Skip Hologram Creation if Values are null
            if(lines == null || location == null) {
                RPlugin.getInstance().getLogger().log(Level.WARNING, "section info is null, continuing...");

                continue;
            }

            // Attempt to create the loaded Hologram from Config Information...
            final Hologram hologram = new Hologram(key, location, material, lines.toArray(new String[0]));

            register(hologram);
            hologram.update();
        }

        RPlugin.getInstance().getLogger().log(Level.INFO, "all holograms successfully loaded from config!");
    }

    /**
     * saves the specified hologram to config
     * @param hologram the hologram
     */
    public void save(@NotNull Hologram hologram) {
        final ConfigurationSection section = this.config.createSection(hologram.getName());

        section.set("lines", hologram.getLines());
        section.set("material", String.valueOf(hologram.getDisplayType()));
        section.set("location", hologram.getLocation());

        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * removes the specified hologram from config
     * @param hologram the hologram
     */
    public void remove(@NotNull Hologram hologram) {
        this.config.set(hologram.getName(), null);

        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public Hologram get(@NotNull String name) {
        return this.holograms.stream()
                .filter(hologram -> hologram.getName().equals(name))
                .findFirst().orElse(null);
    }
}
