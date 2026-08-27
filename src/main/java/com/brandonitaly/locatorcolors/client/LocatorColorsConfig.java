package com.brandonitaly.locatorcolors.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;
import java.util.Arrays;

public class LocatorColorsConfig {
    //? if fabric {
    private static final Path CONFIG_PATH = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("locatorcolors.json");
    //?} else {
    /*private static final Path CONFIG_PATH = net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get().resolve("locatorcolors.json");
    *///?}

    public enum LocatorHeadMode {
        ON_KEY("on_key"),
        ALWAYS("always"),
        NEVER("never");

        private final String serializedName;

        LocatorHeadMode(String serializedName) {
            this.serializedName = serializedName;
        }

        public String getSerializedName() {
            return serializedName;
        }

        public static LocatorHeadMode fromString(String str) {
            for (LocatorHeadMode mode : values()) {
                if (mode.serializedName.equalsIgnoreCase(str) || mode.name().equalsIgnoreCase(str)) {
                    return mode;
                }
            }
            return ON_KEY;
        }
    }

    private static volatile boolean colorizeChat;
    private static volatile boolean colorizeTabList;
    private static volatile boolean colorizeNameTags;
    private static volatile boolean colorizeSelf;
    private static volatile LocatorHeadMode locatorHeadMode;
    private static volatile boolean showHeadBorders;
    private static volatile boolean showLocatorDistance;

    private record ConfigData(
        boolean colorizeChat,
        boolean colorizeTabList,
        boolean colorizeNameTags,
        boolean colorizeSelf,
        LocatorHeadMode locatorHeadMode,
        boolean showHeadBorders,
        boolean showLocatorDistance
    ) {}

    private static final ConfigData DEFAULTS = new ConfigData(true, true, true, false, LocatorHeadMode.ON_KEY, true, true);

    private static final Codec<LocatorHeadMode> HEAD_MODE_CODEC = Codec.STRING.xmap(LocatorHeadMode::fromString, LocatorHeadMode::getSerializedName);

    private static final Codec<ConfigData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("colorizeChat", DEFAULTS.colorizeChat()).forGetter(ConfigData::colorizeChat),
        Codec.BOOL.optionalFieldOf("colorizeTabList", DEFAULTS.colorizeTabList()).forGetter(ConfigData::colorizeTabList),
        Codec.BOOL.optionalFieldOf("colorizeNameTags", DEFAULTS.colorizeNameTags()).forGetter(ConfigData::colorizeNameTags),
        Codec.BOOL.optionalFieldOf("colorizeSelf", DEFAULTS.colorizeSelf()).forGetter(ConfigData::colorizeSelf),
        HEAD_MODE_CODEC.optionalFieldOf("locatorHeadMode", DEFAULTS.locatorHeadMode()).forGetter(ConfigData::locatorHeadMode),
        Codec.BOOL.optionalFieldOf("showHeadBorders", DEFAULTS.showHeadBorders()).forGetter(ConfigData::showHeadBorders),
        Codec.BOOL.optionalFieldOf("showLocatorDistance", DEFAULTS.showLocatorDistance()).forGetter(ConfigData::showLocatorDistance)
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

    public static final OptionInstance<Boolean> COLORIZE_SELF = OptionInstance.createBoolean(
        "locatorcolors.option.colorize_self", value -> Tooltip.create(Component.translatable("locatorcolors.option.colorize_self.tooltip")),
        isColorizeSelfEnabled(), LocatorColorsConfig::setColorizeSelf
    );

    public static final OptionInstance<LocatorHeadMode> LOCATOR_HEAD_MODE = new OptionInstance<>(
        "locatorcolors.option.locator_head_mode",
        value -> Tooltip.create(Component.translatable("locatorcolors.option.locator_head_mode.tooltip." + value.getSerializedName())),
        (component, value) -> Component.translatable("locatorcolors.option.locator_head_mode." + value.getSerializedName()),
        new OptionInstance.Enum<>(Arrays.asList(LocatorHeadMode.values()), HEAD_MODE_CODEC),
        getLocatorHeadMode(),
        LocatorColorsConfig::setLocatorHeadMode
    );

    public static final OptionInstance<Boolean> SHOW_HEAD_BORDERS = OptionInstance.createBoolean(
        "locatorcolors.option.show_head_borders", value -> Tooltip.create(Component.translatable("locatorcolors.option.show_head_borders.tooltip")),
        isShowHeadBordersEnabled(), LocatorColorsConfig::setShowHeadBorders
    );

    public static final OptionInstance<Boolean> SHOW_LOCATOR_DISTANCE = OptionInstance.createBoolean(
        "locatorcolors.option.show_locator_distance", value -> Tooltip.create(Component.translatable("locatorcolors.option.show_locator_distance.tooltip")),
        isShowLocatorDistanceEnabled(), LocatorColorsConfig::setShowLocatorDistance
    );

    // Getters & Setters
    public static boolean isColorizeChatEnabled() { return colorizeChat; }
    public static void setColorizeChat(boolean enabled) { if (colorizeChat != enabled) { colorizeChat = enabled; save(); } }

    public static boolean isColorizeTabListEnabled() { return colorizeTabList; }
    public static void setColorizeTabList(boolean enabled) { if (colorizeTabList != enabled) { colorizeTabList = enabled; save(); } }

    public static boolean isColorizeNameTagsEnabled() { return colorizeNameTags; }
    public static void setColorizeNameTags(boolean enabled) { if (colorizeNameTags != enabled) { colorizeNameTags = enabled; save(); } }

    public static boolean isColorizeSelfEnabled() { return colorizeSelf; }
    public static void setColorizeSelf(boolean enabled) { if (colorizeSelf != enabled) { colorizeSelf = enabled; save(); } }

    public static LocatorHeadMode getLocatorHeadMode() { return locatorHeadMode != null ? locatorHeadMode : LocatorHeadMode.ON_KEY; }
    public static void setLocatorHeadMode(LocatorHeadMode mode) { if (locatorHeadMode != mode) { locatorHeadMode = mode; save(); } }

    public static boolean isShowHeadBordersEnabled() { return showHeadBorders; }
    public static void setShowHeadBorders(boolean enabled) { if (showHeadBorders != enabled) { showHeadBorders = enabled; save(); } }

    public static boolean isShowLocatorDistanceEnabled() { return showLocatorDistance; }
    public static void setShowLocatorDistance(boolean enabled) { if (showLocatorDistance != enabled) { showLocatorDistance = enabled; save(); } }

    public static OptionInstance<?>[] asOptions() {
        return new OptionInstance<?>[] {
            COLORIZE_CHAT,
            COLORIZE_TAB_LIST,
            COLORIZE_NAME_TAGS,
            COLORIZE_SELF,
            LOCATOR_HEAD_MODE,
            SHOW_HEAD_BORDERS,
            SHOW_LOCATOR_DISTANCE
        };
    }

    private static void load() {
        ConfigData data = JsonCodecFileStore.read(CONFIG_PATH, CODEC, DEFAULTS, "LocatorColorsConfig");
        colorizeChat = data.colorizeChat();
        colorizeTabList = data.colorizeTabList();
        colorizeNameTags = data.colorizeNameTags();
        colorizeSelf = data.colorizeSelf();
        locatorHeadMode = data.locatorHeadMode() != null ? data.locatorHeadMode() : LocatorHeadMode.ON_KEY;
        showHeadBorders = data.showHeadBorders();
        showLocatorDistance = data.showLocatorDistance();
    }

    private static void save() {
        JsonCodecFileStore.write(CONFIG_PATH, CODEC, new ConfigData(colorizeChat, colorizeTabList, colorizeNameTags, colorizeSelf, getLocatorHeadMode(), showHeadBorders, showLocatorDistance), "LocatorColorsConfig");
    }

    public static void resetToDefault() {
        COLORIZE_CHAT.set(DEFAULTS.colorizeChat());
        COLORIZE_TAB_LIST.set(DEFAULTS.colorizeTabList());
        COLORIZE_NAME_TAGS.set(DEFAULTS.colorizeNameTags());
        COLORIZE_SELF.set(DEFAULTS.colorizeSelf());
        LOCATOR_HEAD_MODE.set(DEFAULTS.locatorHeadMode());
        SHOW_HEAD_BORDERS.set(DEFAULTS.showHeadBorders());
        SHOW_LOCATOR_DISTANCE.set(DEFAULTS.showLocatorDistance());
        save();
    }
}