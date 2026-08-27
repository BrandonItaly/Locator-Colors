package com.brandonitaly.locatorcolors.mixin;

import com.brandonitaly.locatorcolors.client.LocatorColorsConfig;
import com.brandonitaly.locatorcolors.util.LocatorColorUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
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

        TextColor color = LocatorColorUtil.getPlayerColor(targetUUID, player.getGameProfile().name());
        return originalName.copy().withStyle(style -> style.withColor(color));
    }
}