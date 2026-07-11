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

    private String[] fields = {"X", "Y", "W", "H"};
    private int editingField = -1;
    private String editBuffer = "";
    private long cursorTime;

    private static final int FIELD_HEIGHT = 14;

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
        this.editingField = -1;
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

        int bg = Theme.getCurrent().getColor("panel_bg");
        RenderUtil.fillRounded(graphics, x, y, width, height, 6, bg);
        int border = Theme.getCurrent().getColor("border");
        RenderUtil.drawRoundedBorder(graphics, x, y, width, height, 6, 1, border);

        Minecraft mc = Minecraft.getInstance();
        int textCol = Theme.getCurrent().getColor("text");
        int secCol = Theme.getCurrent().getColor("text_secondary");
        int lineY = y + 10;

        graphics.drawString(mc.font, Component.literal("§lWidget Properties"), x + 10, lineY, textCol, false);
        lineY += 22;

        if (target == null) {
            graphics.drawString(mc.font, Component.literal("No widget selected"), x + 10, lineY, secCol, false);
            return;
        }

        graphics.drawString(mc.font, Component.literal("ID: " + target.getId()), x + 10, lineY, secCol, false);
        lineY += 18;

        int[] vals = {editingX, editingY, editingW, editingH};
        for (int i = 0; i < fields.length; i++) {
            String label = fields[i] + ": ";
            String val = editingField == i ? editBuffer : String.valueOf(vals[i]);
            boolean hovered = mouseX >= x + 50 && mouseX < x + width - 10 && mouseY >= lineY && mouseY < lineY + FIELD_HEIGHT;

            graphics.drawString(mc.font, Component.literal(label), x + 10, lineY, textCol, false);
            int valCol = editingField == i ? 0xFFFFFF00 : (hovered ? 0xFFA0A0FF : textCol);
            RenderUtil.fill(graphics, x + 50, lineY - 1, x + width - 10, lineY + FIELD_HEIGHT, editingField == i ? 0x40404040 : (hovered ? 0x20333333 : 0x00000000));
            graphics.drawString(mc.font, Component.literal(val), x + 52, lineY, valCol, false);

            if (editingField == i && (System.currentTimeMillis() / 500) % 2 == 0) {
                int cx = x + 52 + mc.font.width(editBuffer.substring(0, Math.min(cursorPos(), editBuffer.length())));
                graphics.fill(cx, lineY, cx + 1, lineY + mc.font.lineHeight, 0xFFFFFFFF);
            }

            lineY += FIELD_HEIGHT + 2;
        }

        lineY += 8;

        graphics.drawString(mc.font, Component.literal("§7Type: " + getTypeName(target)), x + 10, lineY, secCol, false);
        lineY += 18;

        if (target instanceof Button) {
            graphics.drawString(mc.font, Component.literal("Label: " + ((Button) target).getLabel()), x + 10, lineY, textCol, false);
        } else if (target instanceof Label) {
            graphics.drawString(mc.font, Component.literal("Text: " + ((Label) target).getText()), x + 10, lineY, textCol, false);
        } else if (target instanceof Checkbox) {
            graphics.drawString(mc.font, Component.literal("Checked: " + ((Checkbox) target).isChecked()), x + 10, lineY, textCol, false);
        } else if (target instanceof Slider) {
            graphics.drawString(mc.font, Component.literal("Value: " + ((Slider) target).getValue()), x + 10, lineY, textCol, false);
        } else if (target instanceof Dropdown) {
            graphics.drawString(mc.font, Component.literal("Selected: " + ((Dropdown) target).getSelected()), x + 10, lineY, textCol, false);
        } else if (target instanceof TextField) {
            graphics.drawString(mc.font, Component.literal("Value: " + ((TextField) target).getValue()), x + 10, lineY, textCol, false);
        }

        lineY += 22;
        graphics.drawString(mc.font, Component.literal("§7/duckyeditor export"), x + 10, lineY, secCol, false);
    }

    private int cursorPos() {
        return editBuffer.length();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;

        int lineY = y + 10 + 22 + 18;
        for (int i = 0; i < fields.length; i++) {
            if (mouseX >= x + 50 && mouseX < x + width - 10 && mouseY >= lineY && mouseY < lineY + FIELD_HEIGHT) {
                editingField = i;
                editBuffer = String.valueOf(getFieldValue(i));
                cursorTime = System.currentTimeMillis();
                return true;
            }
            lineY += FIELD_HEIGHT + 2;
        }

        editingField = -1;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (editingField == -1) return false;

        if (keyCode == 257 || keyCode == 335) {
            commitEdit();
            return true;
        }
        if (keyCode == 258 || keyCode == 261) {
            commitEdit();
            editingField = -1;
            return true;
        }
        if (keyCode == 259) {
            if (editBuffer.length() > 0) {
                editBuffer = editBuffer.substring(0, editBuffer.length() - 1);
            }
            return true;
        }
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (editingField == -1) return false;
        if (Character.isDigit(codePoint) || codePoint == '-') {
            editBuffer += codePoint;
            return true;
        }
        return false;
    }

    private void commitEdit() {
        if (target == null || editingField == -1) return;
        try {
            int val = Integer.parseInt(editBuffer);
            switch (editingField) {
                case 0: target.setX(val); editingX = val; break;
                case 1: target.setY(val); editingY = val; break;
                case 2: target.setWidth(Math.max(10, val)); editingW = target.getWidth(); break;
                case 3: target.setHeight(Math.max(10, val)); editingH = target.getHeight(); break;
            }
        } catch (NumberFormatException e) {
            // ignore invalid input
        }
        editingField = -1;
    }

    private int getFieldValue(int idx) {
        switch (idx) {
            case 0: return editingX;
            case 1: return editingY;
            case 2: return editingW;
            case 3: return editingH;
        }
        return 0;
    }

    private String getTypeName(Widget w) {
        if (w instanceof Panel) return "Panel";
        if (w instanceof Button) return "Button";
        if (w instanceof Label) return "Label";
        if (w instanceof TextField) return "TextField";
        if (w instanceof Checkbox) return "Checkbox";
        if (w instanceof Slider) return "Slider";
        if (w instanceof Dropdown) return "Dropdown";
        if (w instanceof ScrollPanel) return "ScrollPanel";
        return "Widget";
    }

    public boolean contains(double mouseX, double mouseY) {
        return visible && mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public void clickedOutside() {
        commitEdit();
        editingField = -1;
    }

    public boolean isEditing() { return editingField != -1; }
}
