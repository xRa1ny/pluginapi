package me.xra1ny.pluginapi.models.user;

import lombok.Getter;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.UserAlreadyRegisteredException;
import me.xra1ny.pluginapi.exceptions.UserNotRegisteredException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class UserManager {
    @Getter(onMethod = @__(@NotNull))
    private final Class<? extends RUser> userClass;

    /**
     * the list of all currently registered users
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<RUser> users = new ArrayList<>();

    @Getter(onMethod = @__(@NotNull))
    private final UserTimeoutHandler userTimeoutHandler;

    public UserManager(@NotNull Class<? extends RUser> userClass, long userTimeout) {
        this.userClass = userClass;
        this.userTimeoutHandler = new UserTimeoutHandler(userTimeout);;
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
     * @throws UserNotRegisteredException if the user identified by the player specified is not yet registered
     */
    @Nullable
    public <T extends RUser> T get(@NotNull Player player) throws UserNotRegisteredException {
        return get(player.getUniqueId());
    }

    /**
     * retrieves the user of the player uuid specified
     * @param uuid the player uuid
     * @return the user
     * @param <T> the user
     * @throws UserNotRegisteredException if the user identified by the player uuid specified is not yet registered
     */
    public <T extends RUser> T get(@NotNull UUID uuid) throws UserNotRegisteredException {
        final T user = (T) this.users.stream()
                .filter(_user -> _user.getPlayer().getUniqueId().equals(uuid))
                .findFirst().orElse(null);

        if(user == null) {
            throw new UserNotRegisteredException(uuid);
        }

        return user;
    }

    /**
     * retrieves the user of the player name specified
     * @param name the player name
     * @return the user
     * @param <T> the user
     * @throws UserNotRegisteredException if the user identified by the player name specified is not yet registered
     */
    public <T extends RUser> T get(@NotNull String name) throws UserNotRegisteredException {
        final Player player = Bukkit.getPlayer(name);

        if(player == null) {
            throw new UserNotRegisteredException(name);
        }

        return get(player);
    }
}
