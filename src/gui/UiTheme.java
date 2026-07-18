package gui;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Font;

/** Identidade visual compartilhada por toda a aplicação. */
public final class UiTheme {

    public static final Color BACKGROUND = new Color(244, 246, 248);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_ALT = new Color(244, 246, 248);
    public static final Color PRIMARY = new Color(24, 75, 122);
    public static final Color PRIMARY_DARK = new Color(18, 57, 94);
    public static final Color PRIMARY_LIGHT = new Color(220, 238, 255);
    public static final Color ACCENT = new Color(46, 158, 91);
    public static final Color DANGER = new Color(214, 69, 69);
    public static final Color TEXT = new Color(45, 55, 72);
    public static final Color TEXT_MUTED = new Color(95, 107, 122);
    public static final Color BORDER = new Color(217, 222, 228);
    public static final Color BORDER_STRONG = new Color(190, 198, 207);
    public static final Font FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = FONT.deriveFont(Font.BOLD);
    public static final Font TITLE_FONT = FONT_BOLD.deriveFont(18f);
    public static final Font SUBTITLE_FONT = FONT_BOLD.deriveFont(15f);
    public static final int SPACE_XS = 8;
    public static final int SPACE_MD = 16;
    public static final int SPACE_LG = 20;
    private static final double SCALE = detectarEscala();

    private UiTheme() {
    }

    public static void aplicar() {
        UIManager.put("defaultFont", FONT);
        UIManager.put("Label.font", FONT);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("Button.background", SURFACE);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Button.focus", PRIMARY);
        UIManager.put("Menu.font", FONT);
        UIManager.put("MenuItem.font", FONT);
        UIManager.put("CheckBoxMenuItem.font", FONT);
        UIManager.put("ToggleButton.font", FONT_BOLD);
        UIManager.put("ToggleButton.background", SURFACE);
        UIManager.put("ToggleButton.foreground", TEXT);
        UIManager.put("ToggleButton.focus", PRIMARY);
        UIManager.put("TextField.font", FONT);
        UIManager.put("TextArea.font", FONT);
        UIManager.put("TextPane.font", FONT);
        UIManager.put("ComboBox.font", FONT);
        UIManager.put("Spinner.font", FONT);
        UIManager.put("Tree.font", FONT);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", SURFACE);
        UIManager.put("ProgressBar.foreground", ACCENT);
        UIManager.put("ProgressBar.background", BORDER);
        UIManager.put("Table.font", FONT.deriveFont(12f));
        UIManager.put("TableHeader.font", FONT_BOLD.deriveFont(12f));
        UIManager.put("TabbedPane.font", FONT_BOLD.deriveFont(13f));
        UIManager.put("TabbedPane.background", SURFACE);
        UIManager.put("TabbedPane.foreground", TEXT);
        UIManager.put("TabbedPane.selected", PRIMARY_LIGHT);
        UIManager.put("TabbedPane.contentAreaColor", SURFACE);
        UIManager.put("TabbedPane.focus", PRIMARY);
        UIManager.put("TitledBorder.font", SUBTITLE_FONT);
        UIManager.put("ScrollBar.width", scale(11));
        UIManager.put("ScrollBar.thumb", new Color(174, 184, 196));
        UIManager.put("ScrollBar.track", BACKGROUND);
        UIManager.put("Separator.foreground", BORDER);
        UIManager.put("TextField.selectionBackground", PRIMARY_LIGHT);
        UIManager.put("TextField.selectionForeground", TEXT);
        UIManager.put("TextField.background", SURFACE);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("FormattedTextField.background", SURFACE);
        UIManager.put("FormattedTextField.foreground", TEXT);
        UIManager.put("ComboBox.background", SURFACE);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("List.background", SURFACE);
        UIManager.put("List.foreground", TEXT);
        UIManager.put("TextArea.background", SURFACE);
        UIManager.put("TextArea.foreground", TEXT);
    }

    public static int scale(int value) {
        return Math.max(1, (int) Math.round(value * SCALE));
    }

    public static Dimension scaledSize(int width, int height) {
        return new Dimension(scale(width), scale(height));
    }

    public static Insets scaledInsets(int top, int left, int bottom, int right) {
        return new Insets(scale(top), scale(left), scale(bottom), scale(right));
    }

    public static void estilizarBotao(AbstractButton botao) {
        botao.setFont(FONT_BOLD);
        botao.setFocusPainted(true);
        botao.setRolloverEnabled(true);
        botao.setOpaque(true);
        botao.setBackground(SURFACE);
        botao.setForeground(TEXT);
    }

    public static Color corContraste(Color fundo) {
        double luminancia = (0.299 * fundo.getRed() + 0.587 * fundo.getGreen() + 0.114 * fundo.getBlue()) / 255.0;
        return luminancia > 0.62 ? TEXT : Color.WHITE;
    }

    public static Color bordaContraste(Color fundo) {
        double luminancia = (0.299 * fundo.getRed() + 0.587 * fundo.getGreen() + 0.114 * fundo.getBlue()) / 255.0;
        if (fundo.getAlpha() < 255) {
            return new Color(44, 56, 68, 120);
        }
        return luminancia > 0.62 ? new Color(97, 110, 124) : new Color(255, 255, 255, 110);
    }

    public static Border cardBorder(String title) {
        return new RoundedCardBorder(title);
    }

    public static void card(JComponent component, String title) {
        component.setOpaque(true);
        component.setBackground(SURFACE);
        component.setBorder(cardBorder(title));
    }

    private static double detectarEscala() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device = ge.getDefaultScreenDevice();
            double dpi = Toolkit.getDefaultToolkit().getScreenResolution();
            if (device == null || dpi <= 0) {
                return 1.0;
            }
            return Math.max(1.0, dpi / 96.0);
        } catch (HeadlessException ex) {
            return 1.0;
        } catch (Throwable ex) {
            return 1.0;
        }
    }
}
