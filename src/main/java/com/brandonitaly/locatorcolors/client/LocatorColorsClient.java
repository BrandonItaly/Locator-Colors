package com.brandonitaly.locatorcolors.client;

import com.brandonitaly.locatorcolors.client.gui.LocatorColorsConfigScreen;
//? if fabric {
import net.fabricmc.api.ClientModInitializer;
//?} else if neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
*///?}

public class LocatorColorsClient /*? if fabric {*/ implements ClientModInitializer /*?}*/ {

    //? if fabric {
    @Override
    public void onInitializeClient() {
    }
    //?}

    //? if neoforge {
    /*public static void init(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, 
            (container, parent) -> new LocatorColorsConfigScreen(parent)
        );
    }
    *///?}
}