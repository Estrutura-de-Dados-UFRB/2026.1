package gui;

import model.Rota;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;

@SuppressWarnings({"serial", "this-escape"})
public class BottomRoutePanel extends gui.components.ModernCard {

    private final JTextArea area = new JTextArea(8, 80);

    public BottomRoutePanel() {
        super("Resumo da rota");
        area.setEditable(false);
        area.setFont(UiTheme.FONT.deriveFont(12f));
        area.setForeground(UiTheme.TEXT);
        area.setBackground(new java.awt.Color(248, 250, 252));
        area.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(UiTheme.BORDER));
        content().add(scroll, BorderLayout.CENTER);
    }

    public void mostrar(Rota rota) {
        if (rota == null) {
            area.setText("ROTA CALCULADA\n\nNenhuma rota ativa.");
            return;
        }
        area.setText(rota.formatarResumo());
        area.setCaretPosition(0);
    }

    public void limpar() {
        area.setText("ROTA CALCULADA\n\nNenhuma rota ativa.");
    }
}
