package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import model.Grafo;
import model.Ponto;
import model.Rota;

/**
 * Painel de estatísticas exibindo métricas da rota.
 */
@SuppressWarnings({"serial", "this-escape"})
public class StatisticsPanel extends gui.components.ModernCard {

    private final JLabel lblNumeroPontos;
    private final JLabel lblTotalAlunos;
    private final JLabel lblDistanciaTotal;
    private final JLabel lblTempoTotal;
    private final JLabel lblNumeroParadas;
    private final JLabel lblCombustivel;
    private final JLabel lblAlgoritmo;

    public StatisticsPanel() {
        super("Estatísticas");
        content().setLayout(new BoxLayout(content(), BoxLayout.Y_AXIS));

        Font fonteTitulo = UiTheme.FONT_BOLD.deriveFont(12f);
        Font fonteValor = UiTheme.FONT.deriveFont(12f);

        lblAlgoritmo = criarLinha("Algoritmo:", "-", fonteTitulo, fonteValor);
        content().add(Box.createVerticalStrut(5));
        content().add(new JSeparator());
        content().add(Box.createVerticalStrut(5));

        lblNumeroPontos = criarLinha("Nº de Pontos:", "0", fonteTitulo, fonteValor);
        lblTotalAlunos = criarLinha("Estudantes:", "0", fonteTitulo, fonteValor);
        lblDistanciaTotal = criarLinha("Distância:", "0.00 km", fonteTitulo, fonteValor);
        lblTempoTotal = criarLinha("Tempo:", "0 min", fonteTitulo, fonteValor);
        lblNumeroParadas = criarLinha("Paradas:", "0", fonteTitulo, fonteValor);
        lblCombustivel = criarLinha("Combustível:", "0.0 L", fonteTitulo, fonteValor);
    }

    private JLabel criarLinha(String rotulo, String valorInicial,
                               Font fonteRotulo, Font fonteValor) {
        JPanel linha = new JPanel(new BorderLayout());
        linha.setBackground(UiTheme.SURFACE);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, UiTheme.scale(21)));

        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(fonteRotulo);
        lblRotulo.setForeground(UiTheme.TEXT_MUTED);
        linha.add(lblRotulo, BorderLayout.WEST);

        JLabel lblValor = new JLabel(valorInicial);
        lblValor.setFont(fonteValor);
        lblValor.setForeground(UiTheme.TEXT);
        linha.add(lblValor, BorderLayout.EAST);

        content().add(linha);
        return lblValor;
    }

    public void atualizar(Grafo grafo, Rota rota, String algoritmo) {
        if (grafo != null) {
            lblNumeroPontos.setText(String.valueOf(grafo.getPontos().size()));
            int totalAlunos = grafo.getPontos().stream()
                    .mapToInt(Ponto::getQuantidadeAlunos).sum();
            lblTotalAlunos.setText(String.valueOf(totalAlunos));
        }

        if (rota != null) {
            lblTotalAlunos.setText(String.valueOf(rota.calcularTotalAlunos()));
            lblDistanciaTotal.setText(String.format("%.2f km", rota.getDistanciaTotal()));
            lblTempoTotal.setText(String.format("%.0f min", rota.getTempoTotal()));
            lblNumeroParadas.setText(String.valueOf(rota.getPercurso().size()));
            lblCombustivel.setText(String.format("%.1f L",
                    rota.getDistanciaTotal() / 5.0));
        }

        lblAlgoritmo.setText(algoritmo != null ? algoritmo : "-");
        revalidate();
        repaint();
    }

    public void limpar() {
        lblNumeroPontos.setText("0");
        lblTotalAlunos.setText("0");
        lblDistanciaTotal.setText("0.00 km");
        lblTempoTotal.setText("0 min");
        lblNumeroParadas.setText("0");
        lblCombustivel.setText("0.0 L");
        lblAlgoritmo.setText("-");
    }
}
