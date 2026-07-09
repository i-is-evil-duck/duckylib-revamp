package com.j3ly.duckylib.layout;

import com.j3ly.duckylib.gui.widget.*;
import com.j3ly.duckylib.gui.theme.Theme;

public class LayoutSerializer {

    public static String serialize(Widget widget) {
        StringBuilder sb = new StringBuilder();
        sb.append("# DuckyLib UI Layout - Exported\n\n");
        serializeWidget(sb, widget, 0);
        return sb.toString();
    }

    private static void serializeWidget(StringBuilder sb, Widget widget, int indent) {
        String ind = "  ".repeat(indent);

        sb.append(ind).append("[[widget]]\n");
        sb.append(ind).append("type = \"").append(getTypeName(widget)).append("\"\n");
        sb.append(ind).append("id = \"").append(escape(widget.getId())).append("\"\n");
        sb.append(ind).append("x = ").append(widget.getX()).append("\n");
        sb.append(ind).append("y = ").append(widget.getY()).append("\n");
        sb.append(ind).append("width = ").append(widget.getWidth()).append("\n");
        sb.append(ind).append("height = ").append(widget.getHeight()).append("\n");

        if (widget instanceof Panel) {
            Panel p = (Panel) widget;
            sb.append(ind).append("background = \"").append(colorToHex(p.getBackgroundColor())).append("\"\n");
        } else if (widget instanceof Button) {
            Button b = (Button) widget;
            sb.append(ind).append("label = \"").append(escape(b.getLabel())).append("\"\n");
        } else if (widget instanceof Label) {
            Label l = (Label) widget;
            sb.append(ind).append("text = \"").append(escape(l.getText())).append("\"\n");
        } else if (widget instanceof TextField) {
            TextField tf = (TextField) widget;
            sb.append(ind).append("placeholder = \"").append(escape(tf.getPlaceholder())).append("\"\n");
        } else if (widget instanceof Checkbox) {
            Checkbox cb = (Checkbox) widget;
            sb.append(ind).append("label = \"").append(escape(cb.getLabel())).append("\"\n");
            sb.append(ind).append("default = ").append(cb.isChecked()).append("\n");
        } else if (widget instanceof Slider) {
            Slider s = (Slider) widget;
            sb.append(ind).append("label = \"").append(escape(s.getLabel())).append("\"\n");
            sb.append(ind).append("min = ").append(s.getMin()).append("\n");
            sb.append(ind).append("max = ").append(s.getMax()).append("\n");
            sb.append(ind).append("step = ").append(s.getStep()).append("\n");
            sb.append(ind).append("default = ").append(s.getValue()).append("\n");
        } else if (widget instanceof Dropdown) {
            Dropdown d = (Dropdown) widget;
            sb.append(ind).append("options = [");
            for (int i = 0; i < d.getOptions().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append("\"").append(escape(d.getOptions().get(i))).append("\"");
            }
            sb.append("]\n");
            sb.append(ind).append("default_index = ").append(d.getSelectedIndex()).append("\n");
        }

        sb.append("\n");

        for (Widget child : widget.getChildren()) {
            serializeWidget(sb, child, indent + 1);
        }
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
