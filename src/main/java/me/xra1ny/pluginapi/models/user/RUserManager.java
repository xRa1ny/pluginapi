package me.xra1ny.pluginapi.models.user;

import lombok.Getter;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.user.UserAlreadyRegisteredException;
import me.xra1ny.pluginapi.exceptions.user.UserNotRegisteredException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class RUserManager {
    @Getter(onMethod = @__(@NotNull))
    private final Class<? extends RUser> userClass;

    /**
     * the list of all currently registered users
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<RUser> users = new ArrayList<>();

    @Getter(onMethod = @__(@NotNull))
    private final UserTimeoutHandler userTimeoutHandler;

    public RUserManager(@NotNull Class<? extends RUser> userClass, long userTimeout) {
        this.userClass = userClass;
        this.userTimeoutHandler = new UserTimeoutHandler(userTimeout);
        this.userTimeoutHandler.start();
    }

    /**
     * retrieves the registration state of the user specified
     * @param user the user
     * @return true if the user specified is registered, false otherwise
     */
    public boolean isRegistered(@NotNull RUser user) {
        return this.users.contains(user);
    }

    /**
     * registers the user specified
     * @param user the user
     * @throws UserAlreadyRegisteredException if the user specified is already registered
     */
    public void register(@NotNull RUser user) throws UserAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register user " + user + "...");

        if(isRegistered(user)) {
            throw new UserAlreadyRegisteredException(user);
        }

        this.users.add(user);
        RPlugin.getInstance().getLogger().log(Level.INFO, "user " + user + " successfully registered!");
    }

    /**
     * unregisters the user specified
     * @param user the user
     * @throws UserNotRegisteredException if the user specified is not yet registered
     */
    public void unregister(@NotNull RUser user) throws UserNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister user " + user + "...");

        if(!isRegistered(user)) {
            throw new UserNotRegisteredException(user);
        }

        this.users.remove(user);
        RPlugin.getInstance().getLogger().log(Level.INFO, "user " + user + " successfully unregistered!");
    }

    /**
     * retrieves the user of the player specified
     * @param player the player
     * @return the user
     * @param <T> the user
     */
    @Nullable
    public <T extends RUser> T get(@NotNull Player player) {
        return get(player.getUniqueId());
    }

    /**
     * retrieves the user of the player uuid specified
     * @param uuid the player uuid
     * @return the user
     * @param <T> the user
     */
    public <T extends RUser> T get(@NotNull UUID uuid) {
        return (T) this.users.stream()
                .filter(_user -> _user.getPlayer().getUniqueId().equals(uuid))
                .findFirst().orElse(null);
    }

    /**
     * retrieves the user of the player name specified
     * @param name the player name
     * @return the user
     * @param <T> the user
     */
    public <T extends RUser> T get(@NotNull String name) {
        return get(Bukkit.getPlayer(name));
    }
}
