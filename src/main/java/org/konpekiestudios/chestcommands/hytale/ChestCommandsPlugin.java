package org.konpekiestudios.chestcommands.hytale;

import org.konpekiestudios.chestcommands.core.action.ActionRegistry;
import org.konpekiestudios.chestcommands.core.action.OpenMenuAction;
import org.konpekiestudios.chestcommands.core.action.GiveItemAction;
import org.konpekiestudios.chestcommands.core.condition.ConditionRegistry;
import org.konpekiestudios.chestcommands.core.condition.HasPermissionCondition;
import org.konpekiestudios.chestcommands.core.service.MenuService;
import org.konpekiestudios.chestcommands.core.service.MenuLoader;
import org.konpekiestudios.chestcommands.core.menu.Menu;
import org.konpekiestudios.chestcommands.core.menu.ChestMenu;
import org.konpekiestudios.chestcommands.api.ActionContext;
import org.konpekiestudios.chestcommands.api.ChestCommandsAPI;
import org.konpekiestudios.chestcommands.api.CommandDispatcher;
import org.konpekiestudios.chestcommands.config.PluginMenuConfig;
import org.konpekiestudios.chestcommands.registrar.ConfigMenusRegistrar;

// Importar classes do Hytale, como Plugin, Server, etc.
// Assume Player is from Hytale API
import com.hypixel.hytale.EntityStore;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ChestCommandsPlugin extends JavaPlugin implements ChestCommandsAPI { // implements Plugin ou similar
    private static ChestCommandsPlugin instance;
    private HytaleMenuRenderer renderer;
    private MenuService menuService;
    private MenuLoader menuLoader;

    private PluginMenuConfig config;
    private final Map<String, org.konpekiestudios.chestcommands.config.PluginMenuConfig.MenuDef> commandToMenu = new HashMap<>();

    private CommandDispatcher dispatcher;

    public ChestCommandsPlugin() {
        this(new JavaPluginInit()); // Call the other constructor with a dummy init
    }

    // Constructor required by Hytale plugin loader
    public ChestCommandsPlugin(JavaPluginInit init) {
        super(init); // Call super with init
        instance = this;
    }

    public static ChestCommandsAPI getAPI() {
        return instance;
    }

    public static ChestCommandsPlugin getInstance() {
        return instance;
    }

    @Override
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

        // Use reflective dispatcher
        dispatcher = new ReflectiveCommandDispatcher();

        try {
            // Assume data folder is current or configurable
            File dataFolder = new File("."); // Adjust as needed
            config = PluginMenuConfig.load(dataFolder);
            // For now, no command registration in onEnable; handle in adapter
            // ConfigMenusRegistrar.registerAll(config, registerCommandFn, dispatcher);
            // build a command->menu map for quick lookup
            for (PluginMenuConfig.MenuDef m : config.menus.values()) {
                if (m.command != null) {
                    String key = m.command.startsWith("/") ? m.command.substring(1) : m.command;
                    commandToMenu.put(key, m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Registrar eventos
    }

    @Override
    public void onDisable() {
        // Limpeza
    }

    // Método chamado pelo adapter Hytale quando um comando registrado é executado
    public void handleCommand(Object player, String command) {
        if (command == null) return;
        String key = command.startsWith("/") ? command.substring(1) : command;
        var menuDef = commandToMenu.get(key);
        if (menuDef != null) {
            // abrir menu via ConfigMenuAction (adapter deve implementar open(player))
            new ConfigMenuAction(menuDef, config != null ? config.rows : 5, dispatcher).open(player);
        }
    }

    // Método para abrir menu - adaptadores Hytale podem usar este método
    public void openMenu(Object player, String menuId) { // Use Object for now, replace with Player
        HytalePlayerRef playerRef = new HytalePlayerRef(player);
        // Load menu and open
        Menu menu = loadMenu(menuId);
        if (menu != null) {
            // renderer.open(player, menu);
        }
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
