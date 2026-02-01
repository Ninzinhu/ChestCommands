package org.konpekiestudios.chestcommands.hooks;

import java.util.Optional;
import java.util.UUID;

public interface EconomyService {
    double getBalance(UUID playerId);
    double getBankBalance(UUID playerId);
    Optional<String> getLastTransaction(UUID playerId);
    boolean withdraw(UUID playerId, double amount);
    boolean deposit(UUID playerId, double amount);
    boolean transfer(UUID fromPlayerId, UUID toPlayerId, double amount);
}
