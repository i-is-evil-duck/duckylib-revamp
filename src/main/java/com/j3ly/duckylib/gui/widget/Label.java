package com.j3ly.duckylib.gui.widget;

import com.j3ly.duckylib.gui.theme.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class Label extends Widget {
    private String text;
    private int color;
    private float scale;
    private boolean shadow;
    private boolean centered;

    public Label(String id, int x, int y, String text) {
        super(id, x, y, 0, 0);
        this.text = text;
        this.color = Theme.getCurrent().getColor("text");
        this.scale = 1.0f;
        this.shadow = false;
        this.centered = false;
        updateSize();
    }

    private void updateSize() {
        Minecraft mc = Minecraft.getInstance();
        this.width = (int)(mc.font.width(text) * scale);
        this.height = (int)(mc.font.lineHeight * scale);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        int absX = getAbsoluteX();
        int absY = getAbsoluteY();

        Minecraft mc = Minecraft.getInstance();

        graphics.pose().pushPose();
        graphics.pose().translate(absX, absY, 0);
        graphics.pose().scale(scale, scale, 1);

        if (centered) {
            int textWidth = mc.font.width(text);
            graphics.drawString(mc.font, Component.literal(text), -textWidth / 2, 0, color, shadow);
        } else {
            graphics.drawString(mc.font, Component.literal(text), 0, 0, color, shadow);
        }

        graphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) { return false; }
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

    public void setText(String text) { 
        this.text = text; 
        updateSize();
    }
    public String getText() { return text; }
    public void setColor(int color) { this.color = color; }
    public int getColor() { return color; }
    public void setScale(float scale) { this.scale = scale; updateSize(); }
    public void setCentered(boolean centered) { this.centered = centered; }
}
