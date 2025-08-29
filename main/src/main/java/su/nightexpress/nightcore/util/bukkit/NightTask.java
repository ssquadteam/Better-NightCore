package su.nightexpress.nightcore.util.bukkit;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.Engine;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.util.TimeUtil;

/**
 * NightTask now uses FoliaScheduler for Folia compatibility.
 * This provides a unified task interface that works on both Folia and Paper/Spigot servers.
 */
public class NightTask {

    private final NightCorePlugin plugin;
    private final WrappedTask wrappedTask;

    public NightTask(@NotNull NightCorePlugin plugin, @Nullable WrappedTask wrappedTask) {
        this.plugin = plugin;
        this.wrappedTask = wrappedTask;
    }

    @NotNull
    public static NightTask create(@NotNull NightCorePlugin plugin, @NotNull Runnable runnable, int interval) {
        return create(plugin, runnable, TimeUtil.secondsToTicks(interval));
    }

    @NotNull
    public static NightTask create(@NotNull NightCorePlugin plugin, @NotNull Runnable runnable, long interval) {
        if (interval <= 0) {
            return new NightTask(plugin, null);
        }
        WrappedTask task = Engine.scheduler().runTimer(runnable, 0L, interval);
        return new NightTask(plugin, task);
    }

    @NotNull
    public static NightTask createAsync(@NotNull NightCorePlugin plugin, @NotNull Runnable runnable, int interval) {
        return createAsync(plugin, runnable, TimeUtil.secondsToTicks(interval));
    }

    @NotNull
    public static NightTask createAsync(@NotNull NightCorePlugin plugin, @NotNull Runnable runnable, long interval) {
        if (interval <= 0) {
            return new NightTask(plugin, null);
        }
        WrappedTask task = Engine.scheduler().runTimerAsync(runnable, 0L, interval);
        return new NightTask(plugin, task);
    }

    @Nullable
    public WrappedTask getWrappedTask() {
        return this.wrappedTask;
    }

    public boolean isValid() {
        return this.wrappedTask != null;
    }

    public boolean isRunning() {
        return this.wrappedTask != null && !this.wrappedTask.isCancelled();
    }

    public boolean stop() {
        if (this.wrappedTask == null) return false;

        this.wrappedTask.cancel();
        return true;
    }
}
