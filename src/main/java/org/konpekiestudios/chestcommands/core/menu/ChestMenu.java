package org.konpekiestudios.chestcommands.core.menu;

import java.util.HashMap;
import java.util.Map;

public class ChestMenu extends Menu {
    // ChestMenu is a specific Menu type (bau) — reuse fields in Menu

    public ChestMenu(String title, int rows) {
        setTitle(title);
        setRows(rows);
        setItems(new HashMap<>());
    }

    // Métodos para adicionar itens, carregar de YAML, etc.
    public void addItem(int slot, MenuItem item) {
        getItems().put(slot, item);
    }

    // Conveniência: expor getters que delegam à classe base
    public String getTitle() { return super.getTitle(); }
    public int getRows() { return super.getRows(); }
    public Map<Integer, MenuItem> getItems() { return super.getItems(); }
}
