import gui.MainWindow;
import gui.UiTheme;

import javax.swing.*;
import java.awt.GraphicsEnvironment;

/**
 * Ponto de entrada do sistema de planejamento
 * de rotas de transporte universitário.
 *
 * Cruz das Almas - Bahia - Brasil
 */
public class Main {

    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("Este sistema precisa de um ambiente grafico para abrir a interface.");
            return;
        }

        // Look & Feel consistente: evita que temas escuros do sistema vazem para
        // formulários que usam a identidade visual clara da aplicação.
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException
                 | InstantiationException
                 | IllegalAccessException
                 | UnsupportedLookAndFeelException ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (ClassNotFoundException
                     | InstantiationException
                     | IllegalAccessException
                     | UnsupportedLookAndFeelException ignored) {
            }
        }
        UiTheme.aplicar();

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
