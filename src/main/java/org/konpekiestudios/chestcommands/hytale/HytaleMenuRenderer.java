package org.konpekiestudios.chestcommands.hytale;

import org.konpekiestudios.chestcommands.core.menu.Menu;
import org.konpekiestudios.chestcommands.core.menu.ChestMenu;
// Importar classes do Hytale, como Player e UI
import com.hypixel.hytale.api.EntityStore;

public class HytaleMenuRenderer {
    public void open(EntityStore player, Menu menu) { // Use EntityStore for player
        // Construir UI real do Hytale e abrir
        // UI ui = buildUI(menu);
        // player.openUI(ui);
    }

    public void open(EntityStore player, ChestMenu chestMenu) { // Método sobrecarregado para ChestMenu
        // Lógica para abrir ChestMenu
    }

    // Método para mapear slot lógico para slot real
    public int mapToSlot(int eventSlot) {
        return eventSlot; // Placeholder
    }
}
