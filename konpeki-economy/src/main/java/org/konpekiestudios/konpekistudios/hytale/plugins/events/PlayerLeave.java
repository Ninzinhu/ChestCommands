//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins.events;

import org.konpekiestudios.konpekistudios.hytale.plugins.KonpekiEconomy;
import org.konpekiestudios.konpekistudios.hytale.plugins.json.PlayerManager;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;

public class PlayerLeave {
    public PlayerLeave() {
        PlayerManager playerManager = KonpekiEconomy.getInstance().getPlayerManager();
        KonpekiEconomy.getInstance().getEventRegistry().register(PlayerDisconnectEvent.class, (event) -> {
            if (!HytaleServer.get().isShuttingDown()) {
                playerManager.unloadPlayer(event.getPlayerRef().getUuid());
            }

        });
    }
}
