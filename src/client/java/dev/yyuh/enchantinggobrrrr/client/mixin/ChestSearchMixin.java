package dev.yyuh.enchantinggobrrrr.client.mixin;

import dev.yyuh.enchantinggobrrrr.client.EnchantingGoBrrrrClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(HandledScreen.class)
public abstract class ChestSearchMixin extends Screen {

    protected ChestSearchMixin(Text title) {
        super(title);
    }

    @Unique
    private static final Map<String, Boolean> modules = new HashMap<>();
    static {
        modules.put("Highlighting Red Enchantments: <bool>", false);
        modules.put("Highlighting Pink Enchantments: <bool>", false);
        modules.put("Highlighting Godlike Enchantments: <bool>", false);
    }
    @Unique private boolean render;
    @Unique private boolean isEmptyString;
    @Unique private static TextFieldWidget itemSearchBox;
    @Unique private ButtonWidget resetButton;
    @Unique private static boolean highlightingRed;
    @Unique private static boolean highlightingPink;
    @Unique private static boolean highlightingGodlike;
    @Unique private static String lastSearchQuery = "";

    @Inject(at = @At("RETURN"), method = "init()V")
    private void addSearchBox(CallbackInfo info) {
        render = ((ChestSearchAccessor) this).getHandler() instanceof GenericContainerScreenHandler && EnchantingGoBrrrrClient.CHEST
        || ((ChestSearchAccessor) this).getHandler() instanceof HopperScreenHandler && EnchantingGoBrrrrClient.CHEST
        || ((ChestSearchAccessor) this).getHandler() instanceof ShulkerBoxScreenHandler && EnchantingGoBrrrrClient.SHULKER
        || ((ChestSearchAccessor) this).getHandler() instanceof AnvilScreenHandler && EnchantingGoBrrrrClient.ANVIL
        || ((ChestSearchAccessor) this).getHandler() instanceof EnchantmentScreenHandler && EnchantingGoBrrrrClient.ENCHANTING_TABLE
        || ((ChestSearchAccessor) this).getHandler() instanceof PlayerScreenHandler && EnchantingGoBrrrrClient.INVENTORY;
        if (!render) return;

        int SEARCH_BOX_WIDTH = 80;
        itemSearchBox = new TextFieldWidget(
                this.textRenderer,
                4,
                this.height - 22,
                SEARCH_BOX_WIDTH,
                16,
                Text.literal("Type to search...")
        );
        this.addSelectableChild(itemSearchBox);

        itemSearchBox.setText(lastSearchQuery);
        itemSearchBox.setChangedListener(str -> {
            isEmptyString = str.trim().isEmpty();
            lastSearchQuery = str;
        });
        isEmptyString = lastSearchQuery.trim().isEmpty();

        resetButton = ButtonWidget.builder(Text.literal("Reset"), button -> {
            itemSearchBox.setText("");
            lastSearchQuery = "";
        }).dimensions(SEARCH_BOX_WIDTH + 8, this.height - 24, 36, 20).build();

        ButtonWidget highlightGodlike = ButtonWidget.builder(Text.literal("Godlike Enchants"), button -> {
            if (!highlightingGodlike) {
                highlightingGodlike = true;
                modules.put("Highlighting Godlike Enchantments: <bool>", true);
                return;
            }
            highlightingGodlike = false;
            modules.put("Highlighting Godlike Enchantments: <bool>", false);
        }).dimensions(4, this.height - 106, 85, 20).build();
        ButtonWidget highlightRed = ButtonWidget.builder(Text.literal("Red Enchants"), button -> {
            if (!highlightingRed) {
                highlightingRed = true;
                modules.put("Highlighting Red Enchantments: <bool>", true);
                return;
            }
            highlightingRed = false;
            modules.put("Highlighting Red Enchantments: <bool>", false);
        }).dimensions(4, this.height - 60, 85, 20).build();
        ButtonWidget highlightPink = ButtonWidget.builder(Text.literal("Pink Enchants"), button -> {
            if (!highlightingPink) {
                highlightingPink = true;
                modules.put("Highlighting Pink Enchantments: <bool>", true);
                return;
            }
            highlightingPink = false;
            modules.put("Highlighting Pink Enchantments: <bool>", false);
        }).dimensions(4, this.height - 83, 85, 20).build();

        this.addDrawableChild(resetButton);
        this.addDrawableChild(highlightRed);
        this.addDrawableChild(highlightGodlike);
        this.addDrawableChild(highlightPink);
    }

    @Inject(at = @At("HEAD"), method = "keyPressed(III)Z", cancellable = true)
    private void checkKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            info.setReturnValue(true);
        } else if (render && itemSearchBox.isActive()) {
            itemSearchBox.keyPressed(keyCode, scanCode, modifiers);
            info.setReturnValue(true);
        }
    }

    @Inject(at = @At("TAIL"), method = "drawSlot(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/screen/slot/Slot;)V")
    private void renderMatchingResults(DrawContext context, Slot slot, CallbackInfo info) {
        ItemStack itemStack = slot.getStack();

        if (!render) return;
        if (itemStack.isEmpty()) return;
        if(this.client == null) return;

        Item.TooltipContext tooltipContext = Item.TooltipContext.create(this.client.world);
        List<Text> tooltip = itemStack.getTooltip(tooltipContext, this.client.player, TooltipType.BASIC);

        if(!isEmptyString) {
            String searchText = itemSearchBox.getText().trim().toLowerCase();
            boolean matchesName = slot.getStack().getName().getString().toLowerCase().contains(searchText);
            boolean matchesLore = false;

            for (Text loreLine : tooltip) {
                if (loreLine.getString().toLowerCase().contains(searchText)) {
                    matchesLore = true;
                    break;
                }
            }

            int color = (matchesName || matchesLore) ? 0xAA00FF00 : 0xAAFF0000;
            context.fillGradient(slot.x, slot.y, slot.x + 16, slot.y + 16, color, color);
            return;
        }

        if (highlightingGodlike) {
            for (Text loreLine : tooltip) {
                if (tooltip.getFirst() == loreLine) continue;
                String line = loreLine.getString().toLowerCase();
                if (line.contains("soulbound")
                        || line.contains("energiefeld")
                        || line.contains("klingensturm")
                        || line.contains("mehrfachschuss")
                ) {
                    context.fillGradient(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xAABB0119, 0xAA5C046F);
                    break;
                }
            }
        }

        if (highlightingPink) {
            for (Text loreLine : tooltip) {
                if (tooltip.getFirst() == loreLine) continue;
                if (loreLine.toString().contains("Susanoo")) continue;
                if (containsColor(loreLine, Formatting.LIGHT_PURPLE)) {
                    context.fillGradient(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xAAF551F5, 0xAAF551F5);
                    break;
                }
            }
        }

        if (highlightingRed) {
            for (Text loreLine : tooltip) {
                if (tooltip.getFirst() == loreLine) continue;
                if (loreLine.toString().contains("Shinigami")) continue;
                if (containsColor(loreLine, Formatting.DARK_RED)) {
                    context.fillGradient(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xAAF21313, 0xAAF21313);
                    break;
                }
            }
        }
    }


    @Inject(at = @At("RETURN"), method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V")
    private void renderSearchBox(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (!render) return;
        itemSearchBox.render(context, mouseX, mouseY, delta);
        resetButton.render(context, mouseX, mouseY, delta);

        context.drawText(this.textRenderer, "Highlight Rare Enchantments: ", 4, this.height - 117, -1, true);
        context.drawText(
                this.textRenderer,
                "Search:",
                4,
                this.height - 34,
                -1,
                true
        );

        int lastHeight = 0;
        for(String module : modules.keySet()) {
            String state = (modules.get(module)) ? "§aEnabled" : "§cDisabled";

            lastHeight = lastHeight + 10;
            context.drawText(this.textRenderer, module.replace("<bool>", state), 4, lastHeight, -1, true);
        }
    }

    @Unique
    private boolean containsColor(Text text, Formatting color) {
        if (text.getStyle().getColor() != null &&
                text.getStyle().getColor().equals(TextColor.fromFormatting(color))) {
            return true;
        }
        for (Text sibling : text.getSiblings()) {
            if (containsColor(sibling, color)) {
                return true;
            }
        }

        return false;
    }
}