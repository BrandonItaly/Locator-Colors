package com.brandonitaly.locatorcolors.mixin;

import com.brandonitaly.locatorcolors.client.LocatorColorsConfig;
import com.brandonitaly.locatorcolors.util.LocatorColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
    private void locatorcolors$colorizeTabName(PlayerInfo info, CallbackInfoReturnable<Component> cir) {
        if (!LocatorColorsConfig.isColorizeTabListEnabled()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        
        UUID targetId = info.getProfile().id();
        String targetName = info.getProfile().name();

        // If disabled and the tab list entry belongs to the client player, abort
        if (mc.player != null && !LocatorColorsConfig.isColorizeSelfEnabled() && targetId.equals(mc.player.getUUID())) {
            return;
        }

        Component original = cir.getReturnValue();
        
        // Fallback to the profile name
        Component baseComponent = original != null ? original : Component.literal(targetName);

        // Copy and apply the calculated locator color
        MutableComponent coloredName = baseComponent.copy().withStyle(style ->
            style.withColor(LocatorColorUtil.getPlayerColor(targetId, targetName))
        );

        cir.setReturnValue(coloredName);
    }
}