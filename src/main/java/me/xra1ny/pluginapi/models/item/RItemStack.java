package me.xra1ny.pluginapi.models.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.exceptions.ClassNotAnnotatedException;
import me.xra1ny.pluginapi.models.user.RUser;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to create ItemStacks that can be interacted with
 */
@Slf4j
public abstract class RItemStack extends ItemStack {
    @Getter(onMethod = @__(@NotNull))
    private final Map<RUser, Integer> cooldownQueue = new HashMap<>();

    @Getter
    private int cooldown = 0;

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

        setType(itemStack.getType());
        setAmount(itemStack.getAmount());
        setItemMeta(itemStack.getItemMeta());
        this.cooldown = info.cooldown();
    }

    public RItemStack(@NotNull ItemStack item) {
        super(item);
        log.info("");
        log.info("Initialising ItemStack {}", this);
    }

    public abstract boolean onLeftClick(@NotNull PlayerInteractEvent e, @NotNull RUser user);

    public abstract boolean onRightClick(@NotNull PlayerInteractEvent e, @NotNull RUser user);
    public abstract void onCooldown(@NotNull PlayerInteractEvent e, @NotNull RUser user);

    public abstract void onCooldownExpire(@NotNull RUser user);

    public final void handleInteraction(@NotNull PlayerInteractEvent e, @NotNull RUser user) {
        if(!cooldownQueue.containsKey(user)) {
            final Action action = e.getAction();

            final boolean result;

            if(action.name().startsWith("LEFT_CLICK")) {
                result = onLeftClick(e, user);
            }else {
                result = onRightClick(e, user);
            }

            if(result) {
                if(cooldown >= 1)
                    cooldownQueue.put(user, cooldown);
            }
        }else {
            onCooldown(e, user);
        }
    }
}
