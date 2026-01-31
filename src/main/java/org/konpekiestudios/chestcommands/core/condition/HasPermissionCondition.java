package org.konpekiestudios.chestcommands.core.condition;

import org.konpekiestudios.chestcommands.api.ActionContext;
import org.konpekiestudios.chestcommands.api.Condition;

public class HasPermissionCondition implements Condition {
    private final String permission;

    public HasPermissionCondition(String permission) {
        this.permission = permission;
    }

    @Override
    public boolean test(ActionContext context) {
        // Check if player has permission
        return true; // placeholder
    }
}
