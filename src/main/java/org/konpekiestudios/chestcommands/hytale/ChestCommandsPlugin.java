package org.konpekiestudios.chestcommands.hytale;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.konpekiestudios.chestcommands.api.CommandDispatcher;

public class ChestCommandsPlugin {
    // Use Object for runtime-only types (Hytale classes) to avoid compile-time linkage
    private final Logger logger = Logger.getLogger("ChestCommands");
    private CommandDispatcher commandDispatcher;
    private File configDir;

    public ChestCommandsPlugin() {
        // kept minimal - Hytale-specific init will be done via onEnableReflection
    }

    // Called reflectively by our bootstrap when Hytale plugin init is available
    public void onEnableReflection(Object pluginInitContext) {
        logger.info("[ChestCommands] onEnableReflection called");

        ensureConfigFolder();

        // init core services (MenuLoader etc.)
        this.commandDispatcher = new ReflectiveCommandDispatcher();

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
