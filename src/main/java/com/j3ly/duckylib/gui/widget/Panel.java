package com.j3ly.duckylib.gui.widget;

import com.j3ly.duckylib.gui.theme.Theme;
import com.j3ly.duckylib.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class Panel extends Widget {
    private int backgroundColor;
    private int borderColor;
    private int borderWidth;
    private boolean clipChildren = false;

    public Panel(String id, int x, int y, int width, int height) {
        super(id, x, y, width, height);
        Theme theme = Theme.getCurrent();
        this.backgroundColor = theme.getColor("panel_bg");
        this.borderColor = theme.getColor("border");
        this.borderWidth = 1;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        int absX = getAbsoluteX();
        int absY = getAbsoluteY();

        RenderUtil.fillRounded(graphics, absX, absY, width, height, 4, backgroundColor);

        if (borderWidth > 0) {
            RenderUtil.drawRoundedBorder(graphics, absX, absY, width, height, 4, borderWidth, borderColor);
        }

        if (clipChildren) {
            graphics.enableScissor(getAbsoluteX(), getAbsoluteY(), getAbsoluteX() + width, getAbsoluteY() + height);
        }

        for (Widget child : children) {
            child.render(graphics, mouseX, mouseY, partialTick);
        }

        if (clipChildren) {
            graphics.disableScissor();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        if (contains(mouseX, mouseY)) {
            if (onClick != null) onClick.run();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseReleased(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseScrolled(mouseX, mouseY, delta)) return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).charTyped(codePoint, modifiers)) return true;
        }
        return false;
    }

    public void setBackgroundColor(int color) { this.backgroundColor = color; }
    public int getBackgroundColor() { return backgroundColor; }
    public void setBorderColor(int color) { this.borderColor = color; }
    public void setBorderWidth(int width) { this.borderWidth = width; }
    public void setClipChildren(boolean clip) { this.clipChildren = clip; }
}
