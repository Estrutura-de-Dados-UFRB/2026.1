package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import gui.components.ModernButton;

/**
 * Painel lateral com botões de controle.
 */
@SuppressWarnings({"serial", "this-escape"})
public class ControlPanel extends JPanel {

    private final JButton btnAdicionar;
    private final JButton btnRemover;
    private final JButton btnEditar;
    private final JButton btnCalcularRota;
    private final JButton btnDijkstra;
    private final JButton btnPrim;
    private final JButton btnKruskal;
    private final JButton btnGuloso;
    private final JButton btnTSP;
    private final JButton btnVRP;
    private final JButton btnSimular;
    private final JButton btnLimpar;
    private final JButton btnExportarCSV;
    private final JButton btnExportarPDF;
    private final JProgressBar progressBar;

    public ControlPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UiTheme.BACKGROUND);

        Font fonteBotao = UiTheme.FONT_BOLD.deriveFont(12f);
        Dimension tamanhoBotao = UiTheme.scaledSize(188, 34);

        // Título
        JLabel titulo = new JLabel("CONTROLES");
        titulo.setFont(UiTheme.FONT_BOLD.deriveFont(16f));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titulo);
        add(Box.createVerticalStrut(15));

        // --- Gerenciamento de Pontos ---
        JLabel lblPontos = new JLabel("Gerenciar Pontos");
        lblPontos.setFont(UiTheme.FONT_BOLD.deriveFont(12f));
        lblPontos.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblPontos);
        add(Box.createVerticalStrut(5));

        btnAdicionar = criarBotao("➕ Adicionar Ponto", tamanhoBotao, fonteBotao);
        btnRemover = criarBotao("➖ Remover Ponto", tamanhoBotao, fonteBotao);
        btnEditar = criarBotao("✏️ Editar Ponto", tamanhoBotao, fonteBotao);

        add(btnAdicionar);
        add(Box.createVerticalStrut(5));
        add(btnRemover);
        add(Box.createVerticalStrut(5));
        add(btnEditar);
        add(Box.createVerticalStrut(15));

        // --- Algoritmos ---
        JLabel lblAlgoritmos = new JLabel("Algoritmos");
        lblAlgoritmos.setFont(UiTheme.FONT_BOLD.deriveFont(12f));
        lblAlgoritmos.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblAlgoritmos);
        add(Box.createVerticalStrut(5));

        btnCalcularRota = criarBotao("🗺️ Calcular Rota", tamanhoBotao, fonteBotao);
        btnDijkstra = criarBotao("📏 Dijkstra", tamanhoBotao, fonteBotao);
        btnPrim = criarBotao("🌳 Prim (AGM)", tamanhoBotao, fonteBotao);
        btnKruskal = criarBotao("🌲 Kruskal (AGM)", tamanhoBotao, fonteBotao);
        btnGuloso = criarBotao("🎯 Algoritmo Guloso", tamanhoBotao, fonteBotao);
        btnTSP = criarBotao("🔄 Caixeiro Viajante", tamanhoBotao, fonteBotao);
        btnVRP = criarBotao("🚌 VRP (Múltiplos Ônibus)", tamanhoBotao, fonteBotao);

        add(btnCalcularRota);
        add(Box.createVerticalStrut(5));
        add(btnDijkstra);
        add(Box.createVerticalStrut(5));
        add(btnPrim);
        add(Box.createVerticalStrut(5));
        add(btnKruskal);
        add(Box.createVerticalStrut(5));
        add(btnGuloso);
        add(Box.createVerticalStrut(5));
        add(btnTSP);
        add(Box.createVerticalStrut(5));
        add(btnVRP);
        add(Box.createVerticalStrut(15));

        // --- Ações ---
        JLabel lblAcoes = new JLabel("Ações");
        lblAcoes.setFont(UiTheme.FONT_BOLD.deriveFont(12f));
        lblAcoes.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblAcoes);
        add(Box.createVerticalStrut(5));

        btnSimular = criarBotao("🧪 Simular Novo Aluno", tamanhoBotao, fonteBotao);
        btnLimpar = criarBotao("🧹 Limpar Rota", tamanhoBotao, fonteBotao);
        btnExportarCSV = criarBotao("📄 Exportar CSV", tamanhoBotao, fonteBotao);
        btnExportarPDF = criarBotao("📕 Exportar PDF", tamanhoBotao, fonteBotao);

        add(btnSimular);
        add(Box.createVerticalStrut(5));
        add(btnLimpar);
        add(Box.createVerticalStrut(5));
        add(btnExportarCSV);
        add(Box.createVerticalStrut(5));
        add(btnExportarPDF);
        add(Box.createVerticalStrut(15));

        // Barra de progresso
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setMaximumSize(UiTheme.scaledSize(188, 22));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(progressBar);

        add(Box.createVerticalGlue());
    }

    private JButton criarBotao(String texto, Dimension tamanho, Font fonte) {
        JButton btn = new ModernButton(texto, null, ModernButton.Variant.SECONDARY);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, tamanho.height));
        btn.setPreferredSize(tamanho);
        btn.setMinimumSize(new Dimension(tamanho.width, tamanho.height));
        btn.setFont(fonte);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    public void setProgresso(int valor) {
        progressBar.setValue(valor);
    }

    public void setProgressoIndeterminado(boolean indeterminado) {
        progressBar.setIndeterminate(indeterminado);
    }

    // Listeners

    public void aoAdicionar(ActionListener l) { btnAdicionar.addActionListener(l); }
    public void aoRemover(ActionListener l) { btnRemover.addActionListener(l); }
    public void aoEditar(ActionListener l) { btnEditar.addActionListener(l); }
    public void aoCalcularRota(ActionListener l) { btnCalcularRota.addActionListener(l); }
    public void aoDijkstra(ActionListener l) { btnDijkstra.addActionListener(l); }
    public void aoPrim(ActionListener l) { btnPrim.addActionListener(l); }
    public void aoKruskal(ActionListener l) { btnKruskal.addActionListener(l); }
    public void aoGuloso(ActionListener l) { btnGuloso.addActionListener(l); }
    public void aoTSP(ActionListener l) { btnTSP.addActionListener(l); }
    public void aoVRP(ActionListener l) { btnVRP.addActionListener(l); }
    public void aoSimular(ActionListener l) { btnSimular.addActionListener(l); }
    public void aoLimpar(ActionListener l) { btnLimpar.addActionListener(l); }
    public void aoExportarCSV(ActionListener l) { btnExportarCSV.addActionListener(l); }
    public void aoExportarPDF(ActionListener l) { btnExportarPDF.addActionListener(l); }

    public JButton getBtnLimpar() { return btnLimpar; }
}
