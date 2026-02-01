package org.konpekiestudios.chestcommands.hytale;

import com.hypixel.hytale.api.BuilderActionBase;
import com.hypixel.hytale.api.Action;
import com.hypixel.hytale.api.BuilderSupport;
import com.hypixel.hytale.api.BuilderDescriptorState;

import javax.annotation.Nullable;

public class BuilderActionOpenChestMenu extends BuilderActionBase {
    @Nullable
    @Override
    public String getShortDescription() {
        return "Opens the chest menu UI";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return "Opens a configurable chest menu UI for the player, with items and actions";
    }

    @Nullable
    @Override
    public Action build(BuilderSupport builderSupport) {
        return new OpenChestMenuAction(this);
    }

    @Nullable
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }
}
