package gui;

import model.Grafo;
import model.Ponto;
import model.Rota;
import gui.components.AppIcon;
import gui.components.ModernButton;
import gui.components.RoundedPanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Monta o shell responsivo da janela sem conhecer ações ou serviços. */
final class MainContentView {
    private final MapPanel map; private final StatisticsPanel statistics; private final BottomRoutePanel route;
    private final JLabel status; private final JProgressBar progress; private final JTabbedPane tabs;
    private final JLabel distance=new JLabel("Distância percorrida: 0.00 km");
    private final JLabel students=new JLabel("Alunos: 0");
    private JPanel compact; private JPanel side; private boolean expanded;

    MainContentView(MapPanel map,StatisticsPanel statistics,BottomRoutePanel route,JLabel status,JProgressBar progress,JTabbedPane tabs){
        this.map=map;this.statistics=statistics;this.route=route;this.status=status;this.progress=progress;this.tabs=tabs;
    }
    JPanel build(AppHeader header){
        JPanel root=new JPanel(new BorderLayout(UiTheme.scale(8),UiTheme.scale(8)));root.setBackground(UiTheme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(UiTheme.scale(8),UiTheme.scale(12),UiTheme.scale(8),UiTheme.scale(12)));
        root.add(header,BorderLayout.NORTH);root.add(createTabs(),BorderLayout.CENTER);root.add(createStatus(),BorderLayout.SOUTH);return root;
    }
    void updateSummary(Grafo graph,Rota current){
        if(current!=null){distance.setText(String.format("Distância percorrida: %.2f km",current.getDistanciaTotal()));students.setText("Alunos: "+current.calcularTotalAlunos());}
        else{int total=graph.getPontos().stream().mapToInt(Ponto::getQuantidadeAlunos).sum();distance.setText("Distância percorrida: 0.00 km");students.setText("Alunos: "+total);}
    }
    private JTabbedPane createTabs(){
        tabs.setFont(UiTheme.FONT_BOLD.deriveFont(14f));tabs.setBackground(UiTheme.SURFACE);tabs.setForeground(UiTheme.TEXT);tabs.setBorder(BorderFactory.createEmptyBorder());
        tabs.addTab("  Mapa  ",createMapScreen());tabs.setToolTipTextAt(0,"Mapa, estatísticas, percurso e animação do veículo");return tabs;
    }
    private JPanel createMapScreen(){
        JPanel screen=new JPanel(new BorderLayout());screen.setBackground(UiTheme.BACKGROUND);screen.setBorder(BorderFactory.createEmptyBorder(8,0,0,0));
        compact=createCompact();JLayeredPane overlay=new JLayeredPane();overlay.setLayout(new GridBagLayout());
        GridBagConstraints fill=new GridBagConstraints();fill.gridx=0;fill.gridy=0;fill.weightx=1;fill.weighty=1;fill.fill=GridBagConstraints.BOTH;overlay.add(map,fill);overlay.setLayer(map,JLayeredPane.DEFAULT_LAYER);
        GridBagConstraints card=new GridBagConstraints();card.gridx=0;card.gridy=0;card.weightx=1;card.weighty=1;card.anchor=GridBagConstraints.NORTHEAST;card.insets=UiTheme.scaledInsets(14,0,0,14);
        overlay.add(compact,card);overlay.setLayer(compact,JLayeredPane.PALETTE_LAYER);side=createSide();side.setVisible(false);screen.add(overlay,BorderLayout.CENTER);screen.add(side,BorderLayout.EAST);return screen;
    }
    private JPanel createCompact(){
        RoundedPanel panel=new RoundedPanel();panel.setLayout(new BorderLayout(12,0));panel.setBorder(BorderFactory.createEmptyBorder(10,14,13,16));
        JPanel metrics=new JPanel(new FlowLayout(FlowLayout.LEFT,14,0));metrics.setOpaque(false);
        distance.setFont(UiTheme.FONT_BOLD.deriveFont(13f));students.setFont(UiTheme.FONT_BOLD.deriveFont(13f));distance.setForeground(UiTheme.PRIMARY_DARK);students.setForeground(UiTheme.PRIMARY_DARK);
        metrics.add(distance);metrics.add(students);JButton detail=new ModernButton("Detalhes", AppIcon.Kind.CHEVRON,ModernButton.Variant.GHOST);detail.addActionListener(e->toggle());
        panel.add(metrics,BorderLayout.CENTER);panel.add(detail,BorderLayout.EAST);return panel;
    }
    private JPanel createSide(){
        RoundedPanel panel=new RoundedPanel();
        panel.setLayout(new BorderLayout(0,UiTheme.scale(6)));
        panel.setPreferredSize(new Dimension(UiTheme.scale(400),0));
        panel.setMinimumSize(new Dimension(UiTheme.scale(360),0));
        panel.setBorder(BorderFactory.createEmptyBorder(8,9,11,11));
        JPanel top=new JPanel(new BorderLayout(8,0));top.setOpaque(false);
        JLabel title=new JLabel("Painel da rota");title.setFont(UiTheme.FONT_BOLD.deriveFont(13f));title.setForeground(UiTheme.PRIMARY_DARK);
        JButton collapse=new ModernButton("Recolher",AppIcon.Kind.CHEVRON,ModernButton.Variant.GHOST);collapse.addActionListener(e->toggle());
        top.add(title,BorderLayout.WEST);top.add(collapse,BorderLayout.EAST);
        statistics.setMinimumSize(new Dimension(0,UiTheme.scale(72)));
        route.setMinimumSize(new Dimension(0,UiTheme.scale(190)));
        JSplitPane split=new JSplitPane(JSplitPane.VERTICAL_SPLIT,statistics,route);
        split.setBorder(null);
        split.setDividerSize(UiTheme.scale(8));
        split.setContinuousLayout(true);
        split.setOneTouchExpandable(true);
        split.setResizeWeight(0.38);
        split.setToolTipText("Arraste o divisor; duplo clique para ampliar ou restaurar o resumo");
        configureSummaryExpansion(split);

        panel.add(top,BorderLayout.NORTH);panel.add(split,BorderLayout.CENTER);return panel;
    }
    private void configureSummaryExpansion(JSplitPane split){
        boolean[] initialized={false};
        boolean[] summaryExpanded={false};
        int[] regularLocation={-1};
        split.addHierarchyListener(e->{
            if(!initialized[0]&&split.isShowing()){
                initialized[0]=true;
                SwingUtilities.invokeLater(()->split.setDividerLocation(0.38));
            }
        });
        if(split.getUI() instanceof BasicSplitPaneUI ui){
            ui.getDivider().addMouseListener(new MouseAdapter(){
                @Override public void mouseClicked(MouseEvent event){
                    if(event.getClickCount()!=2||!SwingUtilities.isLeftMouseButton(event))return;
                    if(!summaryExpanded[0]){
                        regularLocation[0]=split.getDividerLocation();
                        split.setDividerLocation(UiTheme.scale(72));
                    }else{
                        int location=regularLocation[0]>0?regularLocation[0]:(int)(split.getHeight()*0.38);
                        split.setDividerLocation(location);
                    }
                    summaryExpanded[0]=!summaryExpanded[0];
                }
            });
        }
    }
    private JPanel createStatus(){
        JPanel bar=new JPanel(new BorderLayout(8,8));bar.setBackground(UiTheme.SURFACE);bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,0,0,0,UiTheme.BORDER_STRONG),BorderFactory.createEmptyBorder(8,12,5,12)));
        status.setForeground(UiTheme.TEXT_MUTED);progress.setIndeterminate(false);progress.setPreferredSize(UiTheme.scaledSize(190,12));progress.setBorderPainted(false);
        bar.add(status,BorderLayout.CENTER);bar.add(progress,BorderLayout.EAST);return bar;
    }
    private void toggle(){expanded=!expanded;compact.setVisible(!expanded);side.setVisible(expanded);compact.getParent().revalidate();compact.getParent().repaint();}
}
