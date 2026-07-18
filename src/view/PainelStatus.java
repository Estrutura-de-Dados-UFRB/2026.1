package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import util.SoundPlayer;

public class PainelStatus extends JPanel {

    private JLabel lblJogador;
    private JLabel lblVez;
    private JLabel lblMensagem;
    private JButton btnConfig;
    private JanelaJogo janela;

    public PainelStatus(JanelaJogo janela) {
        this.janela = janela;
        configurarPainel();
        inicializarComponentes();
    }

    private void configurarPainel() {
        setBackground(new Color(12, 14, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        setPreferredSize(new Dimension(800, 64));
        setLayout(new BorderLayout()); 
    }

    private void inicializarComponentes() {
        lblJogador = new JLabel("Jogador: 1");
        lblJogador.setFont(new Font("Arial", Font.BOLD, 16));
        lblJogador.setForeground(Color.WHITE);

        lblVez = new JLabel("Sua vez!", SwingConstants.CENTER);
        lblVez.setFont(new Font("Arial", Font.BOLD, 18));

        lblMensagem = new JLabel("Cava: 0 pedras", SwingConstants.RIGHT);
        lblMensagem.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMensagem.setForeground(new Color(230, 230, 230));

        // =========================================================
        // CONFIGURAÇÃO DO BOTÃO DE ÍCONE FLUTUANTE
        // =========================================================
        btnConfig = new JButton(); 
        
        try {
            java.net.URL url = getClass().getResource("/images/config.png");
            if (url == null) {
                url = util.ResourceLoader.getResource("/images/config.png");
            }
            if (url != null) {
                ImageIcon iconOriginal = new ImageIcon(url);
                // Aumentamos o tamanho de 22x22 para 34x34
                Image imgEscalada = iconOriginal.getImage().getScaledInstance(34, 34, Image.SCALE_SMOOTH);
                btnConfig.setIcon(new ImageIcon(imgEscalada));
            } else {
                // Fallback de segurança se a imagem não carregar
                btnConfig.setText("⚙"); 
                btnConfig.setFont(new Font("Arial", Font.BOLD, 28));
                btnConfig.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            btnConfig.setText("⚙");
            btnConfig.setFont(new Font("Arial", Font.BOLD, 28));
            btnConfig.setForeground(Color.WHITE);
        }

        // Remove bordas e fundos para deixar parecendo apenas uma imagem solta
        btnConfig.setPreferredSize(new Dimension(46, 46));
        btnConfig.setFocusPainted(false);
        btnConfig.setBorderPainted(false);
        btnConfig.setContentAreaFilled(false); 
        btnConfig.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efeito Hover: Fica mais claro quando o mouse passa por cima
        btnConfig.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnConfig.setContentAreaFilled(true); // Ativa o fundo temporariamente
                btnConfig.setBackground(new Color(255, 255, 255, 40)); // Branco transparente (brilho leve)
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnConfig.setContentAreaFilled(false); // Remove o fundo quando o mouse sai
            }
        });
        
        btnConfig.addActionListener(e -> mostrarMenuConfig());

        JPanel painelLeste = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        painelLeste.setOpaque(false);
        
        // Alinhamento vertical centralizado para o texto e o ícone
        lblMensagem.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 10)); 
        
        painelLeste.add(lblMensagem);
        painelLeste.add(btnConfig);

        add(lblJogador, BorderLayout.WEST);
        add(lblVez, BorderLayout.CENTER);
        add(painelLeste, BorderLayout.EAST);
    }

    private void mostrarMenuConfig() {
        SoundPlayer.tocar("click.wav", false);
        JPopupMenu popup = new JPopupMenu();
        
        JMenuItem itemHist = new JMenuItem("Ver Histórico");
        JMenuItem itemNovo = new JMenuItem("Novo Jogo");
        JMenuItem itemSair = new JMenuItem("Sair");

        itemHist.addActionListener(e -> {
            // AQUI: Toca o som do relatório!
            SoundPlayer.tocar("relatorio.wav", false);
            new Historico().setVisible(true);
        });
        itemNovo.addActionListener(e -> janela.novoJogo());
        itemSair.addActionListener(e -> {
            SoundPlayer.parar();
            System.exit(0);
        });

        popup.add(itemHist);
        popup.addSeparator(); 
        popup.add(itemNovo);
        popup.add(itemSair);

        popup.show(btnConfig, 0, btnConfig.getHeight());
    }

    public void atualizarJogador(int id) {
        lblJogador.setText("Jogador: " + id);
    }

    public void atualizarVez(boolean suaVez) {
        lblVez.setText(suaVez ? "Sua vez!" : "Aguardando...");
        lblVez.setForeground(suaVez ? new Color(60, 210, 105) : new Color(230, 80, 80));
    }

    public void atualizarMensagem(String mensagem) {
        lblMensagem.setText(mensagem);
    }
}
