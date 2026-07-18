package view;

import java.awt.*;
import javax.swing.*;
import util.SoundPlayer;

public class PainelControles extends JPanel {

    private JButton btnComprar;
    private JButton btnPassar;
    private JanelaJogo janela;

    public PainelControles(JanelaJogo janela) {
        this.janela = janela;
        configurarPainel();
        inicializarComponentes();
    }

    private void configurarPainel() {
        setBackground(new Color(12, 14, 12));
        setPreferredSize(new Dimension(200, 400));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));
    }

    private void inicializarComponentes() {
        btnComprar = new JButton("Comprar");
        btnPassar = new JButton("Passar a Vez");

        estilizarBotao(btnComprar);
        estilizarBotao(btnPassar);

        btnComprar.addActionListener(e -> {
            SoundPlayer.tocar("click.wav", false);
            janela.comprarPedra();
        });

        btnPassar.addActionListener(e -> {
            SoundPlayer.tocar("aviso.wav", false);
            int confirm = JOptionPane.showConfirmDialog(
                    janela,
                    "Tem certeza que deseja passar a vez?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                janela.passarVez();
                janela.getPainelMao().limparSelecao();
            }
        });

        // Adiciona um espaço flexível antes e depois para centralizar os botões verticalmente
        add(Box.createVerticalGlue());
        
        if (janela.getQtdJogadores() <= 3) {
            adicionarBotao(btnComprar);
        }
        
        adicionarBotao(btnPassar);
        add(Box.createVerticalGlue());
    }

    private void estilizarBotao(JButton botao) {
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao.setMaximumSize(new Dimension(170, 46));
        botao.setPreferredSize(new Dimension(170, 46));
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setForeground(Color.WHITE);
        botao.setBackground(new Color(20, 130, 55));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void adicionarBotao(JButton botao) {
        add(botao);
        add(Box.createVerticalStrut(15));
    }

    public void setComprarEnabled(boolean habilitar) {
        btnComprar.setEnabled(habilitar);
    }

    public void setPassarEnabled(boolean habilitar) {
        btnPassar.setEnabled(habilitar);
    }
}