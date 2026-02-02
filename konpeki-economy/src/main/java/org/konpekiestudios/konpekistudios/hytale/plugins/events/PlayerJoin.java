//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins.events;

import org.konpekiestudios.konpekistudios.hytale.plugins.KonpekiEconomy;
import org.konpekiestudios.konpekistudios.hytale.plugins.json.PlayerManager;
import org.konpekiestudios.konpekistudios.hytale.plugins.json.UserProfile;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import java.util.UUID;

public class PlayerJoin {
    public PlayerJoin() {
        PlayerManager playerManager = KonpekiEconomy.getInstance().getPlayerManager();
        KonpekiEconomy.getInstance().getEventRegistry().register(PlayerConnectEvent.class, (event) -> {
            UUID playerUuid = event.getPlayerRef().getUuid();
            playerManager.loadPlayerData(playerUuid);
            UserProfile profile = playerManager.getProfile(playerUuid);
            if (profile != null) {
                ((HytaleLogger.Api)KonpekiEconomy.getInstance().getLogger().atInfo()).log("Loaded data for player " + String.valueOf(playerUuid));
                HytaleLogger.Api var10000 = (HytaleLogger.Api)KonpekiEconomy.getInstance().getLogger().atInfo();
                String var10001 = String.valueOf(playerUuid);
                var10000.log("Player " + var10001 + " joined. Balance: " + profile.balance);
            } else {
                ((HytaleLogger.Api)KonpekiEconomy.getInstance().getLogger().atSevere()).log("Failed to load profile for " + String.valueOf(playerUuid));
            }

        });
    }
}
