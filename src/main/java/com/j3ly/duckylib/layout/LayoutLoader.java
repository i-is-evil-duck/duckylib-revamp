package com.j3ly.duckylib.layout;

import com.j3ly.duckylib.DuckyLib;
import com.j3ly.duckylib.gui.theme.Theme;
import com.j3ly.duckylib.gui.widget.*;
import com.moandjiezana.toml.Toml;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class LayoutLoader {

    public static Widget loadLayout(ResourceLocation location) {
        try {
            InputStream stream = net.minecraft.client.Minecraft.getInstance().getResourceManager()
                .open(location);
            Toml toml = new Toml().read(new InputStreamReader(stream));
            return parseWidget(toml);
        } catch (Exception e) {
            DuckyLib.LOGGER.error("Failed to load layout: " + location, e);
            return null;
        }
    }

    public static Widget parseWidget(Toml toml) {
        String type = toml.getString("type", "panel");
        String id = toml.getString("id", "unnamed");
        int x = toml.getLong("x", 0L).intValue();
        int y = toml.getLong("y", 0L).intValue();
        int width = toml.getLong("width", 100L).intValue();
        int height = toml.getLong("height", 20L).intValue();

        Widget widget = createWidget(type, id, x, y, width, height, toml);

        if (toml.contains("visible")) widget.setVisible(toml.getBoolean("visible"));
        if (toml.contains("enabled")) widget.setEnabled(toml.getBoolean("enabled"));

        List<Toml> children = toml.getTables("children");
        if (children != null) {
            for (Toml childToml : children) {
                Widget child = parseWidget(childToml);
                if (child != null) widget.addChild(child);
            }
        }

        return widget;
    }

    private static Widget createWidget(String type, String id, int x, int y, int width, int height, Toml toml) {
        switch (type.toLowerCase()) {
            case "panel":
                Panel panel = new Panel(id, x, y, width, height);
                if (toml.contains("background")) panel.setBackgroundColor(Theme.parseColor(toml.getString("background")));
                if (toml.contains("border")) panel.setBorderColor(Theme.parseColor(toml.getString("border")));
                if (toml.contains("border_width")) panel.setBorderWidth(toml.getLong("border_width").intValue());
                if (toml.contains("clip_children")) panel.setClipChildren(toml.getBoolean("clip_children"));
                return panel;

            case "button":
                String label = toml.getString("label", "Button");
                Button button = new Button(id, x, y, width, height, label);
                return button;

            case "label":
                String text = toml.getString("text", "");
                Label lbl = new Label(id, x, y, text);
                if (toml.contains("color")) lbl.setColor(Theme.parseColor(toml.getString("color")));
                if (toml.contains("scale")) lbl.setScale(toml.getDouble("scale").floatValue());
                if (toml.contains("centered")) lbl.setCentered(toml.getBoolean("centered"));
                return lbl;

            case "textfield":
                TextField tf = new TextField(id, x, y, width, height);
                if (toml.contains("placeholder")) tf.setPlaceholder(toml.getString("placeholder"));
                if (toml.contains("max_length")) tf.setMaxLength(toml.getLong("max_length").intValue());
                if (toml.contains("default")) tf.setValue(toml.getString("default"));
                return tf;

            case "checkbox":
                String cbLabel = toml.getString("label", "");
                Checkbox cb = new Checkbox(id, x, y, cbLabel);
                if (toml.contains("default")) cb.setChecked(toml.getBoolean("default"));
                return cb;

            case "slider":
                float min = toml.getDouble("min", 0.0).floatValue();
                float max = toml.getDouble("max", 1.0).floatValue();
                float step = toml.getDouble("step", 0.1).floatValue();
                float defaultVal = toml.getDouble("default", (double) min).floatValue();
                Slider slider = new Slider(id, x, y, width, min, max, step, defaultVal);
                if (toml.contains("label")) slider.setLabel(toml.getString("label"));
                return slider;

            case "dropdown":
                Dropdown dd = new Dropdown(id, x, y, width, height);
                List<String> options = toml.getList("options");
                if (options != null) dd.setOptions(options);
                if (toml.contains("default_index")) dd.setSelectedIndex(toml.getLong("default_index").intValue());
                return dd;

            case "scrollpanel":
                ScrollPanel sp = new ScrollPanel(id, x, y, width, height);
                return sp;

            default:
                DuckyLib.LOGGER.warn("Unknown widget type: " + type);
                return new Panel(id, x, y, width, height);
        }
    }
}
