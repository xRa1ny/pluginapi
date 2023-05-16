package me.xra1ny.pluginapi.models.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.ClassNotAnnotatedException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

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

    public void register(@NotNull RItemStack itemStack) {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register itemstack " + itemStack.getClass().getName() + "...");
        if(isRegistered(itemStack)) {
            RPlugin.getInstance().getLogger().log(Level.INFO, "itemstack " + itemStack.getClass().getName() + " already registered!");

            return;
        }
        this.items.add(itemStack);
        RPlugin.getInstance().getLogger().log(Level.INFO, "itemstack " + itemStack.getClass().getName() + " registered!");
    }

    public void registerAll(@NotNull String packageName) {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register all itemstacks in package " + packageName + "...");
        for(Class<? extends RItemStack> clazz : new Reflections(packageName).getSubTypesOf(RItemStack.class)) {
            try {
                register(clazz.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                RPlugin.getInstance().getLogger().log(Level.INFO, "error while registering itemstack " + clazz.getName() + "!");
                e.printStackTrace();
            }
        }
    }

    public void unregister(@NotNull RItemStack itemStack) {
        this.items.remove(itemStack);
        RPlugin.getInstance().getLogger().info("itemstack " + itemStack.getClass().getName() + " unregistered!");
    }

    public void unregisterAll(@NotNull String packageName) {
        RPlugin.getInstance().getLogger().log(Level.INFO, "unregistering itemstacks...");
        this.items.stream()
                .filter(itemStack -> itemStack.getClass().getPackage().getName().equals(packageName))
                .forEach(this::unregister);
        RPlugin.getInstance().getLogger().log(Level.INFO, "itemstacks unregistered!");
    }

    @NotNull
    public List<RItemStack> getAll(@NotNull ItemStack itemStack) {
        return this.items.stream()
                .filter(_itemStack -> _itemStack.toString().equals(itemStack.toString()))
                .toList();
    }
}
