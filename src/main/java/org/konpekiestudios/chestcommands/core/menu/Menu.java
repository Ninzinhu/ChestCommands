package org.konpekiestudios.chestcommands.core.menu;

import java.util.Map;
import org.konpekiestudios.chestcommands.core.menu.MenuItem;

public class Menu {
    private String id;
    private String title;
    private int rows;
    private Map<Integer, MenuItem> items;

    // Getters e setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }
    public Map<Integer, MenuItem> getItems() { return items; }
    public void setItems(Map<Integer, MenuItem> items) { this.items = items; }
}
