package org.konpekiestudios.konpekistudios.hytale.plugins;

import org.konpekiestudios.chestcommands.api.PlayerRef;

public record HytalePlayerRef(Object player) implements PlayerRef {
    @Override
    public String getName() {
        // return player.getName(); assume method exists
        return player.toString(); // placeholder
    }
}
