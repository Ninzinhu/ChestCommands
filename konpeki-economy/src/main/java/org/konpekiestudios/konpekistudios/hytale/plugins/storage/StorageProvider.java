//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins.storage;

import org.konpekiestudios.konpekistudios.hytale.plugins.json.UserProfile;
import java.util.List;
import java.util.UUID;

public interface StorageProvider {
    void init();

    void shutdown();

    void loadPlayerData(UUID var1);

    UserProfile getProfile(UUID var1);

    void updateProfile(UUID var1, UserProfile var2);

    void savePlayerData(UUID var1);

    void unloadPlayer(UUID var1);

    void unloadAll();

    List<UserProfile> getTopBalances(int var1, int var2);
}
