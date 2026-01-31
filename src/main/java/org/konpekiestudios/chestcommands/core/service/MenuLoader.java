package org.konpekiestudios.chestcommands.core.service;

import org.konpekiestudios.chestcommands.core.menu.Menu;
import org.konpekiestudios.chestcommands.core.parser.MenuParser;
import org.konpekiestudios.chestcommands.core.parser.YamlMenuParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MenuLoader {
    private final MenuParser parser = new YamlMenuParser();
    private final Map<String, Menu> loadedMenus = new HashMap<>();

    public Menu loadMenu(String menuId) {
        if (loadedMenus.containsKey(menuId)) {
            return loadedMenus.get(menuId);
        }
        try {
            // Use class loader to load from resources
            String resourcePath = "menus/" + menuId + ".yml";
            var inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                System.err.println("Menu file not found: " + resourcePath);
                return null;
            }
            String content = new String(inputStream.readAllBytes());
            Menu menu = parser.parse(content);
            loadedMenus.put(menuId, menu);
            return menu;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
