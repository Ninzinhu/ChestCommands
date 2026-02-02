//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins.json;

import org.konpekiestudios.konpekistudios.hytale.plugins.KonpekiEconomy;
import org.konpekiestudios.konpekistudios.hytale.plugins.config.ConfigData;
import org.konpekiestudios.konpekistudios.hytale.plugins.storage.JSONStorageProvider;
import org.konpekiestudios.konpekistudios.hytale.plugins.storage.SQLStorageProvider;
import org.konpekiestudios.konpekistudios.hytale.plugins.storage.StorageProvider;
import com.hypixel.hytale.logger.HytaleLogger;
import java.util.List;
import java.util.UUID;

public class PlayerManager {
    private final StorageProvider storageProvider;
    private final String storageType;

    public PlayerManager(ConfigData configData) {
        this.storageType = configData.storage.type.toLowerCase();
        switch (this.storageType) {
            case "json":
                this.storageProvider = new JSONStorageProvider();
                break;
            case "sqlite":
                this.storageProvider = new SQLStorageProvider("sqlite", (String)null, 0, (String)null, (String)null, (String)null);
                break;
            case "mysql":
                ConfigData.Storage.MySQL mysql = configData.storage.mysql;
                this.storageProvider = new SQLStorageProvider("mysql", mysql.host, mysql.port, mysql.database, mysql.username, mysql.password);
                break;
            default:
                ((HytaleLogger.Api)KonpekiEconomy.getInstance().getLogger().atWarning()).log("Unknown storage type: " + configData.storage.type + ", defaulting to JSON");
                this.storageProvider = new JSONStorageProvider();
        }

        this.storageProvider.init();
    }

    public void loadPlayerData(UUID uuid) {
        this.storageProvider.loadPlayerData(uuid);
    }

    public UserProfile getProfile(UUID uuid) {
        return this.storageProvider.getProfile(uuid);
    }

    public void updateProfile(UUID uuid, UserProfile profile) {
        this.storageProvider.updateProfile(uuid, profile);
    }

    public void savePlayerData(UUID uuid) {
        this.storageProvider.savePlayerData(uuid);
    }

    public void unloadPlayer(UUID uuid) {
        this.storageProvider.unloadPlayer(uuid);
    }

    public void unloadAll() {
        this.storageProvider.unloadAll();
        this.storageProvider.shutdown();
    }

    public List<UserProfile> getTopBalances(int limit, int offset) {
        return this.storageProvider.getTopBalances(limit, offset);
    }
}
