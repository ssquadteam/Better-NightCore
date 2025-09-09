package su.nightexpress.nightcore.db.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightPlugin;
import su.nightexpress.nightcore.db.AbstractUser;
import su.nightexpress.nightcore.db.AbstractUserManager;
import su.nightexpress.nightcore.manager.AbstractListener;

import java.util.UUID;

public class UserListener<P extends NightPlugin, U extends AbstractUser> extends AbstractListener<P> {

    private final AbstractUserManager<P, U> manager;

    public UserListener(@NotNull P plugin, @NotNull AbstractUserManager<P, U> manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUserLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

        UUID uuid = event.getUniqueId();
        String name = event.getName();

        this.manager.isInDatabaseAsync(uuid).thenAccept(exists -> {
            if (!exists) {
                U user = this.manager.create(uuid, name);
                this.manager.addInDatabase(user);
                this.manager.cacheTemporary(user);
            } else {
                this.manager.getOrFetchAsync(uuid).thenAccept(user -> {
                    if (user != null) {
                        this.manager.cacheTemporary(user);
                    }
                });
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUserJoin(PlayerJoinEvent event) {
        this.manager.handleJoin(event.getPlayer()); // Get loaded and cache permanent.
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUserQuit(PlayerQuitEvent event) {
        this.manager.handleQuit(event.getPlayer()); // Get loaded and cache temporary.
    }
}
