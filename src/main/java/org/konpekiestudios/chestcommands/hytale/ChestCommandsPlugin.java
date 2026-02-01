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
        System.out.println("[ChestCommands] Plugin onEnable chamado!");
        // Cria a pasta ChestCommandsConfig se não existir
        File configDir = new File("ChestCommandsConfig");
        System.out.println("ChestCommands: Creating ChestCommandsConfig folder at " + configDir.getAbsolutePath());
        if (!configDir.exists()) {
            boolean created = configDir.mkdirs();
            if (created) {
                System.out.println("ChestCommands: Folder created successfully.");
            } else {
                System.out.println("ChestCommands: Failed to create folder. Please create it manually.");
            }
        } else {
            System.out.println("ChestCommands: Folder already exists.");
        }

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

        // Carregar todos os menus de arquivos .yml na pasta
        commandToMenu.clear();
        config = new PluginMenuConfig();
        File[] files = configDir.listFiles((dir, name) -> name.endsWith(".yml") || name.endsWith(".yaml"));
        if (files != null) {
            for (File file : files) {
                try (java.io.FileInputStream in = new java.io.FileInputStream(file)) {
                    org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
                    Map<String, Object> m = yaml.load(in);
                    PluginMenuConfig.MenuDef md = new PluginMenuConfig.MenuDef();
                    String menuId = file.getName().replaceFirst("\\.ya?ml$", "");
                    md.id = menuId;
                    md.command = asString(m.get("command"));
                    md.title = asString(m.get("title"));
                    if (m.get("rows") instanceof Number) md.rows = ((Number) m.get("rows")).intValue();
                    Object itemsObj = m.get("items");
                    if (itemsObj instanceof Map) {
                        Map<String, Object> items = (Map<String, Object>) itemsObj;
                        for (Map.Entry<String, Object> it : items.entrySet()) {
                            String slot = it.getKey();
                            Object iv = it.getValue();
                            if (!(iv instanceof Map)) continue;
                            Map<String, Object> im = (Map<String, Object>) iv;
                            PluginMenuConfig.ItemDef id = new PluginMenuConfig.ItemDef();
                            id.material = asString(im.get("material"));
                            id.name = asString(im.get("name"));
                            Object lore = im.get("lore");
                            if (lore instanceof java.util.List) id.lore = (java.util.List<String>) lore;
                            id.action = asString(im.get("action"));
                            md.items.put(slot, id);
                        }
                    }
                    config.menus.put(menuId, md);
                    if (md.command != null) {
                        String key = md.command.startsWith("/") ? md.command.substring(1) : md.command;
                        commandToMenu.put(key, md);
                    }
                    System.out.println("ChestCommands: Loaded menu '" + menuId + "' from " + file.getName());
                } catch (Exception e) {
                    System.out.println("ChestCommands: Failed to load menu from " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        // Registrar comandos dinamicamente após carregar menus
        for (String key : commandToMenu.keySet()) {
            try {
                dispatcher.registerCommand(key, (player, args) -> {
                    sendChat(player, "[ChestCommands] Interceptando comando: " + key + " para player: " + player);
                    handleCommand(player, key);
                });
                System.out.println("[ChestCommands] Comando registrado: /" + key);
            } catch (Exception e) {
                System.out.println("[ChestCommands] Falha ao registrar comando: /" + key + " - " + e.getMessage());
            }
        }
        // Teste: registrar comando de debug
        dispatcher.registerCommand("testchest", (player, args) -> {
            sendChat(player, "[ChestCommands] Comando /testchest interceptado para player: " + player);
        });
        // Registrar comando de teste para abrir baú vazio
        dispatcher.registerCommand("testchestui", (player, args) -> {
            sendChat(player, "[ChestCommands] Comando /testchestui interceptado para player: " + player);
            ChestMenu testMenu = new ChestMenu("Teste Baú", 3); // 3 linhas, vazio
            try {
                renderer.open((EntityStore) player, testMenu);
                sendChat(player, "[ChestCommands] renderer.open chamado para player: " + player + ", menu: " + testMenu.getTitle());
            } catch (Exception e) {
                sendChat(player, "[ChestCommands] Erro ao abrir baú de teste: " + e.getMessage());
            }
        });
        // Registrar eventos
    }

    @Override
    public void onDisable() {
        // Limpeza
    }

    // Método chamado pelo adapter Hytale quando um comando registrado é executado
    public void handleCommand(Object player, String command) {
        sendChat(player, "[ChestCommands] handleCommand chamado para comando: " + command + ", player: " + player);
        if (command == null) {
            sendChat(player, "[ChestCommands] Comando nulo recebido, ignorando.");
            return;
        }
        String key = command.startsWith("/") ? command.substring(1) : command;
        var menuDef = commandToMenu.get(key);
        if (menuDef != null) {
            sendChat(player, "[ChestCommands] Menu encontrado para comando: " + key + " (menuId: " + menuDef.id + ")");
            ChestMenu chestMenu = loadMenu(menuDef.id); // Assume loadMenu returns ChestMenu
            if (chestMenu != null) {
                sendChat(player, "[ChestCommands] ChestMenu carregado com sucesso para menuId: " + menuDef.id);
                try {
                    renderer.open((EntityStore) player, chestMenu);
                    sendChat(player, "[ChestCommands] renderer.open chamado para player: " + player + ", menu: " + chestMenu.getTitle());
                    sendChat(player, "[ChestCommands] Menu '" + chestMenu.getTitle() + "' aberto!");
                } catch (Exception e) {
                    sendChat(player, "[ChestCommands] Erro ao abrir menu: " + e.getMessage());
                }
            } else {
                sendChat(player, "[ChestCommands] Menu não encontrado ou inválido. Usando fallback textual.");
                new ConfigMenuAction(menuDef, config != null ? config.rows : 5, dispatcher).open(player);
            }
        } else {
            sendChat(player, "[ChestCommands] Nenhum menu encontrado para comando: " + key + ". Verifique se o arquivo .yml está correto e se o comando está registrado.");
        }
    }

    // Envia mensagem ao chat do jogador (Hytale)
    private void sendChat(Object player, String message) {
        try {
            if (player instanceof EntityStore) {
                boolean sent = false;
                // Usar método do Server.jar para enviar mensagem ao chat
                try {
                    EntityStore.class.getMethod("sendMessage", String.class).invoke(player, message);
                    sent = true;
                } catch (NoSuchMethodException nsme) {
                    try {
                        EntityStore.class.getMethod("sendChatMessage", String.class).invoke(player, message);
                        sent = true;
                    } catch (NoSuchMethodException nsme2) {
                        // Nenhum método encontrado
                    }
                }
                if (!sent) {
                    // Tenta fallback via API do servidor
                    try {
                        EntityStore.class.getMethod("sendSystemMessage", String.class).invoke(player, message);
                        sent = true;
                    } catch (NoSuchMethodException nsme3) {
                        // Nenhum método de chat encontrado
                        // Loga no console do servidor
                        System.out.println("[ChestCommands] Nenhum método de chat encontrado em EntityStore para mensagem: " + message);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[ChestCommands] Falha ao enviar mensagem ao chat: " + e.getMessage());
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
        // Load from config
        if (config != null && config.menus.containsKey(menuId)) {
            PluginMenuConfig.MenuDef def = config.menus.get(menuId);
            ChestMenu chestMenu = new ChestMenu(def.title, def.rows != null ? def.rows : 6);
            if (def.items != null) {
                for (Map.Entry<String, PluginMenuConfig.ItemDef> entry : def.items.entrySet()) {
                    try {
                        int slot = Integer.parseInt(entry.getKey());
                        PluginMenuConfig.ItemDef itemDef = entry.getValue();
                        // Create MenuItem
                        org.konpekiestudios.chestcommands.core.menu.MenuItem item = new org.konpekiestudios.chestcommands.core.menu.MenuItem();
                        item.setId(itemDef.material); // or id
                        item.setIcon(itemDef.material);
                        item.setDisplayName(itemDef.name);
                        // For now, skip actions and conditions
                        chestMenu.addItem(slot, item);
                    } catch (NumberFormatException e) {
                        // ignore invalid slot
                    }
                }
            }
            return chestMenu;
        }
        return null;
    }

    // Novo método para abrir UI de baú
    public void openChestUI(EntityStore player, ChestMenu menu) {
        // Usar renderer para abrir a UI
        renderer.open(player, menu);
    }

    private static String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }
}
