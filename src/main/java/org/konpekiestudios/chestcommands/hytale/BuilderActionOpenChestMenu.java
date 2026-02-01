package org.konpekiestudios.chestcommands.hytale;

import com.hypixel.hytale.Action;
import com.hypixel.hytale.BuilderActionBase;

public class BuilderActionOpenChestMenu extends BuilderActionBase {
    @Override
    public String getShortDescription() {
        return "Opens the chest UI";
    }

    @Override
    public String getLongDescription() {
        return "Opens a chest-like UI for the player";
    }

    @Override
    public Action build(Object builderSupport) {
        String id = "default";
        // If builderSupport is not null and has getStateSupport, try to get id
        // This is a stub, as BuilderSupport is empty
        return new OpenChestMenuAction(id);
    }
}
