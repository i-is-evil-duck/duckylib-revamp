package com.j3ly.duckylib.editor;

import com.j3ly.duckylib.gui.DuckyScreen;
import com.j3ly.duckylib.gui.widget.*;
import com.j3ly.duckylib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Random;

public class EditorOverlay {
    private static boolean active = false;
    private static Widget selectedWidget = null;
    private static PropertyPanel propertyPanel;
    private static int dragStartX, dragStartY;
    private static int widgetStartX, widgetStartY;
    private static boolean dragging = false;
    private static boolean resizing = false;
    private static int resizeHandle = -1;

    private static final int HANDLE_SIZE = 6;

    private static final String[] TOOLBAR_BUTTONS = {"Panel", "Button", "Label", "Checkbox", "Slider", "TextField", "Dropdown"};
    private static final int TOOLBAR_Y = 5;
    private static final int TOOLBAR_BTN_H = 16;

    public static void toggle() {
        active = !active;
        if (active) {
            propertyPanel = new PropertyPanel();
        }
    }

    public static boolean isActive() { return active; }

    public static void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!active) return;

        if (selectedWidget != null && selectedWidget.isVisible()) {
            int absX = selectedWidget.getAbsoluteX();
            int absY = selectedWidget.getAbsoluteY();
            int w = selectedWidget.getWidth();
            int h = selectedWidget.getHeight();

            RenderUtil.drawBorder(graphics, absX - 1, absY - 1, w + 2, h + 2, 1, 0xFF5865F2);

            drawHandle(graphics, absX - HANDLE_SIZE/2, absY - HANDLE_SIZE/2);
            drawHandle(graphics, absX + w - HANDLE_SIZE/2, absY - HANDLE_SIZE/2);
            drawHandle(graphics, absX - HANDLE_SIZE/2, absY + h - HANDLE_SIZE/2);
            drawHandle(graphics, absX + w - HANDLE_SIZE/2, absY + h - HANDLE_SIZE/2);
        }

        if (propertyPanel != null) {
            propertyPanel.render(graphics, mouseX, mouseY, partialTick);
        }

        Minecraft mc = Minecraft.getInstance();

        int sw = mc.getWindow().getGuiScaledWidth();
        int btnX = 5;
        RenderUtil.fill(graphics, 0, TOOLBAR_Y - 2, sw, TOOLBAR_Y + TOOLBAR_BTN_H + 2, 0xAA111111);

        for (int i = 0; i < TOOLBAR_BUTTONS.length; i++) {
            String label = TOOLBAR_BUTTONS[i];
            int btnW = mc.font.width("+" + label) + 12;
            boolean hover = mouseX >= btnX && mouseX < btnX + btnW && mouseY >= TOOLBAR_Y && mouseY < TOOLBAR_Y + TOOLBAR_BTN_H;
            int col = hover ? 0xFF5865F2 : 0xFF2f3136;
            RenderUtil.fill(graphics, btnX, TOOLBAR_Y, btnX + btnW, TOOLBAR_Y + TOOLBAR_BTN_H, col);
            RenderUtil.drawBorder(graphics, btnX, TOOLBAR_Y, btnW, TOOLBAR_BTN_H, 1, 0xFF1e1e1e);
            graphics.drawString(mc.font, "+" + label, btnX + 6, TOOLBAR_Y + 4, 0xFFdcddde, false);
            toolbarBtnBounds[i] = new int[]{btnX, btnX + btnW};
            btnX += btnW + 4;
        }
    }

    private static int[][] toolbarBtnBounds = new int[TOOLBAR_BUTTONS.length][2];

    private static void drawHandle(GuiGraphics graphics, int x, int y) {
        RenderUtil.fill(graphics, x, y, x + HANDLE_SIZE, y + HANDLE_SIZE, 0xFFFFFFFF);
        RenderUtil.drawBorder(graphics, x, y, HANDLE_SIZE, HANDLE_SIZE, 1, 0xFF5865F2);
    }

    public static boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active) return false;

        if (propertyPanel != null) {
            if (propertyPanel.contains(mouseX, mouseY)) {
                return propertyPanel.mouseClicked(mouseX, mouseY, button);
            }
            propertyPanel.clickedOutside();
        }

        if (mouseY >= TOOLBAR_Y && mouseY < TOOLBAR_Y + TOOLBAR_BTN_H) {
            for (int i = 0; i < TOOLBAR_BUTTONS.length; i++) {
                if (mouseX >= toolbarBtnBounds[i][0] && mouseX < toolbarBtnBounds[i][1]) {
                    addWidget(TOOLBAR_BUTTONS[i]);
                    return true;
                }
            }
        }

        if (selectedWidget != null) {
            int absX = selectedWidget.getAbsoluteX();
            int absY = selectedWidget.getAbsoluteY();
            int w = selectedWidget.getWidth();
            int h = selectedWidget.getHeight();

            if (isHandleHit(mouseX, mouseY, absX + w - HANDLE_SIZE/2, absY + h - HANDLE_SIZE/2)) {
                resizing = true;
                resizeHandle = 3;
                dragStartX = (int) mouseX;
                dragStartY = (int) mouseY;
                return true;
            }
        }

        Widget root = getRootScreen();
        if (root != null) {
            Widget clicked = findWidgetAt(root, mouseX, mouseY);
            if (clicked != null) {
                selectedWidget = clicked;
                dragging = true;
                dragStartX = (int) mouseX;
                dragStartY = (int) mouseY;
                widgetStartX = clicked.getX();
                widgetStartY = clicked.getY();

                if (propertyPanel != null) {
                    propertyPanel.setWidget(clicked);
                }
                return true;
            }
        }

        selectedWidget = null;
        if (propertyPanel != null) propertyPanel.setWidget(null);
        return true;
    }

    public static boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!active) return false;

        if (dragging && selectedWidget != null) {
            int newX = widgetStartX + (int)(mouseX - dragStartX);
            int newY = widgetStartY + (int)(mouseY - dragStartY);
            selectedWidget.setX(newX);
            selectedWidget.setY(newY);

            if (propertyPanel != null) propertyPanel.refresh();
            return true;
        }

        if (resizing && selectedWidget != null) {
            int dx = (int)(mouseX - dragStartX);
            int dy = (int)(mouseY - dragStartY);

            if (resizeHandle == 3) {
                selectedWidget.setWidth(Math.max(10, selectedWidget.getWidth() + dx));
                selectedWidget.setHeight(Math.max(10, selectedWidget.getHeight() + dy));
                dragStartX = (int) mouseX;
                dragStartY = (int) mouseY;
            }

            if (propertyPanel != null) propertyPanel.refresh();
            return true;
        }

        return false;
    }

    public static boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        resizing = false;
        resizeHandle = -1;
        return false;
    }

    public static boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!active) return false;
        return propertyPanel != null && propertyPanel.keyPressed(keyCode, scanCode, modifiers);
    }

    public static boolean charTyped(char codePoint, int modifiers) {
        if (!active) return false;
        return propertyPanel != null && propertyPanel.charTyped(codePoint, modifiers);
    }

    private static boolean isHandleHit(double mx, double my, int hx, int hy) {
        return mx >= hx && mx < hx + HANDLE_SIZE && my >= hy && my < hy + HANDLE_SIZE;
    }

    private static Widget findWidgetAt(Widget root, double mouseX, double mouseY) {
        for (int i = root.getChildren().size() - 1; i >= 0; i--) {
            Widget found = findWidgetAt(root.getChildren().get(i), mouseX, mouseY);
            if (found != null) return found;
        }

        if (root.contains(mouseX, mouseY)) {
            return root;
        }

        return null;
    }

    private static Widget getRootScreen() {
        if (Minecraft.getInstance().screen instanceof DuckyScreen) {
            return ((DuckyScreen) Minecraft.getInstance().screen).getRootWidget();
        }
        return null;
    }

    private static void addWidget(String type) {
        Widget root = getRootScreen();
        if (root == null) return;

        Widget w = null;
        int bx = 40 + new Random().nextInt(100);
        int by = 60 + new Random().nextInt(100);

        switch (type) {
            case "Panel":
                w = new Panel("panel_" + System.currentTimeMillis(), bx, by, 200, 150);
                break;
            case "Button":
                w = new Button("btn_" + System.currentTimeMillis(), bx, by, 100, 25, "Button");
                break;
            case "Label":
                w = new Label("lbl_" + System.currentTimeMillis(), bx, by, "Label");
                break;
            case "Checkbox":
                w = new Checkbox("cb_" + System.currentTimeMillis(), bx, by, "Checkbox");
                break;
            case "Slider":
                w = new Slider("sld_" + System.currentTimeMillis(), bx, by, 120, 0, 100, 1, 50);
                break;
            case "TextField":
                w = new TextField("tf_" + System.currentTimeMillis(), bx, by, 120, 22);
                break;
            case "Dropdown":
                w = new Dropdown("dd_" + System.currentTimeMillis(), bx, by, 120, 22);
                break;
        }

        if (w != null) {
            root.addChild(w);
            selectedWidget = w;
            if (propertyPanel != null) propertyPanel.setWidget(w);
        }
    }

    public static Widget getSelectedWidget() { return selectedWidget; }
    public static boolean isEditing() { return propertyPanel != null && propertyPanel.isEditing(); }
}
