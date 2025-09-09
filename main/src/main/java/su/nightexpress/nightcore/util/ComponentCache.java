package su.nightexpress.nightcore.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ComponentCache {
    
    private static final ConcurrentMap<String, Component> COMPONENT_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, String> SERIALIZED_CACHE = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 1000;
    
    @NotNull
    public static Component getOrCreateComponent(@NotNull String text) {
        return COMPONENT_CACHE.computeIfAbsent(text, key -> {
            if (COMPONENT_CACHE.size() >= MAX_CACHE_SIZE) {
                clearCache();
            }
            return LegacyComponentSerializer.legacySection().deserialize(key);
        });
    }
    
    @Nullable
    public static String getOrCreateSerialized(@NotNull Component component) {
        String key = component.toString();
        return SERIALIZED_CACHE.computeIfAbsent(key, k -> {
            if (SERIALIZED_CACHE.size() >= MAX_CACHE_SIZE) {
                clearSerializedCache();
            }
            return LegacyComponentSerializer.legacySection().serialize(component);
        });
    }
    
    public static void clearCache() {
        COMPONENT_CACHE.clear();
    }
    
    public static void clearSerializedCache() {
        SERIALIZED_CACHE.clear();
    }
    
    public static void clearAllCaches() {
        clearCache();
        clearSerializedCache();
    }
    
    public static int getComponentCacheSize() {
        return COMPONENT_CACHE.size();
    }
    
    public static int getSerializedCacheSize() {
        return SERIALIZED_CACHE.size();
    }
}
