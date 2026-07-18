package gui;

import model.Grafo;
import model.Ponto;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Color;
import java.util.List;

@SuppressWarnings({"serial", "this-escape"})
public class DetailsPanel extends gui.components.ModernCard {

    private final JLabel nome = new JLabel("-");
    private final JLabel tipo = new JLabel("-");
    private final JLabel latitude = new JLabel("-");
    private final JLabel longitude = new JLabel("-");
    private final JLabel alunos = new JLabel("-");
    private final JLabel bairro = new JLabel("-");
    private final JLabel conexoes = new JLabel("-");
    private final JLabel distanciaMedia = new JLabel("-");
    private final JLabel tempoMedio = new JLabel("-");
    private final JLabel prioridade = new JLabel("-");
    private final JLabel capacidade = new JLabel("-");

    public DetailsPanel() {
        super("Detalhes do ponto");

        JPanel grid = new JPanel(new GridLayout(0, 2, 6, 6));
        grid.setBackground(UiTheme.SURFACE);
        adicionarLinha(grid, "Nome", nome);
        adicionarLinha(grid, "Tipo", tipo);
        adicionarLinha(grid, "Latitude", latitude);
        adicionarLinha(grid, "Longitude", longitude);
        adicionarLinha(grid, "Alunos", alunos);
        adicionarLinha(grid, "Bairro", bairro);
        adicionarLinha(grid, "Conexoes", conexoes);
        adicionarLinha(grid, "Distancia media", distanciaMedia);
        adicionarLinha(grid, "Tempo medio", tempoMedio);
        adicionarLinha(grid, "Prioridade", prioridade);
        adicionarLinha(grid, "Capacidade", capacidade);
        content().add(grid);
        setPreferredSize(new Dimension(260, 0));
    }

    public void mostrar(Ponto ponto, Grafo grafo) {
        if (ponto == null) {
            limpar();
            return;
        }
        nome.setText(ponto.getNome());
        tipo.setText(ponto.getTipo().getRotulo());
        latitude.setText(String.format("%.6f", ponto.getLatitude()));
        longitude.setText(String.format("%.6f", ponto.getLongitude()));
        alunos.setText(String.valueOf(ponto.getQuantidadeAlunos()));
        bairro.setText(ponto.getBairro());
        conexoes.setText(String.valueOf(grafo.getVizinhos(ponto).size()));
        distanciaMedia.setText(String.format("%.2f km", ponto.getDistanciaMedia()));
        tempoMedio.setText(String.format("%.0f min", ponto.getTempoMedio()));
        prioridade.setText(String.valueOf(ponto.getPrioridade()));
        capacidade.setText(String.valueOf(ponto.getCapacidade()));
    }

    public void limpar() {
        nome.setText("-");
        tipo.setText("-");
        latitude.setText("-");
        longitude.setText("-");
        alunos.setText("-");
        bairro.setText("-");
        conexoes.setText("-");
        distanciaMedia.setText("-");
        tempoMedio.setText("-");
        prioridade.setText("-");
        capacidade.setText("-");
    }

    private void adicionarLinha(JPanel grid, String rotulo, JLabel valor) {
        JLabel label = new JLabel(rotulo);
        label.setForeground(UiTheme.TEXT_MUTED);
        valor.setForeground(UiTheme.TEXT);
        valor.setFont(UiTheme.FONT_BOLD);
        grid.add(label);
        grid.add(valor);
    }
}
