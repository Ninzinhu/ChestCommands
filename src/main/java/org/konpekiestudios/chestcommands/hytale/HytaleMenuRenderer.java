package org.konpekiestudios.chestcommands.hytale;

import org.konpekiestudios.chestcommands.core.menu.Menu;
import org.konpekiestudios.chestcommands.core.menu.ChestMenu;
// Importar classes do Hytale, como Player e UI
import com.hypixel.hytale.EntityStore;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.Map;

public class HytaleMenuRenderer {
    public void open(EntityStore player, Menu menu) { // Use EntityStore for player
        // For now, delegate to ChestMenu if possible
        if (menu instanceof ChestMenu) {
            open(player, (ChestMenu) menu);
        } else {
            // Fallback: send message
            try {
                Method sendMessage = player.getClass().getMethod("sendMessage", String.class);
                sendMessage.invoke(player, "Menu opened: " + menu.getTitle());
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public void open(EntityStore player, ChestMenu chestMenu) { // Método sobrecarregado para ChestMenu
        try {
            // Try to create and open a chest UI using reflection
            // Assume classes like ItemContainerWindow or Window exist
            Class<?> windowClass = Class.forName("com.hypixel.hytale.server.core.ui.ItemContainerWindow", false, player.getClass().getClassLoader());
            Constructor<?> ctor = windowClass.getConstructor(String.class, int.class); // title, rows
            Object window = ctor.newInstance(chestMenu.getTitle(), chestMenu.getRows());

            // Fill slots
            Map<Integer, org.konpekiestudios.chestcommands.core.menu.MenuItem> items = chestMenu.getItems();
            for (Map.Entry<Integer, org.konpekiestudios.chestcommands.core.menu.MenuItem> entry : items.entrySet()) {
                int slot = entry.getKey();
                org.konpekiestudios.chestcommands.core.menu.MenuItem item = entry.getValue();
                // Assume setItem method
                Method setItem = windowClass.getMethod("setItem", int.class, Object.class); // slot, item
                // For now, create a dummy item or use reflection to create ItemStack
                // This is placeholder; real implementation needs Item creation
                Object dummyItem = createDummyItem(item);
                setItem.invoke(window, slot, dummyItem);
            }

            // Open the window for the player
            Method openUI = player.getClass().getMethod("openUI", windowClass);
            openUI.invoke(player, window);

        } catch (Exception e) {
            // Fallback to textual UI
            try {
                Method sendMessage = player.getClass().getMethod("sendMessage", String.class);
                sendMessage.invoke(player, "§cFailed to open UI, using text fallback.");
                // Call the textual open from ConfigMenuAction
                // But since we don't have access, send basic info
                sendMessage.invoke(player, "Menu: " + chestMenu.getTitle());
                for (Map.Entry<Integer, org.konpekiestudios.chestcommands.core.menu.MenuItem> entry : chestMenu.getItems().entrySet()) {
                    sendMessage.invoke(player, "Slot " + entry.getKey() + ": " + entry.getValue().getDisplayName());
                }
            } catch (Exception ex) {
                // ignore
            }
        }
    }

    private Object createDummyItem(org.konpekiestudios.chestcommands.core.menu.MenuItem item) {
        // Placeholder: try to create an ItemStack or similar
        try {
            Class<?> itemClass = Class.forName("com.hypixel.hytale.server.core.item.ItemStack", false, Thread.currentThread().getContextClassLoader());
            Constructor<?> ctor = itemClass.getConstructor(String.class, int.class); // material, amount
            // Assume item has material and amount
            return ctor.newInstance(item.getIcon(), 1); // placeholder
        } catch (Exception e) {
            return null;
        }
    }

    // Método para mapear slot lógico para slot real
    public int mapToSlot(int eventSlot) {
        return eventSlot; // Placeholder
    }
}
