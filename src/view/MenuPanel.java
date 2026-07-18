package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import util.SoundPlayer;

/**
 * MenuPanel: Responsável por toda a interface visual do menu, 
 * incluindo navegação, opções de volume e créditos.
 */
public class MenuPanel extends JPanel {

    // Referência à janela principal (MainFrame) para permitir a troca de telas
    private MainFrame frame;
    // Painel customizado que desenha a imagem de fundo e efeitos visuais
    private BackgroundPanel fundo;
    // Controle deslizante para o volume
    private JSlider sliderVolume;
    // Texto que exibe a porcentagem do volume (ex: 50%)
    private JLabel lblVolumeValue;

    public MenuPanel(MainFrame frame) {
        this.frame = frame;
        // Define o layout como BorderLayout para o fundo ocupar todo o espaço
        setLayout(new BorderLayout());

        // Inicializa o painel de fundo com a imagem do menu
        fundo = new BackgroundPanel("/images/domino-menu.png");
        // Usa GridBagLayout no fundo para centralizar os menus facilmente
        fundo.setLayout(new GridBagLayout());
        add(fundo);

        // Chama o método para construir os botões iniciais
        mostrarMenuPrincipal();
        
        // Inicia a trilha sonora do menu em modo de repetição (loop)
        SoundPlayer.tocar("game_start.wav", true);
    }

    /**
     * Monta a tela inicial do menu (Iniciar, Opções, Sair).
     */
    private void mostrarMenuPrincipal() {
        // Limpa tudo que estava desenhado no fundo para evitar sobreposição
        fundo.removeAll();
        
        // Cria um "card" invisível para agrupar os botões verticalmente
        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(false); // Mantém o fundo transparente para ver a imagem de trás

        // Cria os botões usando o método utilitário personalizado
        JButton iniciar = criarBotao("INICIAR", "click.wav");
        JButton opcoes = criarBotao("OPÇÕES", "click.wav");
        JButton sair = criarBotao("SAIR", "click.wav");

        // Define as ações de clique para cada botão
        iniciar.addActionListener(e -> mostrarEscolhaJogadores());
        opcoes.addActionListener(e -> mostrarOpcoes());
        sair.addActionListener(e -> {
            SoundPlayer.parar(); // Para a música antes de fechar
            System.exit(0);      // Fecha o programa completamente
        });

        // Configura o posicionamento dos botões no GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;         // Todos na mesma coluna (coluna 0)
        gbc.weightx = 0.0;     // Não expande horizontalmente
        gbc.weighty = 0.0;     // Não expande verticalmente
        gbc.insets = new Insets(4, 0, 4, 0); // Espaçamento de 4 pixels entre botões

        // Adiciona os botões ao card, um abaixo do outro (mudando gridy)
        gbc.gridy = 0; card.add(iniciar, gbc);
        gbc.gridy = 1; card.add(opcoes, gbc);
        gbc.gridy = 2; card.add(sair, gbc);

        // Centraliza o card inteiro no meio da tela
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.weightx = 1.0; 
        centerGbc.weighty = 1.0;
        centerGbc.anchor = GridBagConstraints.CENTER;
        fundo.add(card, centerGbc);

        // Adiciona o texto de rodapé no canto inferior
        fundo.add(criarRodape("Estruturas de Dados • Java Swing"), criarRodapeConstraints());
        
        // Comandos obrigatórios para o Java redesenhar a tela com as mudanças
        fundo.revalidate();
        fundo.repaint();
    }

    /**
     * Monta a tela de Opções (Volume e Créditos).
     */
    private void mostrarOpcoes() {
        fundo.removeAll();
        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; 
        gbc.weightx = 0.0; 
        gbc.weighty = 0.0;

        // Painel que organiza Volume e Participantes lado a lado (X_AXIS)
        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.X_AXIS));
        painelConteudo.setOpaque(false);

        // Adiciona os componentes de volume e créditos
        painelConteudo.add(criarVolumeOverlay());
        painelConteudo.add(Box.createRigidArea(new Dimension(100, 0))); // Espaço vazio de 100px entre eles
        painelConteudo.add(criarParticipantesOverlay());

        // Adiciona o conteúdo ao card com margem inferior de 40px
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 40, 0); 
        card.add(painelConteudo, gbc);

        // Botão para retornar ao menu principal
        JButton voltar = criarBotao("VOLTAR", "click.wav");
        voltar.addActionListener(e -> mostrarMenuPrincipal());
        
        gbc.gridy = 1; 
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(voltar, gbc);

        // Centraliza tudo no fundo
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.weightx = 1.0; 
        centerGbc.weighty = 1.0;
        centerGbc.anchor = GridBagConstraints.CENTER;
        fundo.add(card, centerGbc);

        fundo.add(criarRodape("Opções e Créditos do Jogo"), criarRodapeConstraints());
        fundo.revalidate();
        fundo.repaint();
    }

    /**
     * Monta a tela para escolher quantos jogadores participarão.
     */
    private void mostrarEscolhaJogadores() {
        fundo.removeAll();
        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(false);

        // Botões para as diferentes quantidades de jogadores
        JButton doisJogadores = criarBotao("2 JOGADORES", "start.wav");
        JButton tresJogadores = criarBotao("3 JOGADORES", "start.wav");
        JButton quatroJogadores = criarBotao("4 JOGADORES", "start.wav");
        JButton voltar = criarBotao("VOLTAR", "click.wav");

        // Ações: cada botão inicia o jogo com o número correspondente
        doisJogadores.addActionListener(e -> iniciarJogo(2));
        tresJogadores.addActionListener(e -> iniciarJogo(3));
        quatroJogadores.addActionListener(e -> iniciarJogo(4));
        voltar.addActionListener(e -> mostrarMenuPrincipal());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; 
        gbc.insets = new Insets(4, 0, 4, 0);

        gbc.gridy = 0; card.add(doisJogadores, gbc);
        gbc.gridy = 1; card.add(tresJogadores, gbc);
        gbc.gridy = 2; card.add(quatroJogadores, gbc);
        gbc.gridy = 3; 
        gbc.insets = new Insets(20, 0, 0, 0); // Espaço extra para o botão voltar
        card.add(voltar, gbc);

        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.weightx = 1.0; centerGbc.weighty = 1.0;
        centerGbc.anchor = GridBagConstraints.CENTER;
        fundo.add(card, centerGbc);

        fundo.add(criarRodape("Escolha a quantidade de jogadores"), criarRodapeConstraints());
        fundo.revalidate();
        fundo.repaint();
    }

    /**
     * Cria o componente visual do controle de volume.
     */
    private JPanel criarVolumeOverlay() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS)); // Organiza de cima para baixo
        painel.setOpaque(false);

        // Label do título "VOLUME"
        JLabel lblVolume = new JLabel("VOLUME");
        Font fonteTitulo = new Font("Alegre Sans", Font.BOLD, 28);
        Map<TextAttribute, Object> atributos = new HashMap<>();
        atributos.put(TextAttribute.TRACKING, -0.05); // Deixa as letras mais juntas
        lblVolume.setFont(fonteTitulo.deriveFont(atributos));
        lblVolume.setForeground(Color.WHITE); 
        lblVolume.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Cria o Slider vertical
        sliderVolume = new JSlider(SwingConstants.VERTICAL, 0, 100, (int)(SoundPlayer.getVolumeGlobal() * 100));
        sliderVolume.setPreferredSize(new Dimension(40, 160));
        sliderVolume.setOpaque(false);
        sliderVolume.setMajorTickSpacing(25); // Marcações a cada 25%
        sliderVolume.setPaintTicks(true);    // Desenha as marcações
        sliderVolume.setForeground(Color.WHITE);
        sliderVolume.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Estilização customizada do Slider (UI básica)
        sliderVolume.setUI(new javax.swing.plaf.basic.BasicSliderUI(sliderVolume) {
            @Override
            public void paintThumb(Graphics g) { // Desenha a "bolinha" do slider
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.WHITE);
                g2d.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }
            @Override
            public void paintTrack(Graphics g) { // Desenha a "barra" do slider
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.fillRect(trackRect.x + trackRect.width / 2 - 2, trackRect.y, 4, trackRect.height);
            }
        });

        // Texto que mostra o valor atual do volume
        lblVolumeValue = new JLabel(sliderVolume.getValue() + "%");
        lblVolumeValue.setFont(new Font("Alegre Sans", Font.BOLD, 22));
        lblVolumeValue.setForeground(Color.WHITE);
        lblVolumeValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblVolumeValue.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        // Listener: toda vez que mexer no slider, altera o volume real do sistema
        sliderVolume.addChangeListener(e -> {
            int value = sliderVolume.getValue();
            lblVolumeValue.setText(value + "%");
            SoundPlayer.setVolumeGlobal(value / 100.0f); // Converte de 0-100 para 0.0-1.0
        });

        painel.add(lblVolume);
        painel.add(Box.createRigidArea(new Dimension(0, 10))); 
        painel.add(sliderVolume);
        painel.add(lblVolumeValue);

        return painel;
    }

    /**
     * Cria a lista visual com os nomes dos desenvolvedores.
     */
    private JPanel criarParticipantesOverlay() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);

        JLabel lblTitulo = new JLabel("PARTICIPANTES");
        Font fonteTitulo = new Font("Alegre Sans", Font.BOLD, 28);
        Map<TextAttribute, Object> atributos = new HashMap<>();
        atributos.put(TextAttribute.TRACKING, -0.05);
        lblTitulo.setFont(fonteTitulo.deriveFont(atributos));
        lblTitulo.setForeground(Color.WHITE); 
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        painel.add(lblTitulo);
        painel.add(Box.createRigidArea(new Dimension(0, 15))); 

        // Lista de nomes cadastrados no projeto
        String[] nomes = {"Benjamin da Conceição Neves ", "Caio Conceição dos Santos", "Jessé dos Santos Nery", "Daniel Bezerra Medeiros de Souza", "Daniel José Cerqueira Brito", "João Pedro Carneiro da Silva"};
        Font fonteNomes = new Font("Alegre Sans", Font.PLAIN, 24);

        for (String nome : nomes) {
            JLabel lblNome = new JLabel(nome);
            lblNome.setFont(fonteNomes);
            lblNome.setForeground(new Color(220, 220, 220)); 
            lblNome.setAlignmentX(Component.CENTER_ALIGNMENT);
            painel.add(lblNome);
            painel.add(Box.createRigidArea(new Dimension(0, 8))); // Espaço entre nomes
        }

        return painel;
    }

    /**
     * Helper para criar textos pequenos de rodapé.
     */
    private JLabel criarRodape(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Alegre Sans", Font.ITALIC, 18));
        lbl.setForeground(Color.WHITE); 
        return lbl;
    }

    /**
     * Helper para fixar o rodapé no canto inferior direito.
     */
    private GridBagConstraints criarRodapeConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; 
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST; // Âncora: Sul + Leste
        gbc.insets = new Insets(0, 0, 20, 30); 
        return gbc;
    }

    /**
     * Gerencia a transição para a Janela do Jogo com um pequeno delay.
     */
    private void iniciarJogo(int qtdJogadores) {
        // Cria um timer para esperar 800ms antes de mudar de tela
        // Isso permite que o usuário ouça o som de "start" antes do silêncio do jogo
        Timer delay = new Timer(800, e -> {
            SoundPlayer.parar(); // Para a música do menu
            // Manda o frame principal trocar o painel atual pela JanelaJogo
            frame.trocarPainel(new JanelaJogo(frame, qtdJogadores));
        });
        
        delay.setRepeats(false); // Garante que o timer só rode uma vez
        delay.start();
    }

    /**
     * Método "Fábrica" de botões personalizados: define fonte, sons e efeitos de hover.
     */
    private JButton criarBotao(String texto, String somClique) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(320, 35));
        
        // Aplica a fonte Alegre Sans com tracking negativo
        Font fonteBase = new Font("Alegre Sans", Font.BOLD, 34);
        Map<TextAttribute, Object> atributos = new HashMap<>();
        atributos.put(TextAttribute.TRACKING, -0.05); 
        btn.setFont(fonteBase.deriveFont(atributos));

        Color corInativa = new Color(200, 200, 200);
        btn.setForeground(corInativa); 
        
        // Remove as bordas e fundos padrão do JButton para um visual mais limpo
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor de "mãozinha"

        // Gerencia eventos de Mouse (Hover e Clique)
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { // Quando o mouse entra no botão
                btn.setForeground(Color.WHITE); // Brilha
                SoundPlayer.tocar("click.wav", false); // Toca som de seleção
            }
            public void mouseExited(MouseEvent e) { // Quando o mouse sai
                btn.setForeground(corInativa); // Volta ao normal
            }
            public void mousePressed(MouseEvent e) { // Quando o botão é pressionado
                SoundPlayer.tocar(somClique, false); // Toca o som passado por parâmetro
            }
        });
        return btn;
    }

    /**
     * Classe Interna: Responsável por desenhar o fundo dinâmico do menu.
     */
    class BackgroundPanel extends JPanel {
        private Image imagemFundo;
        private Image estaticaGif; // GIF de estática de TV

        public BackgroundPanel(String caminho) {
            // Carrega a imagem de fundo via URL do recurso
            java.net.URL urlFundo = getClass().getResource(caminho);
            if (urlFundo == null) {
                urlFundo = util.ResourceLoader.getResource(caminho);
            }
            if (urlFundo != null) {
                imagemFundo = new ImageIcon(urlFundo).getImage();
            } else {
                // Fallback caso o recurso não seja encontrado
                imagemFundo = new ImageIcon("src/images/domino-menu.png").getImage(); 
            }

            // Carrega o efeito de estática de televisão
            java.net.URL urlGif = getClass().getResource("/images/Television_static.gif");
            if (urlGif == null) {
                urlGif = util.ResourceLoader.getResource("/images/Television_static.gif");
            }
            if (urlGif != null) {
                estaticaGif = new ImageIcon(urlGif).getImage();
            }
        }

        // Camada 1: Desenha a imagem de fundo e uma camada de escurecimento
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(); int h = getHeight();

            if (imagemFundo != null) g2.drawImage(imagemFundo, 0, 0, w, h, this);
            
            // Desenha um retângulo preto semitransparente para dar contraste aos botões
            g2.setColor(new Color(10, 10, 15, 120)); 
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }

        // Camada 2: Desenha efeitos Visuais POR CIMA dos botões (Estática e Gradiente)
        @Override
        protected void paintChildren(Graphics g) {
            super.paintChildren(g); // Desenha os botões primeiro
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();

            // Aplica a estática de TV com apenas 5% de opacidade (Alpha 0.05)
            if (estaticaGif != null) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
                g2.drawImage(estaticaGif, 0, 0, w, h, this);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); 
            }

            // Cria um gradiente radial (circular) para escurecer as bordas da tela (vinheta)
            Point center = new Point(w / 2, h / 2);
            float radius = Math.max(w, h) * 0.75f;
            
            if (radius > 0) {
                RadialGradientPaint p = new RadialGradientPaint(center, radius, 
                    new float[]{0.0f, 0.5f, 1.0f}, // Estágios do gradiente
                    new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 100), new Color(0, 0, 0, 240)});
                g2.setPaint(p);
                g2.fillRect(0, 0, w, h);
            }

            g2.dispose();
        }
    }
}
