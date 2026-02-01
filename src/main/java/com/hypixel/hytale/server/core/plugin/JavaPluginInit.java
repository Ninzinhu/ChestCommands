package com.hypixel.hytale.server.core.plugin;

/** Stub for JavaPluginInit class used in plugin instantiation. */
public class JavaPluginInit {
    private String pluginId;
    private Class<?> mainClass;

    public JavaPluginInit() {
        // Default constructor
    }

    public JavaPluginInit(String pluginId, Class<?> mainClass) {
        this.pluginId = pluginId;
        this.mainClass = mainClass;
    }

    public String getPluginId() {
        return pluginId;
    }

    public Class<?> getMainClass() {
        return mainClass;
    }

    // Placeholder for initialization data
}
