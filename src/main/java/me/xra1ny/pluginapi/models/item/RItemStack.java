package me.xra1ny.pluginapi.models.item;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.ClassNotAnnotatedException;
import me.xra1ny.pluginapi.models.user.RUser;
import me.xra1ny.pluginapi.utils.NamespacedKeys;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Used to create ItemStacks that can be interacted with
 */
@Slf4j
public abstract class RItemStack extends ItemStack {
    /**
     * the cooldown queue of this item stack
     */
    @Getter(onMethod = @__(@NotNull))
    private final Map<RUser, Integer> cooldownQueue = new HashMap<>();

    /**
     * the cooldown of this item stack
     */
    @Getter
    private int cooldown = 0;

    @Getter
    private final boolean localised;

    @Getter(onMethod = @__(@NotNull))
    private final UUID uuid = UUID.randomUUID();

    @SneakyThrows
    public RItemStack() throws ClassNotAnnotatedException {
        @Nullable
        final ItemStackInfo info = getClass().getDeclaredAnnotation(ItemStackInfo.class);

        if(info == null) {
            throw new ClassNotAnnotatedException(getClass(), ItemStackInfo.class);
        }

        @NotNull
        final ItemStack itemStack = ItemBuilder.builder()
                .type(info.type())
                .name(info.name())
                .amount(info.amount())
                .lore(List.of(info.lore()))
                .itemFlags(List.of(info.itemFlags()))
                .build()
                .toItemStack();
        final ItemMeta meta = itemStack.getItemMeta();

        if(info.enchanted()) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
        }

        setType(itemStack.getType());
        setAmount(itemStack.getAmount());
        setItemMeta(meta);
        this.cooldown = info.cooldown();
        this.localised = info.localised();

        RPlugin.getInstance().getItemStackManager().register(this);
    }

    @SneakyThrows
    public RItemStack(@NotNull ItemStack itemStack, boolean enchanted, boolean localised) {
        final ItemMeta meta = itemStack.getItemMeta();

        if(enchanted) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
        }

        setType(itemStack.getType());
        setAmount(itemStack.getAmount());
        setItemMeta(meta);
        this.localised = localised;

        RPlugin.getInstance().getItemStackManager().register(this);
    }

    /**
     * called when this item has been left clicked
     * @param e the player interact event
     * @param user the user
     */
    public abstract boolean onLeftClick(@NotNull PlayerInteractEvent e, @NotNull RUser user);

    /**
     * called when this item has been right clicked
     * @param e the player interact event
     * @param user the user
     */
    public abstract boolean onRightClick(@NotNull PlayerInteractEvent e, @NotNull RUser user);

    /**
     * called when this item has been left or right clicked but the cooldown has not yet expired
     * @param e the player interact event
     * @param user the user
     */
    public abstract void onCooldown(@NotNull PlayerInteractEvent e, @NotNull RUser user);

    /**
     * called when the cooldown of this item expires for the user specified
     * @param user the user
     */
    public abstract void onCooldownExpire(@NotNull RUser user);

    public final void handleInteraction(@NotNull PlayerInteractEvent e, @NotNull RUser user) {
        if(!this.cooldownQueue.containsKey(user)) {
            final Action action = e.getAction();
            final boolean result;

            if(action.name().startsWith("LEFT_CLICK")) {
                result = onLeftClick(e, user);
            }else {
                result = onRightClick(e, user);
            }

            if(result) {
                if(this.cooldown >= 1)
                    this.cooldownQueue.put(user, this.cooldown);
            }
        }else {
            onCooldown(e, user);
        }
    }

    public void add(@NotNull RUser user) {
        final ItemStack itemStack = clone();
        final ItemMeta itemMeta = itemStack.getItemMeta();

        if(this.localised) {
            itemMeta.setDisplayName(RPlugin.getInstance().getLocalisationManager().get(user.getLocalisationConfigClass(), itemMeta.getDisplayName()));
        }

        itemStack.setItemMeta(itemMeta);

        user.getPlayer().getInventory().addItem(itemStack);
    }

    public void give(@NotNull RUser user, int slot) {
        final ItemStack itemStack = clone();
        final ItemMeta itemMeta = itemStack.getItemMeta();

        if(this.localised) {
            itemMeta.setDisplayName(RPlugin.getInstance().getLocalisationManager().get(user.getLocalisationConfigClass(), itemMeta.getDisplayName()));
        }

        itemStack.setItemMeta(itemMeta);

        user.getPlayer().getInventory().setItem(slot, itemStack);
    }

    @Override
    public String toString() {
        return super.toString().replace(getType() + " x " + getAmount(), getType() + " x 1");
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ItemStack item)) {
            return false;
        }

        if(!item.getItemMeta().getPersistentDataContainer().has(NamespacedKeys.ITEM_UUID, PersistentDataType.STRING)) {
            String toString = toString();

            if(this.localised) {
                toString = toString.replace(getItemMeta().getDisplayName(), "");
            }

            return toString.equals(item.toString().replace(item.getType() + " x " + item.getAmount(), item.getType() + " x 1"));
        }

        final UUID uuid = UUID.fromString(getItemMeta().getPersistentDataContainer().get(NamespacedKeys.ITEM_UUID, PersistentDataType.STRING));
        final UUID otherUuid = UUID.fromString(item.getItemMeta().getPersistentDataContainer().get(NamespacedKeys.ITEM_UUID, PersistentDataType.STRING));

        return uuid.equals(otherUuid);
    }

    public boolean isEnchanted() {
        return !getItemMeta().getEnchants().isEmpty();
    }

    /**
     * constructs an empty r item stack from a normal item stack copying its compatible attributes
     *
     * @param itemStack the item stack
     * @return the r item stack
     */
    public static RItemStack build(@NotNull ItemStack itemStack) {
        return new RItemStack(itemStack, !itemStack.getEnchantments().isEmpty(), false) {
            @Override
            public boolean onLeftClick(@NotNull PlayerInteractEvent e, @NotNull RUser user) {
                return false;
            }

            @Override
            public boolean onRightClick(@NotNull PlayerInteractEvent e, @NotNull RUser user) {
                return false;
            }

            @Override
            public void onCooldown(@NotNull PlayerInteractEvent e, @NotNull RUser user) {

            }

            @Override
            public void onCooldownExpire(@NotNull RUser user) {

            }
        };
    }
}
