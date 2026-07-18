package view;

import java.awt.*;
import javax.swing.*;
import util.SoundPlayer; // IMPORTANTE: Importamos o tocador de som aqui!

public class MainFrame extends JFrame {

    private FadeTransition fadeTransition;
    private JPanel painelAtual;

    public MainFrame() {
        setTitle("Domino Premium");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Tela cheia
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Vidro de transição que fica na frente de tudo
        fadeTransition = new FadeTransition();
        setGlassPane(fadeTransition);
        
        // Inicia com o painel da Pré-Tela
        trocarPainel(new PreTelaPanel(this));
        
        // ==========================================
        // A MÚSICA COMEÇA AQUI AGORA!
        // ==========================================
        SoundPlayer.tocar("game_start.wav", true); 
        
        setVisible(true);
        fadeTransition.startFadeIn();
    }

    public void trocarPainel(JPanel novoPainel) {
        if (painelAtual != null) {
            fadeTransition.startFadeOut(() -> {
                executarTroca(novoPainel);
                fadeTransition.startFadeIn();
            });
        } else {
            executarTroca(novoPainel);
        }
    }

    private void executarTroca(JPanel novoPainel) {
        getContentPane().removeAll();
        painelAtual = novoPainel;
        add(painelAtual);
        revalidate();
        repaint();
        novoPainel.requestFocusInWindow(); 
    }

    // Classe da transição suave
    class FadeTransition extends JPanel {
        private float alpha = 0f;
        public FadeTransition() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(0, 0, 0, alpha));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        public void startFadeOut(Runnable onComplete) {
            setVisible(true);
            Timer t = new Timer(15, e -> {
                alpha += 0.05f;
                if (alpha >= 1f) { alpha = 1f; ((Timer)e.getSource()).stop(); onComplete.run(); }
                repaint();
            });
            t.start();
        }
        public void startFadeIn() {
            setVisible(true);
            Timer t = new Timer(15, e -> {
                alpha -= 0.05f;
                if (alpha <= 0f) { alpha = 0f; ((Timer)e.getSource()).stop(); setVisible(false); }
                repaint();
            });
            t.start();
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}   