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

    public UserManager(@NotNull Class<? extends RUser> userClass) {
        this.userClass = userClass;
    }

    /**
     * retrieves the registration state of the specified user
     * @param user the user
     * @return true if the specified user is registered, false otherwise
     */
    public boolean isRegistered(@NotNull RUser user) {
        return this.users.contains(user);
    }

    /**
     * registers the specified user
     * @param user the user
     * @throws UserAlreadyRegisteredException if the specified user is already registered
     */
    public void register(@NotNull RUser user) throws UserAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "registering user " + user + "...");
        if(isRegistered(user)) {
            throw new UserAlreadyRegisteredException(user);
        }

        this.users.add(user);
        RPlugin.getInstance().getLogger().log(Level.INFO, "user " + user + " registered successfully!");
    }

    /**
     * unregisters the specified user
     * @param user the user
     * @throws UserNotRegisteredException if the specified user is not yet registered
     */
    public void unregister(@NotNull RUser user) throws UserNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "unregistering user " + user + "...");
        if(!isRegistered(user)) {
            throw new UserNotRegisteredException(user);
        }

        this.users.remove(user);
        RPlugin.getInstance().getLogger().log(Level.INFO, "user " + user + " successfully unregistered!");
    }

    /**
     * retrieves the user of the specified player
     * @param player the player
     * @return the user
     * @param <T> the user
     * @throws UserNotRegisteredException if the user identified by the specified player is not yet registered
     */
    @Nullable
    public <T extends RUser> T get(@NotNull Player player) throws UserNotRegisteredException {
        @Nullable
        final T user = (T) this.users.stream()
                .filter(_user -> _user.getPlayer().equals(player))
                .findFirst().orElse(null);

        if(user == null) {
            throw new UserNotRegisteredException(player);
        }

        return user;
    }

    /**
     * retrieves the user of the specified player uuid
     * @param uuid the player uuid
     * @return the user
     * @param <T> the user
     * @throws UserNotRegisteredException if the user identified by the specified player uuid is not yet registered
     */
    public <T extends RUser> T get(@NotNull UUID uuid) throws UserNotRegisteredException {
        @Nullable
        final Player player = Bukkit.getPlayer(uuid);

        if(player == null) {
            throw new UserNotRegisteredException(uuid);
        }

        return get(player);
    }

    /**
     * retrieves the user of the specified player name
     * @param name the player name
     * @return the user
     * @param <T> the user
     * @throws UserNotRegisteredException if the user identified by the specified player name is not yet registered
     */
    public <T extends RUser> T get(@NotNull String name) throws UserNotRegisteredException {
        @Nullable
        final Player player = Bukkit.getPlayer(name);

        if(player == null) {
            throw new UserNotRegisteredException(name);
        }

        return get(player);
    }

    /**
     * retrieves a list of all users currently in "buildmode"
     * @return all users currently in "buildmode"
     * @param <T> all users currently in "buildmode"
     */
    public <T extends RUser> List<T> getInBuildMode() {
        return (List<T>) this.users.stream()
                .filter(RUser::isInBuildMode)
                .toList();
    }

    /**
     * retrieves a list of all users currently in "flymode"
     * @return all users currently in "flymode"
     * @param <T> all users currently in "flymode"
     */
    public <T extends RUser> List<T> getInFlyMode() {
        return (List<T>) this.users.stream()
                .filter(RUser::isInFlyMode)
                .toList();
    }

    /**
     * retrieves a list of all users currently in "vanishmode"
     * @return all users currently in "vanishmode"
     * @param <T> all users currently in "vanishmode"
     */
    public <T extends RUser> List<T> getInVanishMode() {
        return (List<T>) this.users.stream()
                .filter(RUser::isInVanishMode)
                .toList();
    }
}
