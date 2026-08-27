package com.brandonitaly.locatorcolors.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;

import java.util.UUID;

public class LocatorColorUtil {

    /**
     * Resolves the raw RGB color (0xRRGGBB) for a player, prioritizing custom waypoint colors
     * from WaypointManager before falling back to the vanilla hash algorithm.
     */
    public static int getPlayerRawColor(UUID playerUUID, String playerName) {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.player != null && mc.player.connection != null) {
            final int[] foundColor = {-1};
            try {
                mc.player.connection.getWaypointManager().forEachWaypoint(mc.player, waypoint -> {
                    if (foundColor[0] != -1) return;

                    UUID wpId = waypoint.id().left().orElse(null);
                    if (wpId != null && playerUUID != null && wpId.equals(playerUUID)) {
                        foundColor[0] = (Integer) waypoint.icon().color.orElseGet(() -> ARGB.setBrightness(ARGB.color(255, wpId.hashCode()), 0.9F));
                        return;
                    }

                    String wpName = waypoint.id().right().orElse(null);
                    if (wpName != null && playerName != null && wpName.equalsIgnoreCase(playerName)) {
                        foundColor[0] = (Integer) waypoint.icon().color.orElseGet(() -> ARGB.setBrightness(ARGB.color(255, wpName.hashCode()), 0.9F));
                    }
                });
            } catch (Exception ignored) {
            }

            if (foundColor[0] != -1) {
                return foundColor[0] & 0x00FFFFFF;
            }
        }

        int hash = (playerUUID != null) ? playerUUID.hashCode() : (playerName != null ? playerName.hashCode() : 0);
        int argb = ARGB.setBrightness(ARGB.color(255, hash), 0.9F);
        return argb & 0x00FFFFFF;
    }

    /**
     * Replicates the color generation math from LocatorBarRenderer, returning TextColor.
     */
    public static TextColor getPlayerColor(UUID playerUUID, String playerName) {
        return TextColor.fromRgb(getPlayerRawColor(playerUUID, playerName));
    }
}