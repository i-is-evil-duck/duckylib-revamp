package com.j3ly.duckylib.gui.widget;

import com.j3ly.duckylib.gui.theme.Theme;
import com.j3ly.duckylib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class Checkbox extends Widget {
    public enum CheckStyle { FILLED, CHECK, CROSS }

    private String label;
    private boolean checked;
    private CheckStyle checkStyle = CheckStyle.FILLED;
    private int boxSize;
    private int checkColor;
    private int boxColor;
    private int boxBorderColor;
    private int labelColor;
    private int spacing;

    public Checkbox(String id, int x, int y, String label) {
        super(id, x, y, 0, 0);
        this.label = label;
        this.checked = false;
        Theme theme = Theme.getCurrent();
        this.boxSize = 14;
        this.checkColor = theme.getColor("checkbox.check_color");
        this.boxColor = theme.getColor("panel_bg");
        this.boxBorderColor = theme.getColor("border");
        this.labelColor = theme.getColor("text");
        this.spacing = 6;
        updateSize();
    }

    private void updateSize() {
        Minecraft mc = Minecraft.getInstance();
        this.width = boxSize + spacing + mc.font.width(label);
        this.height = Math.max(boxSize, mc.font.lineHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        int absX = getAbsoluteX();
        int absY = getAbsoluteY();

        RenderUtil.fillRounded(graphics, absX, absY + (height - boxSize) / 2, boxSize, boxSize, 2, boxColor);
        RenderUtil.drawRoundedBorder(graphics, absX, absY + (height - boxSize) / 2, boxSize, boxSize, 2, 1, boxBorderColor);

        if (checked) {
            int boxX = absX;
            int boxY = absY + (height - boxSize) / 2;
            if (checkStyle == CheckStyle.FILLED) {
                int checkX = boxX + 3;
                int checkY = boxY + 3;
                int checkSize = boxSize - 6;
                RenderUtil.fill(graphics, checkX, checkY, checkX + checkSize, checkY + checkSize, checkColor);
            } else if (checkStyle == CheckStyle.CHECK) {
                int cw = Minecraft.getInstance().font.width("✓");
                graphics.drawString(Minecraft.getInstance().font, Component.literal("✓"), boxX + (boxSize - cw) / 2, boxY + 1, checkColor, false);
            } else if (checkStyle == CheckStyle.CROSS) {
                int cw = Minecraft.getInstance().font.width("✗");
                graphics.drawString(Minecraft.getInstance().font, Component.literal("✗"), boxX + (boxSize - cw) / 2, boxY + 1, checkColor, false);
            }
        }

        Minecraft mc = Minecraft.getInstance();
        graphics.drawString(mc.font, Component.literal(label), absX + boxSize + spacing, absY + (height - mc.font.lineHeight) / 2, labelColor, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;
        if (contains(mouseX, mouseY)) {
            checked = !checked;
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

    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; updateSize(); }
    public void setCheckColor(int color) { this.checkColor = color; }
    public int getCheckColor() { return checkColor; }
    public void setLabelColor(int color) { this.labelColor = color; }
    public int getLabelColor() { return labelColor; }
    public void setCheckStyle(CheckStyle style) { this.checkStyle = style; }
    public CheckStyle getCheckStyle() { return checkStyle; }
}
