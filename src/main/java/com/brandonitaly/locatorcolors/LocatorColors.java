package com.brandonitaly.locatorcolors;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

//? if fabric {
import net.fabricmc.api.ModInitializer;
//?} else if neoforge {
/*import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
*///?}

//? if fabric {
public class LocatorColors implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Locator Colors");
        
        // Fabric registers its ModMenu config screen automatically via fabric.mod.json, 
        // so no manual client initialization is needed here!
    }
}
//?} else if neoforge {
/*@Mod("locatorcolors")
public class LocatorColors {
    private static final Logger LOGGER = LogUtils.getLogger();

    public LocatorColors(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Initializing Locator Colors");

        if (FMLEnvironment.getDist().isClient()) {
            com.brandonitaly.locatorcolors.client.LocatorColorsClient.init(modEventBus, modContainer);
        }
    }
}*/
//?}