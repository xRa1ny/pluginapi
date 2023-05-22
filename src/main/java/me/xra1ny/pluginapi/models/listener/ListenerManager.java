package me.xra1ny.pluginapi.models.listener;

import lombok.Getter;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.ListenerAlreadyRegisteredException;
import me.xra1ny.pluginapi.exceptions.ListenerNotRegisteredException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ListenerManager {
    @Getter(onMethod = @__(@NotNull))
    private final List<Listener> listeners = new ArrayList<>();

    public boolean isRegistered(@NotNull Listener listener) {
        return this.listeners.contains(listener);
    }

    public void register(@NotNull Listener listener) throws ListenerAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register listener " + listener + "...");

        if(isRegistered(listener)) {
            throw new ListenerAlreadyRegisteredException(listener);
        }

        RPlugin.getInstance().getServer().getPluginManager().registerEvents(listener, RPlugin.getInstance());

        this.listeners.add(listener);

        RPlugin.getInstance().getLogger().log(Level.INFO, "listener " + listener + " successfully registered!");
    }

    public void registerAll(@NotNull String packageName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ListenerAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register all listeners in package " + packageName + "...");

        for(Class<? extends Listener> listenerClass : new Reflections(packageName).getSubTypesOf(Listener.class)) {
            register(listenerClass.getDeclaredConstructor().newInstance());
        }
    }

    public void unregister(@NotNull Listener listener) throws ListenerNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister listener " + listener + "...");

        if(!isRegistered(listener)) {
            throw new ListenerNotRegisteredException(listener);
        }

        HandlerList.unregisterAll(listener);
        this.listeners.remove(listener);

        RPlugin.getInstance().getLogger().log(Level.INFO, "listener " + listener + " successfully unregistered!");
    }

    public void unregisterAll(@NotNull String packageName) throws ListenerNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister all listeners in package " + packageName + "...");

        for(Listener listener : this.listeners) {
            if(!listener.getClass().getPackage().getName().equals(packageName)) {
                continue;
            }

            unregister(listener);
        }
    }

    public void unregisterAll() throws ListenerNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister all listeners...");

        for(Listener listener : this.listeners) {
            unregister(listener);
        }
    }
}
