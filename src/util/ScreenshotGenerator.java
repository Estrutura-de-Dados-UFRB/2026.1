package util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import view.JanelaJogo;
import view.MenuPanel;
import view.PreTelaPanel;

public final class ScreenshotGenerator {
    private static final int LARGURA = 1280;
    private static final int ALTURA = 720;

    private ScreenshotGenerator() {
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        File pasta = new File("../docs/prints");
        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        SwingUtilities.invokeAndWait(() -> {
            try {
                salvar(new PreTelaPanel(null), new File(pasta, "pre-tela.png"));
                salvar(new MenuPanel(null), new File(pasta, "menu-principal.png"));
                salvar(new JanelaJogo(null, 2), new File(pasta, "partida-2-jogadores.png"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                SoundPlayer.parar();
            }
        });
    }

    private static void salvar(JPanel painel, File destino) throws Exception {
        painel.setPreferredSize(new Dimension(LARGURA, ALTURA));
        painel.setSize(LARGURA, ALTURA);
        layoutRecursivo(painel);

        BufferedImage imagem = new BufferedImage(LARGURA, ALTURA, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = imagem.createGraphics();
        painel.printAll(g2);
        g2.dispose();

        ImageIO.write(imagem, "png", destino);
    }

    private static void layoutRecursivo(Container container) {
        container.doLayout();
        for (java.awt.Component componente : container.getComponents()) {
            if (componente instanceof Container) {
                layoutRecursivo((Container) componente);
            }
        }
    }
}
