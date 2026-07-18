package gui.components;

import gui.UiTheme;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.FlowLayout;

/** Linha de toolbar com espaçamento consistente e fundo transparente. */
public final class ModernToolbar extends JPanel {
    public ModernToolbar() {
        super(new FlowLayout(FlowLayout.LEFT, UiTheme.scale(8), UiTheme.scale(5)));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }
}
