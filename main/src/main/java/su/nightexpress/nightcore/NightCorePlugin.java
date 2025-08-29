package su.nightexpress.nightcore;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.CommandManager;
import su.nightexpress.nightcore.command.api.NightPluginCommand;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.language.LangManager;
import su.nightexpress.nightcore.util.wrapper.UniTask;
import su.nightexpress.nightcore.Engine;
import su.nightexpress.nightcore.util.bukkit.FoliaScheduler;
import su.nightexpress.nightcore.util.bukkit.FoliaTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;

public interface NightCorePlugin extends Plugin {

    //boolean isEngine();

    void enable();

    void disable();

    void reload();

    @Deprecated
    NightPluginCommand getBaseCommand();

    @Override
    @NotNull FileConfig getConfig();

    @Deprecated
    @NotNull FileConfig getLang();

    @NotNull PluginDetails getDetails();

    void extractResources(@NotNull String jarPath);

    void extractResources(@NotNull String jarParh, @NotNull String toPath);

    @NotNull
    default String getNameLocalized() {
        return this.getDetails().getName();
    }

    @NotNull
    default String getPrefix() {
        return this.getDetails().getPrefix();
    }

    @NotNull
    default String[] getCommandAliases() {
        return this.getDetails().getCommandAliases();
    }

    @NotNull
    @Deprecated
    default String getLanguage() {
        return this.getDetails().getLanguage();
    }

    default void info(@NotNull String msg) {
        this.getLogger().info(msg);
    }

    default void warn(@NotNull String msg) {
        this.getLogger().warning(msg);
    }

    default void error(@NotNull String msg) {
        this.getLogger().severe(msg);
    }

    default void debug(@NotNull String msg) {
        this.info("[DEBUG] " + msg);
    }

    @Deprecated
    @NotNull LangManager getLangManager();

    @Deprecated
    @NotNull CommandManager getCommandManager();

    @NotNull
    default BukkitScheduler getScheduler() {
        return this.getServer().getScheduler();
    }

    @NotNull
    default PluginManager getPluginManager() {
        return this.getServer().getPluginManager();
    }

    void runTask(@NotNull Runnable runnable);

    @Deprecated
    default void runTask(@NotNull Consumer<BukkitTask> consumer) {
        this.runNextTick(() -> consumer.accept(null));
    }

    @Deprecated
    default void runTaskAsync(@NotNull Consumer<BukkitTask> consumer) {
        this.runTaskAsync(() -> consumer.accept(null));
    }

    @Deprecated
    default void runTaskLater(@NotNull Consumer<BukkitTask> consumer, long delay) {
        this.getFoliaScheduler().runLater(() -> consumer.accept(null), delay);
    }

    @Deprecated
    default void runTaskLaterAsync(@NotNull Consumer<BukkitTask> consumer, long delay) {
        this.getFoliaScheduler().runLaterAsync(() -> consumer.accept(null), delay);
    }

    @Deprecated
    default void runTaskTimer(@NotNull Consumer<BukkitTask> consumer, long delay, long interval) {
        this.getFoliaScheduler().runTimer(() -> consumer.accept(null), delay, interval);
    }

    @Deprecated
    default void runTaskTimerAsync(@NotNull Consumer<BukkitTask> consumer, long delay, long interval) {
        this.getFoliaScheduler().runTimerAsync(() -> consumer.accept(null), delay, interval);
    }

    @NotNull
    @Deprecated
    default UniTask createTask(@NotNull Runnable runnable) {
        return new UniTask(this, runnable);
    }

    @NotNull
    @Deprecated
    default UniTask createAsyncTask(@NotNull Runnable runnable) {
        return this.createTask(runnable).setAsync();
    }

    // Folia-compatible scheduling methods

    /**
     * Gets the FoliaScheduler instance for Folia-compatible scheduling.
     * @return The FoliaScheduler instance
     */
    @NotNull
    default FoliaScheduler getFoliaScheduler() {
        return Engine.scheduler();
    }

    /**
     * Runs a task on the next tick (Folia-compatible).
     * @param runnable The task to run
     * @return CompletableFuture that completes when the task finishes
     */
    @NotNull
    default CompletableFuture<Void> runNextTick(@NotNull Runnable runnable) {
        return this.getFoliaScheduler().runNextTick(runnable);
    }

    /**
     * Runs a task asynchronously (Folia-compatible).
     * @param runnable The task to run
     * @return CompletableFuture that completes when the task finishes
     */
    @NotNull
    default CompletableFuture<Void> runTaskAsync(@NotNull Runnable runnable) {
        return this.getFoliaScheduler().runAsync(runnable);
    }

    /**
     * Runs a task at a specific location (Folia-compatible).
     * @param location The location where the task should run
     * @param runnable The task to run
     * @return CompletableFuture that completes when the task finishes
     */
    @NotNull
    default CompletableFuture<Void> runAtLocation(@NotNull Location location, @NotNull Runnable runnable) {
        return this.getFoliaScheduler().runAtLocation(location, runnable);
    }

    /**
     * Runs a task for a specific entity (Folia-compatible).
     * @param entity The entity for which the task should run
     * @param runnable The task to run
     * @return CompletableFuture that completes when the task finishes
     */
    @NotNull
    default CompletableFuture<Void> runAtEntity(@NotNull Entity entity, @NotNull Runnable runnable) {
        return this.getFoliaScheduler().runAtEntity(entity, runnable);
    }

    /**
     * Creates a Folia-compatible task.
     * @param runnable The task to run
     * @param interval The interval in ticks
     * @return FoliaTask that can be cancelled
     */
    @NotNull
    default FoliaTask createFoliaTask(@NotNull Runnable runnable, long interval) {
        return FoliaTask.create(this, runnable, interval);
    }

    /**
     * Creates a Folia-compatible async task.
     * @param runnable The task to run
     * @param interval The interval in ticks
     * @return FoliaTask that can be cancelled
     */
    @NotNull
    default FoliaTask createFoliaTaskAsync(@NotNull Runnable runnable, long interval) {
        return FoliaTask.createAsync(this, runnable, interval);
    }
}
