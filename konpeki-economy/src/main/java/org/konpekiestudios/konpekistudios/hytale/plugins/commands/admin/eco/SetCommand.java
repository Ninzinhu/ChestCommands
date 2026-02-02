//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins.commands.admin.eco;

import org.konpekiestudios.konpekistudios.hytale.plugins.KonpekiEconomy;
import org.konpekiestudios.konpekistudios.hytale.plugins.config.ConfigManager;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class SetCommand extends AbstractAsyncCommand {
    private final RequiredArg<PlayerRef> playerName;
    private final RequiredArg<Float> amount;
    private final ConfigManager configManager = KonpekiEconomy.getInstance().getConfigManager();

    public SetCommand(@Nonnull String name, @Nonnull String description) {
        super(name, description);
        this.playerName = (RequiredArg)this.withRequiredArg("player", "The player to set money to", ArgTypes.PLAYER_REF).suggest((sender, text, paramCount, result) -> {
            for(PlayerRef playerRef : Universe.get().getPlayers()) {
                if (playerRef.getUsername().toLowerCase().startsWith(text.toLowerCase())) {
                    result.suggest(playerRef);
                }
            }

        });
        this.amount = this.withRequiredArg("amount", "The amount of money to set", ArgTypes.FLOAT);
    }

    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
        try {
            PlayerRef targetPlayer = (PlayerRef)this.playerName.get(commandContext);
            float amountToSet = (Float)this.amount.get(commandContext);
            float currentPlayerBalance = KonpekiEconomy.getInstance().getEconomy().getBalance(targetPlayer.getUuid());
            if (amountToSet < this.configManager.getData().balance.minimum) {
                commandContext.sender().sendMessage(Message.raw(this.configManager.getData().messages.min_balance_reached.replace("{player}", targetPlayer.getUsername()).replace("{balance}", String.valueOf(amountToSet)).replace("{currency}", this.configManager.getData().chat.currency).replace("{min_balance}", String.valueOf(this.configManager.getData().balance.minimum))));
                return CompletableFuture.completedFuture(null);
            } else if (amountToSet > this.configManager.getData().balance.maximum) {
                commandContext.sender().sendMessage(Message.raw(this.configManager.getData().messages.max_balance_reached.replace("{player}", targetPlayer.getUsername()).replace("{balance}", String.valueOf(amountToSet)).replace("{currency}", this.configManager.getData().chat.currency).replace("{max_balance}", String.valueOf(this.configManager.getData().balance.maximum))));
                return CompletableFuture.completedFuture(null);
            } else {
                KonpekiEconomy.getInstance().getEconomy().setBalance(targetPlayer.getUuid(), amountToSet);
                commandContext.sender().sendMessage(Message.raw(this.configManager.getData().messages.admin_set_success.replace("{player}", targetPlayer.getUsername()).replace("{balance}", String.valueOf(amountToSet)).replace("{currency}", this.configManager.getData().chat.currency)));
                return CompletableFuture.completedFuture(null);
            }
        } catch (Exception e) {
            commandContext.sender().sendMessage(Message.raw(this.configManager.getData().messages.error_occurred));
            throw new RuntimeException(e);
        }
    }
}
