package com.j3ly.duckylib.layout;

import com.j3ly.duckylib.gui.widget.Widget;
import net.minecraft.resources.ResourceLocation;

/**
 * Utility class for working with TOML layouts.
 */
public class TomlLayout {

    public static Widget load(ResourceLocation location) {
        return LayoutLoader.loadLayout(location);
    }

    public static String export(Widget root) {
        return LayoutSerializer.serialize(root);
    }

    public static void saveToFile(Widget root, java.io.File file) throws java.io.IOException {
        String toml = export(root);
        java.nio.file.Files.write(file.toPath(), toml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
