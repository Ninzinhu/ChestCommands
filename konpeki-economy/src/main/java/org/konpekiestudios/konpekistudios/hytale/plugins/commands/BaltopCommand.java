//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins.commands;

import org.konpekiestudios.konpekistudios.hytale.plugins.KonpekiEconomy;
import org.konpekiestudios.konpekistudios.hytale.plugins.config.ConfigManager;
import org.konpekiestudios.konpekistudios.hytale.plugins.json.UserProfile;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;

public class BaltopCommand extends CommandBase {
    private final ConfigManager configManager;
    private static final int PAGE_SIZE = 10;

    public BaltopCommand(@Nonnull String name, @Nonnull String description) {
        super(name, description);
        this.addAliases(new String[]{"baltop", "moneytop"});
        this.setPermissionGroup(GameMode.Adventure);
        this.setAllowsExtraArguments(true);
        this.configManager = KonpekiEconomy.getInstance().getConfigManager();
    }

    protected void executeSync(@Nonnull CommandContext commandContext) {
        try {
            int page = 1;
            String[] args = commandContext.getInputString().split("\\s+");
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                    if (page < 1) {
                        page = 1;
                    }
                } catch (NumberFormatException var13) {
                    commandContext.sender().sendMessage(Message.raw("Invalid page number. Usage: /baltop [page]"));
                    return;
                }
            }

            int offset = (page - 1) * 10;
            List<UserProfile> topBalances = KonpekiEconomy.getInstance().getPlayerManager().getTopBalances(10, offset);
            if (topBalances.isEmpty()) {
                commandContext.sender().sendMessage(Message.raw("No players found on page " + page + "."));
                return;
            }

            StringBuilder message = new StringBuilder();
            message.append("=== Top Balances (Page ").append(page).append(") ===\n");

            for(int i = 0; i < topBalances.size(); ++i) {
                UserProfile profile = (UserProfile)topBalances.get(i);
                int rank = offset + i + 1;
                String formattedBalance = (new BigDecimal((double)profile.balance)).setScale(this.configManager.getData().chat.decimal_places, RoundingMode.DOWN).toString();
                UUID uuid = UUID.fromString(profile.uuid);
                String playerName = this.getPlayerName(uuid);
                message.append(rank).append(". ").append(playerName).append(": ").append(this.configManager.getData().chat.currency).append(formattedBalance).append("\n");
            }

            message.append("\nUse /baltop ").append(page + 1).append(" to see the next page.");
            commandContext.sender().sendMessage(Message.raw(message.toString()));
        } catch (Exception e) {
            commandContext.sender().sendMessage(Message.raw(this.configManager.getData().messages.error_occurred));
            ((HytaleLogger.Api)KonpekiEconomy.getInstance().getLogger().atSevere()).log("Error executing baltop command", e);
        }

    }

    private String getPlayerName(UUID uuid) {
        try {
            for(PlayerRef player : Universe.get().getPlayers()) {
                if (player.getUuid().equals(uuid)) {
                    return player.getUsername();
                }
            }
        } catch (Exception var5) {
            ((HytaleLogger.Api)KonpekiEconomy.getInstance().getLogger().atWarning()).log("Could not get player name for UUID: " + String.valueOf(uuid));
        }

        return uuid.toString().substring(0, 8);
    }
}
