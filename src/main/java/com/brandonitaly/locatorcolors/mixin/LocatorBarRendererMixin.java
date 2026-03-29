package com.brandonitaly.locatorcolors.mixin;

import com.brandonitaly.locatorcolors.client.LocatorColorsConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.waypoints.TrackedWaypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(net.minecraft.client.gui.contextualbar.LocatorBarRenderer.class)
public class LocatorBarRendererMixin {

    @WrapOperation(
        method = "*",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V"
        )
    )
    private void locatorcolors$renderPlayerHead(
        GuiGraphicsExtractor graphics,
        RenderPipeline renderPipeline,
        Identifier sprite,
        int x, int y, int width, int height, int color,
        Operation<Void> original,
        @Local TrackedWaypoint waypoint 
    ) {
        // 1. Config Toggle & Strict Size Filter (Abort early if disabled or not 9x9)
        if (!LocatorColorsConfig.isShowLocatorHeadsEnabled() || width != 9 || height != 9) {
            original.call(graphics, renderPipeline, sprite, x, y, width, height, color);
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        // 2. Tab Menu & Connection Check (Abort early if Tab is not held)
        if (!mc.options.keyPlayerList.isDown() || mc.getConnection() == null) {
            original.call(graphics, renderPipeline, sprite, x, y, width, height, color);
            return;
        }

        // 3. Waypoint & Player Lookup (Abort early if player isn't found)
        UUID wpId = waypoint.id().left().orElse(null);
        if (wpId == null) {
            original.call(graphics, renderPipeline, sprite, x, y, width, height, color);
            return;
        }

        PlayerInfo info = mc.getConnection().getPlayerInfo(wpId);
        if (info == null) {
            original.call(graphics, renderPipeline, sprite, x, y, width, height, color);
            return;
        }

        // --- DRAW HEAD ---
        Identifier skinTexture = info.getSkin().body().texturePath();
        UUID playerId = info.getProfile().id();
        Player playerByUUID = mc.level != null ? mc.level.getPlayerByUUID(playerId) : null;
        boolean flip = playerByUUID != null && AvatarRenderer.isPlayerUpsideDown(playerByUUID);

        // Draw the 7x7 solid border background
        int solidBorderColor = color | 0xFF000000;
        graphics.fill(x + 2, y + 1, x + 7, y + 8, solidBorderColor);
        graphics.fill(x + 1, y + 2, x + 8, y + 7, solidBorderColor);

        // Draw the 5x5 face on top
        //? if >=26.1 {
        PlayerFaceExtractor.extractRenderState(graphics, skinTexture, x + 2, y + 2, 5, info.showHat(), flip, -1);
        //?} else {
        /*PlayerFaceRenderer.draw(graphics, skinTexture, x + 2, y + 2, 5, true, flip, -1);
        *///?}
    }
}