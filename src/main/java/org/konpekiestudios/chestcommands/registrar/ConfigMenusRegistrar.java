package org.konpekiestudios.chestcommands.registrar;

import org.konpekiestudios.chestcommands.config.PluginMenuConfig;
import org.konpekiestudios.chestcommands.hytale.ConfigMenuAction;

import java.util.function.BiConsumer;

public class ConfigMenusRegistrar {

    /**
     * registerCommand: BiConsumer<String, Consumer<Object>>
     *   - key: comando (ex: "wallet")
     *   - value: callback que recebe o Player (objeto do Hytale adapter)
     *
     * usage: passe uma função que integra com a API de comandos do Hytale.
     */
    public static void registerAll(PluginMenuConfig cfg,
                                   BiConsumer<String, java.util.function.Consumer<Object>> registerCommand) {
        for (PluginMenuConfig.MenuDef m : cfg.menus.values()) {
            if (m.command == null || m.command.isEmpty()) continue;
            String cmd = m.command.startsWith("/") ? m.command.substring(1) : m.command;
            registerCommand.accept(cmd, player -> {
                new ConfigMenuAction(m, cfg.rows).open(player);
            });
        }
    }
}
