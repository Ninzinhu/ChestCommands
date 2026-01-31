package org.konpekiestudios.chestcommands.core.service;

import org.konpekiestudios.chestcommands.core.menu.Menu;
import org.konpekiestudios.chestcommands.core.parser.MenuParser;
import org.konpekiestudios.chestcommands.core.parser.YamlMenuParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            Path path = Paths.get("plugins/ChestCommands/menus/" + menuId + ".yml"); // Adjust path
            String content = Files.readString(path);
            Menu menu = parser.parse(content);
            loadedMenus.put(menuId, menu);
            return menu;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
