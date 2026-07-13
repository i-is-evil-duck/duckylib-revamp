package com.j3ly.duckylib.layout;

import com.j3ly.duckylib.gui.widget.*;
import com.j3ly.duckylib.gui.theme.Theme;

public class LayoutSerializer {

    public static String serialize(Widget widget) {
        StringBuilder sb = new StringBuilder();
        sb.append("# DuckyLib UI Layout - Exported\n\n");
        serializeWidget(sb, widget, 0, "widget");
        return sb.toString();
    }

    private static void serializeWidget(StringBuilder sb, Widget widget, int indent, String arrayKey) {
        String ind = "  ".repeat(indent);

        sb.append(ind).append("[[").append(arrayKey).append("]]\n");
        writeProp(sb, ind, "type", getTypeName(widget));
        writeProp(sb, ind, "id", widget.getId());
        writeProp(sb, ind, "x", widget.getX());
        writeProp(sb, ind, "y", widget.getY());
        writeProp(sb, ind, "width", widget.getWidth());
        writeProp(sb, ind, "height", widget.getHeight());
        if (!widget.isVisible()) writeProp(sb, ind, "visible", false);
        if (!widget.isEnabled()) writeProp(sb, ind, "enabled", false);

        if (widget instanceof Panel p) {
            writeProp(sb, ind, "background", colorToHex(p.getBackgroundColor()));
            writeProp(sb, ind, "border", colorToHex(p.getBorderColor()));
        } else if (widget instanceof Button b) {
            writeProp(sb, ind, "label", escape(b.getLabel()));
            writeProp(sb, ind, "background", colorToHex(b.getBackgroundColor()));
            writeProp(sb, ind, "label_color", colorToHex(b.getLabelColor()));
        } else if (widget instanceof Label l) {
            writeProp(sb, ind, "text", escape(l.getText()));
            writeProp(sb, ind, "color", colorToHex(l.getColor()));
        } else if (widget instanceof TextField tf) {
            writeProp(sb, ind, "placeholder", escape(tf.getPlaceholder()));
            writeProp(sb, ind, "default", escape(tf.getValue()));
        } else if (widget instanceof Checkbox cb) {
            writeProp(sb, ind, "label", escape(cb.getLabel()));
            writeProp(sb, ind, "default", cb.isChecked());
            writeProp(sb, ind, "check_style", cb.getCheckStyle().name());
        } else if (widget instanceof Slider s) {
            writeProp(sb, ind, "label", escape(s.getLabel()));
            writeProp(sb, ind, "min", s.getMin());
            writeProp(sb, ind, "max", s.getMax());
            writeProp(sb, ind, "step", s.getStep());
            writeProp(sb, ind, "default", s.getValue());
        } else if (widget instanceof Dropdown d) {
            sb.append(ind).append("options = [");
            for (int i = 0; i < d.getOptions().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append("\"").append(escape(d.getOptions().get(i))).append("\"");
            }
            sb.append("]\n");
            writeProp(sb, ind, "default_index", d.getSelectedIndex());
        }

        sb.append("\n");

        String childKey = arrayKey + ".children";
        for (Widget child : widget.getChildren()) {
            serializeWidget(sb, child, indent + 1, childKey);
        }
    }

    private static void writeProp(StringBuilder sb, String ind, String key, String val) {
        sb.append(ind).append(key).append(" = \"").append(val).append("\"\n");
    }

    private static void writeProp(StringBuilder sb, String ind, String key, int val) {
        sb.append(ind).append(key).append(" = ").append(val).append("\n");
    }

    private static void writeProp(StringBuilder sb, String ind, String key, float val) {
        sb.append(ind).append(key).append(" = ").append(val).append("\n");
    }

    private static void writeProp(StringBuilder sb, String ind, String key, boolean val) {
        sb.append(ind).append(key).append(" = ").append(val).append("\n");
    }

    private static String getTypeName(Widget widget) {
        if (widget instanceof Panel) return "panel";
        if (widget instanceof Button) return "button";
        if (widget instanceof Label) return "label";
        if (widget instanceof TextField) return "textfield";
        if (widget instanceof Checkbox) return "checkbox";
        if (widget instanceof Slider) return "slider";
        if (widget instanceof Dropdown) return "dropdown";
        if (widget instanceof ScrollPanel) return "scrollpanel";
        return "panel";
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static String colorToHex(int color) {
        return String.format("#%08X", color);
    }
}
