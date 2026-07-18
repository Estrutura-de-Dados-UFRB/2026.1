package gui.components;

import gui.UiTheme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Superfície arredondada com sombra suave e borda discreta. */
public class RoundedPanel extends JPanel {
    private final Color surface;
    public RoundedPanel() { this(UiTheme.SURFACE); }
    public RoundedPanel(Color surface) { this.surface = surface; setOpaque(false); }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = UiTheme.scale(16), shadow = UiTheme.scale(3);
        g2.setColor(new Color(18, 57, 94, 20));
        g2.fillRoundRect(shadow, shadow, getWidth() - 2 * shadow, getHeight() - shadow, arc, arc);
        g2.setColor(surface);
        g2.fillRoundRect(0, 0, getWidth() - shadow, getHeight() - shadow, arc, arc);
        g2.setColor(UiTheme.BORDER);
        g2.drawRoundRect(0, 0, getWidth() - shadow - 1, getHeight() - shadow - 1, arc, arc);
        g2.dispose();
        super.paintComponent(g);
    }
}
