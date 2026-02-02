package com.hypixel.hytale.server.core.event;

/** Stub for CommandEvent. */
public class CommandEvent {
    private String command;
    private Object player;
    private String[] args;

    public CommandEvent(String command, Object player, String[] args) {
        this.command = command;
        this.player = player;
        this.args = args;
    }

    public String getCommand() { return command; }
    public Object getPlayer() { return player; }
    public String[] getArgs() { return args; }
}
