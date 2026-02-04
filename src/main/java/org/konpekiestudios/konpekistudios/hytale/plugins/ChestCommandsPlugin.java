package org.konpekiestudios.konpekistudios.hytale.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class ChestCommandsPlugin extends JavaPlugin {
    // Use Object for runtime-only types (Hytale classes) to avoid compile-time linkage
    private final Logger logger = Logger.getLogger("ChestCommands");
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

        // Initialize command dispatcher
        ReflectiveCommandDispatcher dispatcher = new ReflectiveCommandDispatcher();

        // Register commands
        dispatcher.register("testchestui", (sender, args) -> {
            try {
                logger.info("[ChestCommands] testchestui invoked for sender: " + sender);
                if (sender != null) {
                    HytaleMenuRenderer renderer = new HytaleMenuRenderer();
                    org.konpekiestudios.chestcommands.core.menu.ChestMenu cm = new org.konpekiestudios.chestcommands.core.menu.ChestMenu("Test Chest UI", 3);
                    renderer.open(sender, cm);
                }
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Error opening UI: " + t.getMessage(), t);
                if (sender != null) {
                    try {
                        Method m = sender.getClass().getMethod("sendChatMessage", String.class);
                        m.invoke(sender, "Failed to open chest UI: " + t.getMessage());
                    } catch (Exception e) {
                        logger.info("[ChestCommands] Could not send error message to player: " + e.getMessage());
                    }
                }
            }
        });

        dispatcher.register("coins", (sender, args) -> {
            try {
                logger.info("[ChestCommands] coins invoked for sender: " + sender);
                if (sender != null) {
                    try {
                        Method m = sender.getClass().getMethod("sendChatMessage", String.class);
                        m.invoke(sender, "Your coins: 1000 coins");
                    } catch (Exception e) {
                        logger.info("[ChestCommands] Could not send message to player: " + e.getMessage());
                    }
                }
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Error in coins", t);
            }
        });

        logger.info("[ChestCommands] Commands registered");

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
