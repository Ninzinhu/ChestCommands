package org.konpekiestudios.chestcommands.hytale;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.konpekiestudios.chestcommands.api.CommandDispatcher;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class ChestCommandsPlugin extends JavaPlugin {
    // Use Object for runtime-only types (Hytale classes) to avoid compile-time linkage
    private final Logger logger = Logger.getLogger("ChestCommands");
    private CommandDispatcher commandDispatcher;
    private File configDir;

    public ChestCommandsPlugin(JavaPluginInit init) {
        super(init);
        // kept minimal - Hytale-specific init will be done via onEnableReflection or onEnable
    }

    @Override
    public void onEnable() {
        onEnableReflection(null);
    }

    // Called reflectively by our bootstrap when Hytale plugin init is available
    public void onEnableReflection(Object pluginInitContext) {
        logger.info("[ChestCommands] onEnableReflection called");

        ensureConfigFolder();

        // init core services (MenuLoader etc.)
        this.commandDispatcher = new ReflectiveCommandDispatcher();

        // Try to register event listener for commands
        try {
            Class<?> eventManagerClass = Class.forName("com.hypixel.hytale.server.core.event.EventManager");
            Object eventManager = eventManagerClass.getMethod("getInstance").invoke(null);
            Method registerMethod = eventManagerClass.getMethod("registerListener", Class.class, Consumer.class);
            Class<?> commandEventClass = Class.forName("com.hypixel.hytale.server.core.event.CommandEvent");

            Consumer<?> listener = (Consumer<Object>) event -> {
                try {
                    String cmd = (String) event.getClass().getMethod("getCommand").invoke(event);
                    Object player = event.getClass().getMethod("getPlayer").invoke(event);
                    String[] args = (String[]) event.getClass().getMethod("getArgs").invoke(event);

                    if ("testchestui".equals(cmd)) {
                        // handle testchestui
                        try {
                            logger.info("[ChestCommands] testchestui invoked for sender: " + player);
                            if (player != null) {
                                HytaleMenuRenderer renderer = new HytaleMenuRenderer();
                                org.konpekiestudios.chestcommands.core.menu.ChestMenu cm = new org.konpekiestudios.chestcommands.core.menu.ChestMenu("Test Chest UI", 3);
                                renderer.open(player, cm);
                            }
                        } catch (Throwable t) {
                            logger.log(Level.WARNING, "Error opening UI: " + t.getMessage(), t);
                            if (player != null) {
                                try {
                                    Method m = player.getClass().getMethod("sendChatMessage", String.class);
                                    m.invoke(player, "Failed to open chest UI: " + t.getMessage());
                                } catch (Exception e) {
                                    logger.info("[ChestCommands] Could not send error message to player: " + e.getMessage());
                                }
                            }
                        }
                    } else if ("coins".equals(cmd)) {
                        // handle coins
                        try {
                            logger.info("[ChestCommands] coins invoked for sender: " + player);
                            if (player != null) {
                                Method m = player.getClass().getMethod("sendChatMessage", String.class);
                                m.invoke(player, "Your coins: 1000 coins");
                            }
                        } catch (Throwable t) {
                            logger.log(Level.WARNING, "Error in coins", t);
                        }
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error in event listener", e);
                }
            };

            registerMethod.invoke(eventManager, commandEventClass, listener);
            logger.info("[ChestCommands] Registered event listener for CommandEvent");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not register event listener", e);
        }

        // load menus from config folder
        try (var stream = Files.list(configDir.toPath())) {
            stream.filter(p -> p.toString().endsWith(".yml")).forEach(p -> {
                try (InputStream is = new FileInputStream(p.toFile())) {
                    Map<String, Object> parsed = new org.yaml.snakeyaml.Yaml().load(is);
                    // For now just log file load
                    logger.info("[ChestCommands] Loaded menu file: " + p.getFileName());
                    // register command if specified
                    Object cmd = parsed != null ? parsed.get("command") : null;
                    if (cmd instanceof String) {
                        String command = ((String) cmd).trim();
                        String finalCommand = command.startsWith("/") ? command.substring(1) : command;
                        ((ReflectiveCommandDispatcher) this.commandDispatcher).register(finalCommand, (player, args) -> {
                            // open a test empty menu for now
                            try {
                                HytaleMenuRenderer renderer = new HytaleMenuRenderer();
                                renderer.open(player, null);
                                // send chat confirmation
                                sendChatToPlayer(player, "Opened menu from config: " + p.getFileName());
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, "Failed to open menu for command " + finalCommand, e);
                                throw e; // rethrow to let dispatcher handle
                            }
                        });
                        logger.info("[ChestCommands] Registered command from config: /" + finalCommand);
                    }
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Failed to read menu file " + p.toString(), ex);
                }
            });
        } catch (Exception ex) {
            logger.log(Level.WARNING, "No config files found or failed to load directory: " + configDir, ex);
        }

        // register test commands
        ((ReflectiveCommandDispatcher) this.commandDispatcher).register("testchestui", (player, args) -> {
            try {
                new HytaleMenuRenderer().open(player, null);
                sendChatToPlayer(player, "Opened test chest UI");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to open test chest UI", e);
                throw e;
            }
        });

        logger.info("[ChestCommands] onEnableReflection completed");
    }

    private void ensureConfigFolder() {
        try {
            File base = new File("ChestCommandsConfig");
            if (!base.exists()) {
                boolean created = base.mkdirs();
                logger.info("[ChestCommands] Created config folder: " + base.getAbsolutePath() + " -> " + created);
            }
            this.configDir = base;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not create/read config folder", e);
        }
    }

    private void sendChatToPlayer(Object player, String message) {
        if (player == null) return;
        try {
            Class<?> pClass = player.getClass();
            // try multiple known method names
            List<String> candidates = Arrays.asList("sendChatMessage", "sendSystemMessage", "sendMessage");
            boolean sent = false;
            for (String name : candidates) {
                try {
                    java.lang.reflect.Method m = pClass.getMethod(name, String.class);
                    m.invoke(player, message);
                    sent = true;
                    break;
                } catch (NoSuchMethodException ignored) {
                }
            }
            if (!sent) {
                // fallback: log
                logger.info("[ChestCommands] (fallback) player message: " + message);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to send chat message to player", e);
        }
    }

    // ...existing code...
}
