package view;

import java.awt.*;
import javax.swing.*;

// Tela alternativa simples para interface de jogo não usada no fluxo atual.
public class TelaJogo extends JFrame {

    public JLabel lblMesa;
    public JLabel lblJogador;
    public JLabel lblPlacar;
    public JLabel lblCava;
    public JPanel painelMao;

    public JButton btnJogarEsquerda;
    public JButton btnJogarDireita;
    public JButton btnComprar;
    public JButton btnPassar;
    public JButton btnNovoJogo;

    public TelaJogo() {
        setTitle("Jogo de Dominó");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topo = new JPanel(new GridLayout(1, 3));

        lblJogador = new JLabel("Jogador da vez", SwingConstants.CENTER);
        lblPlacar = new JLabel("Placar", SwingConstants.CENTER);
        lblCava = new JLabel("Cava", SwingConstants.CENTER);

        topo.add(lblJogador);
        topo.add(lblPlacar);
        topo.add(lblCava);

        add(topo, BorderLayout.NORTH);

        lblMesa = new JLabel("Mesa vazia", SwingConstants.CENTER);
        lblMesa.setFont(new Font("Arial", Font.BOLD, 28));
        add(lblMesa, BorderLayout.CENTER);

        painelMao = new JPanel(new FlowLayout());
        JScrollPane scroll = new JScrollPane(painelMao);
        scroll.setPreferredSize(new Dimension(1000, 120));
        add(scroll, BorderLayout.SOUTH);

        JPanel botoes = new JPanel(new GridLayout(5, 1, 5, 5));

        btnJogarEsquerda = new JButton("Jogar à Esquerda");
        btnJogarDireita = new JButton("Jogar à Direita");
        btnComprar = new JButton("Comprar");
        btnPassar = new JButton("Passar");
        btnNovoJogo = new JButton("Novo Jogo");

        botoes.add(btnJogarEsquerda);
        botoes.add(btnJogarDireita);
        botoes.add(btnComprar);
        botoes.add(btnPassar);
        botoes.add(btnNovoJogo);

        add(botoes, BorderLayout.EAST);

        setVisible(true);
    }

    public void atualizarMesa(String texto) {
        lblMesa.setText(texto == null || texto.isEmpty() ? "Mesa vazia" : texto);
    }

    public void atualizarJogador(int id) {
        lblJogador.setText("Jogador da vez: Jogador " + id);
    }

    public void atualizarPlacar(String texto) {
        lblPlacar.setText(texto);
    }

    public void atualizarCava(int qtd) {
        lblCava.setText("Cava: " + qtd + " pedras");
    }

    public void limparMao() {
        painelMao.removeAll();
        painelMao.revalidate();
        painelMao.repaint();
    }

    public void adicionarBotaoPedra(JButton botao) {
        painelMao.add(botao);
        painelMao.revalidate();
        painelMao.repaint();
    }
}
