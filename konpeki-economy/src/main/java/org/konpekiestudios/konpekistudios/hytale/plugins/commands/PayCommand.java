//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins.commands;

import org.konpekiestudios.konpekistudios.hytale.plugins.KonpekiEconomy;
import org.konpekiestudios.konpekistudios.hytale.plugins.config.ConfigManager;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import javax.annotation.Nonnull;

public class PayCommand extends CommandBase {
    private final RequiredArg<String> playerArg;
    private final RequiredArg<Float> balanceArg;
    private final ConfigManager configManager;

    public PayCommand(@Nonnull String name, @Nonnull String description) {
        super(name, description);
        this.setPermissionGroup(GameMode.Adventure);
        this.playerArg = this.withRequiredArg("player", "The player to pay", ArgTypes.STRING);
        this.balanceArg = this.withRequiredArg("amount", "The amount to pay", ArgTypes.FLOAT);
        this.configManager = KonpekiEconomy.getInstance().getConfigManager();
    }

    protected void executeSync(@Nonnull CommandContext commandContext) {
        if (commandContext.sender() instanceof Player) {
            try {
                Player player = (Player)commandContext.sender();
                String playerTarget = (String)this.playerArg.get(commandContext);
                float amount = (Float)this.balanceArg.get(commandContext);
                PlayerRef targetRef = Universe.get().getPlayerByUsername(playerTarget, NameMatching.EXACT);
                if (targetRef == null) {
                    commandContext.sender().sendMessage(Message.raw(this.configManager.getData().messages.unknown_player.replace("{balance}", String.valueOf(amount)).replace("{currency}", this.configManager.getData().chat.currency).replace("{max_balance}", String.valueOf(this.configManager.getData().balance.maximum)).replace("{min_balance}", String.valueOf(this.configManager.getData().balance.minimum)).replace("{player}", playerTarget)));
                    return;
                }

                if (targetRef.getUuid() == commandContext.sender().getUuid()) {
                    commandContext.sender().sendMessage(Message.raw(this.configManager.getData().messages.cannot_pay_self.replace("{player}", targetRef.getUsername()).replace("{balance}", String.valueOf(amount)).replace("{currency}", this.configManager.getData().chat.currency).replace("{max_balance}", String.valueOf(this.configManager.getData().balance.maximum)).replace("{min_balance}", String.valueOf(this.configManager.getData().balance.minimum))));
                    return;
                }

                if (KonpekiEconomy.getInstance().getEconomy().getBalance(commandContext.sender().getUuid()) < amount) {
                    commandContext.sender().sendMessage(Message.raw(this.configManager.getData().messages.insufficient_funds.replace("{balance}", String.valueOf(amount)).replace("{player}", targetRef.getUsername()).replace("{currency}", this.configManager.getData().chat.currency).replace("{max_balance}", String.valueOf(this.configManager.getData().balance.maximum)).replace("{min_balance}", String.valueOf(this.configManager.getData().balance.minimum))));
                    return;
                }

                KonpekiEconomy.getInstance().getEconomy().removeBalance(commandContext.sender().getUuid(), amount);
                player.sendMessage(Message.raw(this.configManager.getData().messages.money_sent.replace("{balance}", String.valueOf(amount)).replace("{player}", targetRef.getUsername()).replace("{currency}", this.configManager.getData().chat.currency).replace("{max_balance}", String.valueOf(this.configManager.getData().balance.maximum)).replace("{min_balance}", String.valueOf(this.configManager.getData().balance.minimum))));
                KonpekiEconomy.getInstance().getEconomy().addBalance(targetRef.getUuid(), amount);
                targetRef.sendMessage(Message.raw(this.configManager.getData().messages.money_received.replace("{balance}", String.valueOf(amount)).replace("{player}", targetRef.getUsername()).replace("{currency}", this.configManager.getData().chat.currency).replace("{max_balance}", String.valueOf(this.configManager.getData().balance.maximum)).replace("{min_balance}", String.valueOf(this.configManager.getData().balance.minimum))));
            } catch (Exception e) {
                commandContext.sender().sendMessage(Message.raw(this.configManager.getData().messages.error_occurred));
                throw new RuntimeException(e);
            }
        } else {
            commandContext.sender().sendMessage(Message.raw("This command can only be executed by a player."));
        }

    }
}
