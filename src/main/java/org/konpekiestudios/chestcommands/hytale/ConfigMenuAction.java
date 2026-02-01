package org.konpekiestudios.chestcommands.hytale;

import org.konpekiestudios.chestcommands.config.PluginMenuConfig.MenuDef;
import org.konpekiestudios.chestcommands.config.PluginMenuConfig.ItemDef;
import org.konpekiestudios.chestcommands.hooks.EconomyService;
import org.konpekiestudios.chestcommands.api.CommandDispatcher;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.Map;
import java.util.function.Function;

// ... imports adapt to actual Hytale API in runtime

public class ConfigMenuAction {
    private final MenuDef def;
    private final int defaultRows;
    private final CommandDispatcher dispatcher;
    private static final Optional<EconomyService> ECON = findEconomy();

    public ConfigMenuAction(MenuDef def, int defaultRows, CommandDispatcher dispatcher) {
        this.def = def;
        this.defaultRows = defaultRows;
        this.dispatcher = dispatcher;
    }

    private static Optional<EconomyService> findEconomy() {
        ServiceLoader<EconomyService> loader = ServiceLoader.load(EconomyService.class);
        for (EconomyService s : loader) return Optional.of(s);
        return Optional.empty();
    }

    // This method is intentionally framework-agnostic; adapt Player type in your Hytale adapter
    public void open(Object player) {
        // Fallback textual UI: send the menu as chat messages to the player.
        String title = def.title != null ? def.title : "Menu";
        // Replace basic {player} placeholder using PlayerReflector
        String playerName = PlayerReflector.getName(player);
        title = title.replace("{player}", playerName);

        PlayerReflector.sendMessage(player, "§8[§6" + title + "§8]");
        // show items sorted by slot
        for (Map.Entry<String, ItemDef> e : def.items.entrySet()) {
            String slotKey = e.getKey();
            ItemDef item = e.getValue();
            String line = "Slot " + slotKey + ": ";
            String name = item.name != null ? replacePlaceholders(item.name, player, PlayerReflector::getUniqueId, PlayerReflector::getName) : "";
            line += name;
            PlayerReflector.sendMessage(player, line);
            if (item.lore != null) {
                for (String loreLine : item.lore) {
                    String l = replacePlaceholders(loreLine, player, PlayerReflector::getUniqueId, PlayerReflector::getName);
                    PlayerReflector.sendMessage(player, "  " + l);
                }
            }
            // Attach a hint about action
            if (item.action != null) {
                PlayerReflector.sendMessage(player, "  §7Action: " + item.action);
            }
        }
        PlayerReflector.sendMessage(player, "§8[Use click action handlers in a real adapter to make this interactive]");

        // Note: for actions configured as command:..., we can run them immediately if desired (not auto-run here).
    }

    // Placeholder replacement utility for strings
    public String replacePlaceholders(String s, Object player, Function<Object, UUID> idProvider, Function<Object, String> nameProvider) {
        if (s == null) return "";
        String out = s;
        try {
            UUID id = idProvider.apply(player);
            String pname = nameProvider.apply(player);
            out = out.replace("{player}", pname == null ? "" : pname);
            if (ECON.isPresent()) {
                EconomyService e = ECON.get();
                if (id != null) {
                    out = out.replace("{balance}", String.valueOf(e.getBalance(id)));
                    out = out.replace("{bank}", String.valueOf(e.getBankBalance(id)));
                    out = out.replace("{last_transaction}", e.getLastTransaction(id).orElse("Nenhuma"));
                } else {
                    out = out.replace("{balance}", "0.0");
                    out = out.replace("{bank}", "0.0");
                    out = out.replace("{last_transaction}", "N/A");
                }
            } else {
                out = out.replace("{balance}", "0.0");
                out = out.replace("{bank}", "0.0");
                out = out.replace("{last_transaction}", "N/A");
            }
        } catch (Throwable t) {
            // ignore placeholders on failure
        }
        return out;
    }

    // Helper to run commands using the dispatcher and collect output lines
    public void runCommandAsPlayer(Object player, String command, java.util.function.Consumer<String> outputCollector) {
        if (dispatcher == null) return;
        dispatcher.executeAsPlayer(player, command, outputCollector);
    }

    public void runCommandAsConsole(String command, java.util.function.Consumer<String> outputCollector) {
        if (dispatcher == null) return;
        dispatcher.executeAsConsole(command, outputCollector);
    }
}
