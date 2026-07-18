package gui.components;

import gui.UiTheme;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;

/** FlowLayout que calcula corretamente a altura quando os grupos quebram de linha. */
public final class WrapLayout extends FlowLayout {
    public WrapLayout(int align, int horizontalGap, int verticalGap) {
        super(align, horizontalGap, verticalGap);
    }

    @Override public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override public Dimension minimumLayoutSize(Container target) {
        Dimension size = layoutSize(target, false);
        size.width = Math.max(1, size.width - getHgap());
        return size;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int availableWidth = target.getWidth();
            if (availableWidth <= 0 && target.getParent() != null) availableWidth = target.getParent().getWidth();
            if (availableWidth <= 0) {
                availableWidth = GraphicsEnvironment.isHeadless()
                        ? UiTheme.scale(1080)
                        : GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width
                                - UiTheme.scale(80);
            }

            Insets insets = target.getInsets();
            int horizontalInsets = insets.left + insets.right + getHgap() * 2;
            int maxWidth = Math.max(1, availableWidth - horizontalInsets);
            Dimension result = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            for (Component component : target.getComponents()) {
                if (!component.isVisible()) continue;
                Dimension size = preferred ? component.getPreferredSize() : component.getMinimumSize();
                if (rowWidth > 0 && rowWidth + getHgap() + size.width > maxWidth) {
                    addRow(result, rowWidth, rowHeight);
                    rowWidth = 0;
                    rowHeight = 0;
                }
                if (rowWidth > 0) rowWidth += getHgap();
                rowWidth += size.width;
                rowHeight = Math.max(rowHeight, size.height);
            }
            addRow(result, rowWidth, rowHeight);
            result.width += horizontalInsets;
            result.height += insets.top + insets.bottom + getVgap() * 2;
            return result;
        }
    }

    private void addRow(Dimension result, int width, int height) {
        if (height <= 0) return;
        result.width = Math.max(result.width, width);
        if (result.height > 0) result.height += getVgap();
        result.height += height;
    }
}
