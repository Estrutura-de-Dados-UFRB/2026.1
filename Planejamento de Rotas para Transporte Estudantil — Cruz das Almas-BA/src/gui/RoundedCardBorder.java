package gui;

import javax.swing.border.AbstractBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

/** Borda de card com título integrado e acabamento arredondado. */
final class RoundedCardBorder extends AbstractBorder {
    private final String title;

    RoundedCardBorder(String title) {
        this.title = title;
    }

    @Override
    public Insets getBorderInsets(java.awt.Component c) {
        return UiTheme.scaledInsets(18, 14, 14, 14);
    }

    @Override
    public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int radius = UiTheme.scale(10);
        g2.setColor(new Color(35, 55, 75, 18));
        g2.fillRoundRect(x + 1, y + UiTheme.scale(2), width - 2, height - UiTheme.scale(2), radius, radius);
        g2.setColor(UiTheme.BORDER);
        g2.drawRoundRect(x, y, width - 1, height - UiTheme.scale(2), radius, radius);
        if (title != null && !title.isBlank()) {
            Font font = UiTheme.FONT_BOLD.deriveFont(12f);
            g2.setFont(font);
            FontMetrics metrics = g2.getFontMetrics();
            int titleWidth = metrics.stringWidth(title) + UiTheme.scale(12);
            g2.setColor(UiTheme.SURFACE);
            g2.fillRect(x + UiTheme.scale(10), y + UiTheme.scale(1), titleWidth, metrics.getHeight());
            g2.setColor(UiTheme.PRIMARY_DARK);
            g2.drawString(title, x + UiTheme.scale(16), y + metrics.getAscent() + UiTheme.scale(2));
        }
        g2.dispose();
    }
}
