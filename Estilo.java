package com.mycompany.sistemacoletaurbana;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

public final class Estilo {

    // --- Paleta -------------------------------------------------------
    public static final Color ASFALTO       = new Color(0x0D1520);  // fundo da janela e do mapa
    public static final Color PAINEL        = new Color(0x131C29);  // cartoes
    public static final Color ELEVADO       = new Color(0x1A2534);  // campos, vertices
    public static final Color BORDA         = new Color(0x263243);
    public static final Color TRACO         = new Color(0x3A4A61);  // vias
    public static final Color TEXTO         = new Color(0xE6EDF5);
    public static final Color TEXTO_SUAVE   = new Color(0x8496AB);
    public static final Color AMBAR         = new Color(0xF2B233);  // acento: rota, selecao
    public static final Color AMBAR_ESCURO  = new Color(0xC98F1E);
    public static final Color VERMELHO      = new Color(0xE2574C);  // bloqueio
    public static final Color VERDE         = new Color(0x4FB477);  // deposito
    public static final Color PISTA         = new Color(0x354357);  // faixa da rota destacada

    // --- Tipografia ---------------------------------------------------
    private static final String FAMILIA_PLACA = escolherFonte(
            "Bahnschrift", "DIN Condensed", "Oswald", "Segoe UI Semibold",
            "Franklin Gothic Medium", "Liberation Sans Narrow", "SansSerif");
    private static final String FAMILIA_TEXTO = escolherFonte(
            "Segoe UI", "Inter", "SF Pro Text", "Ubuntu", "DejaVu Sans", "SansSerif");
    private static final String FAMILIA_DADOS = escolherFonte(
            "Consolas", "JetBrains Mono", "SF Mono", "DejaVu Sans Mono", "Monospaced");

    static {
        // O BasicComboBoxUI ignora setBackground e le direto do UIManager,
        // entao os padroes precisam ser ajustados aqui.
        UIManager.put("ComboBox.background", ELEVADO);
        UIManager.put("ComboBox.foreground", TEXTO);
        UIManager.put("ComboBox.selectionBackground", new Color(0x22303F));
        UIManager.put("ComboBox.selectionForeground", AMBAR);
        UIManager.put("ComboBox.disabledBackground", PAINEL);
        UIManager.put("ComboBox.disabledForeground", TEXTO_SUAVE);
        UIManager.put("List.background", ELEVADO);
        UIManager.put("List.foreground", TEXTO);
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(BORDA));
        UIManager.put("ToolTip.background", ELEVADO);
        UIManager.put("ToolTip.foreground", TEXTO);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(BORDA));
    }

    private Estilo() {
    }

    /** Primeira fonte instalada na maquina, entre as candidatas. */
    private static String escolherFonte(String... candidatas) {
        Set<String> instaladas = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String candidata : candidatas) {
            if (instaladas.contains(candidata)) {
                return candidata;
            }
        }
        return Font.SANS_SERIF;
    }

    /** Face de sinalizacao: titulos e nomes de pontos no mapa. */
    public static Font placa(float tamanho, int estilo) {
        return new Font(FAMILIA_PLACA, estilo, Math.round(tamanho));
    }

    /** Face de texto: rotulos, botoes, campos. */
    public static Font texto(float tamanho, int estilo) {
        return new Font(FAMILIA_TEXTO, estilo, Math.round(tamanho));
    }

    /** Face de dados: pesos, custos, registro de operacao. */
    public static Font dados(float tamanho) {
        return new Font(FAMILIA_DADOS, Font.PLAIN, Math.round(tamanho));
    }

    /** Rotulo de secao: caixa alta, pequeno e espacado, como legenda de planta. */
    public static JLabel eyebrow(String texto) {
        JLabel rotulo = new JLabel(texto.toUpperCase());
        Map<TextAttribute, Object> atributos = new HashMap<>();
        atributos.put(TextAttribute.TRACKING, 0.16);
        rotulo.setFont(placa(11f, Font.BOLD).deriveFont(atributos));
        rotulo.setForeground(TEXTO_SUAVE);
        rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        rotulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return rotulo;
    }

    public static JLabel rotulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(texto(12f, Font.PLAIN));
        rotulo.setForeground(TEXTO_SUAVE);
        return rotulo;
    }

    // --- Componentes --------------------------------------------------

    /** Variantes de botao: acento sólido, neutro de superficie, e ghost de perigo. */
    public enum Variante { ACENTO, NEUTRO, PERIGO }

    public static JButton botao(String texto, Variante variante, java.awt.event.ActionListener acao) {
        BotaoChapado botao = new BotaoChapado(texto, variante);
        if (acao != null) {
            botao.addActionListener(acao);
        }
        return botao;
    }

    /** Cartao arredondado com um rotulo de secao no topo. */
    public static JPanel cartao(String titulo) {
        PainelArredondado cartao = new PainelArredondado(PAINEL, 10);
        cartao.setLayout(new javax.swing.BoxLayout(cartao, javax.swing.BoxLayout.Y_AXIS));
        cartao.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        cartao.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (titulo != null) {
            cartao.add(eyebrow(titulo));
        }
        return cartao;
    }

    public static JTextField campo() {
        JTextField campo = new JTextField();
        campo.setBackground(ELEVADO);
        campo.setForeground(TEXTO);
        campo.setCaretColor(AMBAR);
        campo.setFont(texto(12f, Font.PLAIN));
        campo.setBorder(new BordaArredondada(BORDA, 6, new Insets(5, 8, 5, 8)));
        return campo;
    }

    public static <T> JComboBox<T> combo() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setBackground(ELEVADO);
        combo.setForeground(TEXTO);
        combo.setFont(texto(12f, Font.PLAIN));
        combo.setBorder(new BordaArredondada(BORDA, 6, new Insets(2, 6, 2, 2)));
        combo.setUI(new ComboChapado());
        combo.setRenderer(new RenderizadorItem());
        return combo;
    }

    public static JCheckBox check(String texto, boolean marcado) {
        JCheckBox check = new JCheckBox(texto, marcado);
        check.setIcon(new CaixaMarcacao());
        check.setOpaque(false);
        check.setForeground(TEXTO_SUAVE);
        check.setFont(texto(11.5f, Font.PLAIN));
        check.setFocusPainted(false);
        check.setAlignmentX(Component.LEFT_ALIGNMENT);
        return check;
    }

    /** Aplica barras de rolagem finas e sem setas. */
    public static JScrollPane rolagem(Component conteudo) {
        JScrollPane rolagem = new JScrollPane(conteudo);
        rolagem.setBorder(null);
        rolagem.getViewport().setOpaque(false);
        rolagem.setOpaque(false);
        rolagem.getVerticalScrollBar().setUI(new BarraFina());
        rolagem.getHorizontalScrollBar().setUI(new BarraFina());
        rolagem.getVerticalScrollBar().setUnitIncrement(16);
        return rolagem;
    }

    /** Chip de dado do cabecalho: numero grande + rotulo pequeno. */
    public static JPanel chip(String valor, String rotuloTexto, Color corValor) {
        PainelArredondado chip = new PainelArredondado(PAINEL, 8);
        chip.setLayout(new javax.swing.BoxLayout(chip, javax.swing.BoxLayout.Y_AXIS));
        chip.setBorder(BorderFactory.createEmptyBorder(5, 11, 5, 11));

        JLabel valorRotulo = new JLabel(valor);
        valorRotulo.setFont(placa(15f, Font.BOLD));
        valorRotulo.setForeground(corValor);
        valorRotulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descricao = new JLabel(rotuloTexto.toUpperCase());
        Map<TextAttribute, Object> atributos = new HashMap<>();
        atributos.put(TextAttribute.TRACKING, 0.12);
        descricao.setFont(texto(9f, Font.PLAIN).deriveFont(atributos));
        descricao.setForeground(TEXTO_SUAVE);
        descricao.setAlignmentX(Component.LEFT_ALIGNMENT);

        chip.add(valorRotulo);
        chip.add(descricao);
        return chip;
    }

    // ------------------------------------------------------------------
    // CLASSES DE APOIO
    // ------------------------------------------------------------------

    /** Painel com cantos arredondados. */
    public static class PainelArredondado extends JPanel {
        private final Color cor;
        private final int raio;

        public PainelArredondado(Color cor, int raio) {
            this.cor = cor;
            this.raio = raio;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), raio, raio));
            g2.setColor(BORDA);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, raio, raio));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Borda arredondada de 1px com respiro interno. */
    public static class BordaArredondada implements Border {
        private final Color cor;
        private final int raio;
        private final Insets respiro;

        public BordaArredondada(Color cor, int raio, Insets respiro) {
            this.cor = cor;
            this.raio = raio;
            this.respiro = respiro;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int largura, int altura) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.draw(new RoundRectangle2D.Double(x + 0.5, y + 0.5, largura - 1, altura - 1, raio, raio));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return respiro;
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    /** Botao chapado com cantos arredondados e estados de hover/clique. */
    public static class BotaoChapado extends JButton {
        private final Variante variante;
        private boolean sobre;
        private boolean pressionado;

        public BotaoChapado(String texto, Variante variante) {
            super(texto);
            this.variante = variante;
            setFont(Estilo.texto(12f, Font.BOLD));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            instalarHover(this);
        }

        private void instalarHover(AbstractButton botao) {
            botao.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    sobre = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    sobre = false;
                    pressionado = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressionado = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressionado = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fundo;
            Color frente;
            Color borda = null;

            if (!isEnabled()) {
                fundo = PAINEL;
                frente = new Color(0x53627A);
                borda = BORDA;
            } else if (variante == Variante.ACENTO) {
                fundo = pressionado ? AMBAR_ESCURO : (sobre ? new Color(0xF7C356) : AMBAR);
                frente = ASFALTO;
            } else if (variante == Variante.PERIGO) {
                fundo = sobre ? VERMELHO : PAINEL;
                frente = sobre ? Color.WHITE : VERMELHO;
                borda = VERMELHO;
            } else {
                fundo = sobre ? new Color(0x22303F) : ELEVADO;
                frente = TEXTO;
                borda = BORDA;
            }

            g2.setColor(fundo);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 7, 7));
            if (borda != null) {
                g2.setColor(borda);
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 7, 7));
            }

            g2.setFont(getFont());
            g2.setColor(frente);
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    /** Marcador do checkbox: caixa arredondada com o tique em ambar. */
    private static class CaixaMarcacao implements javax.swing.Icon {
        private static final int LADO = 14;

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean marcado = ((AbstractButton) c).isSelected();

            g2.setColor(marcado ? AMBAR : ELEVADO);
            g2.fill(new RoundRectangle2D.Double(x, y, LADO, LADO, 4, 4));
            g2.setColor(marcado ? AMBAR : BORDA);
            g2.draw(new RoundRectangle2D.Double(x + 0.5, y + 0.5, LADO - 1, LADO - 1, 4, 4));

            if (marcado) {
                g2.setColor(ASFALTO);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawPolyline(new int[]{x + 3, x + 6, x + 11}, new int[]{y + 7, y + 10, y + 4}, 3);
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return LADO;
        }

        @Override
        public int getIconHeight() {
            return LADO;
        }
    }

    /** Combo sem relevo, com uma seta discreta. */
    private static class ComboChapado extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton seta = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TEXTO_SUAVE);
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    g2.fillPolygon(new int[]{cx - 4, cx + 4, cx}, new int[]{cy - 2, cy - 2, cy + 3}, 3);
                    g2.dispose();
                }
            };
            seta.setBorder(null);
            seta.setContentAreaFilled(false);
            seta.setFocusPainted(false);
            seta.setPreferredSize(new Dimension(18, 18));
            return seta;
        }
    }

    /** Itens do combo com respiro e destaque ambar na selecao. */
    private static class RenderizadorItem extends javax.swing.DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(javax.swing.JList<?> lista, Object valor,
                int indice, boolean selecionado, boolean temFoco) {
            super.getListCellRendererComponent(lista, valor, indice, selecionado, temFoco);
            setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            setFont(Estilo.texto(12f, Font.PLAIN));
            setBackground(selecionado ? new Color(0x22303F) : ELEVADO);
            setForeground(selecionado ? AMBAR : TEXTO);
            return this;
        }
    }

    /** Barra de rolagem fina, sem botoes de seta. */
    private static class BarraFina extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(0x2F3E52);
            trackColor = ASFALTO;
        }

        @Override
        protected JButton createDecreaseButton(int orientacao) {
            return botaoInvisivel();
        }

        @Override
        protected JButton createIncreaseButton(int orientacao) {
            return botaoInvisivel();
        }

        private JButton botaoInvisivel() {
            JButton botao = new JButton();
            botao.setPreferredSize(new Dimension(0, 0));
            botao.setBorder(null);
            return botao;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, java.awt.Rectangle limites) {
            g.setColor(trackColor);
            g.fillRect(limites.x, limites.y, limites.width, limites.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, java.awt.Rectangle limites) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fill(new RoundRectangle2D.Double(limites.x + 2, limites.y + 2,
                    limites.width - 4, limites.height - 4, 6, 6));
            g2.dispose();
        }

        @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(8, 30);
        }
    }

    /** Reduz o alcance do componente na vertical (evita esticar em BoxLayout). */
    public static void travarAltura(JComponent componente, int altura) {
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, altura));
        componente.setPreferredSize(new Dimension(componente.getPreferredSize().width, altura));
    }

    /** Divisoria de 1px usada dentro dos cartoes. */
    public static JComponent divisoria() {
        JPanel linha = new JPanel();
        linha.setBackground(BORDA);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        travarAltura(linha, 1);
        return linha;
    }

    /** Utilitario: lista de nomes dos vertices, usada nos combos. */
    public static String[] nomes(List<Vertice> vertices) {
        String[] nomes = new String[vertices.size()];
        for (int i = 0; i < nomes.length; i++) {
            nomes[i] = vertices.get(i).getNome();
        }
        return nomes;
    }
}