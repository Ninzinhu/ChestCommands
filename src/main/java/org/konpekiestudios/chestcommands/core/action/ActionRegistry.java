package org.konpekiestudios.chestcommands.core.action;

import org.konpekiestudios.chestcommands.api.Action;
import org.konpekiestudios.chestcommands.api.ActionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ActionRegistry {
    private static final Map<String, Function<ActionContext, Action>> registry = new HashMap<>();

    public static void register(String id, Function<ActionContext, Action> factory) {
        registry.put(id, factory);
    }

    public static Action create(String id, ActionContext context) {
        Function<ActionContext, Action> factory = registry.get(id);
        return factory != null ? factory.apply(context) : null;
    }
}
