package com.j3ly.duckylib.gui;

import com.j3ly.duckylib.editor.EditorOverlay;
import com.j3ly.duckylib.gui.widget.Widget;
import com.j3ly.duckylib.gui.theme.Theme;
import com.j3ly.duckylib.layout.LayoutLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DuckyScreen extends Screen {
    private Widget rootWidget;
    private Map<String, Consumer<?>> hooks = new HashMap<>();

    protected DuckyScreen(Component title) {
        super(title);
    }

    public static DuckyScreen fromLayout(ResourceLocation layoutLocation) {
        DuckyScreen screen = new DuckyScreen(Component.empty());
        screen.rootWidget = LayoutLoader.loadLayout(layoutLocation);
        if (screen.rootWidget == null) {
            screen.rootWidget = new com.j3ly.duckylib.gui.widget.Panel("root", 0, 0, 0, 0);
        }
        return screen;
    }

    public static DuckyScreen fromWidget(Widget root) {
        DuckyScreen screen = new DuckyScreen(Component.empty());
        screen.rootWidget = root;
        return screen;
    }

    public Widget getRootWidget() { return rootWidget; }
    public void setRootWidget(Widget root) { this.rootWidget = root; }

    @SuppressWarnings("unchecked")
    public <T extends Widget> T getWidget(String id, Class<T> type) {
        if (rootWidget == null) return null;
        return findWidget(rootWidget, id, type);
    }

    @SuppressWarnings("unchecked")
    private <T extends Widget> T findWidget(Widget parent, String id, Class<T> type) {
        if (parent == null) return null;
        if (parent.getId().equals(id) && type.isInstance(parent)) {
            return (T) parent;
        }
        for (Widget child : parent.getChildren()) {
            T found = findWidget(child, id, type);
            if (found != null) return found;
        }
        return null;
    }

    public void onClick(String widgetId, Runnable callback) {
        Widget w = findWidget(rootWidget, widgetId, Widget.class);
        if (w != null) w.setOnClick(callback);
    }

    @SuppressWarnings("unchecked")
    public <T> void onValueChange(String widgetId, Consumer<T> callback) {
        // Hook for future value change events
    }

    @Override
    protected void init() {
        super.init();
        if (rootWidget != null) {
            rootWidget.setWidth(this.width);
            rootWidget.setHeight(this.height);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, Theme.getCurrent().getColor("background"));

        if (rootWidget != null) {
            rootWidget.render(graphics, mouseX, mouseY, partialTick);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (EditorOverlay.isActive()) {
            return EditorOverlay.mouseClicked(mouseX, mouseY, button);
        }
        if (rootWidget != null) {
            return rootWidget.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (EditorOverlay.isActive()) {
            EditorOverlay.mouseReleased(mouseX, mouseY, button);
        }
        if (rootWidget != null) {
            rootWidget.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (EditorOverlay.isActive()) {
            return EditorOverlay.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        if (rootWidget != null) {
            return rootWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (rootWidget != null) {
            return rootWidget.mouseScrolled(mouseX, mouseY, delta);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (EditorOverlay.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (rootWidget != null) {
            return rootWidget.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (EditorOverlay.charTyped(codePoint, modifiers)) return true;
        if (rootWidget != null) {
            return rootWidget.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void tick() {
        if (rootWidget != null) rootWidget.tick();
    }
}
