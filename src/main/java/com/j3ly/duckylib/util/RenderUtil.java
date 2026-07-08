package com.j3ly.duckylib.util;

import net.minecraft.client.gui.GuiGraphics;

public class RenderUtil {

    public static void fill(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        graphics.fill(x1, y1, x2, y2, color);
    }

    public static void drawBorder(GuiGraphics graphics, int x, int y, int width, int height, int thickness, int color) {
        graphics.fill(x, y, x + width, y + thickness, color);
        graphics.fill(x, y + height - thickness, x + width, y + height, color);
        graphics.fill(x, y, x + thickness, y + height, color);
        graphics.fill(x + width - thickness, y, x + width, y + height, color);
    }

    public static void fillRounded(GuiGraphics graphics, int x, int y, int width, int height, int radius, int color) {
        graphics.fill(x + radius, y, x + width - radius, y + height, color);
        graphics.fill(x, y + radius, x + width, y + height - radius, color);

        int r = radius;
        graphics.fill(x + r/2, y, x + r, y + r/2, color);
        graphics.fill(x, y + r/2, x + r/2, y + r, color);
        graphics.fill(x + width - r, y, x + width - r/2, y + r/2, color);
        graphics.fill(x + width - r/2, y + r/2, x + width, y + r, color);
        graphics.fill(x + r/2, y + height - r/2, x + r, y + height, color);
        graphics.fill(x, y + height - r, x + r/2, y + height - r/2, color);
        graphics.fill(x + width - r, y + height - r/2, x + width - r/2, y + height, color);
        graphics.fill(x + width - r/2, y + height - r, x + width, y + height - r/2, color);
    }

    public static void drawRoundedBorder(GuiGraphics graphics, int x, int y, int width, int height, int radius, int thickness, int color) {
        drawBorder(graphics, x + radius, y, width - radius * 2, thickness, thickness, color);
        drawBorder(graphics, x + radius, y + height - thickness, width - radius * 2, thickness, thickness, color);
        drawBorder(graphics, x, y + radius, thickness, height - radius * 2, thickness, color);
        drawBorder(graphics, x + width - thickness, y + radius, thickness, height - radius * 2, thickness, color);

        int r = radius;
        graphics.fill(x + r/2, y, x + r/2 + thickness, y + thickness, color);
        graphics.fill(x, y + r/2, x + thickness, y + r/2 + thickness, color);
        graphics.fill(x + width - r/2 - thickness, y, x + width - r/2, y + thickness, color);
        graphics.fill(x + width - thickness, y + r/2, x + width, y + r/2 + thickness, color);
        graphics.fill(x + r/2, y + height - thickness, x + r/2 + thickness, y + height, color);
        graphics.fill(x, y + height - r/2 - thickness, x + thickness, y + height - r/2, color);
        graphics.fill(x + width - r/2 - thickness, y + height - thickness, x + width - r/2, y + height, color);
        graphics.fill(x + width - thickness, y + height - r/2 - thickness, x + width, y + height - r/2, color);
    }

    public static int blendColors(int color1, int color2, float ratio) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = (int)(a1 + (a2 - a1) * ratio);
        int r = (int)(r1 + (r2 - r1) * ratio);
        int g = (int)(g1 + (g2 - g1) * ratio);
        int b = (int)(b1 + (b2 - b1) * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
