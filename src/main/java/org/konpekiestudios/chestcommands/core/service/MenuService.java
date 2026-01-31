package org.konpekiestudios.chestcommands.core.service;

import org.konpekiestudios.chestcommands.api.ActionContext;
import org.konpekiestudios.chestcommands.api.Condition;
import org.konpekiestudios.chestcommands.api.PlayerRef;
import org.konpekiestudios.chestcommands.core.menu.Menu;
import org.konpekiestudios.chestcommands.core.menu.MenuItem;

public class MenuService {
    public void handleClick(PlayerRef player, Menu menu, int slot) {
        MenuItem item = menu.getItems().get(slot);
        if (item == null) return;

        ActionContext context = new ActionContext() {
            @Override
            public PlayerRef getPlayer() {
                return player;
            }

            @Override
            public String getValue() {
                return ""; // or parse from action
            }
        };

        // Check conditions
        for (Condition condition : item.getConditions()) {
            if (!condition.test(context)) {
                return; // Don't execute if condition fails
            }
        }

        // Execute actions
        for (org.konpekiestudios.chestcommands.api.Action action : item.getActions()) {
            action.execute(context);
        }
    }
}
