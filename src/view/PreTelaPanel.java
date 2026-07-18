package view;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import util.SoundPlayer; 

/**
 * PreTelaPanel: A famosa tela de "Press Start".
 * Cria a primeira impressão do jogo com efeitos visuais avançados,
 * aguardando o usuário clicar ou apertar qualquer tecla para ir ao menu.
 */
public class PreTelaPanel extends JPanel {
    
    // Referência à janela principal para podermos trocar para o MenuPanel depois
    private MainFrame frame;

    public PreTelaPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        // Cria o painel de fundo personalizado (que desenha a imagem, a estática e o gradiente)
        BackgroundPanel fundo = new BackgroundPanel("/images/dominocuphead.png");
        fundo.setLayout(new BorderLayout());
        add(fundo);

        // Cria o título animado e estilizado "DOMINÓ"
        CartoonTitle lblTitulo = new CartoonTitle("DOMINÓ");
        lblTitulo.setPreferredSize(new Dimension(800, 220)); // Espaço suficiente para as letras pularem

        // Painel invisível no topo só para centralizar e dar uma margem (60px) do teto
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelTopo.setOpaque(false); // Transparente
        painelTopo.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0)); 
        painelTopo.add(lblTitulo);

        // Adiciona o painel do título na parte Norte (topo) do fundo
        fundo.add(painelTopo, BorderLayout.NORTH); 

        // =======================================================
        // EVENTOS DE ENTRADA (Como sair dessa tela)
        // =======================================================
        
        // Cria uma ação de mouse que avança para o menu
        MouseAdapter avancarMouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Toca o som de start e avança pro menu
                SoundPlayer.tocar("start.wav", false);
                frame.trocarPainel(new MenuPanel(frame));
            }
        };

        // Adiciona o clique do mouse tanto no fundo quanto no painel geral
        fundo.addMouseListener(avancarMouse);
        addMouseListener(avancarMouse);
        
        // Adiciona um ouvinte de teclado: apertou qualquer tecla, vai pro menu
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                SoundPlayer.tocar("start.wav", false);
                frame.trocarPainel(new MenuPanel(frame));
            }
        });
        
        // Essencial para o KeyListener funcionar: o painel precisa estar "em foco" para ouvir o teclado
        setFocusable(true);
    }

    /**
     * Classe Interna Customizada: Cria aquele texto com letras tortas,
     * sombra 3D e bordas grossas estilo cartoon/Cuphead.
     */
    class CartoonTitle extends JComponent {
        private String text;

        public CartoonTitle(String text) { 
            this.text = text; 
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            
            // Ativa o Antialiasing para deixar as bordas do desenho suaves (sem serrilhado)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Define a fonte base
            Font font = new Font("Alegre Sans", Font.BOLD, 170);
            FontRenderContext frc = g2.getFontRenderContext();

            // =======================================================
            // MATEMÁTICA DO EFEITO CARTOON
            // =======================================================
            // Ângulos de rotação para cada letra (alternando entre cair pra esquerda e direita)
            double[] rotations = {-0.02, 0.05, -0.04, 0.03, -0.05, 0.06}; 
            // Variação de altura no eixo Y (faz as letras parecerem estar pulando)
            int[] yBounce = {0, 15, -8, 12, -4, 18}; 
            // Aproxima as letras para elas se sobreporem um pouco
            float letterSpacing = -12f; 

            // 1º Passo: Calcula a largura total da palavra para podermos centralizá-la
            float totalWidth = 0;
            for (int i = 0; i < text.length(); i++) {
                TextLayout tl = new TextLayout(String.valueOf(text.charAt(i)), font, frc);
                totalWidth += tl.getAdvance() + letterSpacing;
            }

            // Define o ponto de partida (X e Y) da primeira letra para ficar bem no meio
            float startX = (getWidth() - totalWidth) / 2;
            float startY = getHeight() / 2 + 50; 
            
            // Array para guardar os "desenhos" (Shapes) gerados de cada letra
            Shape[] letterShapes = new Shape[text.length()];
            float currentX = startX;

            // 2º Passo: Aplica as transformações (girar e mover) letra por letra
            for (int i = 0; i < text.length(); i++) {
                String ch = String.valueOf(text.charAt(i));
                TextLayout tl = new TextLayout(ch, font, frc); // Transforma a string em um layout geométrico
                Shape outline = tl.getOutline(null); // Pega o contorno da letra
                
                // Ferramenta matemática do Java para manipulação 2D
                AffineTransform transform = new AffineTransform();
                // Move a letra para a posição X atual e aplica o pulinho no Y
                transform.translate(currentX, startY + yBounce[i % yBounce.length]);
                // Gira a letra a partir do seu próprio centro
                transform.rotate(rotations[i % rotations.length], tl.getAdvance()/2, -tl.getAscent()/2);
                
                // Salva a forma distorcida e move o cursor X para a próxima letra
                letterShapes[i] = transform.createTransformedShape(outline);
                currentX += tl.getAdvance() + letterSpacing;
            }

            // =======================================================
            // DESENHANDO NA TELA (Sombra -> Borda -> Preenchimento)
            // =======================================================
            
            // 3º Passo: Desenha o efeito 3D (Sombra projetada para baixo)
            g2.setColor(new Color(15, 15, 15)); // Quase preto
            for (Shape s : letterShapes) {
                // Desenha a letra repetidas vezes descendo no eixo Y para criar um bloco de sombra
                for (int d = 1; d <= 16; d++) {
                    AffineTransform shadowTx = AffineTransform.getTranslateInstance(0, d);
                    Shape shadowLayer = shadowTx.createTransformedShape(s);
                    g2.fill(shadowLayer);
                    g2.setStroke(new BasicStroke(2f));
                    g2.draw(shadowLayer);
                }
            }

            // 4º Passo: Desenha um contorno (Stroke) bem grosso na letra original
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(14f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); // 14 pixels de borda arredondada
            for (Shape s : letterShapes) g2.draw(s);

            // 5º Passo: Preenche a parte de dentro da letra com branco
            g2.setColor(Color.WHITE); 
            for (Shape s : letterShapes) g2.fill(s);

            // Limpa o pincel de desenho da memória
            g2.dispose();
        }
    }

    /**
     * Classe Interna Customizada: O painel que desenha as camadas de fundo.
     */
    class BackgroundPanel extends JPanel {
        private Image imagemFundo; // Imagem principal
        private Image estaticaGif; // GIF de ruído de TV

        public BackgroundPanel(String caminho) {
            // Tenta carregar a imagem do caminho informado
            java.net.URL urlFundo = getClass().getResource(caminho);
            if (urlFundo == null) {
                urlFundo = util.ResourceLoader.getResource(caminho);
            }
            if (urlFundo != null) {
                imagemFundo = new ImageIcon(urlFundo).getImage();
            } else { // Fallback de segurança se não achar a imagem
                imagemFundo = new ImageIcon("src/images/domino.jpg").getImage();
            }

            // Carrega o GIF animado da estática
            java.net.URL urlGif = getClass().getResource("/images/Television_static.gif");
            if (urlGif == null) {
                urlGif = util.ResourceLoader.getResource("/images/Television_static.gif");
            }
            if (urlGif != null) {
                estaticaGif = new ImageIcon(urlGif).getImage();
            }
        }

        // Camada de Baixo: Imagem + Filtro Escuro
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();

            // Desenha a arte principal esticada do tamanho da tela
            if (imagemFundo != null) g2.drawImage(imagemFundo, 0, 0, w, h, this);
            
            // Desenha um retângulo preto com transparência (Alpha 120/255) pra dar contraste
            g2.setColor(new Color(10, 10, 15, 120)); 
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }

        // Camada de Cima (Efeitos e Textos): Desenhada DEPOIS que todos os botões/títulos já foram desenhados
        @Override
        protected void paintChildren(Graphics g) {
            super.paintChildren(g); // Pede pro Java desenhar os filhos (O título animado)
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();

            // A MÁGICA DA ESTÁTICA: Aplica o GIF por cima de tudo com 5% de força
            if (estaticaGif != null) {
                // Altera o canal Alpha do Graphics2D para 0.05f (5% de opacidade)
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
                g2.drawImage(estaticaGif, 0, 0, w, h, this); // Desenha o gif transparente
                // Devolve a opacidade para 1.0f (100%) para não estragar os próximos desenhos
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); 
            }

            // Efeito de Vinheta (Bordas escuras) usando Gradiente Radial
            Point center = new Point(w / 2, h / 2);
            float radius = Math.max(w, h) * 0.75f; // O raio do gradiente vai até as bordas da tela
            
            if (radius > 0) {
                RadialGradientPaint p = new RadialGradientPaint(center, radius, 
                    new float[]{0.0f, 0.5f, 1.0f}, // Centro, Metade, Borda
                    new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 100), new Color(0, 0, 0, 240)}); // Cores de transparente até quase preto puro
                g2.setPaint(p);
                g2.fillRect(0, 0, w, h); // Desenha a vinheta cobrindo a tela toda
            }

            // Desenha o texto "PRESSIONE ALGUM BOTÃO" na parte de baixo
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Font fontePrompt = new Font("Alegre Sans", Font.BOLD, 26);
            
            // Usa o seu esquema de afastar um pouco as letras (Tracking positivo de 0.05)
            Map<TextAttribute, Object> atributos = new HashMap<>();
            atributos.put(TextAttribute.TRACKING, 0.05); 
            g2.setFont(fontePrompt.deriveFont(atributos));
            g2.setColor(Color.WHITE); 

            String prompt = "PRESSIONE ALGUM BOTÃO";
            FontMetrics fm = g2.getFontMetrics(); // Ajuda a calcular as dimensões do texto para centralizar
            // Desenha centralizado no eixo X e 50 pixels acima do chão no eixo Y
            g2.drawString(prompt, (w - fm.stringWidth(prompt)) / 2, h - 50);

            g2.dispose();
        }
    }
}
