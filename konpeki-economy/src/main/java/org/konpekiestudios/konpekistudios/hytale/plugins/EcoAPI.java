package com.dunystudios.hytale.plugins;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

public class EcoAPI extends JavaPlugin {
    public EcoAPI(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getLogger().atInfo().log("EcoAPI plugin has been initialized.");
    }
}
