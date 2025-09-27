package su.nightexpress.nightcore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.bridge.paper.PaperBridge;
import su.nightexpress.nightcore.integration.VaultHook;
import su.nightexpress.nightcore.integration.permission.PermissionProvider;
import su.nightexpress.nightcore.integration.permission.PermissionBridge;
import su.nightexpress.nightcore.integration.permission.impl.LuckPermissionProvider;
import su.nightexpress.nightcore.integration.permission.impl.VaultPermissionProvider;
import su.nightexpress.nightcore.util.ItemNbt;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bridge.Software;
import su.nightexpress.nightcore.util.bukkit.FoliaScheduler;

import java.util.Set;
import java.util.HashSet;

@Deprecated
public class Engine {

    private static final Set<NightPlugin> CHILDRENS = new HashSet<>();

    private static NightCore          core;
    private static PermissionProvider permissions;
    private static FoliaScheduler     foliaScheduler;
    private static volatile boolean   isShuttingDown = false;

    @NotNull
    @Deprecated
    public static Set<NightPlugin> getChildrens() {
        return NightCore.CHILDRENS;
    }

    @NotNull
    @Deprecated
    public static NightCore core() {
        return NightCore.get();
    }

    @NotNull
    @Deprecated
    public static Software software() {
        return Software.instance();
    }

    @NotNull
    public static FoliaScheduler scheduler() {
        if (foliaScheduler == null) throw new IllegalStateException("FoliaScheduler is not initialized!");
        return foliaScheduler;
    }

    public static boolean isShuttingDown() {
        return isShuttingDown;
    }

    @Nullable
    public static PermissionProvider getPermissions() {
        return PermissionBridge.getProvider();
    }

    @Deprecated
    public static boolean hasPermissions() {
        return permissions != null;
    }

    public static void clear() {
        isShuttingDown = true;


        if (foliaScheduler != null) {
            foliaScheduler.cancelAllTasks();
        }

        CHILDRENS.clear();
        permissions = null;
        foliaScheduler = null;
        core = null;
    }

    private static void init(@NotNull NightCore instance) {
        core = instance;

        Version version = Version.detect();
        if (version.isDropped()) return;

        foliaScheduler = new FoliaScheduler(instance);
        core.info("Scheduler initialized for " + (foliaScheduler.isFolia() ? "Folia" : foliaScheduler.isPaper() ? "Paper" : "Spigot") + " server.");

        Software.INSTANCE.load(new PaperBridge());
        core.info("Server version detected as " + version.getLocalized() + ". Using " + software().getName() + ".");

        loadPermissionsProvider();

        Plugins.detectPlugins();
    }

    private static void loadPermissionsProvider() {
        if (Plugins.isInstalled(Plugins.LUCK_PERMS)) {
            permissions = new LuckPermissionProvider();
        }
        else if (Plugins.isInstalled(Plugins.VAULT)) {
            permissions = new VaultPermissionProvider();
        }
        else return;

        core.info("Found permissions provider: " + permissions.getName());
    }

    public static boolean handleEnable(@NotNull NightPlugin plugin) {
        if (plugin instanceof NightCore nightCore) {
            init(nightCore);
        }
        else {
            CHILDRENS.add(plugin);
            plugin.info("Powered by " + core.getName());
        }

        return checkVersion(plugin);
    }

    public static boolean checkVersion(@NotNull NightCorePlugin plugin) {
        Version current = Version.getCurrent();
        if (current != Version.UNKNOWN && current.isSupported()) return true;

        plugin.warn("=".repeat(35));

        if (current == Version.UNKNOWN) {
            plugin.warn("WARNING: This plugin is not supposed to run on this server version!");
            plugin.warn("If server version is newer than " + Version.values()[Version.values().length - 2] + ", then wait for an update please.");
            plugin.warn("The plugin may not work properly.");
        }
        else if (current.isDeprecated()) {
            plugin.warn("WARNING: You're running an outdated server version (" + current.getLocalized() + ")!");
            plugin.warn("This version will no longer be supported in future relases.");
            plugin.warn("Please upgrade your server to " + Lists.next(current, (Version::isSupported)).getLocalized() + ".");
        }
        else if (current.isDropped()) {
            plugin.error("ERROR: You're running an unsupported server version (" + current.getLocalized() + ")!");
            plugin.error("Please upgrade your server to " + Lists.next(current, (Version::isSupported)).getLocalized() + ".");
        }

        plugin.warn("ABSOLUTELY NO DISCORD SUPPORT WILL BE PROVIDED");
        plugin.warn("=".repeat(35));

        if (current.isDropped()) {
            plugin.getPluginManager().disablePlugin(plugin);
            return false;
        }

        return true;
    }
}
