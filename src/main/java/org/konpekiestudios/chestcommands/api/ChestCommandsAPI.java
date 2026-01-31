package org.konpekiestudios.chestcommands.api;

import java.util.function.Function;
import org.konpekiestudios.chestcommands.api.PlayerRef;
import org.konpekiestudios.chestcommands.api.ActionContext;
import org.konpekiestudios.chestcommands.api.Action;
import org.konpekiestudios.chestcommands.api.Condition;

public interface ChestCommandsAPI {
    void openMenu(PlayerRef player, String menuId);
    void registerAction(String id, Function<ActionContext, Action> factory);
    void registerCondition(String id, Function<ActionContext, Condition> factory);
}
