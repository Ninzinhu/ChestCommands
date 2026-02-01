package org.konpekiestudios.chestcommands.hytale;

import org.konpekiestudios.chestcommands.core.action.ActionRegistry;
import org.konpekiestudios.chestcommands.core.action.OpenMenuAction;
import org.konpekiestudios.chestcommands.core.action.GiveItemAction;
import org.konpekiestudios.chestcommands.core.condition.ConditionRegistry;
import org.konpekiestudios.chestcommands.core.service.MenuService;
import org.konpekiestudios.chestcommands.core.service.MenuLoader;
import org.konpekiestudios.chestcommands.core.menu.Menu;
import org.konpekiestudios.chestcommands.core.menu.ChestMenu;
import org.konpekiestudios.chestcommands.api.ActionContext;
import org.konpekiestudios.chestcommands.api.ChestCommandsAPI;

// Importar classes do Hytale, como Plugin, Server, etc.
// Assume Player is from Hytale API

public class ChestCommandsPlugin implements ChestCommandsAPI { // implements Plugin ou similar
    private static ChestCommandsPlugin instance;
    private HytaleMenuRenderer renderer;
    private MenuService menuService;
    private MenuLoader menuLoader;

    public ChestCommandsPlugin() {
        instance = this;
    }

    public static ChestCommandsAPI getAPI() {
        return instance;
    }

    public static ChestCommandsPlugin getInstance() {
        return instance;
    }

    public void onEnable() {
        // Inicializar serviços
        renderer = new HytaleMenuRenderer();
        menuService = new MenuService();
        menuLoader = new MenuLoader();
        // Registrar ações e condições padrão
        ActionRegistry.register("open_menu", ctx -> new OpenMenuAction(ctx.getValue()));
        ActionRegistry.register("give_item", ctx -> {
            String[] parts = ctx.getValue().split(":");
            return new GiveItemAction(parts[0], Integer.parseInt(parts[1]));
        });
        ConditionRegistry.register("permission", ctx -> new HasPermissionCondition(ctx.getValue()));
        // Registrar eventos
    }

    public void onDisable() {
        // Limpeza
    }

    // Método para abrir menu
    public void openMenu(Object player, String menuId) { // Use Object for now, replace with Player
        HytalePlayerRef playerRef = new HytalePlayerRef(player);
        // Load menu and open
        // Menu menu = loadMenu(menuId);
        // renderer.open(player, menu);
    }

    @Override
    public void openMenu(org.konpekiestudios.chestcommands.api.PlayerRef player, String menuId) {
        Menu menu = menuLoader.loadMenu(menuId);
        if (menu != null) {
            // renderer.open(player, menu); // Need to cast or adjust
        }
    }

    @Override
    public void registerAction(String id, java.util.function.Function<ActionContext, org.konpekiestudios.chestcommands.api.Action> factory) {
        ActionRegistry.register(id, factory);
    }

    @Override
    public void registerCondition(String id, java.util.function.Function<ActionContext, org.konpekiestudios.chestcommands.api.Condition> factory) {
        ConditionRegistry.register(id, factory);
    }

    // Novo método para carregar ChestMenu
    public ChestMenu loadMenu(String menuId) {
        // Implementar carregamento de YAML para ChestMenu
        // Por exemplo, usar YamlMenuParser para carregar
        // Retornar null se não encontrado
        return null; // Placeholder
    }

    // Novo método para abrir UI de baú
    public void openChestUI(EntityStore player, ChestMenu menu) {
        // Usar renderer para abrir a UI
        renderer.open(player, menu);
    }
}
