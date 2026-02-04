package org.konpekiestudios.chestcommands.hytale;

import java.io.File;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.konpekiestudios.konpekistudios.hytale.plugins.ReflectiveCommandDispatcher;

public class ChestCommandsPlugin extends JavaPlugin {
    private final Logger logger = Logger.getLogger("ChestCommands");

    public ChestCommandsPlugin(JavaPluginInit init) {
        super(init);
        logger.info("[ChestCommands] Constructor called!");
    }

    @Override
    public void onEnable() {
        logger.info("[ChestCommands] onEnable called!");
        ensureConfigFolder();

        ReflectiveCommandDispatcher dispatcher = new ReflectiveCommandDispatcher();

        dispatcher.register("say", (sender, args) -> {
            try {
                logger.info("[ChestCommands] /say invoked for sender: " + sender);
                if (sender != null) {
                    Method m = sender.getClass().getMethod("sendChatMessage", String.class);
                    m.invoke(sender, "Olá!");
                }
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Error in /say: " + t.getMessage(), t);
            }
        });

        logger.info("[ChestCommands] /say command registered!");

        // Comando hello-word
        dispatcher.register("hello-word", (sender, args) -> {
            try {
                logger.info("[ChestCommands] /hello-word invoked for sender: " + sender);
                if (sender != null) {
                    Method m = sender.getClass().getMethod("sendChatMessage", String.class);
                    m.invoke(sender, "Hello, Hytale World!");
                }
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Error in /hello-word: " + t.getMessage(), t);
            }
        });

        logger.info("[ChestCommands] /hello-word command registered!");
    }

    private void ensureConfigFolder() {
        try {
            // Cria a pasta de config na raiz de onde o jar for executado
            File pluginFolder = new File("ChestCommandsConfig");
            if (!pluginFolder.exists()) {
                boolean created = pluginFolder.mkdirs();
                logger.info("[ChestCommands] Created config folder: " + pluginFolder.getAbsolutePath() + " -> " + created);
            } else {
                logger.info("[ChestCommands] Config folder already exists: " + pluginFolder.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not create/read config folder", e);
        }
    }
}
