package gui.components;

import gui.UiTheme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

/** Controle compacto de velocidade para uso em toolbars responsivas. */
public class SpeedControlPanel extends RoundedPanel {
    private final ModernSlider slider = new ModernSlider(5, 120, 40);
    private final JLabel value = new JLabel("40 km/h", JLabel.CENTER);
    public SpeedControlPanel() {
        setLayout(new BorderLayout(UiTheme.scale(8), 0));
        setBorder(BorderFactory.createEmptyBorder(5, 9, 8, 11));
        JLabel title = new JLabel("Velocidade");
        title.setFont(UiTheme.FONT_BOLD.deriveFont(11f));
        title.setForeground(UiTheme.TEXT_MUTED);
        JPanel rail = new JPanel(new BorderLayout(UiTheme.scale(5), 0));
        rail.setOpaque(false);
        ModernButton minus = compact(AppIcon.Kind.MINUS, "Diminuir 5 km/h");
        ModernButton plus = compact(AppIcon.Kind.PLUS, "Aumentar 5 km/h");
        minus.addActionListener(e -> slider.setValue(Math.max(slider.getMinimum(), slider.getValue() - 5)));
        plus.addActionListener(e -> slider.setValue(Math.min(slider.getMaximum(), slider.getValue() + 5)));
        rail.add(minus, BorderLayout.WEST); rail.add(slider, BorderLayout.CENTER); rail.add(plus, BorderLayout.EAST);
        value.setFont(UiTheme.FONT_BOLD.deriveFont(12f));
        value.setForeground(UiTheme.PRIMARY_DARK);
        value.setPreferredSize(UiTheme.scaledSize(58, 30));
        slider.addChangeListener(e -> value.setText(slider.getValue() + " km/h"));
        add(title, BorderLayout.WEST);
        add(rail, BorderLayout.CENTER);
        add(value, BorderLayout.EAST);
        setPreferredSize(UiTheme.scaledSize(300, 44));
        setMinimumSize(UiTheme.scaledSize(250, 44));
    }
    private ModernButton compact(AppIcon.Kind kind, String tooltip) {
        ModernButton button = new ModernButton("", kind, ModernButton.Variant.GHOST, true);
        button.setPreferredSize(UiTheme.scaledSize(28, 28));
        button.setMinimumSize(UiTheme.scaledSize(28, 28));
        button.setToolTipText(tooltip); return button;
    }
    public int getValue() { return slider.getValue(); }
}
