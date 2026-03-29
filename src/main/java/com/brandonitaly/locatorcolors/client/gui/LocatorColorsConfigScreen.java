package com.brandonitaly.locatorcolors.client.gui;

import com.brandonitaly.locatorcolors.client.LocatorColorsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.client.OptionInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocatorColorsConfigScreen extends OptionsSubScreen {
    public LocatorColorsConfigScreen(Screen previous) {
        super(previous, Minecraft.getInstance().options, Component.translatable("locatorcolors.gui.mod_options"));
    }

    @Override
    protected void addOptions() {
        if (this.list != null) {
            List<OptionInstance<?>> options = new ArrayList<>(Arrays.asList(LocatorColorsConfig.asOptions()));
            this.list.addSmall(options.toArray(new OptionInstance[0]));
        }
    }

    @Override
    protected void addFooter() {
        LinearLayout footer = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));

        // Reset to Defaults Button
        footer.addChild(Button.builder(Component.translatable("controls.reset"), (button) -> {
            LocatorColorsConfig.resetToDefault();
            this.minecraft.setScreen(new LocatorColorsConfigScreen(this.lastScreen));
        }).build());

        // Done Button
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).build());
    }
}