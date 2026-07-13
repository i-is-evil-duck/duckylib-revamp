package com.j3ly.duckylib.gui.widget;

import com.j3ly.duckylib.gui.theme.Theme;
import com.j3ly.duckylib.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

public class ScrollPanel extends Panel {
    private float scrollOffset = 0;
    private int contentHeight = 0;
    private int scrollbarWidth = 4;
    private boolean scrollbarVisible = false;
    private boolean scrolling = false;

    public ScrollPanel(String id, int x, int y, int width, int height) {
        super(id, x, y, width, height);
        setClipChildren(true);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        int absX = getAbsoluteX();
        int absY = getAbsoluteY();

        RenderUtil.fillRounded(graphics, absX, absY, width, height, 4, getBackgroundColor());

        contentHeight = 0;
        for (Widget child : children) {
            contentHeight = Math.max(contentHeight, child.getY() + child.getHeight());
        }

        scrollbarVisible = contentHeight > height;

        graphics.enableScissor(absX, absY, absX + width - (scrollbarVisible ? scrollbarWidth + 2 : 0), absY + height);

        for (Widget child : children) {
            int originalY = child.getY();
            child.setY(originalY - (int)scrollOffset);
            child.render(graphics, mouseX, mouseY, partialTick);
            child.setY(originalY);
        }

        graphics.disableScissor();

        if (scrollbarVisible) {
            int trackX = absX + width - scrollbarWidth - 2;
            int trackY = absY + 2;
            int trackHeight = height - 4;

            RenderUtil.fill(graphics, trackX, trackY, trackX + scrollbarWidth, trackY + trackHeight, Theme.getCurrent().getColor("slider.track_color"));

            float thumbRatio = (float)height / contentHeight;
            int thumbHeight = Math.max(20, (int)(trackHeight * thumbRatio));
            int thumbY = trackY + (int)((trackHeight - thumbHeight) * (scrollOffset / (contentHeight - height)));

            RenderUtil.fillRounded(graphics, trackX, thumbY, scrollbarWidth, thumbHeight, scrollbarWidth / 2, Theme.getCurrent().getColor("slider.thumb_color"));
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!visible) return false;
        if (contains(mouseX, mouseY) && scrollbarVisible) {
            scrollOffset -= delta * 20;
            scrollOffset = Math.max(0, Math.min(scrollOffset, contentHeight - height));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !enabled) return false;

        int absX = getAbsoluteX();
        int absY = getAbsoluteY();

        if (scrollbarVisible) {
            int trackX = absX + width - scrollbarWidth - 2;
            int trackY = absY + 2;
            int trackHeight = height - 4;
            float thumbRatio = (float)height / contentHeight;
            int thumbHeight = Math.max(20, (int)(trackHeight * thumbRatio));
            int thumbY = trackY + (int)((trackHeight - thumbHeight) * (scrollOffset / (contentHeight - height)));

            if (mouseX >= trackX && mouseX < trackX + scrollbarWidth && mouseY >= thumbY && mouseY < thumbY + thumbHeight) {
                scrolling = true;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY + scrollOffset, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrolling && scrollbarVisible) {
            int trackHeight = height - 4;
            float scrollRatio = (float)dragY / trackHeight;
            scrollOffset += scrollRatio * (contentHeight - height);
            scrollOffset = Math.max(0, Math.min(scrollOffset, contentHeight - height));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY + scrollOffset, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        scrolling = false;
        return super.mouseReleased(mouseX, mouseY + scrollOffset, button);
    }

    public void setScrollOffset(float offset) {
        this.scrollOffset = Math.max(0, Math.min(offset, contentHeight - height));
    }
    public float getScrollOffset() { return scrollOffset; }
}
