package org.konpekiestudios.chestcommands.hytale;

import org.konpekiestudios.chestcommands.api.PlayerRef;

public class HytalePlayerRef implements PlayerRef {
    private final Object player; // Assume Hytale Player

    public HytalePlayerRef(Object player) {
        this.player = player;
    }

    @Override
    public String getName() {
        // return player.getName(); assume method exists
        return player.toString(); // placeholder
    }

    public Object getPlayer() {
        return player;
    }
}
