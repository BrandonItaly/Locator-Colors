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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

        List<PlayerMatch> targetPlayers = new ArrayList<>();
        UUID localPlayerId = mc.player.getUUID();
        boolean colorizeSelf = LocatorColorsConfig.isColorizeSelfEnabled();

        for (PlayerInfo info : mc.getConnection().getOnlinePlayers()) {
            if (info.getProfile() == null || info.getProfile().name() == null) continue;

            UUID targetId = info.getProfile().id();
            if (!colorizeSelf && targetId.equals(localPlayerId)) {
                continue;
            }

            String name = info.getProfile().name();
            if (!name.isEmpty()) {
                TextColor color = LocatorColorUtil.getPlayerColor(targetId, name);
                targetPlayers.add(new PlayerMatch(name, color));
            }
        }

        if (targetPlayers.isEmpty()) return contents;

        // Sort descending by name length so longer names match first (e.g. Daniel before Dan)
        targetPlayers.sort(Comparator.comparingInt((PlayerMatch p) -> p.name().length()).reversed());

        String rawMessage = contents.getString();
        boolean anyPresent = false;
        for (PlayerMatch player : targetPlayers) {
            if (rawMessage.contains(player.name())) {
                anyPresent = true;
                break;
            }
        }

        if (!anyPresent) return contents;

        return replacePlayerNames(contents, targetPlayers);
    }

    private record PlayerMatch(String name, TextColor color) {}

    private Component replacePlayerNames(Component component, List<PlayerMatch> players) {
        MutableComponent result;
        Style baseStyle = component.getStyle();

        if (component.getContents() instanceof TranslatableContents translatable) {
            Object[] args = translatable.getArgs();
            Object[] newArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof String str) {
                    newArgs[i] = colorizeAllPlayersInText(str, players, baseStyle);
                } else if (args[i] instanceof Component argComp) {
                    newArgs[i] = replacePlayerNames(argComp, players);
                } else {
                    newArgs[i] = args[i];
                }
            }
            result = Component.translatable(translatable.getKey(), newArgs).withStyle(baseStyle);
        } else if (component.getContents() instanceof PlainTextContents plain) {
            result = colorizeAllPlayersInText(plain.text(), players, baseStyle);
        } else {
            result = MutableComponent.create(component.getContents()).withStyle(baseStyle);
        }

        for (Component sibling : component.getSiblings()) {
            result.append(replacePlayerNames(sibling, players));
        }

        return result;
    }

    private MutableComponent colorizeAllPlayersInText(String text, List<PlayerMatch> players, Style baseStyle) {
        MutableComponent comp = Component.empty();
        int cursor = 0;
        int len = text.length();

        while (cursor < len) {
            int earliestIndex = -1;
            PlayerMatch bestMatch = null;

            for (PlayerMatch player : players) {
                int index = text.indexOf(player.name(), cursor);
                while (index != -1) {
                    if (isWordBoundary(text, index, player.name().length())) {
                        if (earliestIndex == -1 || index < earliestIndex || (index == earliestIndex && player.name().length() > bestMatch.name().length())) {
                            earliestIndex = index;
                            bestMatch = player;
                        }
                        break;
                    }
                    index = text.indexOf(player.name(), index + 1);
                }
            }

            if (earliestIndex == -1 || bestMatch == null) {
                comp.append(Component.literal(text.substring(cursor)).withStyle(baseStyle));
                break;
            }

            if (earliestIndex > cursor) {
                comp.append(Component.literal(text.substring(cursor, earliestIndex)).withStyle(baseStyle));
            }

            Style coloredStyle = (baseStyle != null ? baseStyle : Style.EMPTY).withColor(bestMatch.color());
            comp.append(Component.literal(bestMatch.name()).withStyle(coloredStyle));

            cursor = earliestIndex + bestMatch.name().length();
        }

        return comp;
    }

    private boolean isWordBoundary(String text, int index, int matchLen) {
        int before = index - 1;
        if (before >= 0) {
            char c = text.charAt(before);
            if (Character.isLetterOrDigit(c) || c == '_') {
                return false;
            }
        }

        int after = index + matchLen;
        if (after < text.length()) {
            char c = text.charAt(after);
            if (Character.isLetterOrDigit(c) || c == '_') {
                return false;
            }
        }

        return true;
    }
}