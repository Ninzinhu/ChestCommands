package org.konpekiestudios.chestcommands.hytale;

import org.konpekiestudios.chestcommands.config.PluginMenuConfig.MenuDef;
import org.konpekiestudios.chestcommands.config.PluginMenuConfig.ItemDef;
import org.konpekiestudios.chestcommands.hooks.EconomyService;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.Map;

// ... imports adapt to actual Hytale API in runtime

public class ConfigMenuAction {
    private final MenuDef def;
    private final int defaultRows;
    private static final Optional<EconomyService> ECON = findEconomy();

    public ConfigMenuAction(MenuDef def, int defaultRows) {
        this.def = def;
        this.defaultRows = defaultRows;
    }

    private static Optional<EconomyService> findEconomy() {
        ServiceLoader<EconomyService> loader = ServiceLoader.load(EconomyService.class);
        for (EconomyService s : loader) return Optional.of(s);
        return Optional.empty();
    }

    // This method is intentionally framework-agnostic; adapt Player type in your Hytale adapter
    public void open(Object player) {
        // The adapter should cast player to the real Hytale Player and open the menu
        // Here we only prepare data and would call the renderer in the adapter
    }

    // Placeholder replacement utility for strings
    public String replacePlaceholders(String s, Object player, java.util.function.Function<Object, UUID> idProvider, java.util.function.Function<Object, String> nameProvider) {
        if (s == null) return "";
        String out = s;
        try {
            UUID id = idProvider.apply(player);
            String pname = nameProvider.apply(player);
            out = out.replace("{player}", pname == null ? "" : pname);
            if (ECON.isPresent()) {
                EconomyService e = ECON.get();
                out = out.replace("{balance}", String.valueOf(e.getBalance(id)));
                out = out.replace("{bank}", String.valueOf(e.getBankBalance(id)));
                out = out.replace("{last_transaction}", e.getLastTransaction(id).orElse("Nenhuma"));
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
}
