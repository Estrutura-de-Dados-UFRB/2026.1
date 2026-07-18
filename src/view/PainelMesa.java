package view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import util.Lista;
import util.Pedras2;

// Painel que desenha a mesa e posiciona as pedras jogadas.
public class PainelMesa extends JPanel {

    private List<Pedras2> pedras;
    private Lista<Pedras2> mesaLista;
    private Image imagemFundo;

    public PainelMesa() {
        pedras = new ArrayList<>();
        mesaLista = new Lista<>();
        carregarImagemFundo();
        configurarPainel();
    }

    private void configurarPainel() {
        setBackground(Color.BLACK);
        setBorder(null);
        setPreferredSize(new Dimension(900, 520));
        setMinimumSize(new Dimension(620, 360));
        setLayout(null);
    }

    public void setPedrasMesa(List<Pedras2> novasPedras) {
        this.pedras = new ArrayList<>(novasPedras);

        // Sincroniza a lista auxiliar usada pelo GerenciadorMesa para desenhar a sequencia.
        this.mesaLista = new Lista<>();
        for (Pedras2 p : novasPedras) {
            mesaLista.addFim(p);
        }

        atualizarMesaGUI();
    }

    public void adicionarPedra(Pedras2 pedra, boolean esquerda) {
        if (esquerda) {
            pedras.add(0, pedra);
            mesaLista.addInicio(pedra);
        } else {
            pedras.add(pedra);
            mesaLista.addFim(pedra);
        }

        atualizarMesaGUI();
    }

    public void adicionarPedraAnimada(Pedras2 pedra, boolean esquerda) {
        // Ponto de extensao para animacao; por enquanto a pedra entra diretamente na mesa.
        adicionarPedra(pedra, esquerda);
    }

    public void limparMesa() {
        pedras.clear();
        mesaLista = new Lista<>();
        atualizarMesaGUI();
    }

    private void atualizarMesaGUI() {
        repaint();
    }

    private void carregarImagemFundo() {
        java.net.URL url = getClass().getResource("/images/mesa.png");
        if (url == null) {
            url = util.ResourceLoader.getResource("/images/mesa.png");
        }

        if (url != null) {
            imagemFundo = new ImageIcon(url).getImage();
        } else {
            imagemFundo = new ImageIcon("src/images/mesa.png").getImage();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (imagemFundo != null) {
            desenharImagemFundo(g2d);
            g2d.setColor(new Color(0, 0, 0, 95));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Carrega as faces numeradas de 0 a 6 antes de delegar o desenho das pedras.
        Image[] imgFaces = new Image[7];

        for (int i = 0; i <= 6; i++) {
            ImageIcon icon = carregarImagem(i, 60); // Aumentado de 34 para 42

            if (icon != null) {
                imgFaces[i] = icon.getImage();
            }
        }

        // Desenha a mesa inteira a partir da lista encadeada sincronizada acima.
        GerenciadorMesa.desenharMesa(
                g2d,
                mesaLista,
                imgFaces,
                getWidth(),
                getHeight()
        );

        g2d.dispose();
    }

    private void desenharImagemFundo(Graphics2D g2d) {
        int larguraPainel = getWidth();
        int alturaPainel = getHeight();
        int larguraImagem = imagemFundo.getWidth(this);
        int alturaImagem = imagemFundo.getHeight(this);

        if (larguraImagem <= 0 || alturaImagem <= 0 || larguraPainel <= 0 || alturaPainel <= 0) {
            return;
        }

        double escala = Math.max(
                (double) larguraPainel / larguraImagem,
                (double) alturaPainel / alturaImagem
        );

        int novaLargura = (int) Math.ceil(larguraImagem * escala);
        int novaAltura = (int) Math.ceil(alturaImagem * escala);
        int sobra = Math.max(24, larguraPainel / 40);

        novaLargura += sobra * 2;
        novaAltura += sobra * 2;

        int x = (larguraPainel - novaLargura) / 2;
        int y = (alturaPainel - novaAltura) / 2;

        g2d.drawImage(imagemFundo, x, y, novaLargura, novaAltura, this);
    }

    private ImageIcon carregarImagem(int lado, int tamanho) {
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

        Image img = icon.getImage().getScaledInstance(
                tamanho,
                tamanho,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(img);
    }
}
