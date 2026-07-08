package com.j3ly.duckylib.api.events;

import com.j3ly.duckylib.gui.widget.Widget;

public class DuckyClickEvent {
    private final Widget widget;
    private final int button;
    private final double mouseX;
    private final double mouseY;

    public DuckyClickEvent(Widget widget, int button, double mouseX, double mouseY) {
        this.widget = widget;
        this.button = button;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public Widget getWidget() { return widget; }
    public int getButton() { return button; }
    public double getMouseX() { return mouseX; }
    public double getMouseY() { return mouseY; }
}
