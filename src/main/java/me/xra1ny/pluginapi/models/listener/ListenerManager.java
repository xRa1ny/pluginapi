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

public class ListenerManager {
    @Getter(onMethod = @__(@NotNull))
    private final List<Listener> listeners = new ArrayList<>();

    public boolean isRegistered(@NotNull Listener listener) {
        return this.listeners.contains(listener);
    }

    public void register(@NotNull Listener listener) throws ListenerAlreadyRegisteredException {
        if(isRegistered(listener)) {
            throw new ListenerAlreadyRegisteredException(listener);
        }

        RPlugin.getInstance().getServer().getPluginManager().registerEvents(listener, RPlugin.getInstance());
        this.listeners.add(listener);
    }

    public void registerAll(@NotNull String packageName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ListenerAlreadyRegisteredException {
        for(Class<? extends Listener> listenerClass : new Reflections(packageName).getSubTypesOf(Listener.class)) {
            register(listenerClass.getDeclaredConstructor().newInstance());
        }
    }

    public void unregister(@NotNull Listener listener) throws ListenerNotRegisteredException {
        if(!isRegistered(listener)) {
            throw new ListenerNotRegisteredException(listener);
        }

        HandlerList.unregisterAll(listener);
        this.listeners.remove(listener);
    }

    public void unregisterAll(@NotNull String packageName) throws ListenerNotRegisteredException {
        for(Listener listener : this.listeners) {
            if(!listener.getClass().getPackage().getName().equals(packageName)) {
                continue;
            }

            unregister(listener);
        }
    }

    public void unregisterAll() throws ListenerNotRegisteredException {
        for(Listener listener : this.listeners) {
            unregister(listener);
        }
    }
}
