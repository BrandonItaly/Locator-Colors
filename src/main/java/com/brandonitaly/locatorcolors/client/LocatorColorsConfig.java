package com.brandonitaly.locatorcolors.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;

public class LocatorColorsConfig {
    //? if fabric {
    private static final Path CONFIG_PATH = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("locatorcolors.json");
    //?} else {
    /*private static final Path CONFIG_PATH = net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get().resolve("locatorcolors.json");
    *///?}

    private static volatile boolean colorizeChat;
    private static volatile boolean colorizeTabList;
    private static volatile boolean colorizeNameTags;
    private static volatile boolean showLocatorHeads;
    private static volatile boolean colorizeSelf;

    private record ConfigData(boolean colorizeChat, boolean colorizeTabList, boolean colorizeNameTags, boolean showLocatorHeads, boolean colorizeSelf) {}

    private static final ConfigData DEFAULTS = new ConfigData(true, true, true, true, false);

    private static final Codec<ConfigData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("colorizeChat", DEFAULTS.colorizeChat()).forGetter(ConfigData::colorizeChat),
        Codec.BOOL.optionalFieldOf("colorizeTabList", DEFAULTS.colorizeTabList()).forGetter(ConfigData::colorizeTabList),
        Codec.BOOL.optionalFieldOf("colorizeNameTags", DEFAULTS.colorizeNameTags()).forGetter(ConfigData::colorizeNameTags),
        Codec.BOOL.optionalFieldOf("showLocatorHeads", DEFAULTS.showLocatorHeads()).forGetter(ConfigData::showLocatorHeads),
        Codec.BOOL.optionalFieldOf("colorizeSelf", DEFAULTS.colorizeSelf()).forGetter(ConfigData::colorizeSelf)
    ).apply(instance, ConfigData::new));

    static { load(); }

    public static final OptionInstance<Boolean> COLORIZE_CHAT = OptionInstance.createBoolean(
        "locatorcolors.option.colorize_chat", value -> Tooltip.create(Component.translatable("locatorcolors.option.colorize_chat.tooltip")),
        isColorizeChatEnabled(), LocatorColorsConfig::setColorizeChat
    );

    public static final OptionInstance<Boolean> COLORIZE_TAB_LIST = OptionInstance.createBoolean(
        "locatorcolors.option.colorize_tab_list", value -> Tooltip.create(Component.translatable("locatorcolors.option.colorize_tab_list.tooltip")),
        isColorizeTabListEnabled(), LocatorColorsConfig::setColorizeTabList
    );

    public static final OptionInstance<Boolean> COLORIZE_NAME_TAGS = OptionInstance.createBoolean(
        "locatorcolors.option.colorize_name_tags", value -> Tooltip.create(Component.translatable("locatorcolors.option.colorize_name_tags.tooltip")),
        isColorizeNameTagsEnabled(), LocatorColorsConfig::setColorizeNameTags
    );

    public static final OptionInstance<Boolean> SHOW_LOCATOR_HEADS = OptionInstance.createBoolean(
        "locatorcolors.option.show_locator_heads", value -> Tooltip.create(Component.translatable("locatorcolors.option.show_locator_heads.tooltip")),
        isShowLocatorHeadsEnabled(), LocatorColorsConfig::setShowLocatorHeads
    );

    public static final OptionInstance<Boolean> COLORIZE_SELF = OptionInstance.createBoolean(
        "locatorcolors.option.colorize_self", value -> Tooltip.create(Component.translatable("locatorcolors.option.colorize_self.tooltip")),
        isColorizeSelfEnabled(), LocatorColorsConfig::setColorizeSelf
    );

    // Getters & Setters
    public static boolean isColorizeChatEnabled() { return colorizeChat; }
    public static void setColorizeChat(boolean enabled) { if (colorizeChat != enabled) { colorizeChat = enabled; save(); } }

    public static boolean isColorizeTabListEnabled() { return colorizeTabList; }
    public static void setColorizeTabList(boolean enabled) { if (colorizeTabList != enabled) { colorizeTabList = enabled; save(); } }

    public static boolean isColorizeNameTagsEnabled() { return colorizeNameTags; }
    public static void setColorizeNameTags(boolean enabled) { if (colorizeNameTags != enabled) { colorizeNameTags = enabled; save(); } }

    public static boolean isShowLocatorHeadsEnabled() { return showLocatorHeads; }
    public static void setShowLocatorHeads(boolean enabled) { if (showLocatorHeads != enabled) { showLocatorHeads = enabled; save(); } }

    public static boolean isColorizeSelfEnabled() { return colorizeSelf; }
    public static void setColorizeSelf(boolean enabled) { if (colorizeSelf != enabled) { colorizeSelf = enabled; save(); } }

    public static OptionInstance<?>[] asOptions() {
        return new OptionInstance<?>[] { COLORIZE_CHAT, COLORIZE_TAB_LIST, COLORIZE_NAME_TAGS, SHOW_LOCATOR_HEADS, COLORIZE_SELF };
    }

    private static void load() {
        ConfigData data = JsonCodecFileStore.read(CONFIG_PATH, CODEC, DEFAULTS, "LocatorColorsConfig");
        colorizeChat = data.colorizeChat();
        colorizeTabList = data.colorizeTabList();
        colorizeNameTags = data.colorizeNameTags();
        showLocatorHeads = data.showLocatorHeads();
        colorizeSelf = data.colorizeSelf();
    }

    private static void save() {
        JsonCodecFileStore.write(CONFIG_PATH, CODEC, new ConfigData(colorizeChat, colorizeTabList, colorizeNameTags, showLocatorHeads, colorizeSelf), "LocatorColorsConfig");
    }

    public static void resetToDefault() {
        COLORIZE_CHAT.set(DEFAULTS.colorizeChat());
        COLORIZE_TAB_LIST.set(DEFAULTS.colorizeTabList());
        COLORIZE_NAME_TAGS.set(DEFAULTS.colorizeNameTags());
        SHOW_LOCATOR_HEADS.set(DEFAULTS.showLocatorHeads());
        COLORIZE_SELF.set(DEFAULTS.colorizeSelf());
        save();
    }
}