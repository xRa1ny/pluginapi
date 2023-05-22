package me.xra1ny.pluginapi.models.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.ClassNotAnnotatedException;
import me.xra1ny.pluginapi.exceptions.ItemStackAlreadyRegisteredException;
import me.xra1ny.pluginapi.exceptions.ItemStackNotRegisteredException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Slf4j
public final class ItemStackManager {
    @Getter(onMethod = @__(@NotNull))
    private final List<RItemStack> items = new ArrayList<>();

    @Getter(onMethod = @__(@NotNull))
    private final ItemStackCooldownHandler cooldownHandler;

    public ItemStackManager() throws ClassNotAnnotatedException {
        this.cooldownHandler = new ItemStackCooldownHandler(this);
        this.cooldownHandler.start();
    }

    public boolean isRegistered(@NotNull RItemStack itemStack) {
        return this.items.contains(itemStack);
    }

    public void register(@NotNull RItemStack itemStack) throws ItemStackAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register itemstack " + itemStack + "...");

        if(isRegistered(itemStack)) {
            throw new ItemStackAlreadyRegisteredException(itemStack);
        }

        this.items.add(itemStack);

        RPlugin.getInstance().getLogger().log(Level.INFO, "itemstack " + itemStack + " successfully registered!");
    }

    public void registerAll(@NotNull String packageName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ItemStackAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register all itemstacks in package " + packageName + "...");

        for(Class<? extends RItemStack> clazz : new Reflections(packageName).getSubTypesOf(RItemStack.class)) {
            register(clazz.getDeclaredConstructor().newInstance());
        }
    }

    public void unregister(@NotNull RItemStack itemStack) throws ItemStackNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister itemstack " + itemStack + "...");

        if(!isRegistered(itemStack)) {
            throw new ItemStackNotRegisteredException(itemStack);
        }

        this.items.remove(itemStack);

        RPlugin.getInstance().getLogger().info("itemstack " + itemStack + " successfully unregistered!");
    }

    public void unregisterAll(@NotNull String packageName) throws ItemStackNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "unregistering itemstacks...");

        for(RItemStack itemStack : this.items) {
            if(!itemStack.getClass().getPackage().getName().equals(packageName)) {
                continue;
            }

            unregister(itemStack);
        }

        RPlugin.getInstance().getLogger().log(Level.INFO, "itemstacks unregistered!");
    }

    @NotNull
    public List<RItemStack> getAll(@NotNull ItemStack itemStack) {
        return this.items.stream()
                .filter(_itemStack -> _itemStack.toString().equals(itemStack.toString()))
                .toList();
    }
}
