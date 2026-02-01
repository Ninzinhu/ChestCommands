package org.konpekiestudios.chestcommands.core.menu;

import java.util.HashMap;
import java.util.Map;

public class ChestMenu {
    private String title;
    private int rows;
    private Map<Integer, MenuItem> items; // Itens no baú, indexados por slot

    public ChestMenu(String title, int rows) {
        this.title = title;
        this.rows = rows;
        this.items = new HashMap<>();
    }

    // Métodos para adicionar itens, carregar de YAML, etc.
    public void addItem(int slot, MenuItem item) {
        items.put(slot, item);
    }

    public String getTitle() { return title; }
    public int getRows() { return rows; }
    public Map<Integer, MenuItem> getItems() { return items; }
}
