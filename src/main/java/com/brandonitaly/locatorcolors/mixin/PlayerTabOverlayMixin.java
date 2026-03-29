package com.brandonitaly.locatorcolors.mixin;

import com.brandonitaly.locatorcolors.client.LocatorColorsConfig;
import com.brandonitaly.locatorcolors.util.LocatorColorUtil;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
    private void locatorcolors$colorizeTabName(PlayerInfo info, CallbackInfoReturnable<Component> cir) {
        if (!LocatorColorsConfig.isColorizeTabListEnabled()) {
            return;
        }

        Component original = cir.getReturnValue();
        
        // Fallback to the profile name
        Component baseComponent = original != null ? original : Component.literal(
            info.getProfile().name()
        );

        // Copy and apply the calculated locator color
        MutableComponent coloredName = baseComponent.copy().withStyle(style ->
            style.withColor(LocatorColorUtil.getPlayerColor(info.getProfile().id(), 
                info.getProfile().name()
            ))
        );

        cir.setReturnValue(coloredName);
    }
}