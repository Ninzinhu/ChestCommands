package org.konpekiestudios.chestcommands.hytale;

import com.hypixel.hytale.Action;
import com.hypixel.hytale.Role;
import com.hypixel.hytale.Ref;
import com.hypixel.hytale.EntityStore;
import org.konpekiestudios.chestcommands.core.menu.ChestMenu;

public class OpenChestMenuAction extends Action {
    private final BuilderActionOpenChestMenu builder;
    private final String menuId; // Parâmetro opcional para ID do menu

    public OpenChestMenuAction(BuilderActionOpenChestMenu builder) {
        this.builder = builder;
        this.menuId = "default"; // Pode ser passado via definição de interação, similar a BuilderActionOpenShop
    }

    @Override
    public void execute(Role role) {
        Ref<EntityStore> playerReference = role.getStateSupport().getInteractionIterationTarget();
        if (playerReference != null) {
            EntityStore player = playerReference.get();
            ChestCommandsPlugin plugin = ChestCommandsPlugin.getInstance();
            ChestMenu menu = plugin.loadMenu(menuId); // Carrega menu de arquivo YAML
            if (menu != null) {
                plugin.openChestUI(player, menu); // Abre a UI de baú
            }
        }
    }
}
