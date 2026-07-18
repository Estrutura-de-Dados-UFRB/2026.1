import dados.DadosMetroSP;
import grafo.RedeMetro;
import grafo.ResultadoRota;
import algoritmos.Dijkstra;
import model.Conexao;
import model.Estacao;
import algoritmos.Betweenness;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.*;

public class MapaMetroSPApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MapaMetroSPApp::criarJanela);
    }

    private static void criarJanela() {
        RedeMetro rede = DadosMetroSP.carregar();
        MapaPanel mapaPanel = new MapaPanel(rede);

        JFrame frame = new JFrame("MetroSP — Esboço de Interface (protótipo acadêmico)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(mapaPanel);
        scroll.getHorizontalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(criarPainelControle(rede, mapaPanel), BorderLayout.EAST);

        frame.setSize(1400, 900);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel criarPainelControle(RedeMetro rede, MapaPanel mapaPanel) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setPreferredSize(new Dimension(260, 0));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("MetroSP");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(4));

        JTextArea instrucoes = new JTextArea(
            "Clique 1: origem\nClique 2: destino (calcula as duas rotas)\nClique direito: interditar/reativar estação\nClique novamente: reinicia seleção"
        );
        instrucoes.setEditable(false);
        instrucoes.setOpaque(false);
        instrucoes.setFont(instrucoes.getFont().deriveFont(11f));
        instrucoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(instrucoes);
        painel.add(Box.createVerticalStrut(10));

        JLabel origemLabel = new JLabel("Origem: —");
        origemLabel.setFont(origemLabel.getFont().deriveFont(12f));
        origemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(origemLabel);

        JLabel destinoLabel = new JLabel("Destino: —");
        destinoLabel.setFont(destinoLabel.getFont().deriveFont(12f));
        destinoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(destinoLabel);
        painel.add(Box.createVerticalStrut(10));

        mapaPanel.setLabelsSelecao(origemLabel, destinoLabel);

        JButton limpar = new JButton("Limpar seleção");
        limpar.setAlignmentX(Component.LEFT_ALIGNMENT);
        limpar.addActionListener(e -> mapaPanel.limparSelecao());
        painel.add(limpar);
        painel.add(Box.createVerticalStrut(6));

        JButton importantes = new JButton("Estações mais importantes");
        importantes.setAlignmentX(Component.LEFT_ALIGNMENT);
        importantes.addActionListener(e -> mostrarMaisImportantes(rede));
        painel.add(importantes);
        painel.add(Box.createVerticalStrut(10));

        JButton verDetalhes = new JButton("Ver detalhes da rota");
        verDetalhes.setAlignmentX(Component.LEFT_ALIGNMENT);
        verDetalhes.addActionListener(e -> mapaPanel.mostrarDetalhesRota());
        painel.add(verDetalhes);
        painel.add(Box.createVerticalStrut(10));

        JLabel legendaTitulo = new JLabel("Linhas:");
        legendaTitulo.setFont(legendaTitulo.getFont().deriveFont(Font.BOLD, 12f));
        legendaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(legendaTitulo);

        for (Map.Entry<String, Color> ent : MapaPanel.CORES.entrySet()) {
            JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
            linha.setAlignmentX(Component.LEFT_ALIGNMENT);
            JPanel quadrado = new JPanel();
            quadrado.setBackground(ent.getValue());
            quadrado.setPreferredSize(new Dimension(12, 12));
            linha.add(quadrado);
            JLabel nome = new JLabel(ent.getKey());
            nome.setFont(nome.getFont().deriveFont(10f));
            linha.add(nome);
            painel.add(linha);
        }

        JPanel legendaRota = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        legendaRota.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel corPreta = new JPanel();
        corPreta.setBackground(Color.BLACK);
        corPreta.setPreferredSize(new Dimension(12, 12));
        legendaRota.add(corPreta);
        legendaRota.add(new JLabel("Menor tempo"));
        painel.add(legendaRota);

        JPanel legendaRota2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        legendaRota2.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel corAzul = new JPanel();
        corAzul.setBackground(MapaPanel.COR_MENOS_BALDEACOES);
        corAzul.setPreferredSize(new Dimension(12, 12));
        legendaRota2.add(corAzul);
        legendaRota2.add(new JLabel("Menos baldeações (trecho exclusivo)"));
        painel.add(legendaRota2);

        return painel;
    }

    private static void mostrarMaisImportantes(RedeMetro rede) {
        List<Map.Entry<String, Integer>> top = Betweenness.top(rede, 10);

        StringBuilder sb = new StringBuilder();
        sb.append("Top 10 por centralidade de intermediação (betweenness)\n");
        sb.append("[aproximação de caminho único — ver Betweenness.java]\n\n");
        for (int i = 0; i < top.size(); i++) {
            Map.Entry<String, Integer> ent = top.get(i);
            sb.append(String.format("%2d. %-30s passagens=%d%n",
                i + 1, ent.getKey(), ent.getValue()));
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area),
            "Estações mais importantes", JOptionPane.INFORMATION_MESSAGE);
    }

    static class MapaPanel extends JPanel {

        static final Map<String, Color> CORES = new LinkedHashMap<>();
        static {
            CORES.put("Linha 1 - Azul", new Color(0, 88, 163));
            CORES.put("Linha 2 - Verde", new Color(0, 146, 82));
            CORES.put("Linha 3 - Vermelha", new Color(230, 28, 40));
            CORES.put("Linha 4 - Amarela", new Color(230, 180, 0));
            CORES.put("Linha 5 - Lilás", new Color(155, 89, 182));
            CORES.put("Linha 7 - Rubi", new Color(136, 33, 47));
            CORES.put("Linha 8 - Diamante", new Color(120, 120, 120));
            CORES.put("Linha 9 - Esmeralda", new Color(0, 150, 130));
            CORES.put("Linha 10 - Turquesa", new Color(0, 160, 165));
            CORES.put("Linha 11 - Coral", new Color(230, 100, 70));
            CORES.put("Linha 12 - Safira", new Color(30, 60, 130));
            CORES.put("Linha 13 - Jade", new Color(0, 120, 100));
            CORES.put("Linha 15 - Prata", new Color(150, 150, 150));
            CORES.put("Linha 17 - Ouro", new Color(180, 140, 0));
        }

        static final Color COR_MENOS_BALDEACOES = new Color(30, 90, 220);

        private final RedeMetro rede;
        private final Map<String, double[]> pos;
        private final Set<String> nomesHub = new HashSet<>();

        private String origemSel;
        private String destinoSel;
        private ResultadoRota rotaAtual;
        private ResultadoRota rotaMenosBaldeacoes;

        private JLabel origemLabel;
        private JLabel destinoLabel;

        private static final int LARGURA = 2200;
        private static final int ALTURA = 1600;
        private static final double RAIO_NORMAL = 4;
        private static final double RAIO_HUB = 7;

        MapaPanel(RedeMetro rede) {
            this.rede = rede;
            setPreferredSize(new Dimension(LARGURA, ALTURA));
            setBackground(Color.WHITE);
            setToolTipText("");

            Map<String, List<String>> porNome = new HashMap<>();
            for (Estacao e : rede.getEstacoes()) {
                porNome.computeIfAbsent(e.getNome(), k -> new ArrayList<>()).add(e.getId());
            }
            for (Map.Entry<String, List<String>> ent : porNome.entrySet()) {
                if (ent.getValue().size() > 1) nomesHub.add(ent.getKey());
            }

            this.pos = calcularLayoutForceDirected(rede, LARGURA, ALTURA);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    String id = estacaoMaisProxima(e.getPoint());
                    if (id == null) return;
                    if (SwingUtilities.isRightMouseButton(e)) {
                        Estacao est = rede.getEstacao(id);
                        if (est.isAtiva()) rede.interditar(id); else rede.reativar(id);
                        recalcularSeNecessario();
                        repaint();
                        return;
                    }
                    if (origemSel == null) {
                        origemSel = id;
                        destinoSel = null;
                        rotaAtual = null;
                        rotaMenosBaldeacoes = null;
                        atualizarLabelsSelecao();
                    } else if (destinoSel == null && !id.equals(origemSel)) {
                        destinoSel = id;
                        calcularRota();
                    } else {
                        origemSel = id;
                        destinoSel = null;
                        rotaAtual = null;
                        rotaMenosBaldeacoes = null;
                        atualizarLabelsSelecao();
                    }
                    atualizarLabelsSelecao();
                    repaint();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    String id = estacaoMaisProxima(e.getPoint());
                    setToolTipText(id == null ? null :
                        rede.getEstacao(id).getNome() + " — " + rede.getEstacao(id).getLinha());
                }
            });
        }

        void limparSelecao() {
            origemSel = null;
            destinoSel = null;
            rotaAtual = null;
            rotaMenosBaldeacoes = null;
            atualizarLabelsSelecao();
            repaint();
        }

        void setLabelsSelecao(JLabel origem, JLabel destino) {
            this.origemLabel = origem;
            this.destinoLabel = destino;
            atualizarLabelsSelecao();
        }

        private void atualizarLabelsSelecao() {
            if (origemLabel == null || destinoLabel == null) return;
            origemLabel.setText("Origem: " + (origemSel == null ? "—"
                : rede.getEstacao(origemSel).getNome() + " (" + rede.getEstacao(origemSel).getLinha() + ")"));
            destinoLabel.setText("Destino: " + (destinoSel == null ? "—"
                : rede.getEstacao(destinoSel).getNome() + " (" + rede.getEstacao(destinoSel).getLinha() + ")"));
        }
        private void recalcularSeNecessario() {
            if (origemSel != null && destinoSel != null) calcularRota();
        }

        /** Calcula os dois algoritmos na mesma seleção de origem/destino. */
        private void calcularRota() {
            rotaAtual = Dijkstra.calcularCaminhoMinimo(rede, origemSel, destinoSel);
            rotaMenosBaldeacoes = Dijkstra.calcularMenosBaldeacoes(rede, origemSel, destinoSel);
        }

        /** Exibe os resultados calculados em uma janela de diálogo separada. */
        void mostrarDetalhesRota() {
            String texto;
            if (origemSel == null) {
                texto = "Nenhuma seleção.";
            } else if (destinoSel == null) {
                texto = "Origem: " + rede.getEstacao(origemSel).getNome() + "\nClique no destino.";
            } else if (rotaAtual == null || rotaAtual.éVazio()) {
                texto = "Origem: " + rede.getEstacao(origemSel).getNome()
                    + "\nDestino: " + rede.getEstacao(destinoSel).getNome()
                    + "\n\nSem rota disponível (verifique estações interditadas).";
            } else {
                boolean mesmaRota = rotaMenosBaldeacoes != null
                    && !rotaMenosBaldeacoes.éVazio()
                    && rotaAtual.getCaminho().equals(rotaMenosBaldeacoes.getCaminho());
                if (mesmaRota) {
                    texto = "A rota mais rápida já é a de menos baldeações:\n\n" + rotaAtual;
                } else {
                    texto = "Rota mais rápida:\n" + rotaAtual
                        + "\n\n---\n\nRota com menos baldeações:\n" + rotaMenosBaldeacoes;
                }
            }
            JTextArea area = new JTextArea(texto);
            area.setEditable(false);
            area.setFont(area.getFont().deriveFont(12f));
            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(360, 220));
            JOptionPane.showMessageDialog(this, scroll, "Detalhes da rota", JOptionPane.INFORMATION_MESSAGE);
        }

        private String estacaoMaisProxima(Point p) {
            String melhor = null;
            double melhorDist = 12;
            for (Map.Entry<String, double[]> ent : pos.entrySet()) {
                double dx = ent.getValue()[0] - p.x;
                double dy = ent.getValue()[1] - p.y;
                double d = Math.sqrt(dx * dx + dy * dy);
                if (d < melhorDist) { melhorDist = d; melhor = ent.getKey(); }
            }
            return melhor;
        }

        private Set<String> paresDoCaminho(ResultadoRota rota) {
            Set<String> pares = new HashSet<>();
            if (rota == null || rota.éVazio()) return pares;
            List<Estacao> caminho = rota.getCaminho();
            for (int i = 1; i < caminho.size(); i++) {
                pares.add(chave(caminho.get(i - 1).getId(), caminho.get(i).getId()));
            }
            return pares;
        }

        private static String chave(String a, String b) {
            return a.compareTo(b) < 0 ? a + "|" + b : b + "|" + a;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Set<String> paresRapida = paresDoCaminho(rotaAtual);
            Set<String> paresBaldeacao = paresDoCaminho(rotaMenosBaldeacoes);
            // trecho exclusivo da rota de menos baldeações (não presente na rota rápida)
            Set<String> paresExclusivosBaldeacao = new HashSet<>(paresBaldeacao);
            paresExclusivosBaldeacao.removeAll(paresRapida);

            Set<String> desenhadas = new HashSet<>();

            for (String id : rede.getIds()) {
                for (Conexao c : rede.getConexoes(id)) {
                    String outro = c.getDestino().getId();
                    String k = chave(id, outro);
                    if (!desenhadas.add(k)) continue;
                    double[] p1 = pos.get(id);
                    double[] p2 = pos.get(outro);
                    if (p1 == null || p2 == null) continue;
                    Estacao eu = rede.getEstacao(id);
                    Estacao ev = rede.getEstacao(outro);
                    boolean mesmaLinha = eu.getLinha().equals(ev.getLinha());

                    if (paresRapida.contains(k)) {
                        g2.setStroke(new BasicStroke(4.5f));
                        g2.setColor(Color.BLACK);
                    } else if (paresExclusivosBaldeacao.contains(k)) {
                        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER, 10, new float[]{6f, 4f}, 0));
                        g2.setColor(COR_MENOS_BALDEACOES);
                    } else if (mesmaLinha) {
                        g2.setStroke(new BasicStroke(2.5f));
                        g2.setColor(CORES.getOrDefault(eu.getLinha(), Color.DARK_GRAY));
                    } else {
                        g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER, 10, new float[]{4f, 4f}, 0));
                        g2.setColor(new Color(180, 180, 180));
                    }
                    g2.draw(new Line2D.Double(p1[0], p1[1], p2[0], p2[1]));
                }
            }

            for (String id : rede.getIds()) {
                Estacao e = rede.getEstacao(id);
                double[] p = pos.get(id);
                if (p == null) continue;
                boolean hub = nomesHub.contains(e.getNome());
                double raio = hub ? RAIO_HUB : RAIO_NORMAL;

                Color cor = CORES.getOrDefault(e.getLinha(), Color.DARK_GRAY);
                if (id.equals(origemSel)) cor = Color.MAGENTA;
                else if (id.equals(destinoSel)) cor = new Color(255, 140, 0);

                g2.setColor(cor);
                g2.fill(new Ellipse2D.Double(p[0] - raio, p[1] - raio, raio * 2, raio * 2));
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(hub ? 1.5f : 1f));
                g2.draw(new Ellipse2D.Double(p[0] - raio, p[1] - raio, raio * 2, raio * 2));

                if (!e.isAtiva()) {
                    g2.setColor(Color.RED);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine((int) (p[0] - raio - 3), (int) (p[1] - raio - 3),
                        (int) (p[0] + raio + 3), (int) (p[1] + raio + 3));
                    g2.drawLine((int) (p[0] - raio - 3), (int) (p[1] + raio + 3),
                        (int) (p[0] + raio + 3), (int) (p[1] - raio - 3));
                }

                if (hub) {
                    g2.setColor(Color.BLACK);
                    g2.setFont(g2.getFont().deriveFont(10f));
                    g2.drawString(e.getNome(), (float) (p[0] + raio + 3), (float) (p[1] + 4));
                }
            }
        }

        private static Map<String, double[]> calcularLayoutForceDirected(RedeMetro rede, int largura, int altura) {
            List<String> ids = new ArrayList<>(rede.getIds());
            int n = ids.size();
            Map<String, double[]> p = new HashMap<>();
            Random rnd = new Random(42);
            for (String id : ids) {
                p.put(id, new double[]{rnd.nextDouble() * largura, rnd.nextDouble() * altura});
            }
            if (n == 0) return p;

            double area = (double) largura * altura;
            double k = Math.sqrt(area / n) * 1.6;
            int iteracoes = 300;

            for (int iter = 0; iter < iteracoes; iter++) {
                Map<String, double[]> disp = new HashMap<>();
                for (String id : ids) disp.put(id, new double[]{0, 0});

                for (int i = 0; i < n; i++) {
                    for (int j = i + 1; j < n; j++) {
                        String a = ids.get(i), b = ids.get(j);
                        double[] pa = p.get(a), pb = p.get(b);
                        double dx = pa[0] - pb[0], dy = pa[1] - pb[1];
                        double dist = Math.max(Math.sqrt(dx * dx + dy * dy), 0.01);
                        double force = (k * k) / dist;
                        double ux = dx / dist, uy = dy / dist;
                        disp.get(a)[0] += ux * force;
                        disp.get(a)[1] += uy * force;
                        disp.get(b)[0] -= ux * force;
                        disp.get(b)[1] -= uy * force;
                    }
                }

                for (String id : ids) {
                    for (Conexao c : rede.getConexoes(id)) {
                        String outro = c.getDestino().getId();
                        double[] pa = p.get(id), pb = p.get(outro);
                        double dx = pa[0] - pb[0], dy = pa[1] - pb[1];
                        double dist = Math.max(Math.sqrt(dx * dx + dy * dy), 0.01);
                        double force = (dist * dist) / k;
                        double ux = dx / dist, uy = dy / dist;
                        disp.get(id)[0] -= ux * force;
                        disp.get(id)[1] -= uy * force;
                    }
                }

                double temp = largura * (1.0 - (double) iter / iteracoes) * 0.06;
                for (String id : ids) {
                    double[] d = disp.get(id);
                    double dist = Math.max(Math.sqrt(d[0] * d[0] + d[1] * d[1]), 0.01);
                    double limitado = Math.min(dist, temp);
                    double[] pt = p.get(id);
                    pt[0] += (d[0] / dist) * limitado;
                    pt[1] += (d[1] / dist) * limitado;
                    pt[0] = Math.min(largura - 40, Math.max(40, pt[0]));
                    pt[1] = Math.min(altura - 40, Math.max(40, pt[1]));
                }
            }
            return p;
        }
    }
}
