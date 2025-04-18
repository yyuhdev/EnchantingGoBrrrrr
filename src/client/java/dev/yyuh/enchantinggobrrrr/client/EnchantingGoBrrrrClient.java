package dev.yyuh.enchantinggobrrrr.client;

import dev.yyuh.enchantinggobrrrr.client.config.ConfigUtils;
import dev.yyuh.enchantinggobrrrr.client.settings.SettingsUIBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class EnchantingGoBrrrrClient implements ClientModInitializer {

    public static boolean ENCHANTING_TABLE = false;
    public static boolean INVENTORY = false;
    public static boolean ANVIL = false;
    public static boolean CHEST = false;
    public static boolean SHULKER = false;

    public static int COLOR = 0xFFFF69B4;
    public static int FALLBACK_COLOR = 0xFFFF69B4;

    public static int COLOR_MATCH = 0xAA00FF00;
    public static int COLOR_NO_MATCH = 0xAAFF0000;
    public static int FALLBACK_COLOR_MATCH = 0xAA00FF00;
    public static int FALLBACK_COLOR_NO_MATCH = 0xAAFF0000;

    public static int PINK_COLOR = 0xAAF551F5;
    public static int RED_COLOR = 0xAAF21313;
    public static int FALLBACK_PINK_COLOR = 0xAAF551F5;
    public static int FALLBACK_RED_COLOR = 0xAAF21313;


    private final KeyBinding OPEN_SETTINGS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Open Settings",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "EnchantingGoBrrrr"
    ));

    @Override
    public void onInitializeClient() {

        ENCHANTING_TABLE = ConfigUtils.get("enchanting_table");
        INVENTORY = ConfigUtils.get("inventory");
        ANVIL = ConfigUtils.get("anvil");
        CHEST = ConfigUtils.get("chest");
        SHULKER = ConfigUtils.get("shulker");

        COLOR = ConfigUtils.get("color", FALLBACK_COLOR);
        COLOR_MATCH = ConfigUtils.get("color_match", FALLBACK_COLOR_MATCH);
        COLOR_NO_MATCH = ConfigUtils.get("color_nomatch", FALLBACK_COLOR_NO_MATCH);
        PINK_COLOR = ConfigUtils.get("color_pink", FALLBACK_PINK_COLOR);
        RED_COLOR = ConfigUtils.get("color_red", FALLBACK_RED_COLOR);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_SETTINGS.wasPressed()) {
                SettingsUIBuilder builder = SettingsUIBuilder.create(MinecraftClient.getInstance().currentScreen, "EnchantingGoBrrr Settings")
                        .setDescription("Highlight Enchantments by their type & search for Enchantments.")
                        .addSpace(10)
                        .addToggle("Enchanting Table", ENCHANTING_TABLE, value -> {
                            ENCHANTING_TABLE = value;
                            ConfigUtils.save("enchanting_table", value);
                        }, "Toggle highlighting inside Enchanting Tables")
                        .addToggle("Inventory", INVENTORY, value -> {
                            INVENTORY = value;
                            ConfigUtils.save("inventory", value);
                        }, "Toggle highlighting inside your Inventory")
                        .addToggle("Anvil", ANVIL, value -> {
                            ANVIL = value;
                            ConfigUtils.save("anvil", value);
                        }, "Toggle highlighting inside Anvils")
                        .addToggle("Chests", CHEST, value -> {
                            CHEST = value;
                            ConfigUtils.save("chest", value);
                        }, "Toggle highlighting inside Chests")
                        .addToggle("Shulkers", SHULKER, value -> {
                            SHULKER = value;
                            ConfigUtils.save("shulker", value);
                        }, "Toggle highlighting inside Shulkers")
                        .addSpace(10)
                        .addColorPicker("Color Theme", COLOR, FALLBACK_COLOR, integer -> {
                            COLOR = integer;
                            ConfigUtils.save("color", integer);
                        })
                        .addColorPicker("Pink Highlight Color", PINK_COLOR, FALLBACK_PINK_COLOR, integer -> {
                            PINK_COLOR = integer;
                            ConfigUtils.save("color_pink", integer);
                        })
                        .addColorPicker("Red Highlight Color", RED_COLOR, FALLBACK_RED_COLOR, integer -> {
                            RED_COLOR = integer;
                            ConfigUtils.save("color_red", integer);
                        })
                        .addColorPicker("Search Match Color", COLOR_MATCH, FALLBACK_COLOR_MATCH, integer -> {
                            COLOR_MATCH = integer;
                            ConfigUtils.save("color_match", integer);
                        })
                        .addColorPicker("Search Match Color", COLOR_NO_MATCH, FALLBACK_COLOR_NO_MATCH, integer -> {
                            COLOR_NO_MATCH = integer;
                            ConfigUtils.save("color_nomatch", integer);
                        })
                        .addSpace(5)
                        .addButton("Back", () -> {
                            MinecraftClient.getInstance().setScreen(null);
                        });
                MinecraftClient.getInstance().setScreen(builder.build());
            }
        });
    }
}
