//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins;

import org.konpekiestudios.konpekistudios.hytale.plugins.KonpekiEcoAPI.Service;
import org.konpekiestudios.konpekistudios.hytale.plugins.commands.BalanceCommand;
import org.konpekiestudios.konpekistudios.hytale.plugins.commands.BaltopCommand;
import org.konpekiestudios.konpekistudios.hytale.plugins.commands.PayCommand;
import org.konpekiestudios.konpekistudios.hytale.plugins.commands.admin.EcoCommand;
import org.konpekiestudios.konpekistudios.hytale.plugins.config.ConfigManager;
import org.konpekiestudios.konpekistudios.hytale.plugins.events.PlayerJoin;
import org.konpekiestudios.konpekistudios.hytale.plugins.events.PlayerLeave;
import org.konpekiestudios.konpekistudios.hytale.plugins.json.PlayerManager;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import javax.annotation.Nonnull;

public class KonpekiEconomy extends JavaPlugin {
    private ConfigManager configManager;
    private static KonpekiEconomy instance = null;
    private final PlayerManager playerManager;
    private final Economy economy;

    public KonpekiEconomy(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        this.configManager = new ConfigManager();
        this.configManager.setup();
        this.playerManager = new PlayerManager(this.configManager.getData());
        this.economy = new Economy(this.playerManager, this.configManager);
    }

    protected void setup() {
        Service.setInstance(this.economy);
        new PlayerJoin();
        new PlayerLeave();
        this.getCommandRegistry().registerCommand(new BalanceCommand("balance", "Check your balance"));
        this.getCommandRegistry().registerCommand(new PayCommand("pay", "Pay another player"));
        this.getCommandRegistry().registerCommand(new BaltopCommand("baltop", "View top player balances"));
        this.getCommandRegistry().registerCommand(new EcoCommand("eco", "Admin economy commands"));
        ((HytaleLogger.Api)this.getLogger().atInfo()).log("KonpekiEconomy plugin has been initialized.");
    }

    protected void shutdown() {
        ((HytaleLogger.Api)this.getLogger().atInfo()).log("KonpekiEconomy plugin is shutting down.");
        this.playerManager.unloadAll();
    }

    public static KonpekiEconomy getInstance() {
        return instance;
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public Economy getEconomy() {
        return this.economy;
    }
}
