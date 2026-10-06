package com.brandonitaly.locatorcolors.mixin;

import com.brandonitaly.locatorcolors.client.LocatorColorsConfig;
import com.brandonitaly.locatorcolors.client.LocatorColorsKeyBindings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? if >=26.1 {
import net.minecraft.client.gui.components.PlayerFaceExtractor;
//?} else {
/*import net.minecraft.client.gui.components.PlayerFaceRenderer;
*///?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.waypoints.TrackedWaypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

//? if >=26.2 {
/*@Mixin(net.minecraft.client.gui.contextualbar.LocatorBar.class)
*///?} else {
@Mixin(net.minecraft.client.gui.contextualbar.LocatorBarRenderer.class)
//?}
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
        LocatorColorsConfig.LocatorHeadMode headMode = LocatorColorsConfig.getLocatorHeadMode();

        // Only customize the waypoint marker, not its other sprites.
        if (width != 9 || height != 9) {
            original.call(graphics, renderPipeline, sprite, x, y, width, height, color);
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) {
            original.call(graphics, renderPipeline, sprite, x, y, width, height, color);
            return;
        }

        // Heads and distance labels have independent visibility settings.
        boolean isInspecting = mc.options.keyPlayerList.isDown() || LocatorColorsKeyBindings.isInspectDown();
        boolean showHead = headMode == LocatorColorsConfig.LocatorHeadMode.ALWAYS
            || (headMode == LocatorColorsConfig.LocatorHeadMode.ON_KEY && isInspecting);
        boolean showDistance = isInspecting && LocatorColorsConfig.isShowLocatorDistanceEnabled();
        if (!showHead && !showDistance) {
            original.call(graphics, renderPipeline, sprite, x, y, width, height, color);
            return;
        }

        // 3. Waypoint & Player Lookup
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

        UUID playerId = info.getProfile().id();
        Player playerByUUID = mc.level != null ? mc.level.getPlayerByUUID(playerId) : null;

        if (showHead) {
            Identifier skinTexture = info.getSkin().body().texturePath();
            boolean flip = playerByUUID != null && AvatarRenderer.isPlayerUpsideDown(playerByUUID);
            locatorcolors$drawHead(graphics, skinTexture, x, y, color, info.showHat(), flip);
        } else {
            original.call(graphics, renderPipeline, sprite, x, y, width, height, color);
        }

        // --- DRAW DISTANCE (when inspecting) ---
        if (showDistance && playerByUUID != null && mc.player != null) {
            double dist = mc.player.distanceTo(playerByUUID);
            int distMeters = (int) Math.round(dist);
            String distStr = distMeters >= 1000 ? String.format("%.1fk", distMeters / 1000.0) : (distMeters + "m");

            float scale = 0.75F;
            float textWidth = mc.font.width(distStr) * scale;
            float renderX = (x + 4.5F) - (textWidth / 2.0F);
            float renderY = y - 8.0F;

            graphics.pose().pushMatrix();
            graphics.pose().translate(renderX, renderY);
            graphics.pose().scale(scale, scale);
            //? if >=26.1 {
            graphics.text(mc.font, distStr, 0, 0, 0xFFFFFFFF);
            //?} else {
            /*graphics.drawString(mc.font, distStr, 0, 0, 0xFFFFFFFF);
            *///?}
            graphics.pose().popMatrix();
        }
    }

    private void locatorcolors$drawHead(GuiGraphicsExtractor graphics, Identifier skinTexture,
                                       int x, int y, int color, boolean showHat, boolean flip) {
        if (!LocatorColorsConfig.isShowHeadBordersEnabled()) {
            // Draw the 7x7 face directly in the center, skipping the colored background border
            //? if >=26.1 {
            PlayerFaceExtractor.extractRenderState(graphics, skinTexture, x + 1, y + 1, 7, showHat, flip, -1);
            //?} else {
            /*PlayerFaceRenderer.draw(graphics, skinTexture, x + 1, y + 1, 7, showHat, flip, -1);
            *///?}
        } else {
            // Draw the 7x7 solid border background
            int solidBorderColor = color | 0xFF000000;
            graphics.fill(x + 2, y + 1, x + 7, y + 8, solidBorderColor);
            graphics.fill(x + 1, y + 2, x + 8, y + 7, solidBorderColor);

            // Draw the 5x5 face on top
            //? if >=26.1 {
            PlayerFaceExtractor.extractRenderState(graphics, skinTexture, x + 2, y + 2, 5, showHat, flip, -1);
            //?} else {
            /*PlayerFaceRenderer.draw(graphics, skinTexture, x + 2, y + 2, 5, showHat, flip, -1);
            *///?}
        }

    }
}
