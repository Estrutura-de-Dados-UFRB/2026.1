package com.mycompany.sistemacoletaurbana;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.JPanel;

public class PainelGrafo extends JPanel {

    private static final int RAIO = 25;
    private static final double CURVATURA = 22;
    private static final int PASSO_GRADE = 26;

    private static final Color GRADE = new Color(0x16202D);
    private static final TexturePaint LISTRAS_BARREIRA = criarListrasBarreira();

    private Grafo mapa;
    private final SimuladorVias simulador;

    private final Map<Vertice, Point2D.Double> posicoes = new HashMap<>();
    private final Set<Aresta> arestasDestacadas = new HashSet<>();
    private final Set<Vertice> pontosSemColeta = new HashSet<>();
    private Vertice verticeOrigem;
    private Vertice verticeDestino;

    private Vertice arrastando;
    private boolean houveArrasto;
    private boolean layoutPronto;
    private Consumer<Vertice> aoClicarVertice;

    public PainelGrafo(Grafo mapa, SimuladorVias simulador) {
        this.mapa = mapa;
        this.simulador = simulador;
        setBackground(Estilo.ASFALTO);
        setPreferredSize(new Dimension(780, 560));
        instalarMouse();
    }

    /** Faixa diagonal vermelha e branca, como a de um cavalete de obra. */
    private static TexturePaint criarListrasBarreira() {
        int tamanho = 10;
        BufferedImage imagem = new BufferedImage(tamanho, tamanho, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0xF2F4F7));
        g2.fillRect(0, 0, tamanho, tamanho);
        g2.setColor(Estilo.VERMELHO);
        g2.setStroke(new BasicStroke(5f));
        g2.drawLine(-tamanho, tamanho, tamanho, -tamanho);
        g2.drawLine(0, tamanho * 2, tamanho * 2, 0);
        g2.dispose();
        return new TexturePaint(imagem, new Rectangle(0, 0, tamanho, tamanho));
    }

    // ------------------------------------------------------------------
    // API USADA PELA JANELA
    // ------------------------------------------------------------------

    public void setAoClicarVertice(Consumer<Vertice> aoClicarVertice) {
        this.aoClicarVertice = aoClicarVertice;
    }

    public void setMapa(Grafo mapa) {
        this.mapa = mapa;
        posicoes.clear();
        arestasDestacadas.clear();
        verticeOrigem = null;
        verticeDestino = null;
        layoutPronto = false;
        repaint();
    }

    public void setOrigemDestino(Vertice origem, Vertice destino) {
        this.verticeOrigem = origem;
        this.verticeDestino = destino;
        repaint();
    }

    /** Destaca as vias percorridas por um caminho. */
    public void destacarCaminho(List<Vertice> caminho) {
        arestasDestacadas.clear();
        if (caminho != null) {
            for (int i = 0; i < caminho.size() - 1; i++) {
                Aresta a = mapa.buscarAresta(caminho.get(i), caminho.get(i + 1));
                if (a != null) {
                    arestasDestacadas.add(a);
                }
            }
        }
        repaint();
    }

    /** Destaca um conjunto de vias que nao formam caminho (ex.: a malha economica). */
    public void destacarArestas(java.util.Collection<Aresta> arestas) {
        arestasDestacadas.clear();
        if (arestas != null) {
            arestasDestacadas.addAll(arestas);
        }
        repaint();
    }

    /** Marca os pontos que o caminhao nao consegue alcancar a partir da partida. */
    public void destacarInalcancaveis(java.util.Collection<Vertice> inalcancaveis) {
        pontosSemColeta.clear();
        if (inalcancaveis != null) {
            pontosSemColeta.addAll(inalcancaveis);
        }
        repaint();
    }

    public void limparDestaque() {
        arestasDestacadas.clear();
        pontosSemColeta.clear();
        repaint();
    }

    public void reorganizar() {
        posicoes.clear();
        calcularLayoutCircular();
        repaint();
    }

    // ------------------------------------------------------------------
    // POSICIONAMENTO
    // ------------------------------------------------------------------

    private void calcularLayoutCircular() {
        List<Vertice> vertices = mapa.getVertices();
        if (vertices.isEmpty()) {
            return;
        }
        double centroX = getWidth() / 2.0;
        double centroY = getHeight() / 2.0;
        double raio = Math.max(Math.min(getWidth(), getHeight()) / 2.0 - RAIO - 46, 60);

        for (int i = 0; i < vertices.size(); i++) {
            double angulo = 2 * Math.PI * i / vertices.size() - Math.PI / 2;
            posicoes.put(vertices.get(i), new Point2D.Double(
                    centroX + raio * Math.cos(angulo),
                    centroY + raio * Math.sin(angulo)));
        }
        layoutPronto = true;
    }

    private Point2D.Double posicao(Vertice v) {
        return posicoes.get(v);
    }

    private Vertice verticeEm(int x, int y) {
        for (Vertice v : mapa.getVertices()) {
            Point2D.Double p = posicao(v);
            if (p != null && p.distance(x, y) <= RAIO) {
                return v;
            }
        }
        return null;
    }

    private void instalarMouse() {
        MouseAdapter adaptador = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                arrastando = verticeEm(e.getX(), e.getY());
                houveArrasto = false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (arrastando != null) {
                    houveArrasto = true;
                    posicoes.put(arrastando, new Point2D.Double(e.getX(), e.getY()));
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (arrastando != null && !houveArrasto && aoClicarVertice != null) {
                    aoClicarVertice.accept(arrastando);
                }
                arrastando = null;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                setCursor(new java.awt.Cursor(verticeEm(e.getX(), e.getY()) != null
                        ? java.awt.Cursor.HAND_CURSOR : java.awt.Cursor.DEFAULT_CURSOR));
            }
        };
        addMouseListener(adaptador);
        addMouseMotionListener(adaptador);
    }

    // ------------------------------------------------------------------
    // DESENHO
    // ------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        desenharGrade(g2);

        if (mapa == null || mapa.getVertices().isEmpty()) {
            g2.dispose();
            return;
        }
        if (!layoutPronto || posicoes.size() != mapa.getVertices().size()) {
            calcularLayoutCircular();
        }

        // ordem de pintura: vias comuns, depois bloqueios e rota por cima
        List<Aresta> porCima = new ArrayList<>();
        for (Aresta a : mapa.getArestas()) {
            if (arestasDestacadas.contains(a) || SimuladorVias.estaBloqueada(a)) {
                porCima.add(a);
            } else {
                desenharVia(g2, a);
            }
        }
        for (Aresta a : porCima) {
            desenharVia(g2, a);
        }
        for (Vertice v : mapa.getVertices()) {
            desenharPonto(g2, v);
        }
        desenharLegenda(g2);
        g2.dispose();
    }

    private void desenharGrade(Graphics2D g2) {
        g2.setColor(GRADE);
        for (int x = PASSO_GRADE; x < getWidth(); x += PASSO_GRADE) {
            for (int y = PASSO_GRADE; y < getHeight(); y += PASSO_GRADE) {
                g2.fillRect(x, y, 1, 1);
            }
        }
    }

    private void desenharVia(Graphics2D g2, Aresta a) {
        Point2D.Double p1 = posicao(a.getInicio());
        Point2D.Double p2 = posicao(a.getFim());
        if (p1 == null || p2 == null) {
            return;
        }
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double comprimento = Math.hypot(dx, dy);
        if (comprimento < 1) {
            return;
        }
        double ux = dx / comprimento;
        double uy = dy / comprimento;

        double inicioX = p1.x + ux * RAIO;
        double inicioY = p1.y + uy * RAIO;
        double fimX = p2.x - ux * RAIO;
        double fimY = p2.y - uy * RAIO;

        // controle deslocado para a direita do sentido: separa os dois lados da mao dupla
        double ctrlX = (inicioX + fimX) / 2 - uy * CURVATURA;
        double ctrlY = (inicioY + fimY) / 2 + ux * CURVATURA;
        QuadCurve2D.Double curva = new QuadCurve2D.Double(inicioX, inicioY, ctrlX, ctrlY, fimX, fimY);

        boolean bloqueada = SimuladorVias.estaBloqueada(a);
        boolean naRota = arestasDestacadas.contains(a);

        if (bloqueada) {
            g2.setPaint(LISTRAS_BARREIRA);
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.draw(curva);
            g2.setPaint(Estilo.VERMELHO);
        } else if (naRota) {
            // faixa de rolamento
            g2.setColor(Estilo.PISTA);
            g2.setStroke(new BasicStroke(9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(curva);
            // marcacao central
            g2.setColor(Estilo.AMBAR);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                    10f, new float[]{7f, 6f}, 0f));
            g2.draw(curva);
            desenharSeta(g2, ctrlX, ctrlY, fimX, fimY, Estilo.AMBAR, 11);
        } else {
            g2.setColor(Estilo.TRACO);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(curva);
            desenharSeta(g2, ctrlX, ctrlY, fimX, fimY, Estilo.TRACO, 8);
        }

        double meioX = 0.25 * inicioX + 0.5 * ctrlX + 0.25 * fimX;
        double meioY = 0.25 * inicioY + 0.5 * ctrlY + 0.25 * fimY;
        if (bloqueada) {
            desenharEtiqueta(g2, "INTERDITADA", meioX, meioY, Estilo.VERMELHO, Estilo.ASFALTO, true);
        } else {
            desenharEtiqueta(g2, String.format("%.2f", a.getPeso()), meioX, meioY,
                    naRota ? Estilo.AMBAR : Estilo.BORDA,
                    naRota ? Estilo.ASFALTO : Estilo.TEXTO_SUAVE, naRota);
        }
    }

    /** Etiqueta do peso: chip preenchido quando na rota, contorno quando via comum. */
    private void desenharEtiqueta(Graphics2D g2, String texto, double x, double y,
                                  Color corBorda, Color corTexto, boolean preenchido) {
        g2.setFont(Estilo.dados(10f));
        FontMetrics fm = g2.getFontMetrics();
        int largura = fm.stringWidth(texto) + 10;
        int altura = 15;
        RoundRectangle2D.Double chip = new RoundRectangle2D.Double(
                x - largura / 2.0, y - altura / 2.0, largura, altura, 7, 7);

        g2.setColor(preenchido ? corBorda : Estilo.PAINEL);
        g2.fill(chip);
        g2.setColor(corBorda);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(chip);

        g2.setColor(corTexto);
        g2.drawString(texto, (float) (x - fm.stringWidth(texto) / 2.0),
                (float) (y + fm.getAscent() / 2.0 - 1));
    }

    private void desenharSeta(Graphics2D g2, double deX, double deY, double paraX, double paraY,
                              Color cor, double tamanho) {
        double angulo = Math.atan2(paraY - deY, paraX - deX);
        Path2D.Double seta = new Path2D.Double();
        seta.moveTo(paraX, paraY);
        seta.lineTo(paraX - tamanho * Math.cos(angulo - Math.PI / 7),
                    paraY - tamanho * Math.sin(angulo - Math.PI / 7));
        seta.lineTo(paraX - tamanho * Math.cos(angulo + Math.PI / 7),
                    paraY - tamanho * Math.sin(angulo + Math.PI / 7));
        seta.closePath();
        g2.setColor(cor);
        g2.fill(seta);
    }

    private void desenharPonto(Graphics2D g2, Vertice v) {
        Point2D.Double p = posicao(v);
        if (p == null) {
            return;
        }
        String nome = v.getNome();
        boolean selecionado = (v == verticeOrigem || v == verticeDestino);
        boolean semColeta = pontosSemColeta.contains(v);

        Color anel = Estilo.TRACO;
        if (nome.equalsIgnoreCase("Deposito")) {
            anel = Estilo.VERDE;
        } else if (nome.equalsIgnoreCase("Aterro")) {
            anel = Estilo.AMBAR;
        }
        if (semColeta) {
            anel = Estilo.VERMELHO;
        }
        if (selecionado) {
            anel = Color.WHITE;
        }

        Ellipse2D.Double circulo = new Ellipse2D.Double(p.x - RAIO, p.y - RAIO, RAIO * 2, RAIO * 2);
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fill(new Ellipse2D.Double(p.x - RAIO + 2, p.y - RAIO + 3, RAIO * 2, RAIO * 2));
        g2.setColor(Estilo.ELEVADO);
        g2.fill(circulo);
        g2.setColor(anel);
        g2.setStroke(semColeta
                ? new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{5f, 4f}, 0f)
                : new BasicStroke(selecionado ? 3f : 2f));
        g2.draw(circulo);

        // grau, dado usado pelo Carteiro Chines
        g2.setFont(Estilo.dados(10f));
        g2.setColor(Estilo.TEXTO_SUAVE);
        String grau = String.valueOf(v.getGrau());
        g2.drawString(grau, (float) (p.x - g2.getFontMetrics().stringWidth(grau) / 2.0), (float) p.y + 4);

        // nome do ponto, em face de placa
        g2.setFont(Estilo.placa(13f, Font.BOLD));
        FontMetrics fm = g2.getFontMetrics();
        String nomeExibido = nome.toUpperCase();
        g2.setColor(Estilo.TEXTO);
        g2.drawString(nomeExibido, (float) (p.x - fm.stringWidth(nomeExibido) / 2.0),
                (float) (p.y + RAIO + 16));

        if (semColeta && !selecionado) {
            g2.setFont(Estilo.placa(10f, Font.BOLD));
            FontMetrics fmAviso = g2.getFontMetrics();
            g2.setColor(Estilo.VERMELHO);
            g2.drawString("SEM COLETA", (float) (p.x - fmAviso.stringWidth("SEM COLETA") / 2.0),
                    (float) (p.y - RAIO - 8));
        }
        if (selecionado) {
            String marca = (v == verticeOrigem) ? "ORIGEM" : "DESTINO";
            g2.setFont(Estilo.placa(10f, Font.BOLD));
            FontMetrics fmMarca = g2.getFontMetrics();
            g2.setColor(Estilo.AMBAR);
            g2.drawString(marca, (float) (p.x - fmMarca.stringWidth(marca) / 2.0), (float) (p.y - RAIO - 8));
        }
    }

    private void desenharLegenda(Graphics2D g2) {
        int x = 14;
        int y = getHeight() - 46;
        g2.setFont(Estilo.texto(11f, Font.PLAIN));

        g2.setColor(Estilo.TRACO);
        g2.setStroke(new BasicStroke(1.6f));
        g2.drawLine(x, y, x + 24, y);
        g2.setColor(Estilo.TEXTO_SUAVE);
        g2.drawString("via livre", x + 32, y + 4);

        int y2 = y + 16;
        g2.setColor(Estilo.PISTA);
        g2.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x, y2, x + 24, y2);
        g2.setColor(Estilo.AMBAR);
        g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                10f, new float[]{5f, 4f}, 0f));
        g2.drawLine(x, y2, x + 24, y2);
        g2.setColor(Estilo.TEXTO_SUAVE);
        g2.drawString("rota calculada", x + 32, y2 + 4);

        int y3 = y2 + 16;
        g2.setPaint(LISTRAS_BARREIRA);
        g2.setStroke(new BasicStroke(5f));
        g2.drawLine(x, y3, x + 24, y3);
        g2.setColor(Estilo.TEXTO_SUAVE);
        g2.drawString("via interditada", x + 32, y3 + 4);

        g2.setFont(Estilo.texto(10.5f, Font.PLAIN));
        g2.setColor(new Color(0x5C6E85));
        g2.drawString("arraste os pontos para reposicionar  ·  clique para definir origem e destino",
                x, getHeight() - 12);
    }
}