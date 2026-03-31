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
        // 1. Abort immediately if disabled, null, or not a Player
        if (!LocatorColorsConfig.isColorizeNameTagsEnabled() || originalName == null || !(entity instanceof Player player)) {
            return originalName;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.connection == null) {
            return originalName;
        }

        UUID targetUUID = player.getUUID();

        // 2. Enforce Colorize Self toggle
        if (!LocatorColorsConfig.isColorizeSelfEnabled() && targetUUID.equals(mc.player.getUUID())) {
            return originalName;
        }

        final int[] foundColor = {-1};

        // 3. Scan the waypoint manager
        mc.player.connection.getWaypointManager().forEachWaypoint(player, waypoint -> {
            if (foundColor[0] != -1) return;

            // Check UUID first
            UUID wpId = waypoint.id().left().orElse(null);
            if (wpId != null && wpId.equals(targetUUID)) {
                foundColor[0] = (Integer) waypoint.icon().color.orElseGet(() -> ARGB.setBrightness(ARGB.color(255, wpId.hashCode()), 0.9F));
                return;
            }

            // Only evaluate String Name if UUID failed
            String wpName = waypoint.id().right().orElse(null);
            if (wpName != null) {
                String targetName = player.getGameProfile().name();
                
                if (wpName.equalsIgnoreCase(targetName)) {
                    foundColor[0] = (Integer) waypoint.icon().color.orElseGet(() -> ARGB.setBrightness(ARGB.color(255, wpName.hashCode()), 0.9F));
                }
            }
        });

        // 4. Apply color
        if (foundColor[0] != -1) {
            return originalName.copy().withStyle(style -> style.withColor(foundColor[0] & 0xFFFFFF));
        }

        return originalName;
    }
}