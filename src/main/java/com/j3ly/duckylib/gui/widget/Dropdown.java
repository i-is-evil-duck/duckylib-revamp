package com.j3ly.duckylib.gui.widget;

import com.j3ly.duckylib.gui.theme.Theme;
import com.j3ly.duckylib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class Dropdown extends Widget {
    private List<String> options = new ArrayList<>();
    private int selectedIndex = 0;
    private boolean expanded = false;
    private int maxHeight;
    private int itemHeight;
    private int backgroundColor;
    private int hoverColor;
    private int textColor;
    private int borderRadius;

    public Dropdown(String id, int x, int y, int width, int height) {
        super(id, x, y, width, height);
        Theme theme = Theme.getCurrent();
        this.maxHeight = 120;
        this.itemHeight = 20;
        this.backgroundColor = theme.getColor("panel_bg");
        this.hoverColor = theme.getColor("panel_bg_hover");
        this.textColor = theme.getColor("text");
        this.borderRadius = 4;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        int absX = getAbsoluteX();
        int absY = getAbsoluteY();

        Minecraft mc = Minecraft.getInstance();

        RenderUtil.fillRounded(graphics, absX, absY, width, height, borderRadius, backgroundColor);
        RenderUtil.drawRoundedBorder(graphics, absX, absY, width, height, borderRadius, 1, Theme.getCurrent().getColor("border"));

        String selected = options.isEmpty() ? "" : options.get(selectedIndex);
        graphics.drawString(mc.font, Component.literal(selected), absX + 6, absY + (height - mc.font.lineHeight) / 2, textColor, false);

        String arrow = expanded ? "▲" : "▼";
        graphics.drawString(mc.font, Component.literal(arrow), absX + width - 16, absY + (height - mc.font.lineHeight) / 2, textColor, false);

        if (expanded) {
            int listHeight = Math.min(options.size() * itemHeight, maxHeight);
            int listY = absY + height;

            RenderUtil.fillRounded(graphics, absX, listY, width, listHeight, borderRadius, backgroundColor);
            RenderUtil.drawRoundedBorder(graphics, absX, listY, width, listHeight, borderRadius, 1, Theme.getCurrent().getColor("border"));

            for (int i = 0; i < options.size(); i++) {
                int itemY = listY + i * itemHeight;
                if (itemY + itemHeight > listY + listHeight) break;

                boolean itemHovered = mouseX >= absX && mouseX < absX + width && mouseY >= itemY && mouseY < itemY + itemHeight;
                if (itemHovered) {
                    RenderUtil.fill(graphics, absX + 2, itemY, absX + width - 2, itemY + itemHeight, hoverColor);
                }

                graphics.drawString(mc.font, Component.literal(options.get(i)), absX + 6, itemY + (itemHeight - mc.font.lineHeight) / 2, textColor, false);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;

        int absX = getAbsoluteX();
        int absY = getAbsoluteY();

        if (expanded) {
            int listY = absY + height;
            int listHeight = Math.min(options.size() * itemHeight, maxHeight);

            if (mouseX >= absX && mouseX < absX + width && mouseY >= listY && mouseY < listY + listHeight) {
                int index = (int)((mouseY - listY) / itemHeight);
                if (index >= 0 && index < options.size()) {
                    selectedIndex = index;
                    expanded = false;
                    if (onClick != null) onClick.run();
                }
                return true;
            }
            expanded = false;
            return true;
        }

        if (contains(mouseX, mouseY)) {
            expanded = !expanded;
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

    public void addOption(String option) { options.add(option); }
    public void setOptions(List<String> options) {
        this.options = new ArrayList<>(options);
        if (selectedIndex >= this.options.size()) {
            selectedIndex = Math.max(0, this.options.size() - 1);
        }
    }
    public List<String> getOptions() { return new ArrayList<>(options); }
    public String getSelected() { return options.isEmpty() ? "" : options.get(selectedIndex); }
    public int getSelectedIndex() { return selectedIndex; }
    public void setSelectedIndex(int index) { this.selectedIndex = Math.max(0, Math.min(index, options.size() - 1)); }
    public void setBackgroundColor(int color) { this.backgroundColor = color; }
    public int getBackgroundColor() { return backgroundColor; }
    public void setTextColor(int color) { this.textColor = color; }
    public int getTextColor() { return textColor; }
}
