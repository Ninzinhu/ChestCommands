package org.konpekiestudios.chestcommands.api;

import java.util.function.Consumer;

/**
 * Abstraction that adapters implement to execute server commands and capture their output.
 * The implementation is engine-specific (Hytale adapter will provide one).
 */
public interface CommandDispatcher {
    /**
     * Execute a command as the given player and stream each message line produced by the command to the callback.
     * Implementations should ensure the callback receives messages only resulting from this command execution.
     */
    void executeAsPlayer(Object player, String command, Consumer<String> outputLine);

    /**
     * Execute a command as the console; output lines will be sent to the callback.
     */
    void executeAsConsole(String command, Consumer<String> outputLine);

    /**
     * Register a command handler for a name (fallback local registration).
     * Handler accepts (senderObject, args[]) and returns boolean success.
     */
    void register(String name, org.konpekiestudios.chestcommands.api.CommandHandler handler);

    /**
     * Dispatch a command (sender: engine-specific player object or console), command label without slash, and args.
     */
    boolean dispatch(Object sender, String commandLabel, String[] args);
}
