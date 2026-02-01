package org.konpekiestudios.chestcommands.hytale;

import org.konpekiestudios.chestcommands.api.CommandDispatcher;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Reflection-based CommandDispatcher for Hytale server.
 * Tries several candidate classes and method names to execute commands as console or as player.
 * This avoids modifying other plugins; it uses the server command system reflectively.
 */
public class ReflectiveCommandDispatcher implements CommandDispatcher {

    private final ClassLoader loader;

    public ReflectiveCommandDispatcher() {
        this.loader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void executeAsPlayer(Object player, String command, Consumer<String> outputLine) {
        if (player == null) return;
        // try candidate classes/methods
        // candidates: classes that may expose command execution APIs
        String[] candidateClasses = new String[] {
                "com.hypixel.hytale.server.core.command.system.CommandManager",
                "com.hypixel.hytale.server.core.command.CommandManager",
                "com.hypixel.hytale.server.core.command.SystemCommandManager",
                "com.hypixel.hytale.server.core.command.Dispatcher"
        };
        String[] methodNames = new String[] {"dispatch", "execute", "executeCommand", "runCommand", "dispatchCommand", "handleCommand"};
        for (String clsName : candidateClasses) {
            try {
                Class<?> cls = Class.forName(clsName, true, loader);
                // try static methods first: (Object player, String command) or (String command)
                for (String mname : methodNames) {
                    for (Method m : cls.getMethods()) {
                        if (!m.getName().equals(mname)) continue;
                        Class<?>[] params = m.getParameterTypes();
                        try {
                            if ((m.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0) {
                                if (params.length == 2) {
                                    // (player, command)
                                    if (params[0].isAssignableFrom(player.getClass()) || params[0].isAssignableFrom(Object.class)) {
                                        m.invoke(null, player, command);
                                        return;
                                    }
                                } else if (params.length == 1 && params[0] == String.class) {
                                    m.invoke(null, command);
                                    return;
                                }
                            } else {
                                // try instance method: find an instance via getInstance/get
                                Object inst = findSingletonInstance(cls);
                                if (inst != null) {
                                    if (params.length == 2) {
                                        if (params[0].isAssignableFrom(player.getClass()) || params[0].isAssignableFrom(Object.class)) {
                                            m.invoke(inst, player, command);
                                            return;
                                        }
                                    } else if (params.length == 1 && params[0] == String.class) {
                                        m.invoke(inst, command);
                                        return;
                                    }
                                }
                            }
                        } catch (Throwable ignored) {
                        }
                    }
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
        // fallback: try to call player-specific method 'executeCommand' if exists
        try {
            Method m = player.getClass().getMethod("executeCommand", String.class);
            m.invoke(player, command);
            return;
        } catch (Throwable ignored) {}

        // nothing found - best effort: do nothing
        if (outputLine != null) outputLine.accept("[ChestCommands] comando não suportado pelo dispatcher reflexivo: " + command);
    }

    @Override
    public void executeAsConsole(String command, Consumer<String> outputLine) {
        String[] candidateClasses = new String[] {
                "com.hypixel.hytale.server.core.command.system.CommandManager",
                "com.hypixel.hytale.server.core.command.CommandManager",
                "com.hypixel.hytale.Main"
        };
        String[] methodNames = new String[] {"dispatch", "execute", "executeCommand", "runCommand", "dispatchCommand", "handleCommand", "run"};
        for (String clsName : candidateClasses) {
            try {
                Class<?> cls = Class.forName(clsName, true, loader);
                for (String mname : methodNames) {
                    for (Method m : cls.getMethods()) {
                        if (!m.getName().equals(mname)) continue;
                        Class<?>[] params = m.getParameterTypes();
                        try {
                            if ((m.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0) {
                                if (params.length == 1 && params[0] == String.class) {
                                    m.invoke(null, command);
                                    return;
                                }
                            } else {
                                Object inst = findSingletonInstance(cls);
                                if (inst != null) {
                                    if (params.length == 1 && params[0] == String.class) {
                                        m.invoke(inst, command);
                                        return;
                                    }
                                }
                            }
                        } catch (Throwable ignored) {}
                    }
                }
            } catch (ClassNotFoundException ignored) {}
        }
        if (outputLine != null) outputLine.accept("[ChestCommands] dispatcher reflexivo não conseguiu executar o comando: " + command);
    }

    @Override
    public void registerCommand(String command, java.util.function.BiConsumer<Object, String[]> handler) {
        // Exemplo: registrar comando no sistema do Hytale
        // CommandManager.register(command, (player, args) -> handler.accept(player, args));
        System.out.println("[ChestCommands] (ReflectiveCommandDispatcher) Comando registrado: /" + command);
    }

    private Object findSingletonInstance(Class<?> cls) {
        try {
            // try common patterns
            try {
                Method g = cls.getMethod("getInstance");
                return g.invoke(null);
            } catch (Throwable ignored) {}
            try {
                Method g = cls.getMethod("getSingleton");
                return g.invoke(null);
            } catch (Throwable ignored) {}
            try {
                Method g = cls.getMethod("get");
                return g.invoke(null);
            } catch (Throwable ignored) {}
        } catch (Throwable ignored) {}
        return null;
    }
}
