//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import org.konpekiestudios.konpekistudios.hytale.plugins.config.ConfigManager;
import org.konpekiestudios.konpekistudios.hytale.plugins.json.PlayerManager;
import org.konpekiestudios.konpekistudios.hytale.plugins.json.UserProfile;

public class Economy implements KonpekiEcoAPI {
    private final PlayerManager playerManager;
    private final ConfigManager configManager;

    public Economy(PlayerManager playerManager, ConfigManager configManager) {
        this.playerManager = playerManager;
        this.configManager = configManager;
    }

    public float getBalance(UUID uuid) {
        return Float.parseFloat((new BigDecimal((double)this.playerManager.getProfile(uuid).balance)).setScale(this.configManager.getData().chat.decimal_places, RoundingMode.DOWN).toString());
    }

    public void addBalance(UUID uuid, float v) {
        UserProfile userProfile = this.playerManager.getProfile(uuid);
        userProfile.setBalance(userProfile.balance + v);
        this.playerManager.updateProfile(uuid, userProfile);
    }

    public void removeBalance(UUID uuid, float v) {
        UserProfile userProfile = this.playerManager.getProfile(uuid);
        userProfile.setBalance(userProfile.balance - v);
        this.playerManager.updateProfile(uuid, userProfile);
    }

    public void setBalance(UUID uuid, float v) {
        UserProfile userProfile = this.playerManager.getProfile(uuid);
        userProfile.setBalance(v);
        this.playerManager.updateProfile(uuid, userProfile);
    }
}
