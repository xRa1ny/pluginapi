package me.xra1ny.pluginapi.models.localisation;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LocalisationManager {
    private final Map<String, FileConfiguration> configs = new HashMap<>();

    public LocalisationManager(@NotNull String... configUrls) {
        for(String configUrl : configUrls) {
            final File configFile = new File(configUrl);
            final FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            this.configs.put(configFile.getName(), config);
        }
    }

    @Nullable
    public String get(@NotNull String configName, @NotNull String key, @NotNull Replacement... replacements) {
        String value = configs.get(configName).getString(key);

        if(value == null) {
            return null;
        }

        for(Replacement replacement : replacements) {
            value = value.replace(replacement.getKey(), replacement.getValue());
        }

        return value;
    }
}
