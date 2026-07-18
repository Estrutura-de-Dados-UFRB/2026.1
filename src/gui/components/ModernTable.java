package gui.components;

import gui.UiTheme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Tabela com cabeçalho comercial, zebra, hover e seleção suave. */
public class ModernTable extends JTable {
    private int hoverRow = -1;
    public ModernTable(TableModel model) {
        super(model);
        setRowHeight(UiTheme.scale(32));
        setFillsViewportHeight(true);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setFont(UiTheme.FONT.deriveFont(12f));
        setForeground(UiTheme.TEXT);
        setSelectionBackground(new Color(218, 235, 250));
        setSelectionForeground(UiTheme.PRIMARY_DARK);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setPreferredSize(new Dimension(0, UiTheme.scale(38)));
        getTableHeader().setDefaultRenderer(new HeaderRenderer());
        setDefaultRenderer(Object.class, new BodyRenderer());
        addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) { updateHover(e.getPoint()); }
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) { hoverRow = -1; repaint(); }
        });
    }
    private void updateHover(Point point) { int row = rowAtPoint(point); if (row != hoverRow) { hoverRow = row; repaint(); } }
    private static final class HeaderRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c); l.setOpaque(true);
            l.setBackground(UiTheme.PRIMARY_DARK); l.setForeground(Color.WHITE); l.setFont(UiTheme.FONT_BOLD.deriveFont(12f));
            l.setBorder(BorderFactory.createEmptyBorder(0,10,0,8)); l.setHorizontalAlignment(c >= 2 ? CENTER : LEFT); return l;
        }
    }
    private final class BodyRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c) {
            JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c); l.setOpaque(true);
            l.setBackground(s ? getSelectionBackground() : r==hoverRow ? new Color(234,244,253) : r%2==0 ? Color.WHITE : UiTheme.SURFACE_ALT);
            l.setForeground(s ? getSelectionForeground() : UiTheme.TEXT); l.setBorder(BorderFactory.createEmptyBorder(0,10,0,8));
            l.setHorizontalAlignment(c >= 2 ? CENTER : LEFT); return l;
        }
    }
}
