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
        //? if >=26.1 {
        net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping(LocatorColorsKeyBindings.INSPECT_LOCATOR);
        //?} else {
        /*net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(LocatorColorsKeyBindings.INSPECT_LOCATOR);
        *///?}
    }
    //?}

    //? if neoforge {
    /*public static void init(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, 
            (container, parent) -> new LocatorColorsConfigScreen(parent)
        );
        modBus.addListener((net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent event) -> {
            event.register(LocatorColorsKeyBindings.INSPECT_LOCATOR);
        });
    }
    *///?}
}