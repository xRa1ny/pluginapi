package me.xra1ny.pluginapi.models.config;

import lombok.Getter;
import lombok.SneakyThrows;
import me.xra1ny.pluginapi.exceptions.ClassNotAnnotatedException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class RConfig {
    @Getter(onMethod = @__(@NotNull))
    private final String name;

    @Getter(onMethod = @__(@NotNull))
    private final File configFile;

    @Getter(onMethod = @__(@NotNull))
    private final FileConfiguration config;

    @SneakyThrows
    public RConfig() {
        final ConfigInfo info = getClass().getDeclaredAnnotation(ConfigInfo.class);

        if(info == null) {
            throw new ClassNotAnnotatedException(getClass(), ConfigInfo.class);
        }

        this.configFile = new File(info.value());
        this.configFile.createNewFile();
        this.name = this.configFile.getName();
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    @SneakyThrows
    public RConfig(@NotNull String configFileName) {
        this.configFile = new File(configFileName);
        this.configFile.createNewFile();
        this.name = this.configFile.getName();
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public <T> T get(@NotNull Class<T> type, @NotNull String fieldName) {
        final Field field = Arrays.stream(getClass().getDeclaredFields())
                .filter(_field -> Arrays.stream(_field.getAnnotations())
                        .map(Annotation::getClass)
                        .toList().contains(Path.class))
                .filter(_field -> _field.getType().equals(type))
                .filter(_field -> _field.getName().equals(fieldName))
                .findFirst().orElse(null);

        if(field == null) {
            return null;
        }

        final Path path = field.getAnnotation(Path.class);

        return (T) this.config.get(path.value());
    }

    @SneakyThrows
    public void save() {
        for(Field field : getClass().getDeclaredFields()) {
            final Path path = field.getAnnotation(Path.class);

            if(path == null) {
                continue;
            }

            field.setAccessible(true);
            this.config.set(path.value(), field.get(this));
        }

        this.config.save(this.configFile);
    }

    @SneakyThrows
    public void update() {
        for(Field field : getClass().getDeclaredFields()) {
            final Path path = field.getAnnotation(Path.class);

            if(path == null) {
                continue;
            }

            field.setAccessible(true);
            field.set(this, this.config.get(path.value()));
        }
    }
}
