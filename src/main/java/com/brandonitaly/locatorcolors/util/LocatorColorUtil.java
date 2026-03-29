package com.brandonitaly.locatorcolors.util;

import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;

import java.util.UUID;

public class LocatorColorUtil {
    
    /**
     * Replicates the color generation math from LocatorBarRenderer.
     */
    public static TextColor getPlayerColor(UUID playerUUID, String playerName) {
        int hash = (playerUUID != null) ? playerUUID.hashCode() : playerName.hashCode();
        
        int argb = ARGB.setBrightness(ARGB.color(255, hash), 0.9F);
        
        // Strip the alpha channel since TextColor expects RGB
        return TextColor.fromRgb(argb & 0x00FFFFFF);
    }
}