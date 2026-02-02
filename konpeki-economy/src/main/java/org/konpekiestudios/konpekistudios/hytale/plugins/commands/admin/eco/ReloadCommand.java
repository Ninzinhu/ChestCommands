//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins.commands.admin.eco;

import org.konpekiestudios.konpekistudios.hytale.plugins.KonpekiEconomy;
import org.konpekiestudios.konpekistudios.hytale.plugins.config.ConfigManager;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class ReloadCommand extends AbstractAsyncCommand {
    private final ConfigManager configManager;

    public ReloadCommand(@Nonnull String name, @Nonnull String description) {
        super(name, description);
        this.addAliases(new String[]{"rel", "r"});
        this.configManager = KonpekiEconomy.getInstance().getConfigManager();
    }

    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
        try {
            this.configManager.load();
            commandContext.sender().sendMessage(Message.raw("Configuration reloaded successfully."));
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            commandContext.sender().sendMessage(Message.raw(this.configManager.getData().messages.error_occurred));
            throw new RuntimeException(e);
        }
    }
}
