package org.konpekiestudios.chestcommands.core.condition;

import org.konpekiestudios.chestcommands.api.ActionContext;
import org.konpekiestudios.chestcommands.api.Condition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ConditionRegistry {
    private static final Map<String, Function<ActionContext, Condition>> registry = new HashMap<>();

    public static void register(String id, Function<ActionContext, Condition> factory) {
        registry.put(id, factory);
    }

    public static Condition create(String id, ActionContext context) {
        Function<ActionContext, Condition> factory = registry.get(id);
        return factory != null ? factory.apply(context) : null;
    }
}
