package gui.components;

import gui.UiTheme;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Ícones vetoriais pequenos, nítidos em qualquer escala e sem arquivos externos. */
public final class AppIcon implements Icon {
    public enum Kind { ADD, EDIT, DELETE, ROUTE, PLAY, PAUSE, STOP, CLEAR, TARGET, REFRESH, PIN, SEARCH, CHEVRON, MINUS, PLUS }

    private final Kind kind;
    private final Color color;
    private final int size;

    public AppIcon(Kind kind, Color color) {
        this(kind, color, UiTheme.scale(16));
    }

    public AppIcon(Kind kind, Color color, int size) {
        this.kind = kind;
        this.color = color;
        this.size = size;
    }

    @Override public int getIconWidth() { return size; }
    @Override public int getIconHeight() { return size; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(Math.max(1.5f, size / 9f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int m = Math.max(2, size / 5), mid = size / 2, end = size - m;
        switch (kind) {
            case ADD, PLUS -> { g2.drawLine(m, mid, end, mid); g2.drawLine(mid, m, mid, end); }
            case MINUS -> g2.drawLine(m, mid, end, mid);
            case EDIT -> { g2.drawLine(m, end, end - 1, m + 1); g2.drawLine(m, end, m + 3, end - 1); g2.drawLine(end - 3, m, end, m + 3); }
            case DELETE -> { g2.drawRect(m + 2, m + 3, size - 2 * m - 4, size - 2 * m); g2.drawLine(m, m + 1, end, m + 1); g2.drawLine(mid - 2, m - 1, mid + 2, m - 1); }
            case PLAY -> g2.fillPolygon(new int[]{m + 1, end, m + 1}, new int[]{m, mid, end}, 3);
            case PAUSE -> { g2.fillRoundRect(m + 1, m, 3, end - m, 2, 2); g2.fillRoundRect(end - 4, m, 3, end - m, 2, 2); }
            case STOP -> g2.fillRoundRect(m, m, end - m, end - m, 2, 2);
            case REFRESH -> { g2.drawArc(m, m, end - m, end - m, 35, 285); g2.drawLine(end - 1, m, end - 4, m); g2.drawLine(end - 1, m, end - 1, m + 3); }
            case SEARCH -> { g2.drawOval(m, m, size / 2, size / 2); g2.drawLine(mid + 2, mid + 2, end, end); }
            case TARGET -> { g2.drawOval(m, m, end - m, end - m); g2.drawOval(mid - 2, mid - 2, 4, 4); }
            case PIN -> { g2.drawOval(mid - 4, m, 8, 8); g2.drawLine(mid - 4, m + 6, mid, end); g2.drawLine(mid + 4, m + 6, mid, end); }
            case CHEVRON -> { g2.drawLine(m, mid - 2, mid, mid + 2); g2.drawLine(mid, mid + 2, end, mid - 2); }
            case CLEAR -> { g2.drawLine(m, m, end, end); g2.drawLine(end, m, m, end); }
            case ROUTE -> { g2.drawOval(m - 1, m - 1, 4, 4); g2.drawOval(end - 3, end - 3, 4, 4); g2.drawLine(m + 3, m + 2, end - 3, end - 2); }
        }
        g2.dispose();
    }
}
