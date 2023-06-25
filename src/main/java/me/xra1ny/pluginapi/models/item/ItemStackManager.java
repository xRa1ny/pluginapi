package me.xra1ny.pluginapi.models.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.ClassNotAnnotatedException;
import me.xra1ny.pluginapi.exceptions.ItemStackAlreadyRegisteredException;
import me.xra1ny.pluginapi.exceptions.ItemStackNotRegisteredException;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Slf4j
public final class ItemStackManager {
    /**
     * all currently registered items
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<RItemStack> items = new ArrayList<>();

    /**
     * the cooldown handler of this item stack manager
     */
    @Getter(onMethod = @__(@NotNull))
    private final ItemStackCooldownHandler cooldownHandler;

    public ItemStackManager() throws ClassNotAnnotatedException {
        this.cooldownHandler = new ItemStackCooldownHandler(this);
        this.cooldownHandler.start();
    }

    /**
     * checks if the item specified is registered or not
     * @param itemStack the item
     * @return true if the item specified is registered, false otherwise
     */
    public boolean isRegistered(@NotNull RItemStack itemStack) {
        return this.items.contains(itemStack);
    }

    /**
     * registers the item specified
     * @param itemStack the item
     * @throws ItemStackAlreadyRegisteredException if the item specified is already registered
     */
    public void register(@NotNull RItemStack itemStack) throws ItemStackAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register itemstack " + itemStack + "...");

        if(isRegistered(itemStack)) {
            throw new ItemStackAlreadyRegisteredException(itemStack);
        }

        this.items.add(itemStack);
        RPlugin.getInstance().getLogger().log(Level.INFO, "itemstack " + itemStack + " successfully registered!");
    }

    /**
     * registers all item found within the package specified
     * @param packageName the package name
     * @throws NoSuchMethodException if the constructor of any item found within the package specified does not have the signature ()
     * @throws InvocationTargetException if an exception occurs while constructing any item found within the package specified
     * @throws InstantiationException if any item found in within the package specified could not be instantiated (abstract)
     * @throws IllegalAccessException if the constructor of any item found in within the package specified is inaccessible
     * @throws ItemStackAlreadyRegisteredException if any item found in within the package specified is already registered
     */
    public void registerAll(@NotNull String packageName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ItemStackAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register all itemstacks in package " + packageName + "...");

        for(Class<? extends RItemStack> clazz : new Reflections(packageName).getSubTypesOf(RItemStack.class)) {
            register(clazz.getDeclaredConstructor().newInstance());
        }
    }

    /**
     * unregisters the item specified
     * @param itemStack the item
     * @throws ItemStackNotRegisteredException if the item specified is not yet registered
     */
    public void unregister(@NotNull RItemStack itemStack) throws ItemStackNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister itemstack " + itemStack + "...");

        if(!isRegistered(itemStack)) {
            throw new ItemStackNotRegisteredException(itemStack);
        }

        this.items.remove(itemStack);
        RPlugin.getInstance().getLogger().info("itemstack " + itemStack + " successfully unregistered!");
    }

    public void unregisterAll() {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister all itemstacks...");
        this.items.clear();
        RPlugin.getInstance().getLogger().log(Level.INFO, "successfully unregistered all itemstacks!");
    }
}
