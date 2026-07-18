package gui.components;

import gui.UiTheme;
import model.TipoPonto;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/** Legenda recolhível com grupos, filtros, hover e animação curta. */
public final class ModernLegend extends RoundedPanel {
    private static final int EXPANDED_HEIGHT = 252;
    private static final int COLLAPSED_HEIGHT = 50;
    private final JPanel body = new JPanel();
    private final ModernButton collapse = new ModernButton("", AppIcon.Kind.CHEVRON, ModernButton.Variant.GHOST, true);
    private final Map<TipoPonto, LegendToggle> toggles = new EnumMap<>(TipoPonto.class);
    private Consumer<TipoPonto> onToggle;
    private boolean expanded = true;
    private Timer animation;

    public ModernLegend() {
        setLayout(new BorderLayout(0, UiTheme.scale(7)));
        setBorder(BorderFactory.createEmptyBorder(10, 12, 14, 14));
        setPreferredSize(UiTheme.scaledSize(222, EXPANDED_HEIGHT));
        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false);
        JLabel title = new JLabel("Legenda do mapa"); title.setFont(UiTheme.FONT_BOLD.deriveFont(14f)); title.setForeground(UiTheme.PRIMARY_DARK);
        collapse.setPreferredSize(UiTheme.scaledSize(30, 30)); collapse.setToolTipText("Recolher legenda");
        collapse.addActionListener(e -> animateCollapse()); header.add(title, BorderLayout.WEST); header.add(collapse, BorderLayout.EAST);
        body.setOpaque(false); body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        group("PONTOS");
        toggle(TipoPonto.ESCOLA, "E", "Escolas", new Color(214,69,69));
        toggle(TipoPonto.UNIVERSIDADE, "U", "Universidades", new Color(55,101,190));
        toggle(TipoPonto.BAIRRO, "B", "Bairros", new Color(194,139,37));
        toggle(TipoPonto.PONTO_EMBARQUE, "P", "Pontos de embarque", UiTheme.ACCENT);
        body.add(Box.createVerticalStrut(UiTheme.scale(6)));
        JSeparator divider = new JSeparator(); divider.setForeground(UiTheme.BORDER); divider.setMaximumSize(new Dimension(Integer.MAX_VALUE,1)); body.add(divider);
        group("ROTA"); item("S", "Início da rota", new Color(40,120,220)); item("D", "Destino", UiTheme.DANGER);
        add(header, BorderLayout.NORTH); add(body, BorderLayout.CENTER);
    }

    public void setOnToggle(Consumer<TipoPonto> callback) { onToggle = callback; }
    public void setTypeVisible(TipoPonto type, boolean visible) { LegendToggle t=toggles.get(type); if(t!=null)t.setSelectedState(visible); }

    private void group(String text) {
        JLabel label=new JLabel(text); label.setFont(UiTheme.FONT_BOLD.deriveFont(10f)); label.setForeground(UiTheme.TEXT_MUTED);
        label.setBorder(BorderFactory.createEmptyBorder(5,4,3,0)); label.setAlignmentX(Component.LEFT_ALIGNMENT); body.add(label);
    }
    private void toggle(TipoPonto type,String letter,String text,Color color) {
        LegendToggle toggle=new LegendToggle(letter,text,color); toggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        toggle.addActionListener(e->{ toggle.setSelectedState(toggle.isSelected()); if(onToggle!=null)onToggle.accept(type); });
        toggle.setMaximumSize(new Dimension(Integer.MAX_VALUE,UiTheme.scale(30))); toggles.put(type,toggle); body.add(toggle);
    }
    private void item(String letter,String text,Color color) {
        JLabel label=new JLabel(text,new DotIcon(color,letter),JLabel.LEFT); label.setFont(UiTheme.FONT.deriveFont(12f)); label.setForeground(UiTheme.TEXT);
        label.setBorder(BorderFactory.createEmptyBorder(3,5,3,5)); label.setAlignmentX(Component.LEFT_ALIGNMENT); body.add(label);
    }
    private void animateCollapse() {
        expanded=!expanded; if(animation!=null)animation.stop(); body.setVisible(true);
        int start=getPreferredSize().height, end=UiTheme.scale(expanded?EXPANDED_HEIGHT:COLLAPSED_HEIGHT), steps=8;
        final int[] step = {0};
        animation=new Timer(18,null); animation.addActionListener(e->{
            int n=++step[0];
            float p=Math.min(1f,n/(float)steps); int h=Math.round(start+(end-start)*p); setPreferredSize(new Dimension(UiTheme.scale(222),h));
            revalidate(); if(p>=1f){animation.stop(); body.setVisible(expanded); collapse.setToolTipText(expanded?"Recolher legenda":"Expandir legenda");}
        }); animation.start();
    }

    private static final class LegendToggle extends JToggleButton {
        private final Color active; private final String letter; private boolean hover;
        LegendToggle(String letter,String text,Color active) {
            super(text,true); this.active=active; this.letter=letter; setIcon(new DotIcon(active,letter)); setHorizontalAlignment(LEFT);
            setFont(UiTheme.FONT.deriveFont(12f)); setForeground(UiTheme.TEXT); setBorder(BorderFactory.createEmptyBorder(2,5,2,5));
            setBorderPainted(false); setContentAreaFilled(false); setFocusPainted(false); setOpaque(false); setUI(new BasicToggleButtonUI());
            addMouseListener(new MouseAdapter(){@Override public void mouseEntered(MouseEvent e){hover=true;repaint();}@Override public void mouseExited(MouseEvent e){hover=false;repaint();}});
        }
        void setSelectedState(boolean selected){setSelected(selected);setIcon(new DotIcon(selected?active:UiTheme.BORDER_STRONG,letter));repaint();}
        @Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            if(isSelected()||hover){g2.setColor(isSelected()?new Color(234,244,253):UiTheme.SURFACE_ALT);g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);}g2.dispose();super.paintComponent(g);}
    }
    public static final class DotIcon implements Icon {
        private final Color color; private final String text;
        public DotIcon(Color color,String text){this.color=color;this.text=text;}
        @Override public int getIconWidth(){return UiTheme.scale(22);}@Override public int getIconHeight(){return UiTheme.scale(20);}
        @Override public void paintIcon(Component c,Graphics g,int x,int y){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int s=UiTheme.scale(16),oy=(getIconHeight()-s)/2;g2.setColor(new Color(18,57,94,22));g2.fillOval(x+2,y+oy+2,s,s);g2.setColor(color);g2.fillOval(x+1,y+oy,s,s);
            g2.setColor(Color.WHITE);g2.setStroke(new BasicStroke(1.4f));g2.drawOval(x+1,y+oy,s,s);g2.setFont(UiTheme.FONT_BOLD.deriveFont(9f));FontMetrics fm=g2.getFontMetrics();
            g2.drawString(text,x+1+(s-fm.stringWidth(text))/2,y+oy+(s+fm.getAscent()-fm.getDescent())/2);g2.dispose();}
    }
}
