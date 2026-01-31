package org.konpekiestudios.chestcommands.core.action;

import org.konpekiestudios.chestcommands.api.Action;
import org.konpekiestudios.chestcommands.api.ActionContext;

public class RunCommandAction implements Action {
    private final String command;

    public RunCommandAction(String command) {
        this.command = command;
    }

    @Override
    public void execute(ActionContext context) {
        // Execute command as player, e.g., context.getPlayer() run command
        System.out.println("Running command: " + command + " for " + context.getPlayer().getName());
    }
}
