package org.konpekiestudios.chestcommands.api;

/**
 * Functional command handler used by CommandDispatcher implementations.
 */
@FunctionalInterface
public interface CommandHandler {
    /**
     * Handle a command invocation.
     * @param sender engine-specific sender object (player or console)
     * @param args command arguments
     */
    void handle(Object sender, String[] args);
}
