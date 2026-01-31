package org.konpekiestudios.chestcommands.core.service;

import org.konpekiestudios.chestcommands.api.PlayerRef;

import java.util.HashMap;
import java.util.Map;

public class CooldownService {
    private final Map<String, Long> cooldowns = new HashMap<>();

    public boolean isOnCooldown(PlayerRef player, String key) {
        String playerKey = player.getName() + ":" + key;
        Long endTime = cooldowns.get(playerKey);
        if (endTime == null) return false;
        if (System.currentTimeMillis() > endTime) {
            cooldowns.remove(playerKey);
            return false;
        }
        return true;
    }

    public void setCooldown(PlayerRef player, String key, long durationMs) {
        String playerKey = player.getName() + ":" + key;
        cooldowns.put(playerKey, System.currentTimeMillis() + durationMs);
    }
}
