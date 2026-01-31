package org.konpekiestudios.chestcommands.api;

import java.util.function.Function;

public interface ChestCommandsAPI {
    void openMenu(PlayerRef player, String menuId);
    void registerAction(String id, Function<ActionContext, Action> factory);
    void registerCondition(String id, Function<ActionContext, Condition> factory);
}
