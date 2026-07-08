package com.j3ly.duckylib.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import java.util.ArrayList;
import java.util.List;

public abstract class Widget {
    protected String id;
    protected int x, y, width, height;
    protected boolean visible = true;
    protected boolean enabled = true;
    protected boolean hovered = false;
    protected boolean focused = false;
    protected Widget parent;
    protected List<Widget> children = new ArrayList<>();

    protected int marginTop, marginRight, marginBottom, marginLeft;
    protected int paddingTop, paddingRight, paddingBottom, paddingLeft;

    protected Runnable onClick;
    protected Runnable onFocus;
    protected Runnable onBlur;

    public Widget(String id, int x, int y, int width, int height) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
    public abstract boolean mouseClicked(double mouseX, double mouseY, int button);
    public abstract boolean mouseReleased(double mouseX, double mouseY, int button);
    public abstract boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY);
    public abstract boolean mouseScrolled(double mouseX, double mouseY, double delta);
    public abstract boolean keyPressed(int keyCode, int scanCode, int modifiers);
    public abstract boolean charTyped(char codePoint, int modifiers);

    public void tick() {}

    public String getId() { return id; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isHovered() { return hovered; }
    public boolean isFocused() { return focused; }
    public void setFocused(boolean focused) { this.focused = focused; }

    public void setOnClick(Runnable onClick) { this.onClick = onClick; }
    public void setOnFocus(Runnable onFocus) { this.onFocus = onFocus; }
    public void setOnBlur(Runnable onBlur) { this.onBlur = onBlur; }

    public void addChild(Widget child) {
        child.parent = this;
        children.add(child);
    }

    public List<Widget> getChildren() { return children; }
    public Widget getParent() { return parent; }

    public boolean contains(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public int getAbsoluteX() {
        return parent != null ? x + parent.getAbsoluteX() : x;
    }

    public int getAbsoluteY() {
        return parent != null ? y + parent.getAbsoluteY() : y;
    }
}
