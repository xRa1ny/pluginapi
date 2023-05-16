package me.xra1ny.pluginapi.models.task;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.ClassNotAnnotatedException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public abstract class RRepeatableTask {
    /**
     * the bukkit runnable of this repeatable task defining its logic
     */
    @Getter(onMethod = @__(@NotNull))
    private final BukkitRunnable runnable;

    /**
     * the bukkit task of this repeatable task
     */
    @Getter(onMethod = @__(@Nullable))
    private BukkitTask task;

    /**
     * the interval of this repeatable task
     */
    @Getter
    private final int interval;

    public RRepeatableTask() throws ClassNotAnnotatedException {
        final RepeatableTaskInfo info = getClass().getDeclaredAnnotation(RepeatableTaskInfo.class);

        if(info == null) {
            throw new ClassNotAnnotatedException(getClass(), RepeatableTaskInfo.class);
        }else {
            this.interval = info.interval();

            this.runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    tick();
                }
            };
        }
    }

    public RRepeatableTask(int interval) {
        this.interval = interval;

        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
    }

    /**
     * starts this repeatable task
     */
    public final void start() {
        stop();
        task = runnable.runTaskTimer(RPlugin.getInstance(), 0L, (interval/1000)*20);
    }

    /**
     * stops this repeatable task
     */
    public final void stop() {
        if(this.task == null) {
            return;
        }

        task.cancel();
        this.task = null;
    }

    public final boolean isRunning() {
        return this.task != null && !this.task.isCancelled();
    }

    /**
     * called whenever the interval of this repeatable task expires
     */
    public abstract void tick();
}
