package com.brandonitaly.locatorcolors.mixin;

import com.brandonitaly.locatorcolors.client.LocatorColorsConfig;
import com.brandonitaly.locatorcolors.util.LocatorColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.UUID;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

//? if >=26.1 {
    @ModifyVariable(
        method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
//?} else {
/*  @ModifyVariable(
        method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
*///?}
    private Component locatorcolors$colorizeChatSender(Component contents) {
        if (contents == null || !LocatorColorsConfig.isColorizeChatEnabled()) return contents;

        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null || mc.player == null) return contents;

        String rawMessage = contents.getString();
        PlayerInfo sender = null;
        int earliestIndex = Integer.MAX_VALUE;

        for (PlayerInfo info : mc.getConnection().getOnlinePlayers()) {
            String name = info.getProfile().name();
            int index = rawMessage.indexOf(name);
            if (index != -1 && index < earliestIndex) {
                earliestIndex = index;
                sender = info;
                if (index <= 1) break;
            }
        }

        if (sender != null) {
            String targetName = sender.getProfile().name();
            UUID targetId = sender.getProfile().id();

            if (!LocatorColorsConfig.isColorizeSelfEnabled() && targetId.equals(mc.player.getUUID())) {
                return contents;
            }

            TextColor color = LocatorColorUtil.getPlayerColor(targetId, targetName);
            return replaceFirstOccurrence(contents, targetName, color, new boolean[]{false});
        }

        return contents;
    }

    private Component replaceFirstOccurrence(Component component, String targetName, TextColor color, boolean[] found) {
        MutableComponent result;
        Style baseStyle = component.getStyle();

        if (component.getContents() instanceof TranslatableContents translatable) {
            Object[] args = translatable.getArgs();
            Object[] newArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                if (!found[0] && args[i] instanceof String str && str.contains(targetName)) {
                    newArgs[i] = colorizeString(str, targetName, color, baseStyle, found);
                } else if (args[i] instanceof Component argComp) {
                    newArgs[i] = replaceFirstOccurrence(argComp, targetName, color, found);
                } else {
                    newArgs[i] = args[i];
                }
            }
            result = Component.translatable(translatable.getKey(), newArgs).withStyle(baseStyle);
        } else if (!found[0] && component.getContents() instanceof PlainTextContents plain) {
            String text = plain.text();
            if (text.contains(targetName)) {
                result = colorizeString(text, targetName, color, baseStyle, found);
            } else {
                result = MutableComponent.create(component.getContents()).withStyle(baseStyle);
            }
        } else {
            result = MutableComponent.create(component.getContents()).withStyle(baseStyle);
        }

        for (Component sibling : component.getSiblings()) {
            result.append(replaceFirstOccurrence(sibling, targetName, color, found));
        }

        return result;
    }

    private MutableComponent colorizeString(String text, String targetName, TextColor color, Style baseStyle, boolean[] found) {
        int index = text.indexOf(targetName);
        if (index == -1 || found[0]) return Component.literal(text).withStyle(baseStyle);

        found[0] = true;
        MutableComponent comp = Component.empty();
        if (index > 0) comp.append(Component.literal(text.substring(0, index)).withStyle(baseStyle));

        Style coloredStyle = (baseStyle != null ? baseStyle : Style.EMPTY).withColor(color);
        comp.append(Component.literal(targetName).withStyle(coloredStyle));

        if (index + targetName.length() < text.length()) {
            comp.append(Component.literal(text.substring(index + targetName.length())).withStyle(baseStyle));
        }
        return comp;
    }
}