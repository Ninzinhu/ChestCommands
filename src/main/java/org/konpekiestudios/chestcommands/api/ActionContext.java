package org.konpekiestudios.chestcommands.api;

public interface ActionContext {
    PlayerRef getPlayer();
    String getValue(); // Para valores dinâmicos, como menuId
}
