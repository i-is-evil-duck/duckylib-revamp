package com.j3ly.duckylib.api.events;

import com.j3ly.duckylib.gui.widget.Widget;

public class DuckyValueChangeEvent<T> {
    private final Widget widget;
    private final T oldValue;
    private final T newValue;

    public DuckyValueChangeEvent(Widget widget, T oldValue, T newValue) {
        this.widget = widget;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Widget getWidget() { return widget; }
    public T getOldValue() { return oldValue; }
    public T getNewValue() { return newValue; }
}
