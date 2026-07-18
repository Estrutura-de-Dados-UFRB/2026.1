package gui.components;

import gui.UiTheme;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Botão uniforme com estados de hover, foco e pressão. */
public class ModernButton extends JButton {
    public enum Variant { PRIMARY, SECONDARY, SUCCESS, DANGER, GHOST }

    private final Color base;
    private final boolean pill;

    public ModernButton(String text, AppIcon.Kind icon, Variant variant) {
        this(text, icon, variant, false);
    }

    public ModernButton(String text, AppIcon.Kind icon, Variant variant, boolean pill) {
        super(text);
        this.base = backgroundOf(variant);
        this.pill = pill;
        Color foreground = variant == Variant.PRIMARY || variant == Variant.SUCCESS || variant == Variant.DANGER
                ? Color.WHITE : UiTheme.TEXT;
        if (icon != null) setIcon(new AppIcon(icon, foreground));
        setForeground(foreground);
        setFont(UiTheme.FONT_BOLD.deriveFont(12f));
        setIconTextGap(UiTheme.scale(7));
        setMargin(UiTheme.scaledInsets(7, 12, 7, 12));
        setPreferredSize(new Dimension(getPreferredSize().width, UiTheme.scale(36)));
        setMinimumSize(new Dimension(UiTheme.scale(36), UiTheme.scale(36)));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setUI(new BasicButtonUI());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = base;
        if (!isEnabled()) fill = mix(base, UiTheme.BACKGROUND, .55f);
        else if (getModel().isPressed()) fill = mix(base, Color.BLACK, .14f);
        else if (getModel().isRollover()) fill = mix(base, Color.WHITE, .10f);
        int arc = pill ? getHeight() : UiTheme.scale(12);
        int y = getModel().isPressed() ? 1 : 0;
        g2.setColor(fill);
        g2.fillRoundRect(0, y, getWidth() - 1, getHeight() - 1 - y, arc, arc);
        g2.setColor(mix(base, variantBorder(), .25f));
        g2.drawRoundRect(0, y, getWidth() - 1, getHeight() - 1 - y, arc, arc);
        if (isFocusOwner()) {
            g2.setColor(new Color(24, 75, 122, 110));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, arc, arc);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    private Color variantBorder() { return base.equals(UiTheme.SURFACE) ? UiTheme.BORDER_STRONG : base.darker(); }
    private static Color backgroundOf(Variant v) {
        return switch (v) {
            case PRIMARY -> UiTheme.PRIMARY;
            case SUCCESS -> UiTheme.ACCENT;
            case DANGER -> UiTheme.DANGER;
            case GHOST -> new Color(234, 244, 253);
            case SECONDARY -> UiTheme.SURFACE;
        };
    }
    private static Color mix(Color a, Color b, float p) {
        return new Color((int)(a.getRed()*(1-p)+b.getRed()*p), (int)(a.getGreen()*(1-p)+b.getGreen()*p),
                (int)(a.getBlue()*(1-p)+b.getBlue()*p), a.getAlpha());
    }
}
