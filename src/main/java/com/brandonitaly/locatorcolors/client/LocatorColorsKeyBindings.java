package com.brandonitaly.locatorcolors.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class LocatorColorsKeyBindings {
    public static final KeyMapping INSPECT_LOCATOR = new KeyMapping(
        "key.locatorcolors.inspect_locator",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        KeyMapping.Category.register(Identifier.fromNamespaceAndPath("locatorcolors", "controls"))
    );

    public static boolean isInspectDown() {
        return INSPECT_LOCATOR.isDown();
    }
}
