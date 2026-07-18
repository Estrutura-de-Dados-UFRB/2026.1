package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import util.Pedras2;

public class PainelMao extends JPanel {

    private List<Pedras2> mao;
    // As variáveis de "pedraSelecionada" foram removidas, a jogada agora é direta!
    private final List<PecaClickListener> listeners = new ArrayList<>();

    public PainelMao(List<Pedras2> maoInicial) {
        this.mao = new ArrayList<>(maoInicial);
        configurarPainel();
        atualizarMaoGUI();
    }

    public interface PecaClickListener {
        void onPecaClicada(int indice);
    }

    public void addPecaClickListener(PecaClickListener listener) {
        listeners.add(listener);
    }

    public void removePecaClickListener(PecaClickListener listener) {
        listeners.remove(listener);
    }

    private void notificarClique(int indice) {
        for (PecaClickListener listener : listeners) {
            listener.onPecaClicada(indice);
        }
    }

    private void configurarPainel() {
        setBackground(new Color(16, 18, 16));
        setPreferredSize(new Dimension(900, 132));

        TitledBorder border = BorderFactory.createTitledBorder("Sua Mão");
        border.setTitleFont(new Font("Arial", Font.BOLD, 18));
        border.setTitleColor(Color.WHITE);
        setBorder(border);

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
    }

    public void setMao(List<Pedras2> novaMao) {
        this.mao = new ArrayList<>(novaMao);
        atualizarMaoGUI();
    }

    public void limparSelecao() {
        // Como a jogada agora é instantânea, não precisamos mais "limpar" nada.
        // Mas o método continua aqui para não dar erro na JanelaJogo.
        repaint();
    }

    private void atualizarMaoGUI() {
        removeAll();

        // Recria as peças da mão
        for (int i = 0; i < mao.size(); i++) {
            Pedras2 pedra = mao.get(i);
            int indiceAtual = i;

            JPanel painelPedra = criarPainelPedra(pedra, indiceAtual);

            // =======================================================
            // ALTERAÇÃO: Removidos os botões com "1", "2", "3"
            // Adicionamos a pedra diretamente no painel.
            // =======================================================
            add(painelPedra);
        }

        revalidate();
        repaint();
    }

    private JPanel criarPainelPedra(Pedras2 pedra, int indice) {
        JPanel painel = new JPanel(new GridLayout(1, 2));
        painel.setPreferredSize(new Dimension(92, 54));
        painel.setBackground(Color.WHITE);
        painel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        aplicarBorda(painel, false);

        ImageIcon imgA = carregarImagem(pedra.getLadoA());
        ImageIcon imgB = carregarImagem(pedra.getLadoB());

        JLabel ladoA = new JLabel(imgA);
        JLabel ladoB = new JLabel(imgB);

        ladoA.setHorizontalAlignment(SwingConstants.CENTER);
        ladoB.setHorizontalAlignment(SwingConstants.CENTER);

        painel.add(ladoA);
        painel.add(ladoB);

        painel.addMouseListener(new MouseAdapter() {
            // =======================================================
            // ALTERAÇÃO: mousePressed no lugar de mouseClicked
            // Isso resolve o bug do clique falhar!
            // =======================================================
            @Override
            public void mousePressed(MouseEvent e) {
                // Notifica a JanelaJogo na mesma hora, sem som de click!
                notificarClique(indice);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // Efeito visual quando passa o mouse por cima
                aplicarBorda(painel, true);
                painel.setLocation(painel.getX(), painel.getY() - 5); // Sobe um pouquinho
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Volta ao normal quando o mouse sai
                aplicarBorda(painel, false);
                painel.setLocation(painel.getX(), painel.getY() + 5);
            }
        });

        return painel;
    }

    // Simplifiquei o método porque a gente não tem mais "pedra selecionada" parada
    private void aplicarBorda(JPanel painel, boolean hover) {
        if (hover) {
            painel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        } else {
            painel.setBorder(BorderFactory.createLineBorder(new Color(25, 25, 25), 2));
        }
    }

    private ImageIcon carregarImagem(int lado) {
        String caminho = "/images/" + lado + ".png";
        java.net.URL url = getClass().getResource(caminho);
        if (url == null) {
            url = util.ResourceLoader.getResource(caminho);
        }

        if (url == null) {
            System.out.println("Imagem não encontrada: " + caminho);
            return null;
        }

        ImageIcon icon = new ImageIcon(url);

        // Seu código para girar o 6 (MANTIDO INTACTO)
        if (lado == 6) {
            java.awt.image.BufferedImage rotatedImg = new java.awt.image.BufferedImage(38, 38, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = rotatedImg.createGraphics();
            
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.translate(19, 19);
            g2.rotate(Math.toRadians(90));
            
            g2.drawImage(icon.getImage(), -19, -19, 38, 38, null);
            g2.dispose();
            
            return new ImageIcon(rotatedImg);
        }

        Image img = icon.getImage().getScaledInstance(38, 38, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
