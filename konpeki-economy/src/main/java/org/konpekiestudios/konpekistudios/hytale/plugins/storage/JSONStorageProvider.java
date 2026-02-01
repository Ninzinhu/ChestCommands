//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins.storage;

import org.konpekiestudios.konpekistudios.hytale.plugins.KonpekiEconomy;
import org.konpekiestudios.konpekistudios.hytale.plugins.json.UserProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JSONStorageProvider implements StorageProvider {
    private final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
    private final Map<UUID, UserProfile> cache = new ConcurrentHashMap();
    private final Path dataFolder = Path.of("KonpekiEconomy", "profiles");

    public void init() {
        File folder = this.dataFolder.toFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }

    }

    public void shutdown() {
        this.unloadAll();
    }

    public void loadPlayerData(UUID uuid) {
        File file = this.dataFolder.resolve(uuid.toString() + ".json").toFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                UserProfile profile = (UserProfile)this.gson.fromJson(reader, UserProfile.class);
                this.cache.put(uuid, profile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            UserProfile profile = new UserProfile();
            profile.setUuid(uuid.toString());
            profile.setBalance(KonpekiEconomy.getInstance().getConfigManager().getData().balance.starting);
            this.cache.put(uuid, profile);
        }

    }

    public UserProfile getProfile(UUID uuid) {
        return (UserProfile)this.cache.get(uuid);
    }

    public void updateProfile(UUID uuid, UserProfile profile) {
        this.cache.put(uuid, profile);
    }

    public void savePlayerData(UUID uuid) {
        UserProfile profile = (UserProfile)this.cache.get(uuid);
        File file = this.dataFolder.resolve(uuid.toString() + ".json").toFile();
        HytaleLogger.Api var10000 = (HytaleLogger.Api)KonpekiEconomy.getInstance().getLogger().atInfo();
        String var10001 = String.valueOf(uuid);
        var10000.log("Saving data for player " + var10001 + " in file " + file.getAbsolutePath());

        try (FileWriter writer = new FileWriter(file)) {
            this.gson.toJson(profile, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void unloadPlayer(UUID uuid) {
        this.savePlayerData(uuid);
        this.cache.remove(uuid);
    }

    public void unloadAll() {
        for(UUID uuid : this.cache.keySet()) {
            this.savePlayerData(uuid);
        }

        this.cache.clear();
    }

    public List<UserProfile> getTopBalances(int limit, int offset) {
        List<UserProfile> allProfiles = new ArrayList();
        File folder = this.dataFolder.toFile();
        if (!folder.exists()) {
            return allProfiles;
        } else {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files == null) {
                return allProfiles;
            } else {
                for(File file : files) {
                    try (FileReader reader = new FileReader(file)) {
                        UserProfile profile = (UserProfile)this.gson.fromJson(reader, UserProfile.class);
                        allProfiles.add(profile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                allProfiles.sort((a, b) -> Float.compare(b.balance, a.balance));
                int fromIndex = Math.min(offset, allProfiles.size());
                int toIndex = Math.min(offset + limit, allProfiles.size());
                return allProfiles.subList(fromIndex, toIndex);
            }
        }
    }
}
