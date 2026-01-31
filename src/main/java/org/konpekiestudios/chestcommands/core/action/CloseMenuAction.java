package org.konpekiestudios.chestcommands.core.action;

import org.konpekiestudios.chestcommands.api.Action;
import org.konpekiestudios.chestcommands.api.ActionContext;

public class CloseMenuAction implements Action {
    @Override
    public void execute(ActionContext context) {
        // Close the menu for the player
        System.out.println("Closing menu for " + context.getPlayer().getName());
    }
}
