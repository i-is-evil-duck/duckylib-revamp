package com.j3ly.duckylib.gui.widget;

import com.j3ly.duckylib.gui.theme.Theme;
import com.j3ly.duckylib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class Button extends Widget {
    private String label;
    private int labelColor;
    private int backgroundColor;
    private int hoverColor;
    private int activeColor;
    private int borderRadius;

    public Button(String id, int x, int y, int width, int height, String label) {
        super(id, x, y, width, height);
        this.label = label;
        Theme theme = Theme.getCurrent();
        this.labelColor = theme.getColor("text");
        this.backgroundColor = theme.getColor("primary");
        this.hoverColor = theme.getColor("primary_hover");
        this.activeColor = theme.getColor("primary_active");
        this.borderRadius = 4;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        int absX = getAbsoluteX();
        int absY = getAbsoluteY();

        int color = backgroundColor;
        if (!enabled) {
            color = Theme.getCurrent().getColor("text_disabled");
        } else if (contains(mouseX, mouseY)) {
            color = hoverColor;
        }

        RenderUtil.fillRounded(graphics, absX, absY, width, height, borderRadius, color);

        Minecraft mc = Minecraft.getInstance();
        int textWidth = mc.font.width(label);
        int textX = absX + (width - textWidth) / 2;
        int textY = absY + (height - mc.font.lineHeight) / 2;
        graphics.drawString(mc.font, Component.literal(label), textX, textY, labelColor, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;
        if (contains(mouseX, mouseY)) {
            if (onClick != null) onClick.run();
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
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }
    @Override
    public boolean charTyped(char codePoint, int modifiers) { return false; }

    public void setLabel(String label) { this.label = label; }
    public String getLabel() { return label; }
    public void setBackgroundColor(int color) { this.backgroundColor = color; }
    public int getBackgroundColor() { return backgroundColor; }
    public void setLabelColor(int color) { this.labelColor = color; }
    public int getLabelColor() { return labelColor; }
}
