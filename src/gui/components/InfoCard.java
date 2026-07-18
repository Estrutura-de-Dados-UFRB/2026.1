package gui.components;

import gui.UiTheme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import java.awt.BorderLayout;

/** Card compacto de indicador com rótulo e valor em destaque. */
public final class InfoCard extends RoundedPanel {
    private final JLabel value = new JLabel();
    public InfoCard(String label, String initialValue) {
        setLayout(new BorderLayout(0, UiTheme.scale(4)));
        setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 14));
        JLabel caption = new JLabel(label);
        caption.setFont(UiTheme.FONT.deriveFont(12f));
        caption.setForeground(UiTheme.TEXT_MUTED);
        value.setText(initialValue);
        value.setFont(UiTheme.FONT_BOLD.deriveFont(18f));
        value.setForeground(UiTheme.PRIMARY_DARK);
        add(caption, BorderLayout.NORTH);
        add(value, BorderLayout.CENTER);
    }
    public void setValue(String text) { value.setText(text); }
}
