package me.xra1ny.pluginapi.models.task;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.pluginapi.RPlugin;
import me.xra1ny.pluginapi.exceptions.ClassNotAnnotatedException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

@Slf4j
public abstract class RRepeatableTask {
    /**
     * the bukkit runnable of this repeatable task defining its logic
     */
    @Getter(onMethod = @__(@NotNull))
    private BukkitRunnable runnable;

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
        }

        this.interval = info.interval();
        run();
    }

    public RRepeatableTask(int interval) {
        this.interval = interval;
        run();
    }

    private void run() {
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    onTick();
                } catch (Exception ex) {
                    RPlugin.getInstance().getLogger().log(Level.SEVERE, "error in repeatable task tick " + this, ex);
                }
            }
        };
    }

    /**
     * starts this repeatable task
     */
    public final void start() {
        stop();
        this.task = this.runnable.runTaskTimer(RPlugin.getInstance(), 0L, (this.interval / 1000) * 20);
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
    public abstract void onTick() throws Exception;
}
