package su.nightexpress.nightcore.db;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.NightPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class AsyncDatabaseOperations {
    
    private final NightPlugin plugin;
    private final ConcurrentMap<String, CompletableFuture<Boolean>> pendingExistenceChecks;
    private final ConcurrentMap<String, CompletableFuture<Object>> pendingDataLoads;
    
    public AsyncDatabaseOperations(@NotNull NightPlugin plugin) {
        this.plugin = plugin;
        this.pendingExistenceChecks = new ConcurrentHashMap<>();
        this.pendingDataLoads = new ConcurrentHashMap<>();
    }
    
    @NotNull
    public CompletableFuture<Boolean> checkExistsAsync(@NotNull String key, @NotNull Supplier<Boolean> databaseCheck) {
        return pendingExistenceChecks.computeIfAbsent(key, k -> 
            CompletableFuture.supplyAsync(() -> {
                try {
                    return databaseCheck.get();
                } catch (Exception e) {
                    plugin.error("Async existence check failed for key '" + key + "': " + e.getMessage());
                    return false;
                } finally {
                    pendingExistenceChecks.remove(key);
                }
            })
        );
    }
    
    @NotNull
    public <T> CompletableFuture<T> loadDataAsync(@NotNull String key, @NotNull Supplier<T> dataLoader) {
        return (CompletableFuture<T>) pendingDataLoads.computeIfAbsent(key, k -> 
            CompletableFuture.supplyAsync(() -> {
                try {
                    return dataLoader.get();
                } catch (Exception e) {
                    plugin.error("Async data load failed for key '" + key + "': " + e.getMessage());
                    return null;
                } finally {
                    pendingDataLoads.remove(key);
                }
            })
        );
    }
    
    @NotNull
    public CompletableFuture<Void> executeAsync(@NotNull String operationName, @NotNull Runnable databaseOperation) {
        return CompletableFuture.runAsync(() -> {
            try {
                databaseOperation.run();
            } catch (Exception e) {
                plugin.error("Async database operation '" + operationName + "' failed: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
    
    public void clearPendingOperations() {
        pendingExistenceChecks.clear();
        pendingDataLoads.clear();
    }
    
    public int getPendingOperationsCount() {
        return pendingExistenceChecks.size() + pendingDataLoads.size();
    }
}
