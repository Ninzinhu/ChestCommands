package org.konpekiestudios.konpekistudios.hytale.plugins;

import java.util.UUID;

public interface KonpekiEcoAPI {
    float getBalance(UUID playerId);
    void addBalance(UUID playerId, float amount);
    void removeBalance(UUID playerId, float amount);
    void setBalance(UUID playerId, float amount);

    class Service {
        private static KonpekiEcoAPI instance;

        public static KonpekiEcoAPI getInstance() {
            return instance;
        }

        public static void setInstance(KonpekiEcoAPI api) {
            instance = api;
        }
    }
}
