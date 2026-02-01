package org.konpekiestudios.chestcommands.hytale;

import org.konpekiestudios.chestcommands.api.CommandDispatcher;
import org.konpekiestudios.chestcommands.api.CommandHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reflection-based CommandDispatcher for Hytale server.
 * Tries several candidate classes and method names to execute commands as console or as player.
 * This avoids modifying other plugins; it uses the server command system reflectively.
 */
public class ReflectiveCommandDispatcher implements CommandDispatcher {
    private final Logger logger = Logger.getLogger("ChestCommands");
    private final Map<String, CommandHandler> handlers = new HashMap<>();

    public ReflectiveCommandDispatcher() {
        // register built-in test command
        register("testchestui", (sender, args) -> {
            try {
                // Use logger instead of message send to avoid visibility/reflection issues in constructor
                logger.info("[ChestCommands] testchestui invoked; attempting to open test UI...");
                HytaleMenuRenderer renderer = new HytaleMenuRenderer();
                // build a minimal ChestMenu dynamically
                org.konpekiestudios.chestcommands.core.menu.ChestMenu cm = new org.konpekiestudios.chestcommands.core.menu.ChestMenu("Test Chest UI", 3);
                renderer.open(sender, cm);
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Error opening test UI", t);
                throw t;
            }
        });
    }

    private void rendererLog(Object player, String msg) {
        try {
            Method m = player.getClass().getMethod("sendChatMessage", String.class);
            m.invoke(player, msg);
            return;
        } catch (Throwable ignored) {}
        try {
            Method m = player.getClass().getMethod("sendSystemMessage", String.class);
            m.invoke(player, msg);
            return;
        } catch (Throwable ignored) {}
        System.out.println(msg);
    }

    // Public API: allow registering commands programmatically
    @Override
    public void register(String command, CommandHandler handler) {
        handlers.put(command.toLowerCase(), handler);
        logger.info("[ChestCommands] Local registered command: /" + command);
        // try to register in server CommandManager via reflection
        tryRegisterInServer(command, handler);
    }

    @Override
    public boolean dispatch(Object sender, String command, String[] args) {
        CommandHandler h = handlers.get(command.toLowerCase());
        if (h != null) {
            try {
                h.handle(sender, args);
                return true;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Handler threw", e);
                return false;
            }
        }
        return false;
    }

    @Override
    public void executeAsPlayer(Object player, String command, Consumer<String> outputLine) {
        if (player == null) {
            outputLine.accept("[ChestCommands] executeAsPlayer called with null player");
            return;
        }
        String trimmed = command == null ? "" : command.trim();
        if (trimmed.startsWith("/")) trimmed = trimmed.substring(1);
        String[] parts = trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");
        String label = parts.length > 0 ? parts[0] : "";
        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

        // First try local handlers
        if (!label.isEmpty() && dispatch(player, label, args)) {
            outputLine.accept("[ChestCommands] Command handled locally: " + label);
            return;
        }

        // Try server CommandManager dispatch via reflection
        try {
            Object serverInstance = getServerInstance();
            if (serverInstance != null) {
                Object commandManager = null;
                try {
                    Method getCm = serverInstance.getClass().getMethod("getCommandSystem");
                    commandManager = getCm.invoke(serverInstance);
                } catch (NoSuchMethodException e) {
                    try {
                        Method getCm2 = serverInstance.getClass().getMethod("getCommandManager");
                        commandManager = getCm2.invoke(serverInstance);
                    } catch (NoSuchMethodException ignored) {}
                }

                if (commandManager != null) {
                    // try common execute signatures
                    Method[] methods = commandManager.getClass().getMethods();
                    for (Method m : methods) {
                        String n = m.getName().toLowerCase();
                        if (n.contains("dispatch") || n.contains("execute") || n.contains("run") || n.contains("handle")) {
                            Class<?>[] ps = m.getParameterTypes();
                            try {
                                if (ps.length == 2 && ps[0] == Object.class && ps[1] == String.class) {
                                    m.invoke(commandManager, player, trimmed);
                                    outputLine.accept("[ChestCommands] Dispatched to server command manager via " + m.getName());
                                    return;
                                } else if (ps.length == 1 && ps[0] == String.class) {
                                    m.invoke(commandManager, trimmed);
                                    outputLine.accept("[ChestCommands] Dispatched to server command manager via " + m.getName());
                                    return;
                                } else if (ps.length == 2 && ps[0] == Object.class && ps[1] == String[].class) {
                                    m.invoke(commandManager, player, args);
                                    outputLine.accept("[ChestCommands] Dispatched to server command manager via " + m.getName());
                                    return;
                                }
                            } catch (Exception e) {
                                // try next
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            // ignore and fallback
            logger.log(Level.FINE, "executeAsPlayer reflective dispatch failed", t);
        }

        outputLine.accept("[ChestCommands] Could not dispatch command: " + command);
    }

    @Override
    public void executeAsConsole(String command, Consumer<String> outputLine) {
        String trimmed = command == null ? "" : command.trim();
        if (trimmed.startsWith("/")) trimmed = trimmed.substring(1);
        // try to find server command manager and invoke as console
        try {
            Object serverInstance = getServerInstance();
            if (serverInstance != null) {
                Object commandManager = null;
                try {
                    Method getCm = serverInstance.getClass().getMethod("getCommandSystem");
                    commandManager = getCm.invoke(serverInstance);
                } catch (NoSuchMethodException e) {
                    try {
                        Method getCm2 = serverInstance.getClass().getMethod("getCommandManager");
                        commandManager = getCm2.invoke(serverInstance);
                    } catch (NoSuchMethodException ignored) {}
                }

                if (commandManager != null) {
                    for (Method m : commandManager.getClass().getMethods()) {
                        String n = m.getName().toLowerCase();
                        if (n.contains("dispatch") || n.contains("execute") || n.contains("run")) {
                            Class<?>[] ps = m.getParameterTypes();
                            try {
                                if (ps.length == 1 && ps[0] == String.class) {
                                    m.invoke(commandManager, trimmed);
                                    outputLine.accept("[ChestCommands] Dispatched to server command manager via " + m.getName());
                                    return;
                                }
                            } catch (Exception e) {
                                // next
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            logger.log(Level.FINE, "executeAsConsole reflective dispatch failed", t);
        }

        // fallback: run locally if matches registered
        String[] parts = trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");
        String label = parts.length > 0 ? parts[0] : "";
        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
        if (!label.isEmpty() && dispatch(null, label, args)) {
            outputLine.accept("[ChestCommands] Command handled locally: " + label);
            return;
        }

        outputLine.accept("[ChestCommands] Could not execute console command: " + command);
    }

    private Object getServerInstance() {
        try {
            Class<?> serverClass = Class.forName("com.hypixel.hytale.server.core.HytaleServer");
            try {
                Method get = serverClass.getMethod("get");
                return get.invoke(null);
            } catch (NoSuchMethodException ignored) {
                try {
                    Method instance = serverClass.getMethod("getInstance");
                    return instance.invoke(null);
                } catch (NoSuchMethodException ignored2) {
                    try {
                        java.lang.reflect.Field f = serverClass.getDeclaredField("INSTANCE");
                        f.setAccessible(true);
                        return f.get(null);
                    } catch (NoSuchFieldException | IllegalAccessException ignored3) {
                        logger.info("[ChestCommands] Could not obtain HytaleServer instance reflectively");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            logger.info("[ChestCommands] HytaleServer class not present at runtime");
        } catch (Throwable t) {
            logger.log(Level.FINE, "Error obtaining server instance", t);
        }
        return null;
    }

    private void tryRegisterInServer(String command, CommandHandler handler) {
        try {
            Class<?> serverClass = Class.forName("com.hypixel.hytale.server.core.HytaleServer");
            Object serverInstance = null;
            // try common static accessors
            try {
                Method get = serverClass.getMethod("get");
                serverInstance = get.invoke(null);
            } catch (NoSuchMethodException ignored) {
                try {
                    Method instance = serverClass.getMethod("getInstance");
                    serverInstance = instance.invoke(null);
                } catch (NoSuchMethodException ignored2) {
                    // fallback: try field
                    try {
                        java.lang.reflect.Field f = serverClass.getDeclaredField("INSTANCE");
                        f.setAccessible(true);
                        serverInstance = f.get(null);
                    } catch (NoSuchFieldException | IllegalAccessException ignored3) {
                        logger.info("[ChestCommands] Could not obtain HytaleServer instance reflectively");
                    }
                }
            }

            if (serverInstance == null) {
                logger.info("[ChestCommands] No server instance found, cannot register command in server CommandManager");
                return;
            }

            // try to get command manager
            Object commandManager = null;
            try {
                Method getCm = serverClass.getMethod("getCommandSystem");
                commandManager = getCm.invoke(serverInstance);
            } catch (NoSuchMethodException e) {
                try {
                    Method getCm2 = serverClass.getMethod("getCommandManager");
                    commandManager = getCm2.invoke(serverInstance);
                } catch (NoSuchMethodException ignored) {
                    logger.info("[ChestCommands] CommandManager accessor not found via known names");
                }
            }

            if (commandManager == null) {
                logger.info("[ChestCommands] CommandManager not found on server instance");
                return;
            }

            Class<?> cmClass = commandManager.getClass();
            // candidate method names to register
            String[] candidateNames = {"register", "registerCommand", "registerSystemCommand", "registerCommandDefinition"};
            for (String mName : candidateNames) {
                for (Method m : cmClass.getMethods()) {
                    if (!m.getName().equals(mName)) continue;
                    Class<?>[] params = m.getParameterTypes();
                    try {
                        if (params.length == 2 && params[0] == String.class) {
                            // assume signature register(String, handler)
                            m.invoke(commandManager, command, createServerHandlerProxy(m, handler));
                            logger.info("[ChestCommands] Registered command in CommandManager via method: " + mName);
                            return;
                        } else if (params.length == 1 && params[0] == String.class) {
                            // maybe returns a definition
                            Object def = m.invoke(commandManager, command);
                            // try to find define + set executor method
                            trySetExecutor(def, handler);
                            logger.info("[ChestCommands] Registered command definition via: " + mName);
                            return;
                        }
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Attempt to invoke " + mName + " failed", e);
                    }
                }
            }

            logger.info("[ChestCommands] All attempts to register command in server CommandManager finished");
        } catch (ClassNotFoundException e) {
            logger.info("[ChestCommands] HytaleServer class not present at compile/runtime classpath; skipping server registration");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unexpected error while trying to register command in server", e);
        }
    }

    private Object createServerHandlerProxy(Method cmMethod, CommandHandler handler) {
        // create a dynamic proxy compatible with many handler types: try Consumer for player
        return java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{java.util.function.Consumer.class}, (proxy, method, args) -> {
            if ("accept".equals(method.getName()) && args != null && args.length == 1) {
                Object sender = args[0];
                String[] sargs = new String[0];
                handler.handle(sender, sargs);
            }
            return null;
        });
    }

    private void trySetExecutor(Object def, CommandHandler handler) {
        if (def == null) return;
        Class<?> defClass = def.getClass();
        for (Method m : defClass.getMethods()) {
            if (m.getName().toLowerCase().contains("executor") || m.getName().toLowerCase().contains("handler")) {
                try {
                    // try to set handler via proxy
                    m.invoke(def, createServerHandlerProxy(m, handler));
                    logger.info("[ChestCommands] Set executor on command definition via method: " + m.getName());
                    return;
                } catch (Exception e) {
                    logger.log(Level.FINE, "Failed to set executor via " + m.getName(), e);
                }
            }
        }
    }

    // simple functional interface for handlers

}
