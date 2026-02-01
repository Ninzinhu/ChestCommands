package org.konpekiestudios.chestcommands.registrar;

import org.konpekiestudios.chestcommands.config.PluginMenuConfig;
import org.konpekiestudios.chestcommands.hytale.ConfigMenuAction;
import org.konpekiestudios.chestcommands.api.CommandDispatcher;

import java.util.function.BiConsumer;

public class ConfigMenusRegistrar {

    /**
     * registerAll: registra todos os menus do config
     * registerCommandFn: BiConsumer<String, Consumer<Object>> -> função para registrar comandos no servidor
     * commandDispatcher: abstração para executar comandos sem tocar outros plugins
     */
    public static void registerAll(PluginMenuConfig cfg,
                                   BiConsumer<String, java.util.function.Consumer<Object>> registerCommandFn,
                                   CommandDispatcher commandDispatcher) {
        for (PluginMenuConfig.MenuDef m : cfg.menus.values()) {
            if (m.command == null || m.command.isEmpty()) continue;
            String cmd = m.command.startsWith("/") ? m.command.substring(1) : m.command;
            registerCommandFn.accept(cmd, player -> {
                // Quando o comando é executado pelo jogador, abrimos o menu configurado
                new ConfigMenuAction(m, cfg.rows, commandDispatcher).open(player);
            });
        }
    }
}
