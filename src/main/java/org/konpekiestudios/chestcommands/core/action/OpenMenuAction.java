package org.konpekiestudios.chestcommands.core.action;

import org.konpekiestudios.chestcommands.api.Action;
import org.konpekiestudios.chestcommands.api.ActionContext;

public class OpenMenuAction implements Action {
    private final String menuId;

    public OpenMenuAction(String menuId) {
        this.menuId = menuId;
    }

    @Override
    public void execute(ActionContext context) {
        // Implementar abertura de menu via API
        // ChestCommandsAPI api = ...; api.openMenu(context.getPlayer(), menuId);
    }
}
