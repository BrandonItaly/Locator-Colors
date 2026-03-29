package com.brandonitaly.locatorcolors.mixin;

import com.brandonitaly.locatorcolors.client.LocatorColorsConfig;
import com.brandonitaly.locatorcolors.util.LocatorColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.UUID;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @ModifyVariable(
        method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", 
        at = @At("HEAD"), 
        argsOnly = true,
        ordinal = 0 
    )
    private Component locatorcolors$colorizeChatSender(Component contents) {
        if (!LocatorColorsConfig.isColorizeChatEnabled()) return contents;

        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null || mc.player == null) return contents;

        String rawMessage = contents.getString();
        PlayerInfo sender = null;
        int earliestIndex = Integer.MAX_VALUE;
        
        // Heuristic: Find the player whose name appears earliest in the text
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

    /**
     * Recursively walks the Component tree to isolate and colorize a specific string.
     */
    private Component replaceFirstOccurrence(Component component, String targetName, TextColor color, boolean[] found) {
        MutableComponent result;

        if (component.getContents() instanceof TranslatableContents translatable) {
            Object[] args = translatable.getArgs();
            Object[] newArgs = new Object[args.length];
            
            for (int i = 0; i < args.length; i++) {
                newArgs[i] = args[i] instanceof Component argComp ? replaceFirstOccurrence(argComp, targetName, color, found) : args[i];
            }
            result = Component.translatable(translatable.getKey(), newArgs).withStyle(component.getStyle());
        } 
        else if (!found[0] && component.getContents() instanceof PlainTextContents plain) {
            String text = plain.text();
            int index = text.indexOf(targetName);
            
            if (index != -1) {
                found[0] = true; 
                result = Component.empty().withStyle(component.getStyle());
                
                if (index > 0) result.append(Component.literal(text.substring(0, index)));
                result.append(Component.literal(targetName).withStyle(style -> style.withColor(color)));
                if (index + targetName.length() < text.length()) result.append(Component.literal(text.substring(index + targetName.length())));
            } else {
                result = MutableComponent.create(component.getContents()).withStyle(component.getStyle());
            }
        } 
        else {
            result = MutableComponent.create(component.getContents()).withStyle(component.getStyle());
        }

        for (Component sibling : component.getSiblings()) {
            result.append(replaceFirstOccurrence(sibling, targetName, color, found));
        }

        return result;
    }
}