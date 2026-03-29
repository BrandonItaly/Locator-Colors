package com.brandonitaly.locatorcolors.client;

//? if fabric {
import com.brandonitaly.locatorcolors.client.gui.LocatorColorsConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return LocatorColorsConfigScreen::new;
    }
}
//?}