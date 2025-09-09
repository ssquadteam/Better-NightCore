package su.nightexpress.nightcore.core;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.menu.impl.AbstractMenu;
import su.nightexpress.nightcore.menu.api.Menu;
import su.nightexpress.nightcore.ui.menu.MenuRegistry;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.dialog.DialogManager;

import java.util.HashSet;
import java.util.Set;

public class AsyncMenuProcessor {
    
    public AsyncMenuProcessor() {
    }
    
    @NotNull
    public AsyncMenuUpdate processMenusAsync() {
        AsyncMenuUpdate update = new AsyncMenuUpdate();

        this.processMenuRefreshAsync(update);
        this.processMenuTickingAsync(update);

        return update;
    }
    
    private void processMenuRefreshAsync(@NotNull AsyncMenuUpdate update) {
        try {
            Set<Menu> menusToCheck = new HashSet<>(AbstractMenu.PLAYER_MENUS.values());

            for (Menu menu : menusToCheck) {
                if (menu instanceof AbstractMenu abstractMenu && abstractMenu.getOptions().isReadyToRefresh()) {
                    update.addMenuRefresh(abstractMenu);
                }
            }
        } catch (Exception e) {
            // Handle concurrent modification gracefully
        }
    }
    
    private void processMenuTickingAsync(@NotNull AsyncMenuUpdate update) {
        try {
            Set<su.nightexpress.nightcore.ui.menu.Menu> menusToTick = MenuRegistry.getViewers().stream()
                .map(MenuViewer::getMenu)
                .collect(HashSet::new, Set::add, Set::addAll);

            for (su.nightexpress.nightcore.ui.menu.Menu menu : menusToTick) {
                update.addMenuTick(menu);
            }
        } catch (Exception e) {
            // Handle concurrent modification gracefully
        }
    }
    

}
