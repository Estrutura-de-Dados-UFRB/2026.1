package gui.components;

import gui.UiTheme;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

/** Slider com trilha azul, thumb circular e tooltip de valor em tempo real. */
public class ModernSlider extends JSlider {
    public ModernSlider(int min, int max, int value) {
        super(min, max, value);
        setOpaque(false);
        setMajorTickSpacing(20);
        setMinorTickSpacing(5);
        setSnapToTicks(true);
        setPreferredSize(new Dimension(UiTheme.scale(180), UiTheme.scale(32)));
        setToolTipText(value + " km/h");
        setUI(new SliderUi(this));
        addChangeListener(e -> setToolTipText(getValue() + " km/h"));
    }

    @Override public String getToolTipText(MouseEvent event) { return getValue() + " km/h"; }

    private static final class SliderUi extends BasicSliderUI {
        private SliderUi(JSlider slider) { super(slider); }
        @Override protected Dimension getThumbSize() { return UiTheme.scaledSize(20, 20); }
        @Override public void paintTrack(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h = UiTheme.scale(6), y = trackRect.y + (trackRect.height - h) / 2;
            g2.setColor(UiTheme.BORDER);
            g2.fillRoundRect(trackRect.x, y, trackRect.width, h, h, h);
            int filled = Math.max(h, thumbRect.x + thumbRect.width / 2 - trackRect.x);
            g2.setColor(UiTheme.PRIMARY);
            g2.fillRoundRect(trackRect.x, y, filled, h, h, h);
            g2.dispose();
        }
        @Override public void paintThumb(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle r = thumbRect;
            g2.setColor(new Color(18, 57, 94, 35));
            g2.fillOval(r.x + 1, r.y + 2, r.width, r.height);
            g2.setColor(Color.WHITE);
            g2.fillOval(r.x, r.y, r.width - 1, r.height - 1);
            g2.setColor(UiTheme.PRIMARY);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(r.x + 1, r.y + 1, r.width - 3, r.height - 3);
            g2.dispose();
        }
        @Override public void paintFocus(Graphics g) { }
    }
}
