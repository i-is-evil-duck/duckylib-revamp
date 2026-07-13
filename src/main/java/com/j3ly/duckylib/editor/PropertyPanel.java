package com.j3ly.duckylib.editor;

import com.j3ly.duckylib.gui.theme.Theme;
import com.j3ly.duckylib.gui.widget.*;
import com.j3ly.duckylib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class PropertyPanel {
    private int x, y, width, height;
    private Widget target;
    private boolean visible = true;
    private int fieldScroll = 0;

    private List<FieldDef> fields = new ArrayList<>();
    private int editingField = -1;
    private String editBuffer = "";

    private static final int FIELD_H = 14;
    private static final int FIELD_GAP = 2;
    private static final int LABEL_W = 45;

    private enum FieldType { NUMERIC, TEXT, BOOLEAN, COLOR, CYCLE, ACTION }

    private static class FieldDef {
        String label;
        FieldType type;
        java.util.function.Supplier<String> getter;
        java.util.function.Consumer<String> setter;
        java.util.function.Supplier<String[]> cycleOptions;
        Runnable onClick;
        FieldDef(String label, FieldType type, java.util.function.Supplier<String> getter, java.util.function.Consumer<String> setter) {
            this.label = label; this.type = type; this.getter = getter; this.setter = setter;
        }
        FieldDef(String label, FieldType type, java.util.function.Supplier<String> getter, java.util.function.Consumer<String> setter, java.util.function.Supplier<String[]> cycleOptions) {
            this.label = label; this.type = type; this.getter = getter; this.setter = setter; this.cycleOptions = cycleOptions;
        }
        FieldDef(String label, FieldType type, Runnable onClick) {
            this.label = label; this.type = type; this.onClick = onClick;
        }
    }

    public PropertyPanel() {
        this.width = 190;
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
        this.fieldScroll = 0;
        rebuildFields();
    }

    public void refresh() {
        if (target != null) rebuildFields();
    }

    private void rebuildFields() {
        fields.clear();
        if (target == null) return;

        boolean isRoot = target.getParent() == null;
        fields.add(new FieldDef("X", FieldType.NUMERIC, () -> String.valueOf(target.getX()), isRoot ? v -> {} : v -> target.setX(parseInt(v, target.getX()))));
        fields.add(new FieldDef("Y", FieldType.NUMERIC, () -> String.valueOf(target.getY()), isRoot ? v -> {} : v -> target.setY(parseInt(v, target.getY()))));
        fields.add(new FieldDef("W", FieldType.NUMERIC, () -> String.valueOf(target.getWidth()), isRoot ? v -> {} : v -> target.setWidth(Math.max(10, parseInt(v, target.getWidth())))));
        fields.add(new FieldDef("H", FieldType.NUMERIC, () -> String.valueOf(target.getHeight()), isRoot ? v -> {} : v -> target.setHeight(Math.max(10, parseInt(v, target.getHeight())))));

        if (target instanceof Panel p) {
            fields.add(new FieldDef("BG Color", FieldType.COLOR, () -> colorToHex(p.getBackgroundColor()), v -> p.setBackgroundColor(hexToColor(v))));
            fields.add(new FieldDef("Border", FieldType.COLOR, () -> colorToHex(p.getBorderColor()), v -> p.setBorderColor(hexToColor(v))));
        } else if (target instanceof Button b) {
            fields.add(new FieldDef("Label", FieldType.TEXT, b::getLabel, b::setLabel));
            fields.add(new FieldDef("BG Color", FieldType.COLOR, () -> colorToHex(b.getBackgroundColor()), v -> b.setBackgroundColor(hexToColor(v))));
            fields.add(new FieldDef("Text Color", FieldType.COLOR, () -> colorToHex(b.getLabelColor()), v -> b.setLabelColor(hexToColor(v))));
        } else if (target instanceof Label l) {
            fields.add(new FieldDef("Text", FieldType.TEXT, l::getText, l::setText));
            fields.add(new FieldDef("Color", FieldType.COLOR, () -> colorToHex(l.getColor()), v -> l.setColor(hexToColor(v))));
        } else if (target instanceof Checkbox c) {
            fields.add(new FieldDef("Label", FieldType.TEXT, c::getLabel, c::setLabel));
            fields.add(new FieldDef("Checked", FieldType.BOOLEAN, () -> String.valueOf(c.isChecked()), v -> c.setChecked(Boolean.parseBoolean(v))));
            fields.add(new FieldDef("Style", FieldType.CYCLE, () -> c.getCheckStyle().name(), v -> c.setCheckStyle(Checkbox.CheckStyle.valueOf(v)), () -> new String[]{"FILLED", "CHECK", "CROSS"}));
            fields.add(new FieldDef("Check Color", FieldType.COLOR, () -> colorToHex(c.getCheckColor()), v -> c.setCheckColor(hexToColor(v))));
            fields.add(new FieldDef("Text Color", FieldType.COLOR, () -> colorToHex(c.getLabelColor()), v -> c.setLabelColor(hexToColor(v))));
        } else if (target instanceof Slider s) {
            fields.add(new FieldDef("Label", FieldType.TEXT, s::getLabel, s::setLabel));
            fields.add(new FieldDef("Min", FieldType.NUMERIC, () -> String.valueOf((int)s.getMin()), v -> s.setMin(Float.parseFloat(v))));
            fields.add(new FieldDef("Max", FieldType.NUMERIC, () -> String.valueOf((int)s.getMax()), v -> s.setMax(Float.parseFloat(v))));
            fields.add(new FieldDef("Step", FieldType.NUMERIC, () -> String.valueOf((int)s.getStep()), v -> s.setStep(Float.parseFloat(v))));
            fields.add(new FieldDef("Value", FieldType.NUMERIC, () -> String.valueOf((int)s.getValue()), v -> s.setValue(Float.parseFloat(v))));
            fields.add(new FieldDef("Track", FieldType.COLOR, () -> colorToHex(s.getTrackColor()), v -> s.setTrackColor(hexToColor(v))));
            fields.add(new FieldDef("Fill", FieldType.COLOR, () -> colorToHex(s.getFillColor()), v -> s.setFillColor(hexToColor(v))));
            fields.add(new FieldDef("Thumb", FieldType.COLOR, () -> colorToHex(s.getThumbColor()), v -> s.setThumbColor(hexToColor(v))));
        } else if (target instanceof TextField t) {
            fields.add(new FieldDef("Value", FieldType.TEXT, t::getValue, t::setValue));
            fields.add(new FieldDef("Placeholder", FieldType.TEXT, t::getPlaceholder, t::setPlaceholder));
            fields.add(new FieldDef("Transparent", FieldType.BOOLEAN, () -> String.valueOf(t.isBgTransparent()), v -> t.setBgTransparent(Boolean.parseBoolean(v))));
            fields.add(new FieldDef("Border", FieldType.COLOR, () -> colorToHex(t.getBorderColor()), v -> t.setBorderColor(hexToColor(v))));
            fields.add(new FieldDef("Text Color", FieldType.COLOR, () -> colorToHex(t.getTextColor()), v -> t.setTextColor(hexToColor(v))));
            fields.add(new FieldDef("BG Color", FieldType.COLOR, () -> colorToHex(t.getBackgroundColor()), v -> t.setBackgroundColor(hexToColor(v))));
        } else if (target instanceof Dropdown d) {
            fields.add(new FieldDef("BG Color", FieldType.COLOR, () -> colorToHex(d.getBackgroundColor()), v -> d.setBackgroundColor(hexToColor(v))));
            fields.add(new FieldDef("Text Color", FieldType.COLOR, () -> colorToHex(d.getTextColor()), v -> d.setTextColor(hexToColor(v))));
            fields.add(new FieldDef("Selected", FieldType.CYCLE, d::getSelected, v -> {
                int idx = d.getOptions().indexOf(v);
                if (idx >= 0) d.setSelectedIndex(idx);
            }, () -> d.getOptions().toArray(new String[0])));
            for (int i = 0; i < d.getOptions().size(); i++) {
                int ii = i;
                String optLabel = "Opt " + (i + 1);
                fields.add(new FieldDef(optLabel, FieldType.TEXT, () -> d.getOptions().get(ii), v -> {
                    List<String> opts = d.getOptions();
                    opts.set(ii, v);
                    d.setOptions(opts);
                }));
            }
            fields.add(new FieldDef("+ Add Option", FieldType.ACTION, () -> {
                d.addOption("option_" + (d.getOptions().size() + 1));
                rebuildFields();
            }));
        }
    }

    private int parseInt(String s, int fallback) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return fallback; }
    }

    private static String colorToHex(int color) {
        if ((color & 0xFF000000) == 0xFF000000) {
            return String.format("#%06X", color & 0xFFFFFF);
        }
        return String.format("#%08X", color);
    }

    private static int hexToColor(String hex) {
        try {
            String h = hex.startsWith("#") ? hex.substring(1) : hex;
            long val = Long.parseLong(h, 16);
            if (h.length() <= 6) val |= 0xFF000000L;
            return (int) val;
        } catch (NumberFormatException e) {
            return 0xFF000000;
        }
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        updatePosition();

        int maxScroll = Math.max(0, fields.size() * (FIELD_H + FIELD_GAP) - (height - 60));
        fieldScroll = Math.max(0, Math.min(fieldScroll, maxScroll));

        int bg = Theme.getCurrent().getColor("panel_bg");
        RenderUtil.fillRounded(graphics, x, y, width, height, 6, bg);
        int border = Theme.getCurrent().getColor("border");
        RenderUtil.drawRoundedBorder(graphics, x, y, width, height, 6, 1, border);

        Minecraft mc = Minecraft.getInstance();
        int textCol = Theme.getCurrent().getColor("text");
        int secCol = Theme.getCurrent().getColor("text_secondary");
        int lineY = y + 10;

        graphics.drawString(mc.font, Component.literal("§lWidget Properties"), x + 10, lineY, textCol, false);
        lineY += 20;

        if (target == null) {
            graphics.drawString(mc.font, Component.literal("No widget selected"), x + 10, lineY, secCol, false);
            return;
        }

        graphics.drawString(mc.font, Component.literal("ID: " + target.getId()), x + 10, lineY, secCol, false);
        lineY += 16;
        graphics.drawString(mc.font, Component.literal("§7" + getTypeName(target)), x + 10, lineY, secCol, false);
        lineY += 18;

        int contentStart = lineY;
        int visibleEnd = y + height - 10;

        for (int i = 0; i < fields.size(); i++) {
            FieldDef f = fields.get(i);
            int fieldY = contentStart + i * (FIELD_H + FIELD_GAP) - fieldScroll;
            if (fieldY + FIELD_H < y + 25 || fieldY > visibleEnd) continue;

            boolean isEditing = editingField == i;
            boolean isAction = f.type == FieldType.ACTION;
            boolean isBoolean = f.type == FieldType.BOOLEAN;
            boolean isColor = f.type == FieldType.COLOR;
            boolean isCycle = f.type == FieldType.CYCLE;
            boolean hovered = mouseX >= x + 4 && mouseX < x + width - 4 && mouseY >= fieldY && mouseY < fieldY + FIELD_H;

            if (isAction) {
                RenderUtil.fill(graphics, x + 4, fieldY, x + width - 4, fieldY + FIELD_H, hovered ? 0xFF5865F2 : 0x402f3136);
                RenderUtil.drawBorder(graphics, x + 4, fieldY, width - 8, FIELD_H, 1, 0xFF1e1e1e);
                graphics.drawString(mc.font, Component.literal(f.label), x + 8, fieldY + 2, 0xFFdcddde, false);
            } else if (isBoolean) {
                RenderUtil.fill(graphics, x + 4, fieldY, x + width - 4, fieldY + FIELD_H, hovered ? 0x20333333 : 0x00000000);
                String val = f.getter.get();
                String checkBox = val.equals("true") ? "☑ " : "☐ ";
                graphics.drawString(mc.font, Component.literal(f.label + ": " + checkBox), x + 8, fieldY + 2, textCol, false);
            } else if (isColor) {
                RenderUtil.fill(graphics, x + 4, fieldY, x + width - 4, fieldY + FIELD_H, hovered ? 0x20333333 : 0x00000000);
                graphics.drawString(mc.font, Component.literal(f.label + ":"), x + 8, fieldY + 2, textCol, false);
                int swatchX = x + 8 + LABEL_W;
                int swatchY = fieldY + 2;
                int swatchSize = FIELD_H - 4;
                int colorVal = isEditing ? 0x00000000 : hexToColor(f.getter.get());
                if (!isEditing) {
                    RenderUtil.fill(graphics, swatchX, swatchY, swatchX + swatchSize, swatchY + swatchSize, 0xFF000000 | colorVal);
                    RenderUtil.drawBorder(graphics, swatchX, swatchY, swatchSize, swatchSize, 1, 0xFF888888);
                }
                int textX = swatchX + swatchSize + 3;
                int textW = width - 16 - LABEL_W - swatchSize - 3;
                String displayVal = isEditing ? editBuffer : f.getter.get();
                int valCol = isEditing ? 0xFFFFFF00 : (hovered ? 0xFFA0A0FF : secCol);
                RenderUtil.fill(graphics, textX, fieldY, textX + textW, fieldY + FIELD_H, isEditing ? 0x40404040 : 0x00000000);
                graphics.drawString(mc.font, Component.literal(displayVal), textX + 2, fieldY + 2, valCol, false);
                if (isEditing && (System.currentTimeMillis() / 500) % 2 == 0) {
                    int cx = textX + 2 + mc.font.width(editBuffer);
                    graphics.fill(cx, fieldY + 1, cx + 1, fieldY + FIELD_H - 1, 0xFFFFFFFF);
                }
            } else if (isCycle) {
                RenderUtil.fill(graphics, x + 4, fieldY, x + width - 4, fieldY + FIELD_H, 0x00000000);
                graphics.drawString(mc.font, Component.literal(f.label + ":"), x + 8, fieldY + 2, textCol, false);
                String curVal = f.getter.get();
                int pillX = x + 8 + LABEL_W;
                int pillY = fieldY + 1;
                int pillH = FIELD_H - 2;
                String[] opts = f.cycleOptions.get();
                for (String opt : opts) {
                    int pw = mc.font.width(opt) + 8;
                    boolean active = opt.equals(curVal);
                    boolean pillHovered = mouseX >= pillX && mouseX < pillX + pw && mouseY >= pillY && mouseY < pillY + pillH;
                    int pillBg = active ? 0xFF5865F2 : (pillHovered ? 0x60333333 : 0x302f3136);
                    RenderUtil.fill(graphics, pillX, pillY, pillX + pw, pillY + pillH, pillBg);
                    if (active || pillHovered) {
                        RenderUtil.drawBorder(graphics, pillX, pillY, pw, pillH, 1, active ? 0xFF7777FF : 0xFF444444);
                    }
                    graphics.drawString(mc.font, Component.literal(opt), pillX + 4, pillY + 1, active ? 0xFFFFFFFF : secCol, false);
                    pillX += pw + 2;
                }
            } else {
                int editX = x + 8 + LABEL_W;
                int editW = width - 16 - LABEL_W;
                graphics.drawString(mc.font, Component.literal(f.label + ":"), x + 8, fieldY + 2, textCol, false);
                boolean isRootDim = target.getParent() == null && (f.label.equals("X") || f.label.equals("Y") || f.label.equals("W") || f.label.equals("H"));
                String displayVal = isEditing ? editBuffer : f.getter.get();
                int valCol = isEditing ? 0xFFFFFF00 : (isRootDim ? 0xFF888888 : (hovered ? 0xFFA0A0FF : secCol));
                if (!isRootDim) {
                    RenderUtil.fill(graphics, editX, fieldY, editX + editW, fieldY + FIELD_H, isEditing ? 0x40404040 : (hovered ? 0x20333333 : 0x00000000));
                }
                graphics.drawString(mc.font, Component.literal(displayVal), editX + 2, fieldY + 2, valCol, false);
                if (isEditing && (System.currentTimeMillis() / 500) % 2 == 0) {
                    int cx = editX + 2 + mc.font.width(editBuffer);
                    graphics.fill(cx, fieldY + 1, cx + 1, fieldY + FIELD_H - 1, 0xFFFFFFFF);
                }
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        int contentStart = y + 10 + 20 + 16 + 18;

        for (int i = 0; i < fields.size(); i++) {
            int fieldY = contentStart + i * (FIELD_H + FIELD_GAP) - fieldScroll;
            if (mouseX < x + 4 || mouseX >= x + width - 4 || mouseY < fieldY || mouseY >= fieldY + FIELD_H) continue;

            FieldDef f = fields.get(i);
            if (f.type == FieldType.ACTION) {
                if (f.onClick != null) f.onClick.run();
                return true;
            }
            if (f.type == FieldType.BOOLEAN) {
                boolean cur = Boolean.parseBoolean(f.getter.get());
                f.setter.accept(String.valueOf(!cur));
                return true;
            }
            if (f.type == FieldType.CYCLE) {
                int pillX = (int)mouseX;
                int pillStartX = x + 8 + LABEL_W;
                int curPillX = pillStartX;
                String[] opts = f.cycleOptions.get();
                for (String opt : opts) {
                    int pw = Minecraft.getInstance().font.width(opt) + 8;
                    if (pillX >= curPillX && pillX < curPillX + pw) {
                        if (!opt.equals(f.getter.get())) {
                            f.setter.accept(opt);
                        }
                        return true;
                    }
                    curPillX += pw + 2;
                }
                return true;
            }
            if (target.getParent() == null && (f.label.equals("X") || f.label.equals("Y") || f.label.equals("W") || f.label.equals("H"))) {
                return true;
            }
            editingField = i;
            editBuffer = f.getter.get();
            return true;
        }

        editingField = -1;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (editingField == -1) return false;
        if (keyCode == 257 || keyCode == 335 || keyCode == 258 || keyCode == 261) {
            commitEdit();
            if (keyCode == 258 || keyCode == 261) editingField = -1;
            return true;
        }
        if (keyCode == 259) {
            if (editBuffer.length() > 0) editBuffer = editBuffer.substring(0, editBuffer.length() - 1);
            return true;
        }
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (editingField == -1) return false;
        FieldDef f = fields.get(editingField);
        if (f.type == FieldType.NUMERIC) {
            if (Character.isDigit(codePoint) || codePoint == '-' || codePoint == '.') {
                editBuffer += codePoint;
                return true;
            }
        } else if (f.type == FieldType.TEXT) {
            editBuffer += codePoint;
            return true;
        } else if (f.type == FieldType.COLOR) {
            if ("0123456789abcdefABCDEF#".indexOf(codePoint) >= 0) {
                editBuffer += codePoint;
                return true;
            }
        }
        return false;
    }

    private void commitEdit() {
        if (target == null || editingField == -1 || editingField >= fields.size()) return;
        FieldDef f = fields.get(editingField);
        if (f.setter != null) f.setter.accept(editBuffer);
        editingField = -1;
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
