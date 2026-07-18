package gui.components;

import gui.UiTheme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/** Card reutilizável com cabeçalho tipográfico e área de conteúdo. */
public class ModernCard extends RoundedPanel {
    private final JPanel content = new JPanel(new BorderLayout(UiTheme.scale(8), UiTheme.scale(8)));
    public ModernCard(String title) {
        setLayout(new BorderLayout(0, UiTheme.scale(10)));
        setBorder(BorderFactory.createEmptyBorder(UiTheme.scale(16), UiTheme.scale(16), UiTheme.scale(18), UiTheme.scale(18)));
        if (title != null && !title.isBlank()) {
            JLabel label = new JLabel(title);
            label.setFont(UiTheme.FONT_BOLD.deriveFont(15f));
            label.setForeground(UiTheme.PRIMARY_DARK);
            add(label, BorderLayout.NORTH);
        }
        content.setOpaque(false);
        add(content, BorderLayout.CENTER);
    }
    public JPanel content() { return content; }
}
