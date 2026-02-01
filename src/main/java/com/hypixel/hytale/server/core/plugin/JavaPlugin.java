package com.hypixel.hytale.server.core.plugin;

/** Stub for Hytale JavaPlugin base class. */
public abstract class JavaPlugin {
    // Common methods that plugins might override
    public void onEnable() {}
    public void onDisable() {}
    public void onLoad() {}

    // Constructor that takes JavaPluginInit
    public JavaPlugin(JavaPluginInit init) {}
}
