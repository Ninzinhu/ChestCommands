package org.konpekiestudios.chestcommands.hytale;

import com.hypixel.hytale.Action;
import com.hypixel.hytale.Role;
import org.konpekiestudios.chestcommands.core.menu.Menu;

import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenChestMenuAction extends Action {
    private final Logger logger = Logger.getLogger("ChestCommands");
    private final String menuId;

    public OpenChestMenuAction(String menuId) {
        this.menuId = menuId;
    }

    @Override
    public void execute(Role role) {
        Object player = role; // In a real implementation, extract player from role
        try {
            Menu menu = null;
            // TODO: Implement menu lookup by menuId if possible
            HytaleMenuRenderer renderer = new HytaleMenuRenderer();
            renderer.open(player, menu);
            renderer.safeSendMessage(player, "Opened chest menu: " + (menu != null ? menu.getId() : menuId));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to execute OpenChestMenuAction", e);
        }
    }
}
