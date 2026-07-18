package gui.components;

import gui.UiTheme;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import java.awt.Dimension;

/** Spinner alinhado, alto e visualmente compatível com os demais campos. */
public final class ModernSpinner extends JSpinner {
    public ModernSpinner(SpinnerModel model) {
        super(model);
        setFont(UiTheme.FONT);
        setPreferredSize(UiTheme.scaledSize(96, 36));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER_STRONG),
                BorderFactory.createEmptyBorder(2, 8, 2, 4)));
        JComponent editor = getEditor();
        if (editor instanceof DefaultEditor defaultEditor) {
            JFormattedTextField field = defaultEditor.getTextField();
            field.setHorizontalAlignment(JFormattedTextField.CENTER);
            field.setBorder(BorderFactory.createEmptyBorder());
            field.setFont(UiTheme.FONT_BOLD);
        }
        setMinimumSize(new Dimension(UiTheme.scale(80), UiTheme.scale(36)));
    }
}
