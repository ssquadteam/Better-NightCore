package su.nightexpress.nightcore.core;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.menu.impl.AbstractMenu;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncMenuUpdate {
    
    private final Set<AbstractMenu> menusToRefresh;
    private final Set<su.nightexpress.nightcore.ui.menu.Menu> menusToTick;
    private final Set<Player> dialogsToTimeout;
    private final Set<Player> dialogsToTick;
    
    public AsyncMenuUpdate() {
        this.menusToRefresh = ConcurrentHashMap.newKeySet();
        this.menusToTick = ConcurrentHashMap.newKeySet();
        this.dialogsToTimeout = ConcurrentHashMap.newKeySet();
        this.dialogsToTick = ConcurrentHashMap.newKeySet();
    }
    
    public void addMenuRefresh(@NotNull AbstractMenu menu) {
        this.menusToRefresh.add(menu);
    }
    
    public void addMenuTick(@NotNull su.nightexpress.nightcore.ui.menu.Menu menu) {
        this.menusToTick.add(menu);
    }
    
    public void addDialogTimeout(@NotNull Player player) {
        this.dialogsToTimeout.add(player);
    }
    
    public void addDialogTick(@NotNull Player player) {
        this.dialogsToTick.add(player);
    }
    
    public boolean hasUpdates() {
        return !menusToRefresh.isEmpty() || !menusToTick.isEmpty() || 
               !dialogsToTimeout.isEmpty() || !dialogsToTick.isEmpty();
    }
    
    public void applyToMainThread() {
        for (AbstractMenu menu : menusToRefresh) {
            try {
                if (!menu.getViewers().isEmpty()) {
                    menu.flush();
                    menu.getOptions().setLastAutoRefresh(System.currentTimeMillis());
                }
            } catch (Exception e) {
                // Handle menu refresh errors gracefully
            }
        }
        
        for (su.nightexpress.nightcore.ui.menu.Menu menu : menusToTick) {
            try {
                menu.tick();
            } catch (Exception e) {
                // Handle menu tick errors gracefully
            }
        }
        
        for (Player player : dialogsToTimeout) {
            try {
                su.nightexpress.nightcore.dialog.Dialog.stop(player);
            } catch (Exception e) {
                // Handle dialog timeout errors gracefully
            }
        }
    }
    
    @NotNull
    public Set<AbstractMenu> getMenusToRefresh() {
        return Set.copyOf(menusToRefresh);
    }
    
    @NotNull
    public Set<su.nightexpress.nightcore.ui.menu.Menu> getMenusToTick() {
        return Set.copyOf(menusToTick);
    }
    
    @NotNull
    public Set<Player> getDialogsToTimeout() {
        return Set.copyOf(dialogsToTimeout);
    }
    
    @NotNull
    public Set<Player> getDialogsToTick() {
        return Set.copyOf(dialogsToTick);
    }
}
