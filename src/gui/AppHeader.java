package gui;

import gui.components.AppIcon;
import gui.components.ModernButton;
import gui.components.ModernToolbar;
import gui.components.WrapLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.function.Consumer;

/** Cabeçalho e toolbars da aplicação; contém apenas composição visual. */
final class AppHeader extends JPanel {
    enum Command { DIJKSTRA, PRIM, KRUSKAL, BFS, DFS, GREEDY, TSP, PLAY, PAUSE, STOP, CLEAR, POINTS }

    private enum AlgorithmOption {
        DIJKSTRA("Dijkstra", Command.DIJKSTRA), PRIM("Prim", Command.PRIM), KRUSKAL("Kruskal", Command.KRUSKAL),
        BFS("Busca em largura", Command.BFS), DFS("Busca em profundidade", Command.DFS),
        GREEDY("Guloso", Command.GREEDY), TSP("Caixeiro viajante", Command.TSP);

        private final String label;
        private final Command command;
        AlgorithmOption(String label, Command command) { this.label = label; this.command = command; }
        @Override public String toString() { return label; }
    }

    private final Consumer<Command> commands;
    private final gui.components.SpeedControlPanel speed = new gui.components.SpeedControlPanel();

    AppHeader(Consumer<Command> commands) {
        this.commands = commands;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        JPanel brand = createBrand();
        brand.setAlignmentX(LEFT_ALIGNMENT);
        JPanel toolbar = createToolbar();
        toolbar.setAlignmentX(LEFT_ALIGNMENT);
        add(brand); add(Box.createVerticalStrut(UiTheme.scale(4))); add(toolbar);
        final int[] previousWidth = {-1};
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent event) {
                if (getWidth() == previousWidth[0]) return;
                previousWidth[0] = getWidth();
                toolbar.revalidate();
                AppHeader.this.revalidate();
            }
        });
    }

    int speedValue() { return speed.getValue(); }

    private JPanel createBrand() {
        JPanel brand = new JPanel(new BorderLayout(UiTheme.scale(12), 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0,0,UiTheme.PRIMARY,0,getHeight(),UiTheme.PRIMARY_DARK));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),UiTheme.scale(18),UiTheme.scale(18));
                g2.setColor(new Color(255,255,255,28)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,UiTheme.scale(18),UiTheme.scale(18)); g2.dispose();
            }
        };
        brand.setOpaque(false); brand.setBorder(BorderFactory.createEmptyBorder(7,16,7,12));
        JPanel text=new JPanel(); text.setOpaque(false); text.setLayout(new BoxLayout(text,BoxLayout.Y_AXIS));
        JLabel title=new JLabel("Transporte Universitário"); title.setFont(UiTheme.TITLE_FONT); title.setForeground(Color.WHITE);
        JLabel subtitle=new JLabel("Planejamento inteligente de rotas • Cruz das Almas/BA"); subtitle.setFont(UiTheme.FONT); subtitle.setForeground(new Color(220,233,246));
        text.add(title); text.add(Box.createVerticalStrut(2)); text.add(subtitle);
        ModernToolbar actions=new ModernToolbar();
        actions.add(button("Gerenciar pontos",AppIcon.Kind.PIN,ModernButton.Variant.SUCCESS,Command.POINTS,true));
        brand.add(text,BorderLayout.WEST); brand.add(actions,BorderLayout.EAST); return brand;
    }

    private JPanel createToolbar() {
        gui.components.RoundedPanel panel=new gui.components.RoundedPanel();
        panel.setLayout(new WrapLayout(FlowLayout.LEFT,UiTheme.scale(8),UiTheme.scale(2)));
        panel.setBorder(BorderFactory.createEmptyBorder(2,8,4,10));
        ModernToolbar routes=new ModernToolbar(); section(routes,"ROTA");
        JComboBox<AlgorithmOption> algorithms = new JComboBox<>(AlgorithmOption.values());
        algorithms.setFont(UiTheme.FONT);
        algorithms.setForeground(UiTheme.TEXT);
        algorithms.setBackground(UiTheme.SURFACE);
        algorithms.setPreferredSize(UiTheme.scaledSize(170,36));
        routes.add(algorithms);
        ModernButton calculate = new ModernButton("Calcular",AppIcon.Kind.ROUTE,ModernButton.Variant.PRIMARY);
        calculate.addActionListener(e -> commands.accept(((AlgorithmOption) algorithms.getSelectedItem()).command));
        routes.add(calculate);
        ModernToolbar simulation=new ModernToolbar(); section(simulation,"SIMULAÇÃO");
        simulation.add(button("Iniciar",AppIcon.Kind.PLAY,ModernButton.Variant.SUCCESS,Command.PLAY,false));
        simulation.add(button("Pausar",AppIcon.Kind.PAUSE,ModernButton.Variant.SECONDARY,Command.PAUSE,false));
        simulation.add(button("Parar",AppIcon.Kind.STOP,ModernButton.Variant.SECONDARY,Command.STOP,false));
        simulation.add(button("Limpar",AppIcon.Kind.CLEAR,ModernButton.Variant.SECONDARY,Command.CLEAR,false));
        simulation.add(speed);
        panel.add(routes); panel.add(simulation); return panel;
    }

    private void section(JPanel panel,String text) {
        JLabel label=new JLabel(text+"  "); label.setFont(UiTheme.FONT_BOLD.deriveFont(10f)); label.setForeground(UiTheme.TEXT_MUTED); panel.add(label);
    }
    private ModernButton button(String text,AppIcon.Kind icon,ModernButton.Variant variant,Command command,boolean pill) {
        ModernButton button=new ModernButton(text,icon,variant,pill); button.addActionListener(e->commands.accept(command)); return button;
    }
}
