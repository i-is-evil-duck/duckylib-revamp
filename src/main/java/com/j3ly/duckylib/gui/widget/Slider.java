package com.j3ly.duckylib.gui.widget;

import com.j3ly.duckylib.gui.theme.Theme;
import com.j3ly.duckylib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class Slider extends Widget {
    private float min, max, step, value;
    private String label;
    private int trackHeight;
    private int trackColor;
    private int fillColor;
    private int thumbSize;
    private int thumbColor;
    private boolean dragging = false;

    public Slider(String id, int x, int y, int width, float min, float max, float step, float defaultValue) {
        super(id, x, y, width, 20);
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = defaultValue;
        this.label = "{value}";
        Theme theme = Theme.getCurrent();
        this.trackHeight = 6;
        this.trackColor = theme.getColor("slider.track_color");
        this.fillColor = theme.getColor("slider.fill_color");
        this.thumbSize = 14;
        this.thumbColor = theme.getColor("slider.thumb_color");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        int absX = getAbsoluteX();
        int absY = getAbsoluteY();

        Minecraft mc = Minecraft.getInstance();

        String displayLabel = label.replace("{value}", String.format("%.1f", value));
        graphics.drawString(mc.font, Component.literal(displayLabel), absX, absY, Theme.getCurrent().getColor("text"), false);

        int trackY = absY + mc.font.lineHeight + 4;
        int trackWidth = width;

        RenderUtil.fillRounded(graphics, absX, trackY + (thumbSize - trackHeight) / 2, trackWidth, trackHeight, trackHeight / 2, trackColor);

        float percent = (value - min) / (max - min);
        int fillWidth = (int)(trackWidth * percent);
        RenderUtil.fillRounded(graphics, absX, trackY + (thumbSize - trackHeight) / 2, fillWidth, trackHeight, trackHeight / 2, fillColor);

        int thumbX = absX + fillWidth - thumbSize / 2;
        int thumbY = trackY;
        RenderUtil.fillRounded(graphics, thumbX, thumbY, thumbSize, thumbSize, thumbSize / 2, thumbColor);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;
        if (contains(mouseX, mouseY)) {
            dragging = true;
            updateValue(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            updateValue(mouseX);
            return true;
        }
        return false;
    }

    private void updateValue(double mouseX) {
        int absX = getAbsoluteX();
        float percent = (float)Math.max(0, Math.min(1, (mouseX - absX) / width));
        float rawValue = min + (max - min) * percent;
        value = Math.round(rawValue / step) * step;
        value = Math.max(min, Math.min(max, value));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) { return false; }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }
    @Override
    public boolean charTyped(char codePoint, int modifiers) { return false; }

    public float getValue() { return value; }
    public void setValue(float value) { this.value = Math.max(min, Math.min(max, value)); }
    public void setLabel(String label) { this.label = label; }
    public String getLabel() { return label; }
    public float getMin() { return min; }
    public void setMin(float min) { this.min = min; this.value = Math.max(min, Math.min(max, value)); }
    public float getMax() { return max; }
    public void setMax(float max) { this.max = max; this.value = Math.max(min, Math.min(max, value)); }
    public float getStep() { return step; }
    public void setStep(float step) { this.step = Math.max(0.01f, step); }
    public void setTrackColor(int color) { this.trackColor = color; }
    public int getTrackColor() { return trackColor; }
    public void setFillColor(int color) { this.fillColor = color; }
    public int getFillColor() { return fillColor; }
    public void setThumbColor(int color) { this.thumbColor = color; }
    public int getThumbColor() { return thumbColor; }
}
