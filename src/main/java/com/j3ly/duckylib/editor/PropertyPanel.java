package com.j3ly.duckylib.editor;

import com.j3ly.duckylib.gui.theme.Theme;
import com.j3ly.duckylib.gui.widget.*;
import com.j3ly.duckylib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class PropertyPanel {
    private int x, y, width, height;
    private Widget target;
    private boolean visible = true;
    private int editingX, editingY, editingW, editingH;

    public PropertyPanel() {
        this.width = 180;
        this.height = 300;
        updatePosition();
    }

    private void updatePosition() {
        this.x = Minecraft.getInstance().getWindow().getGuiScaledWidth() - width - 10;
        this.y = 30;
    }

    public void setWidget(Widget widget) {
        this.target = widget;
        this.visible = widget != null;
        if (widget != null) {
            editingX = widget.getX();
            editingY = widget.getY();
            editingW = widget.getWidth();
            editingH = widget.getHeight();
        }
    }

    public void refresh() {
        if (target != null) {
            editingX = target.getX();
            editingY = target.getY();
            editingW = target.getWidth();
            editingH = target.getHeight();
        }
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        updatePosition();

        RenderUtil.fillRounded(graphics, x, y, width, height, 6, Theme.getCurrent().getColor("panel_bg"));
        RenderUtil.drawRoundedBorder(graphics, x, y, width, height, 6, 1, Theme.getCurrent().getColor("border"));

        Minecraft mc = Minecraft.getInstance();
        int lineY = y + 10;

        graphics.drawString(mc.font, Component.literal("§lWidget Properties"), x + 10, lineY, Theme.getCurrent().getColor("text"), false);
        lineY += 25;

        if (target == null) return;

        graphics.drawString(mc.font, Component.literal("ID: " + target.getId()), x + 10, lineY, Theme.getCurrent().getColor("text_secondary"), false);
        lineY += 20;

        graphics.drawString(mc.font, Component.literal("X: " + editingX), x + 10, lineY, Theme.getCurrent().getColor("text"), false);
        lineY += 15;
        graphics.drawString(mc.font, Component.literal("Y: " + editingY), x + 10, lineY, Theme.getCurrent().getColor("text"), false);
        lineY += 15;
        graphics.drawString(mc.font, Component.literal("W: " + editingW), x + 10, lineY, Theme.getCurrent().getColor("text"), false);
        lineY += 15;
        graphics.drawString(mc.font, Component.literal("H: " + editingH), x + 10, lineY, Theme.getCurrent().getColor("text"), false);
        lineY += 25;

        if (target instanceof Button) {
            graphics.drawString(mc.font, Component.literal("Label: " + ((Button) target).getLabel()), x + 10, lineY, Theme.getCurrent().getColor("text"), false);
        } else if (target instanceof Label) {
            graphics.drawString(mc.font, Component.literal("Text: " + ((Label) target).getText()), x + 10, lineY, Theme.getCurrent().getColor("text"), false);
        } else if (target instanceof Checkbox) {
            graphics.drawString(mc.font, Component.literal("Checked: " + ((Checkbox) target).isChecked()), x + 10, lineY, Theme.getCurrent().getColor("text"), false);
        } else if (target instanceof Slider) {
            graphics.drawString(mc.font, Component.literal("Value: " + ((Slider) target).getValue()), x + 10, lineY, Theme.getCurrent().getColor("text"), false);
        } else if (target instanceof Dropdown) {
            graphics.drawString(mc.font, Component.literal("Selected: " + ((Dropdown) target).getSelected()), x + 10, lineY, Theme.getCurrent().getColor("text"), false);
        } else if (target instanceof TextField) {
            graphics.drawString(mc.font, Component.literal("Value: " + ((TextField) target).getValue()), x + 10, lineY, Theme.getCurrent().getColor("text"), false);
        }

        lineY += 30;

        graphics.drawString(mc.font, Component.literal("§7Use /duckyeditor export"), x + 10, lineY, Theme.getCurrent().getColor("text_secondary"), false);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return visible && mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public boolean contains(double mouseX, double mouseY) {
        return visible && mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
