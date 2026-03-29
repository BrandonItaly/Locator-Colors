package com.brandonitaly.locatorcolors.client;

import com.brandonitaly.locatorcolors.client.gui.LocatorColorsConfigScreen;

//? if neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
*///?}

public class LocatorColorsClient {

    //? if neoforge {
    /*public static void init(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, 
            (container, parent) -> new LocatorColorsConfigScreen(parent)
        );
    }
    *///?}
}