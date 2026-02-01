//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins.commands.admin;

import org.konpekiestudios.konpekistudios.hytale.plugins.commands.admin.eco.AddCommand;
import org.konpekiestudios.konpekistudios.hytale.plugins.commands.admin.eco.ReloadCommand;
import org.konpekiestudios.konpekistudios.hytale.plugins.commands.admin.eco.RemoveCommand;
import org.konpekiestudios.konpekistudios.hytale.plugins.commands.admin.eco.SetCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import javax.annotation.Nonnull;

public class EcoCommand extends AbstractCommandCollection {
    public EcoCommand(@Nonnull String name, @Nonnull String description) {
        super(name, description);
        this.requirePermission("hyeconomy.admin");
        this.addSubCommand(new AddCommand("add", "Add money to a player's balance"));
        this.addSubCommand(new RemoveCommand("remove", "Remove money to a player's balance"));
        this.addSubCommand(new SetCommand("set", "Set money to a player's balance"));
        this.addSubCommand(new ReloadCommand("reload", "Reload the economy configuration"));
    }
}
