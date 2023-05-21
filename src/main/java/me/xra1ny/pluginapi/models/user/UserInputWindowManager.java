package me.xra1ny.pluginapi.models.user;

import lombok.Getter;
import me.xra1ny.pluginapi.exceptions.UserInputWindowAlreadyRegisteredException;
import me.xra1ny.pluginapi.exceptions.UserInputWindowNotRegisteredException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class UserInputWindowManager {
    /**
     * the list of all currently registered user input windows
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<UserInputWindow> userInputWindows = new ArrayList<>();

    /**
     * retrieves the registration state of the specified user input window
     * @param userInputWindow the user input window
     * @return true if the specified user input window is registered, false otherwise
     */
    public boolean isRegistered(@NotNull UserInputWindow userInputWindow) {
        return this.userInputWindows.contains(userInputWindow);
    }

    /**
     * registers the specified user input window
     * @param userInputWindow the user input window
     * @throws UserInputWindowAlreadyRegisteredException if the specified user input window is already registered
     */
    public void register(@NotNull UserInputWindow userInputWindow) throws UserInputWindowAlreadyRegisteredException {
        if(isRegistered(userInputWindow)) {
            throw new UserInputWindowAlreadyRegisteredException(userInputWindow);
        }

        this.userInputWindows.add(userInputWindow);
    }

    /**
     * unregisters the specified user input window
     * @param userInputWindow the user input window
     * @throws UserInputWindowNotRegisteredException is the specified user input window is not yet registered
     */
    public void unregister(@NotNull UserInputWindow userInputWindow) throws UserInputWindowNotRegisteredException {
        if(!isRegistered(userInputWindow)) {
            throw new UserInputWindowNotRegisteredException(userInputWindow);
        }

        this.userInputWindows.remove(userInputWindow);
    }

    /**
     * retrieves a list of all user input windows that are currently open for the specified user
     * @param user the user
     * @return all user input windows that are currently open for the specified user
     */
    @Nullable
    public UserInputWindow get(@NotNull RUser user) {
        return this.userInputWindows.stream()
                .filter(userInputWindow -> userInputWindow.getUsers().containsKey(user))
                .findFirst()
                .orElse(null);
    }
}
