package dev.yyuh.enchantinggobrrrr.client.settings;

import dev.yyuh.enchantinggobrrrr.client.EnchantingGoBrrrrClient;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

public class SettingsUIBuilder {
    private static final int DEFAULT_BACKGROUND = 0x80333333;
    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFF;
    private static final int DEFAULT_HOVER_COLOR = 0x20FFFFFF;
    private static final int DEFAULT_PADDING = 5;
    private static final int DEFAULT_TITLE_BAR_HEIGHT = 20;
    private static final int DEFAULT_COMPONENT_SPACING = 5;
    private static final int DEFAULT_COMPONENT_HEIGHT = 20;
    private static final int DEFAULT_TEXT_SPACING = 2;

    private final Screen parentScreen;
    private final String title;
    private final List<UIComponent> components = new ArrayList<>();
    private String description;

    private int windowWidth = 400;
    private int backgroundColor = DEFAULT_BACKGROUND;
    private int textColor = DEFAULT_TEXT_COLOR;
    private int hoverColor = DEFAULT_HOVER_COLOR;
    private int padding = DEFAULT_PADDING;
    private int titleBarHeight = DEFAULT_TITLE_BAR_HEIGHT;
    private int componentSpacing = DEFAULT_COMPONENT_SPACING;
    private int componentHeight = DEFAULT_COMPONENT_HEIGHT;
    private int textSpacing = DEFAULT_TEXT_SPACING;

    private SettingsUIBuilder(Screen parentScreen, String title) {
        this.parentScreen = parentScreen;
        this.title = title;
    }

    public static SettingsUIBuilder create(Screen parentScreen, String title) {
        return new SettingsUIBuilder(parentScreen, title);
    }

    public SettingsUIBuilder withWindowWidth(int width) {
        this.windowWidth = width;
        return this;
    }

    public SettingsUIBuilder addTextField(String label, String initialValue, Consumer<String> onChange) {
        components.add(new TextFieldComponent(label, initialValue, onChange, null, null));
        return this;
    }

    public SettingsUIBuilder addTextField(String label, String initialValue, Consumer<String> onChange, String description) {
        components.add(new TextFieldComponent(label, initialValue, onChange, description, null));
        return this;
    }

    public SettingsUIBuilder addTextField(String label, String initialValue, Consumer<String> onChange, String description, String headline) {
        components.add(new TextFieldComponent(label, initialValue, onChange, description, headline));
        return this;
    }

    public SettingsUIBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public SettingsUIBuilder addColorPicker(String text, int initialColor, int fallback, IntConsumer onChange) {
        components.add(new ColorPickerComponent(text, initialColor, onChange, null, fallback));
        return this;
    }

    public SettingsUIBuilder addColorPicker(String text, int initialColor, int fallback, IntConsumer onChange, String description) {
        components.add(new ColorPickerComponent(text, initialColor, onChange, description, fallback));
        return this;
    }

    public SettingsUIBuilder addToggle(String text, boolean initialValue, BooleanConsumer onChange) {
        components.add(new ToggleComponent(text, initialValue, onChange, null));
        return this;
    }

    public SettingsUIBuilder addToggle(String text, boolean initialValue, BooleanConsumer onChange, String description) {
        components.add(new ToggleComponent(text, initialValue, onChange, description));
        return this;
    }

    public SettingsUIBuilder addButton(String text, Runnable onClick) {
        components.add(new ButtonComponent(() -> text, onClick, null, null));
        return this;
    }

    public SettingsUIBuilder addButton(Supplier<String> textSupplier, Runnable onClick) {
        components.add(new ButtonComponent(textSupplier, onClick, null, null));
        return this;
    }

    public SettingsUIBuilder addButton(String text, Runnable onClick, String headline) {
        components.add(new ButtonComponent(() -> text, onClick, headline, null));
        return this;
    }

    public SettingsUIBuilder addButton(Supplier<String> textSupplier, Runnable onClick, String headline) {
        components.add(new ButtonComponent(textSupplier, onClick, headline, null));
        return this;
    }

    public SettingsUIBuilder addButton(String text, Runnable onClick, String headline, String description) {
        components.add(new ButtonComponent(() -> text, onClick, headline, description));
        return this;
    }

    public SettingsUIBuilder addButton(Supplier<String> textSupplier, Runnable onClick, String headline, String description) {
        components.add(new ButtonComponent(textSupplier, onClick, headline, description));
        return this;
    }

    public SettingsUIBuilder addText(String text) {
        components.add(new TextComponent(text, false));
        return this;
    }

    public SettingsUIBuilder addHeadline(String text) {
        components.add(new TextComponent(text, true));
        return this;
    }

    public SettingsUIBuilder addSpace(int height) {
        components.add(new SpaceComponent(height));
        return this;
    }

    public Screen build() {
        int totalHeight = titleBarHeight + padding;

        if (description != null) {
            totalHeight += 10;
        }

        if (!components.isEmpty()) {
            totalHeight += padding;

            for (UIComponent component : components) {
                totalHeight += component.getHeight(this);
                if (component.hasSpacing()) {
                    totalHeight += componentSpacing;
                }
            }

            if (!components.isEmpty()) {
                totalHeight -= componentSpacing;
            }
        }

        return new BuiltSettingsScreen(this, totalHeight);
    }

    private abstract static class UIComponent {
        abstract void create(BuiltSettingsScreen screen, SettingsUIBuilder builder, int x, int y);

        abstract int getHeight(SettingsUIBuilder builder);

        boolean hasSpacing() {
            return true;
        }
    }

    private static class TextFieldComponent extends UIComponent {
        private final String label;
        private final String initialValue;
        private final Consumer<String> onChange;
        private final String description;
        private final String headline;

        TextFieldComponent(String label, String initialValue, Consumer<String> onChange, String description, String headline) {
            this.label = label;
            this.initialValue = initialValue;
            this.onChange = onChange;
            this.description = description;
            this.headline = headline;
        }

        @Override
        void create(BuiltSettingsScreen screen, SettingsUIBuilder builder, int x, int y) {
            int currentY = y;

            if (headline != null) {
                screen.addTextElement(new TextElement(
                        x, currentY,
                        builder.windowWidth - builder.padding * 2,
                        headline,
                        true
                ));
                currentY += 10 + builder.textSpacing;
            }

            if (label != null && !label.isEmpty()) {
                screen.addTextElement(new TextElement(
                        x, currentY,
                        builder.windowWidth - builder.padding * 2,
                        label,
                        false
                ));
                currentY += 10 + builder.textSpacing;
            }

            TextFieldWidget textField = new TextFieldWidget(
                    MinecraftClient.getInstance().textRenderer,
                    x, currentY,
                    builder.windowWidth - builder.padding * 2,
                    builder.componentHeight,
                    Text.of("")
            ) {
                @Override
                public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                    context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, builder.backgroundColor);
                    context.drawBorder(this.getX(), this.getY(), this.width, this.height, EnchantingGoBrrrrClient.COLOR);
                    super.renderWidget(context, mouseX, mouseY, delta);
                }
            };
            textField.setMaxLength(32767);
            textField.setText(initialValue);
            textField.setChangedListener(onChange);
            screen.addTextField(textField);
            currentY += builder.componentHeight;

            if (description != null) {
                screen.addTextElement(new TextElement(
                        x, currentY,
                        builder.windowWidth - builder.padding * 2,
                        description,
                        false
                ));
            }
        }

        @Override
        int getHeight(SettingsUIBuilder builder) {
            int height = builder.componentHeight;
            if (headline != null) height += 10 + builder.textSpacing;
            if (label != null && !label.isEmpty()) height += 10 + builder.textSpacing;
            if (description != null) height += 10 + builder.textSpacing;
            return height;
        }
    }

    private static class ToggleComponent extends UIComponent {
        private final String text;
        private final boolean initialValue;
        private final BooleanConsumer onChange;
        private final String description;

        ToggleComponent(String text, boolean initialValue, BooleanConsumer onChange, String description) {
            this.text = text;
            this.initialValue = initialValue;
            this.onChange = onChange;
            this.description = description;
        }

        @Override
        void create(BuiltSettingsScreen screen, SettingsUIBuilder builder, int x, int y) {
            int currentY = y;

            if (description != null) {
                screen.addTextElement(new TextElement(
                        x, currentY,
                        builder.windowWidth - builder.padding * 2,
                        description,
                        false
                ));
                currentY += 10 + builder.textSpacing;
            }

            ToggleButton button = new ToggleButton(
                    x, currentY,
                    builder.windowWidth - builder.padding * 2,
                    builder.componentHeight,
                    text,
                    initialValue,
                    onChange
            );
            screen.addButton(button);
        }

        @Override
        int getHeight(SettingsUIBuilder builder) {
            int height = builder.componentHeight;
            if (description != null) height += 10 + builder.textSpacing;
            return height;
        }
    }

    private static class ButtonComponent extends UIComponent {
        private final Supplier<String> textSupplier;
        private final Runnable onClick;
        private final String description;
        private final String headline;

        ButtonComponent(Supplier<String> textSupplier, Runnable onClick, String headline, String description) {
            this.textSupplier = textSupplier;
            this.onClick = onClick;
            this.description = description;
            this.headline = headline;
        }

        @Override
        void create(BuiltSettingsScreen screen, SettingsUIBuilder builder, int x, int y) {
            int currentY = y;

            if (headline != null) {
                screen.addTextElement(new TextElement(
                        x, currentY,
                        builder.windowWidth - builder.padding * 2,
                        headline,
                        true
                ));
                currentY += 10 + builder.textSpacing;
            }

            ButtonWidget buttonWidget = new ButtonWidget(
                    x, currentY,
                    builder.windowWidth - builder.padding * 2,
                    builder.componentHeight,
                    Text.of(textSupplier.get()),
                    button -> {
                        onClick.run();
                        button.setMessage(Text.of(textSupplier.get()));
                    }, textSupplier -> (MutableText) textSupplier.get()

            ) {
                @Override
                public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                    this.setMessage(Text.of(textSupplier.get()));
                    int bgColor = isHovered() ? builder.hoverColor : builder.backgroundColor;
                    context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bgColor);
                    context.drawBorder(this.getX(), this.getY(), this.width, this.height, EnchantingGoBrrrrClient.COLOR);
                    context.drawCenteredTextWithShadow(
                            MinecraftClient.getInstance().textRenderer,
                            this.getMessage(),
                            this.getX() + this.width / 2,
                            this.getY() + (this.height - 8) / 2,
                            builder.textColor
                    );
                }
            };
            screen.addButton(buttonWidget);
            currentY += builder.componentHeight;

            if (description != null) {
                screen.addTextElement(new TextElement(
                        x, currentY,
                        builder.windowWidth - builder.padding * 2,
                        description,
                        false
                ));
            }
        }

        @Override
        int getHeight(SettingsUIBuilder builder) {
            int height = builder.componentHeight;
            if (headline != null) height += 10 + builder.textSpacing;
            if (description != null) height += 10 + builder.textSpacing;
            return height;
        }
    }

    private static class ColorPickerComponent extends UIComponent {
        private final String text;
        private final int initialColor;
        private final IntConsumer onChange;
        private final String description;
        private final int fallback;

        ColorPickerComponent(String text, int initialColor, IntConsumer onChange, String description, int fallback) {
            this.text = text;
            this.initialColor = initialColor;
            this.onChange = onChange;
            this.fallback = fallback;
            this.description = description;
        }

        @Override
        void create(BuiltSettingsScreen screen, SettingsUIBuilder builder, int x, int y) {
            screen.addTextElement(new TextElement(
                    x, y,
                    builder.windowWidth - builder.padding * 2,
                    text,
                    false
            ));
            y += 10 + builder.textSpacing;

            ColorPickerButton picker = new ColorPickerButton(
                    x, y,
                    builder.windowWidth - builder.padding * 2,
                    builder.componentHeight,
                    "",
                    initialColor,
                    onChange,
                    fallback
            );
            screen.addTextField(picker);

            if (description != null) {
                screen.addTextElement(new TextElement(
                        x, y + builder.componentHeight + builder.textSpacing,
                        builder.windowWidth - builder.padding * 2,
                        description,
                        false
                ));
            }
        }

        @Override
        int getHeight(SettingsUIBuilder builder) {
            int height = builder.componentHeight + 10 + builder.textSpacing;
            if (description != null) height += 10 + builder.textSpacing;
            return height;
        }
    }


    private static class TextComponent extends UIComponent {
        private final String text;
        private final boolean isHeadline;

        TextComponent(String text, boolean isHeadline) {
            this.text = text;
            this.isHeadline = isHeadline;
        }

        @Override
        void create(BuiltSettingsScreen screen, SettingsUIBuilder builder, int x, int y) {
            screen.addTextElement(new TextElement(
                    x, y,
                    builder.windowWidth - builder.padding * 2,
                    text,
                    isHeadline
            ));
        }

        @Override
        int getHeight(SettingsUIBuilder builder) {
            return isHeadline ? 12 : 10;
        }
    }

    private static class SpaceComponent extends UIComponent {
        private final int height;

        SpaceComponent(int height) {
            this.height = height;
        }

        @Override
        void create(BuiltSettingsScreen screen, SettingsUIBuilder builder, int x, int y) {

        }

        @Override
        int getHeight(SettingsUIBuilder builder) {
            return height;
        }

        @Override
        boolean hasSpacing() {
            return false;
        }
    }

    public static class BuiltSettingsScreen extends Screen {
        private final SettingsUIBuilder builder;
        private final int windowHeight;
        private int windowX, windowY;

        protected BuiltSettingsScreen(SettingsUIBuilder builder, int windowHeight) {
            super(Text.of(builder.title));
            this.builder = builder;
            this.windowHeight = windowHeight;
        }

        @Override
        protected void init() {
            this.windowX = (this.width - builder.windowWidth) / 2;
            this.windowY = (this.height - windowHeight) / 2;

            int currentY = windowY + builder.titleBarHeight - 2;

            if (builder.description != null) {
                this.addDrawable(new TextElement(
                        windowX + builder.padding,
                        currentY,
                        builder.windowWidth - builder.padding * 2,
                        builder.description,
                        false
                ));
                currentY += 10;
            }

            currentY += builder.padding;

            for (UIComponent component : builder.components) {
                component.create(this, builder, windowX + builder.padding, currentY);
                currentY += component.getHeight(builder);
                if (component.hasSpacing()) {
                    currentY += builder.componentSpacing;
                }
            }
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
            super.render(context, mouseX, mouseY, partialTicks);
            context.fill(windowX, windowY, windowX + builder.windowWidth, windowY + windowHeight, builder.backgroundColor);
            context.drawBorder(windowX, windowY, builder.windowWidth, windowHeight, EnchantingGoBrrrrClient.COLOR);

            context.drawText(
                    this.textRenderer,
                    this.getTitle(),
                    windowX + builder.padding,
                    windowY + (builder.titleBarHeight - 8) / 2,
                    builder.textColor,
                    false
            );
        }

        public void addTextField(TextFieldWidget textField) {
            this.addDrawableChild(textField);
        }

        public void addButton(ButtonWidget button) {
            this.addDrawableChild(button);
        }

        public void addTextElement(TextElement textElement) {
            this.addDrawable(textElement);
        }

        @Override
        public boolean shouldPause() {
            return false;
        }

        @Override
        public void close() {
            this.client.setScreen(builder.parentScreen);
        }
    }

    private static class TextElement implements Drawable {
        private final int x;
        private final int y;
        private final int width;
        private final String text;
        private final boolean isHeadline;

        public TextElement(int x, int y, int width, String text, boolean isHeadline) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.text = text;
            this.isHeadline = isHeadline;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            if (isHeadline) {
                context.drawText(
                        MinecraftClient.getInstance().textRenderer,
                        Text.of(text).copy().styled(style -> style.withBold(true)),
                        x, y,
                        0xFFFFFF,
                        false
                );
            } else {
                context.drawTextWrapped(
                        MinecraftClient.getInstance().textRenderer,
                        Text.of(text),
                        x, y, width,
                        0xAAAAAA
                );
            }
        }
    }

    public static class ToggleButton extends ButtonWidget {
        private static final int BACKGROUND = 0x80333333;
        private static final int TEXT_COLOR = 0xFFFFFF;

        private final String text;
        private boolean value;
        private final BooleanConsumer onToggle;

        public ToggleButton(int x, int y, int width, int height, String text, boolean initialValue, BooleanConsumer onToggle) {
            super(x, y, width, height, Text.of(text), button -> {
            }, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
            this.text = text;
            this.value = initialValue;
            this.onToggle = onToggle;
        }

        @Override
        public void onPress() {
            this.value = !this.value;
            if (onToggle != null) {
                onToggle.accept(this.value);
            }
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            int bgColor = value ? EnchantingGoBrrrrClient.COLOR : BACKGROUND;
            context.fill(getX(), getY(), getX() + width, getY() + height, bgColor);

            context.drawBorder(getX(), getY(), width, height, EnchantingGoBrrrrClient.COLOR);

            String displayText = text + ": " + (value ? "ON" : "OFF");
            context.drawCenteredTextWithShadow(
                    MinecraftClient.getInstance().textRenderer,
                    Text.of(displayText),
                    getX() + width / 2,
                    getY() + (height - 8) / 2,
                    TEXT_COLOR
            );
        }

        public boolean getValue() {
            return value;
        }
    }

    public static class ColorPickerButton extends TextFieldWidget {
        private static final int BACKGROUND = 0x80333333;
        private static final int TEXT_COLOR = 0xFFFFFF;
        private static final int PREVIEW_WIDTH = 20;
        private static final int RESET_BUTTON_WIDTH = 40;
        private static final int PREVIEW_PADDING = 1;

        private int color;
        private final IntConsumer onColorChange;
        private int fallback;

        public ColorPickerButton(int x, int y, int width, int height, String text, int initialColor, IntConsumer onColorChange, int fallback) {
            super(MinecraftClient.getInstance().textRenderer, x, y, width - PREVIEW_WIDTH - RESET_BUTTON_WIDTH - 4, height, Text.of(text));
            this.color = initialColor;
            this.onColorChange = onColorChange;
            this.setMaxLength(9);
            this.fallback = fallback;
            this.setText(String.format("#%08X", color));
            this.setChangedListener(this::parseColorInput);
        }

        private void parseColorInput(String input) {
            try {
                String cleanInput = input.startsWith("#") ? input.substring(1) : input;

                if (cleanInput.length() == 6) {
                    this.color = 0xFF000000 | Integer.parseInt(cleanInput, 16);
                } else if (cleanInput.length() == 8) {
                    this.color = (int) Long.parseLong(cleanInput, 16);
                } else if (cleanInput.length() == 3) {
                    int r = Integer.parseInt(cleanInput.substring(0, 1), 16) * 17;
                    int g = Integer.parseInt(cleanInput.substring(1, 2), 16) * 17;
                    int b = Integer.parseInt(cleanInput.substring(2, 3), 16) * 17;
                    this.color = 0xFF000000 | (r << 16) | (g << 8) | b;
                } else {
                    return;
                }

                if (onColorChange != null) {
                    onColorChange.accept(color);
                }
            } catch (NumberFormatException e) {

            }
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            context.fill(getX(), getY(), getX() + width + PREVIEW_WIDTH + RESET_BUTTON_WIDTH + 2, getY() + height, BACKGROUND);

            context.fill(getX(), getY(), getX() + width, getY() + height, BACKGROUND);
            context.drawBorder(getX(), getY(), width, height, EnchantingGoBrrrrClient.COLOR);

            super.renderWidget(context, mouseX, mouseY, delta);

            int previewX = getX() + width + 1;
            context.fill(
                    previewX + PREVIEW_PADDING,
                    getY() + PREVIEW_PADDING,
                    previewX + PREVIEW_WIDTH - PREVIEW_PADDING,
                    getY() + height - PREVIEW_PADDING,
                    color
            );
            context.drawBorder(previewX, getY(), PREVIEW_WIDTH, height, EnchantingGoBrrrrClient.COLOR);

            int resetX = previewX + PREVIEW_WIDTH + 1;
            context.fill(
                    resetX + PREVIEW_PADDING,
                    getY() + PREVIEW_PADDING,
                    resetX + RESET_BUTTON_WIDTH - PREVIEW_PADDING,
                    getY() + height - PREVIEW_PADDING,
                    0x80333333
            );
            context.drawBorder(resetX, getY(), RESET_BUTTON_WIDTH, height, EnchantingGoBrrrrClient.COLOR);
            context.drawTextWithShadow(
                    MinecraftClient.getInstance().textRenderer,
                    "Reset",
                    resetX + (RESET_BUTTON_WIDTH - MinecraftClient.getInstance().textRenderer.getWidth("Reset")) / 2,
                    getY() + (height - 8) / 2,
                    TEXT_COLOR
            );
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int resetX = getX() + width + PREVIEW_WIDTH + 2;
            if (mouseX >= resetX && mouseX <= resetX + RESET_BUTTON_WIDTH &&
                    mouseY >= getY() && mouseY <= getY() + height && button == 0) {
                setColor(fallback);
                if (onColorChange != null) {
                    onColorChange.accept(color);
                }
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        public void setColor(int color) {
            this.color = color;
            this.setText(String.format("#%08X", color));
        }

        public int getColor() {
            return color;
        }
    }
}