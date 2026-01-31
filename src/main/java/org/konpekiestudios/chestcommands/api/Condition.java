package org.konpekiestudios.chestcommands.api;

import org.konpekiestudios.chestcommands.api.ActionContext;

public interface Condition {
    boolean test(ActionContext context);
}
