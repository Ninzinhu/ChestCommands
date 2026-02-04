package org.konpekiestudios.konpekistudios.hytale.plugins;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Utility to get player name/uuid and send messages via reflection if the player type is unknown.
 */
public final class PlayerReflector {
    private PlayerReflector() {}

    public static String getName(Object player) {
        if (player == null) return "";
        try {
            Method m = player.getClass().getMethod("getName");
            Object r = m.invoke(player);
            if (r != null) return r.toString();
        } catch (Throwable ignored) {}
        try {
            Method m = player.getClass().getMethod("name");
            Object r = m.invoke(player);
            if (r != null) return r.toString();
        } catch (Throwable ignored) {}
        return player.toString();
    }

    public static UUID getUniqueId(Object player) {
        if (player == null) return null;
        try {
            Method m = player.getClass().getMethod("getUniqueId");
            Object r = m.invoke(player);
            if (r instanceof UUID) return (UUID) r;
            if (r != null) return UUID.fromString(r.toString());
        } catch (Throwable ignored) {}
        try {
            Method m = player.getClass().getMethod("uuid");
            Object r = m.invoke(player);
            if (r instanceof UUID) return (UUID) r;
            if (r != null) return UUID.fromString(r.toString());
        } catch (Throwable ignored) {}
        return null;
    }

    public static void sendMessage(Object player, String msg) {
        if (player == null) return;
        try {
            Method m = player.getClass().getMethod("sendMessage", String.class);
            m.invoke(player, msg);
        } catch (Throwable ignored) {}
        try {
            Method m = player.getClass().getMethod("send", String.class);
            m.invoke(player, msg);
        } catch (Throwable ignored) {}
    }
}
