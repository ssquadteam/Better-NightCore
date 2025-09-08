package su.nightexpress.nightcore.util.bukkit;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * FoliaScheduler provides a unified scheduling interface that works on both Folia and Paper/Spigot servers.
 * This class wraps FoliaLib to provide Folia-compatible scheduling while maintaining backward compatibility.
 */
public class FoliaScheduler {

    private final FoliaLib foliaLib;
    private volatile boolean isShutdown = false;

    public FoliaScheduler(@NotNull Plugin plugin) {
        this.foliaLib = new FoliaLib(plugin);
    }

    /**
     * Gets the underlying FoliaLib instance for advanced usage.
     * @return The FoliaLib instance
     */
    @NotNull
    public FoliaLib getFoliaLib() {
        return this.foliaLib;
    }

    /**
     * Checks if the server is running Folia.
     * @return true if running on Folia, false otherwise
     */
    public boolean isFolia() {
        return this.foliaLib.isFolia();
    }

    /**
     * Checks if the server is running Paper.
     * @return true if running on Paper, false otherwise
     */
    public boolean isPaper() {
        return this.foliaLib.isPaper();
    }

    /**
     * Checks if the server is running Spigot.
     * @return true if running on Spigot, false otherwise
     */
    public boolean isSpigot() {
        return this.foliaLib.isSpigot();
    }

    /**
     * Runs a task on the next tick.
     * On Folia: Uses GlobalRegionScheduler
     * On Paper/Spigot: Uses main thread
     * @param runnable The task to run
     * @return CompletableFuture that completes when the task finishes
     */
    @NotNull
    public CompletableFuture<Void> runNextTick(@NotNull Runnable runnable) {
        if (!this.isValid()) {
            runnable.run();
            return CompletableFuture.completedFuture(null);
        }
        return this.foliaLib.getScheduler().runNextTick(task -> runnable.run());
    }

    /**
     * Runs a task asynchronously.
     * @param runnable The task to run
     * @return CompletableFuture that completes when the task finishes
     */
    @NotNull
    public CompletableFuture<Void> runAsync(@NotNull Runnable runnable) {
        return this.foliaLib.getScheduler().runAsync(task -> runnable.run());
    }

    /**
     * Runs a task after a delay.
     * @param runnable The task to run
     * @param delay The delay in ticks
     * @return WrappedTask that can be cancelled
     */
    @NotNull
    public WrappedTask runLater(@NotNull Runnable runnable, long delay) {
        if (!this.isValid()) {
            runnable.run();
            return new DummyWrappedTask();
        }
        return this.foliaLib.getScheduler().runLater(runnable, delay);
    }

    /**
     * Runs a task after a delay with TimeUnit.
     * @param runnable The task to run
     * @param delay The delay
     * @param unit The time unit
     * @return WrappedTask that can be cancelled
     */
    @NotNull
    public WrappedTask runLater(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit) {
        return this.foliaLib.getScheduler().runLater(runnable, delay, unit);
    }

    /**
     * Runs a task asynchronously after a delay.
     * @param runnable The task to run
     * @param delay The delay in ticks
     * @return WrappedTask that can be cancelled
     */
    @NotNull
    public WrappedTask runLaterAsync(@NotNull Runnable runnable, long delay) {
        return this.foliaLib.getScheduler().runLaterAsync(runnable, delay);
    }

    /**
     * Runs a repeating task.
     * @param runnable The task to run
     * @param delay The initial delay in ticks
     * @param period The period between executions in ticks
     * @return WrappedTask that can be cancelled
     */
    @NotNull
    public WrappedTask runTimer(@NotNull Runnable runnable, long delay, long period) {
        return this.foliaLib.getScheduler().runTimer(runnable, delay, period);
    }

    /**
     * Runs a repeating task asynchronously.
     * @param runnable The task to run
     * @param delay The initial delay in ticks
     * @param period The period between executions in ticks
     * @return WrappedTask that can be cancelled
     */
    @NotNull
    public WrappedTask runTimerAsync(@NotNull Runnable runnable, long delay, long period) {
        return this.foliaLib.getScheduler().runTimerAsync(runnable, delay, period);
    }

    /**
     * Runs a task at a specific location.
     * On Folia: Uses RegionScheduler for the location
     * On Paper/Spigot: Uses main thread
     * @param location The location where the task should run
     * @param runnable The task to run
     * @return CompletableFuture that completes when the task finishes
     */
    @NotNull
    public CompletableFuture<Void> runAtLocation(@NotNull Location location, @NotNull Runnable runnable) {
        return this.foliaLib.getScheduler().runAtLocation(location, task -> runnable.run());
    }

    /**
     * Runs a task for a specific entity.
     * On Folia: Uses EntityScheduler for the entity
     * On Paper/Spigot: Uses main thread
     * @param entity The entity for which the task should run
     * @param runnable The task to run
     * @return CompletableFuture that completes when the task finishes
     */
    @NotNull
    public CompletableFuture<Void> runAtEntity(@NotNull Entity entity, @NotNull Runnable runnable) {
        return this.foliaLib.getScheduler().runAtEntity(entity, task -> runnable.run())
            .thenApply(result -> null); // Convert EntityTaskResult to Void
    }

    /**
     * Teleports an entity asynchronously.
     * @param entity The entity to teleport
     * @param location The destination location
     * @return CompletableFuture<Boolean> indicating success
     */
    @NotNull
    public CompletableFuture<Boolean> teleportAsync(@NotNull Entity entity, @NotNull Location location) {
        return this.foliaLib.getScheduler().teleportAsync(entity, location);
    }

    public boolean isValid() {
        return !this.isShutdown && this.foliaLib != null;
    }

    /**
     * Cancels all tasks associated with this scheduler.
     * Should be called during plugin disable.
     */
    public void cancelAllTasks() {
        this.isShutdown = true;
        if (this.foliaLib != null) {
            this.foliaLib.getScheduler().cancelAllTasks();
        }
    }

    /**
     * Checks if a location is owned by the current region.
     * @param location The location to check
     * @return true if owned by current region
     */
    public boolean isOwnedByCurrentRegion(@NotNull Location location) {
        if (!this.isValid()) {
            return true;
        }
        return this.foliaLib.getScheduler().isOwnedByCurrentRegion(location);
    }

    /**
     * Dummy WrappedTask implementation for when scheduler is shut down.
     */
    private static class DummyWrappedTask implements WrappedTask {
        @Override
        public void cancel() {
        }

        @Override
        public boolean isCancelled() {
            return true;
        }

        @Override
        public Object getWrappedTask() {
            return null;
        }
    }
}
