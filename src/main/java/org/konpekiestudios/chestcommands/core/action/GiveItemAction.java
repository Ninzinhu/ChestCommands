package org.konpekiestudios.chestcommands.core.action;

import org.konpekiestudios.chestcommands.api.Action;
import org.konpekiestudios.chestcommands.api.ActionContext;

public class GiveItemAction implements Action {
    private final String item;
    private final int amount;

    public GiveItemAction(String item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    @Override
    public void execute(ActionContext context) {
        // Give item to player
        System.out.println("Giving " + amount + " of " + item + " to " + context.getPlayer().getName());
    }
}
