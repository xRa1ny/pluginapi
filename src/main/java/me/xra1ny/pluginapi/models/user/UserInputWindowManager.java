package me.xra1ny.pluginapi.models.user;

import lombok.Getter;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.user.UserInputWindowAlreadyRegisteredException;
import me.xra1ny.pluginapi.exceptions.user.UserInputWindowNotRegisteredException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class UserInputWindowManager {
    /**
     * the list of all currently registered user input windows
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<UserInputWindow> userInputWindows = new ArrayList<>();

    /**
     * checks if the user input window specified is registered or not
     * @param userInputWindow the user input window
     * @return true if the user input window specified is registered, false otherwise
     */
    public boolean isRegistered(@NotNull UserInputWindow userInputWindow) {
        return this.userInputWindows.contains(userInputWindow);
    }

    /**
     * registers the user input window specified
     * @param userInputWindow the user input window
     * @throws UserInputWindowAlreadyRegisteredException if the user input window specified is already registered
     */
    public void register(@NotNull UserInputWindow userInputWindow) throws UserInputWindowAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register user input window " + userInputWindow + "...");

        if(isRegistered(userInputWindow)) {
            throw new UserInputWindowAlreadyRegisteredException(userInputWindow);
        }

        this.userInputWindows.add(userInputWindow);
        RPlugin.getInstance().getLogger().log(Level.INFO, "user input window " + userInputWindow + " successfully registered!");
    }

    /**
     * unregisters the user input window specified
     * @param userInputWindow the user input window
     * @throws UserInputWindowNotRegisteredException is the user input window specified is not yet registered
     */
    public void unregister(@NotNull UserInputWindow userInputWindow) throws UserInputWindowNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister user input window " + userInputWindow + "...");

        if(!isRegistered(userInputWindow)) {
            throw new UserInputWindowNotRegisteredException(userInputWindow);
        }

        this.userInputWindows.remove(userInputWindow);
        RPlugin.getInstance().getLogger().log(Level.INFO, "user input window " + userInputWindow + " successfully unregistered!");
    }

    /**
     * unregisters all user input windows
     * @throws UserInputWindowNotRegisteredException if any user input window is not yet registered
     */
    public void unregisterAll() throws UserInputWindowNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister all user input windows...");
        this.userInputWindows.clear();
        RPlugin.getInstance().getLogger().log(Level.INFO, "successfully unregistered all user input windows!");
    }

    /**
     * retrieves all user input windows that are currently open for the user specified
     * @param user the user
     * @return all user input windows that are currently open for the user specified
     */
    @Nullable
    public UserInputWindow get(@NotNull RUser user) {
        return this.userInputWindows.stream()
                .filter(userInputWindow -> userInputWindow.getUsers().containsKey(user))
                .findFirst()
                .orElse(null);
    }
}
