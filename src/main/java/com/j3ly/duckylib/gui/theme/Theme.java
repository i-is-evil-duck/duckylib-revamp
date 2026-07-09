package com.j3ly.duckylib.gui.theme;

import com.j3ly.duckylib.DuckyLib;
import com.moandjiezana.toml.Toml;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Theme {
    private static Theme current;

    private String name;
    private Map<String, Integer> colors = new HashMap<>();
    private Map<String, Object> properties = new HashMap<>();

    public Theme(String name) {
        this.name = name;
    }

    public static Theme getCurrent() {
        if (current == null) {
            current = loadDefault();
        }
        return current;
    }

    public static void setCurrent(Theme theme) {
        current = theme;
    }

    public static Theme loadDefault() {
        return load(new ResourceLocation(DuckyLib.MOD_ID, "themes/default.toml"));
    }

    public static Theme load(ResourceLocation location) {
        try {
            InputStream stream = net.minecraft.client.Minecraft.getInstance().getResourceManager()
                .open(location);
            Toml toml = new Toml().read(new InputStreamReader(stream));
            return fromToml(toml);
        } catch (Exception e) {
            DuckyLib.LOGGER.error("Failed to load theme: " + location, e);
            return createFallbackTheme();
        }
    }

    private static Theme createFallbackTheme() {
        Theme theme = new Theme("fallback");
        theme.colors.put("background", 0xFF121212);
        theme.colors.put("panel_bg", 0xFF1e1e1e);
        theme.colors.put("primary", 0xFF5865f2);
        theme.colors.put("primary_hover", 0xFF4752c4);
        theme.colors.put("primary_active", 0xFF3c45a5);
        theme.colors.put("text", 0xFFdcddde);
        theme.colors.put("text_secondary", 0xFF96989d);
        theme.colors.put("text_disabled", 0xFF4f545c);
        theme.colors.put("border", 0xFF2f3136);
        theme.colors.put("border_focused", 0xFF5865f2);
        theme.colors.put("error", 0xFFed4245);
        theme.colors.put("success", 0xFF57f287);
        theme.colors.put("warning", 0xFFfee75c);
        theme.colors.put("panel_bg_hover", 0xFF252525);
        theme.colors.put("slider.track_color", 0xFF2f3136);
        theme.colors.put("slider.fill_color", 0xFF5865f2);
        theme.colors.put("slider.thumb_color", 0xFFffffff);
        theme.colors.put("checkbox.check_color", 0xFF5865f2);
        theme.colors.put("textfield.background", 0xFF2f3136);
        theme.colors.put("textfield.background_focused", 0xFF383a40);
        theme.colors.put("textfield.cursor_color", 0xFFdcddde);
        return theme;
    }

    public static Theme fromToml(Toml toml) {
        Theme theme = new Theme(toml.getString("name", "unnamed"));

        Toml colorsToml = toml.getTable("colors");
        if (colorsToml != null) {
            for (Map.Entry<String, Object> entry : colorsToml.toMap().entrySet()) {
                theme.colors.put(entry.getKey(), parseColor(entry.getValue().toString()));
            }
        }

        Toml fontsToml = toml.getTable("fonts");
        if (fontsToml != null) theme.properties.put("fonts", fontsToml.toMap());

        Toml buttonToml = toml.getTable("button");
        if (buttonToml != null) theme.properties.put("button", buttonToml.toMap());

        Toml textfieldToml = toml.getTable("textfield");
        if (textfieldToml != null) theme.properties.put("textfield", textfieldToml.toMap());

        Toml sliderToml = toml.getTable("slider");
        if (sliderToml != null) theme.properties.put("slider", sliderToml.toMap());

        Toml checkboxToml = toml.getTable("checkbox");
        if (checkboxToml != null) theme.properties.put("checkbox", checkboxToml.toMap());

        return theme;
    }

    public int getColor(String key) {
        return colors.getOrDefault(key, 0xFFFFFFFF);
    }

    public void setColor(String key, int color) {
        colors.put(key, color);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String category, String key, T defaultValue) {
        Map<String, Object> cat = (Map<String, Object>) properties.get(category);
        if (cat == null) return defaultValue;
        return (T) cat.getOrDefault(key, defaultValue);
    }

    public static int parseColor(String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        if (hex.length() == 6) hex = "FF" + hex;
        return (int) Long.parseLong(hex, 16);
    }

    public String getName() { return name; }

    public Map<String, Integer> getColors() { return new HashMap<>(colors); }
    public Map<String, Object> getProperties() { return new HashMap<>(properties); }
}
