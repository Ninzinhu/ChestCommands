package org.konpekiestudios.chestcommands.core.menu;

import org.konpekiestudios.chestcommands.api.Action;
import org.konpekiestudios.chestcommands.api.Condition;

import java.util.List;

public class MenuItem {
    private String id;
    private String icon;
    private String displayName;
    private List<Action> actions;
    private List<Condition> conditions;

    // Getters e setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public List<Action> getActions() { return actions; }
    public void setActions(List<Action> actions) { this.actions = actions; }
    public List<Condition> getConditions() { return conditions; }
    public void setConditions(List<Condition> conditions) { this.conditions = conditions; }
}
