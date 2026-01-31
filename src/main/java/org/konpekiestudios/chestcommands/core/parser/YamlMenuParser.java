package org.konpekiestudios.chestcommands.core.parser;

import org.konpekiestudios.chestcommands.core.action.ActionRegistry;
import org.konpekiestudios.chestcommands.core.condition.ConditionRegistry;
import org.konpekiestudios.chestcommands.core.menu.Menu;
import org.konpekiestudios.chestcommands.core.menu.MenuItem;
import org.konpekiestudios.chestcommands.api.Action;
import org.konpekiestudios.chestcommands.api.Condition;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlMenuParser implements MenuParser {
    private final Yaml yaml = new Yaml();

    @Override
    public Menu parse(String content) {
        Map<String, Object> data = yaml.load(content);
        Menu menu = new Menu();
        menu.setId((String) data.get("id"));
        menu.setTitle((String) data.get("title"));
        menu.setRows((Integer) data.get("rows"));

        Map<String, Object> itemsData = (Map<String, Object>) data.get("items");
        Map<Integer, MenuItem> items = new HashMap<>();
        if (itemsData != null) {
            for (Map.Entry<String, Object> entry : itemsData.entrySet()) {
                Map<String, Object> itemData = (Map<String, Object>) entry.getValue();
                MenuItem item = new MenuItem();
                item.setId(entry.getKey());
                item.setIcon((String) itemData.get("icon"));
                item.setDisplayName((String) itemData.get("name"));
                int slot = (Integer) itemData.get("slot");
                items.put(slot, item);

                // Parse actions
                List<Map<String, Object>> actionsData = (List<Map<String, Object>>) itemData.get("actions");
                List<Action> actions = new ArrayList<>();
                if (actionsData != null) {
                    for (Map<String, Object> actionData : actionsData) {
                        for (Map.Entry<String, Object> actionEntry : actionData.entrySet()) {
                            String actionType = actionEntry.getKey();
                            String value = (String) actionEntry.getValue();
                            Action action = ActionRegistry.create(actionType, new ActionContext() {
                                @Override
                                public org.konpekiestudios.chestcommands.api.PlayerRef getPlayer() { return null; }
                                @Override
                                public String getValue() { return value; }
                            });
                            if (action != null) actions.add(action);
                        }
                    }
                }
                item.setActions(actions);

                // Parse conditions similarly
                List<Condition> conditions = new ArrayList<>();
                // Implement if needed
                item.setConditions(conditions);
            }
        }
        menu.setItems(items);
        return menu;
    }
}
