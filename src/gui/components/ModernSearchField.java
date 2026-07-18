package gui.components;

import gui.UiTheme;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/** Campo de busca arredondado com lupa, placeholder e padding interno. */
public final class ModernSearchField extends JTextField {
    private final String placeholder;
    public ModernSearchField(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
        setBackground(UiTheme.SURFACE);
        setFont(UiTheme.FONT);
        setForeground(UiTheme.TEXT);
        setCaretColor(UiTheme.PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(0, UiTheme.scale(38), 0, UiTheme.scale(12)));
        setPreferredSize(UiTheme.scaledSize(260, 38));
        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { repaint(); }
            @Override public void focusLost(FocusEvent e) { repaint(); }
        });
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UiTheme.scale(14), UiTheme.scale(14));
        g2.setColor(isFocusOwner() ? UiTheme.PRIMARY : UiTheme.BORDER_STRONG);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UiTheme.scale(14), UiTheme.scale(14));
        new AppIcon(AppIcon.Kind.SEARCH, UiTheme.TEXT_MUTED).paintIcon(this, g2, UiTheme.scale(12), (getHeight()-UiTheme.scale(16))/2);
        g2.dispose();
        super.paintComponent(g);
        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D hint = (Graphics2D) g.create();
            hint.setFont(getFont());
            hint.setColor(new Color(125, 136, 150));
            Insets in = getInsets();
            hint.drawString(placeholder, in.left, (getHeight() + hint.getFontMetrics().getAscent() - hint.getFontMetrics().getDescent()) / 2);
            hint.dispose();
        }
    }
}
