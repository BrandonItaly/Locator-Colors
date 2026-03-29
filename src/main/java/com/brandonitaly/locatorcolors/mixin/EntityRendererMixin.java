package com.brandonitaly.locatorcolors.mixin;

import com.brandonitaly.locatorcolors.client.LocatorColorsConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @ModifyReturnValue(method = "getNameTag", at = @At("RETURN"))
    private Component locatorcolors$colorizeNameTag(Component originalName, Entity entity) {
        Minecraft mc = Minecraft.getInstance();

        // 1. Validate Config, Entity type, and Client state
        if (!LocatorColorsConfig.isColorizeNameTagsEnabled() || originalName == null || !(entity instanceof Player player) || mc.player == null || mc.player.connection == null) {
            return originalName;
        }

        // 2. Enforce Colorize Self toggle
        if (!LocatorColorsConfig.isColorizeSelfEnabled() && player.getUUID().equals(mc.player.getUUID())) {
            return originalName;
        }

        String playerName = player.getGameProfile().name();

        final int[] foundColor = {-1};

        // 3. Scan the waypoint manager to find this specific player's assigned color
        mc.player.connection.getWaypointManager().forEachWaypoint(player, waypoint -> {
            if (foundColor[0] != -1) return; // Stop searching if we already found them

            UUID wpId = waypoint.id().left().orElse(null);
            String wpName = waypoint.id().right().orElse(null);

            // Check if either UUID or Name perfectly matches
            if ((wpId != null && wpId.equals(player.getUUID())) || (wpName != null && wpName.equalsIgnoreCase(playerName))) {
                
                // Grab explicit color, or calculate the fallback hash dynamically
                foundColor[0] = (Integer) waypoint.icon().color.orElseGet(() -> 
                    wpId != null ? ARGB.setBrightness(ARGB.color(255, wpId.hashCode()), 0.9F) : ARGB.setBrightness(ARGB.color(255, wpName.hashCode()), 0.9F)
                );
            }
        });

        // 4. If a color was found, strip the alpha channel and apply it
        if (foundColor[0] != -1) {
            return originalName.copy().withStyle(style -> style.withColor(foundColor[0] & 0xFFFFFF));
        }

        return originalName;
    }
}