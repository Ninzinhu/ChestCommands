package org.konpekiestudios.chestcommands.hytale;

import org.konpekiestudios.chestcommands.core.menu.Menu;
import org.konpekiestudios.chestcommands.core.menu.ChestMenu;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HytaleMenuRenderer {
    private final Logger logger = Logger.getLogger("ChestCommands");

    private final List<String> candidateWindows = Arrays.asList(
            "com.hypixel.hytale.server.core.ui.ItemContainerWindow",
            "com.hypixel.hytale.server.core.ui.window.ItemContainerWindow",
            "com.hypixel.hytale.server.core.ui.window.Window",
            "com.hypixel.hytale.server.core.inventory.container.ItemContainer",
            "com.hypixel.hytale.server.core.ui.ItemContainer"
    );

    public void open(Object player, Menu menu) {
        try {
            if (player == null) {
                logger.info("[ChestCommands] open called with null player");
                return;
            }

            // Try to build a simple chest-like container if menu is null
            if (menu == null) {
                ChestMenu cm = new ChestMenu("Test Chest", 3);
                cm.setId("test");
                cm.setTitle("Test Chest");
                cm.setRows(3);
                // pass this to the reflective open below
                openWindowReflective(player, cm);
                return;
            }

            openWindowReflective(player, menu);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to open menu via reflection", e);
        }
    }

    private void openWindowReflective(Object player, Menu menu) throws Exception {
        Class<?> playerClass = player.getClass();

        for (String fqcn : candidateWindows) {
            try {
                Class<?> windowClass = Class.forName(fqcn);
                // try constructors: (String title, int rows) or (Menu)
                Object windowInstance = null;
                try {
                    Constructor<?> c = windowClass.getConstructor(String.class, int.class);
                    windowInstance = c.newInstance(menu.getTitle(), menu.getRows());
                } catch (NoSuchMethodException ignored) {
                    try {
                        Constructor<?> c2 = windowClass.getConstructor(Menu.class);
                        windowInstance = c2.newInstance(menu);
                    } catch (NoSuchMethodException ignored2) {
                        try {
                            Constructor<?> c3 = windowClass.getConstructor();
                            windowInstance = c3.newInstance();
                        } catch (NoSuchMethodException ignored3) {
                            // cannot instantiate this window class
                        }
                    }
                }

                if (windowInstance == null) continue;

                // try player open method variants
                String[] openNames = new String[]{"openUI", "openWindow", "openInventory", "openContainer", "showWindow"};
                boolean opened = false;
                for (String openName : openNames) {
                    try {
                        Method open = playerClass.getMethod(openName, windowInstance.getClass());
                        open.invoke(player, windowInstance);
                        logger.info("[ChestCommands] Opened window using " + fqcn + " via player method " + openName);
                        opened = true;
                        break;
                    } catch (NoSuchMethodException ignored) {
                    }
                }

                if (opened) return;

            } catch (ClassNotFoundException ignored) {
                // try next fqcn
            }
        }

        throw new IllegalStateException("No candidate window type available to open");
    }

    public void safeSendMessage(Object player, String message) {
        if (player == null) return;
        try {
            Method m = player.getClass().getMethod("sendChatMessage", String.class);
            m.invoke(player, message);
            return;
        } catch (Exception ignored) {
        }
        try {
            Method m = player.getClass().getMethod("sendSystemMessage", String.class);
            m.invoke(player, message);
            return;
        } catch (Exception ignored) {
        }
        logger.info("[ChestCommands] (fallback) player message: " + message);
    }
}
