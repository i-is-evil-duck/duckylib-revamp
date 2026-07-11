package com.j3ly.duckylib.gui.widget;

import com.j3ly.duckylib.gui.theme.Theme;
import com.j3ly.duckylib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.SharedConstants;

public class TextField extends Widget {
    private String value = "";
    private String placeholder = "";
    private int maxLength = 32;
    private int textColor;
    private int placeholderColor;
    private int backgroundColor;
    private int focusedBackgroundColor;
    private int borderColor;
    private int borderRadius;
    private boolean bgTransparent = false;
    private int cursorPos;
    private long focusedTime;
    private boolean editable = true;

    public TextField(String id, int x, int y, int width, int height) {
        super(id, x, y, width, height);
        Theme theme = Theme.getCurrent();
        this.textColor = theme.getColor("text");
        this.placeholderColor = theme.getColor("text_secondary");
        this.backgroundColor = theme.getColor("textfield.background");
        this.focusedBackgroundColor = theme.getColor("textfield.background_focused");
        this.borderRadius = 4;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        int absX = getAbsoluteX();
        int absY = getAbsoluteY();

        if (!bgTransparent) {
            int bgColor = focused ? focusedBackgroundColor : backgroundColor;
            RenderUtil.fillRounded(graphics, absX, absY, width, height, borderRadius, bgColor);
        }

        if (focused) {
            RenderUtil.drawRoundedBorder(graphics, absX, absY, width, height, borderRadius, 1, Theme.getCurrent().getColor("border_focused"));
        } else if (borderColor != 0) {
            RenderUtil.drawRoundedBorder(graphics, absX, absY, width, height, borderRadius, 1, borderColor);
        }

        Minecraft mc = Minecraft.getInstance();
        int padding = 6;
        int textY = absY + (height - mc.font.lineHeight) / 2;

        String displayText = value.isEmpty() && !focused ? placeholder : value;
        int displayColor = value.isEmpty() && !focused ? placeholderColor : textColor;

        int maxTextWidth = width - padding * 2;
        String clipped = mc.font.plainSubstrByWidth(displayText, maxTextWidth);

        graphics.drawString(mc.font, Component.literal(clipped), absX + padding, textY, displayColor, false);

        if (focused && (System.currentTimeMillis() / 500) % 2 == 0) {
            int cursorX = absX + padding + mc.font.width(clipped.substring(0, Math.min(cursorPos, clipped.length())));
            graphics.fill(cursorX, textY - 1, cursorX + 1, textY + mc.font.lineHeight + 1, Theme.getCurrent().getColor("textfield.cursor_color"));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;
        if (contains(mouseX, mouseY)) {
            focused = true;
            focusedTime = System.currentTimeMillis();
            return true;
        } else {
            focused = false;
            return false;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused || !editable) return false;

        if (keyCode == 259) {
            if (cursorPos > 0) {
                value = value.substring(0, cursorPos - 1) + value.substring(cursorPos);
                cursorPos--;
            }
            return true;
        } else if (keyCode == 262) {
            cursorPos = Math.min(cursorPos + 1, value.length());
            return true;
        } else if (keyCode == 263) {
            cursorPos = Math.max(cursorPos - 1, 0);
            return true;
        } else if (keyCode == 257) {
            focused = false;
            return true;
        }

        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!focused || !editable) return false;
        if (SharedConstants.isAllowedChatCharacter(codePoint) && value.length() < maxLength) {
            value = value.substring(0, cursorPos) + codePoint + value.substring(cursorPos);
            cursorPos++;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) { return false; }
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) { return false; }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) { return false; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; cursorPos = value.length(); }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }
    public String getPlaceholder() { return placeholder; }
    public void setMaxLength(int maxLength) { this.maxLength = maxLength; }
    public void setTextColor(int color) { this.textColor = color; }
    public int getTextColor() { return textColor; }
    public void setBackgroundColor(int color) { this.backgroundColor = color; }
    public int getBackgroundColor() { return backgroundColor; }
    public void setBorderColor(int color) { this.borderColor = color; }
    public int getBorderColor() { return borderColor; }
    public void setBgTransparent(boolean bgTransparent) { this.bgTransparent = bgTransparent; }
    public boolean isBgTransparent() { return bgTransparent; }
}
