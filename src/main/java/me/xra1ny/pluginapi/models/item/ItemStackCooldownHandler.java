package me.xra1ny.pluginapi.models.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.exceptions.ClassNotAnnotatedException;
import me.xra1ny.pluginapi.models.task.RRepeatableTask;
import me.xra1ny.pluginapi.models.task.RepeatableTaskInfo;
import me.xra1ny.pluginapi.models.user.RUser;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.Map;

@Slf4j
@RepeatableTaskInfo(interval = 50)
public class ItemStackCooldownHandler extends RRepeatableTask {
    @Getter(onMethod = @__(@NotNull))
    private final ItemStackManager itemStackManager;

    public ItemStackCooldownHandler(@NotNull ItemStackManager itemStackManager) throws ClassNotAnnotatedException {
        this.itemStackManager = itemStackManager;
    }

    @Override
    public void tick() {
        for(RItemStack itemStack : itemStackManager.getItems()) {
            try {
                for(Map.Entry<RUser, Integer> entry : itemStack.getCooldownQueue().entrySet()) {
                    final RUser user = entry.getKey();
                    int cooldown = entry.getValue();

                    for(int i = 0; i < 50; i++) {
                        // Replace old Value
                        itemStack.getCooldownQueue().put(user, cooldown-=1);

                        // Validate new Value
                        if(cooldown <= 0) {
                            itemStack.getCooldownQueue().remove(user);
                            itemStack.onCooldownExpire(user);
                        }
                    }
                }
            }catch(ConcurrentModificationException ignored) {}
        }
    }
}
