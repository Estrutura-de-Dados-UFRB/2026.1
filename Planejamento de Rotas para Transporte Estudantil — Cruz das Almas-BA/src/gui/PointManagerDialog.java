package gui;

import model.Ponto;
import model.TipoPonto;
import gui.components.AppIcon;
import gui.components.ModernButton;
import gui.components.ModernLegend;
import gui.components.ModernSearchField;
import gui.components.ModernTable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Janela de consulta e gerenciamento; não conhece serviços nem altera o modelo. */
final class PointManagerDialog extends JDialog {
    interface Actions { void create(); void edit(); void remove(); }
    private final Supplier<List<Ponto>> points;
    private final Consumer<Ponto> selection;
    private final Actions actions;
    private final JTabbedPane tabs=new JTabbedPane();
    private final Map<Component,JTable> tables=new LinkedHashMap<>();
    private final Map<Component,PontoTableModel> models=new LinkedHashMap<>();
    private final Map<Component,TipoPonto> types=new LinkedHashMap<>();

    PointManagerDialog(Frame owner,Supplier<List<Ponto>> points,Consumer<Ponto> selection,Actions actions) {
        super(owner,"Pontos cadastrados",false); this.points=points; this.selection=selection; this.actions=actions;
        setLayout(new BorderLayout(UiTheme.scale(10),UiTheme.scale(10))); getContentPane().setBackground(UiTheme.BACKGROUND);
        configureTabs(); add(createHeader(),BorderLayout.NORTH); add(tabs,BorderLayout.CENTER); add(createFooter(),BorderLayout.SOUTH);
        setSize(UiTheme.scale(900),UiTheme.scale(560)); setMinimumSize(UiTheme.scaledSize(720,440)); setLocationRelativeTo(owner);
    }
    void open(){refresh();setVisible(true);}

    private void configureTabs() {
        tabs.setFont(UiTheme.FONT_BOLD.deriveFont(13f));
        tabs.setBackground(UiTheme.SURFACE);
        tabs.setForeground(UiTheme.TEXT);
        addTab("Escolas",TipoPonto.ESCOLA); addTab("Universidades",TipoPonto.UNIVERSIDADE); addTab("Bairros",TipoPonto.BAIRRO);
        addTab("Embarques",TipoPonto.PONTO_EMBARQUE); addTab("Outros",TipoPonto.OUTRO);
    }
    private void addTab(String title,TipoPonto type) {
        PontoTableModel model=new PontoTableModel(); JTable table=new ModernTable(model); table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true); table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting()){Ponto p=selected(table,model);if(p!=null)selection.accept(p);}});
        table.addMouseListener(new java.awt.event.MouseAdapter(){@Override public void mouseClicked(java.awt.event.MouseEvent e){if(e.getClickCount()==2){selectCurrent();actions.edit();refresh();}}});
        JScrollPane scroll=new JScrollPane(table); scroll.setBorder(BorderFactory.createEmptyBorder()); scroll.getViewport().setBackground(UiTheme.SURFACE);
        tabs.addTab(title,scroll); tables.put(scroll,table); models.put(scroll,model); types.put(scroll,type);
    }
    private JPanel createHeader() {
        JPanel header=new JPanel(new BorderLayout(12,8)); header.setBackground(UiTheme.PRIMARY_DARK); header.setBorder(BorderFactory.createEmptyBorder(12,16,12,16));
        JLabel title=new JLabel("Gerenciamento de pontos"); title.setFont(UiTheme.SUBTITLE_FONT); title.setForeground(Color.WHITE);
        ModernSearchField search=new ModernSearchField("Buscar por nome, bairro ou tipo..."); search.setPreferredSize(UiTheme.scaledSize(320,38));
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){private void filter(){String q=search.getText().trim();for(JTable t:tables.values())if(t.getRowSorter() instanceof javax.swing.table.TableRowSorter<?> sorter)sorter.setRowFilter(q.isEmpty()?null:javax.swing.RowFilter.regexFilter("(?i)"+java.util.regex.Pattern.quote(q)));}
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e){filter();}@Override public void removeUpdate(javax.swing.event.DocumentEvent e){filter();}@Override public void changedUpdate(javax.swing.event.DocumentEvent e){filter();}});
        header.add(title,BorderLayout.WEST); header.add(search,BorderLayout.EAST); return header;
    }
    private JPanel createFooter() {
        JPanel footer=new JPanel(new BorderLayout(8,8)); footer.setOpaque(false); footer.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
        JPanel legend=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4)); legend.setOpaque(false);
        legend.add(dot("E","Escola",new Color(214,69,69))); legend.add(dot("U","Universidade",new Color(55,101,190)));
        legend.add(dot("B","Bairro",new Color(194,139,37))); legend.add(dot("P","Embarque",UiTheme.ACCENT));
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); buttons.setOpaque(false);
        JButton create=button("Novo",AppIcon.Kind.ADD,ModernButton.Variant.SUCCESS); create.addActionListener(e->{actions.create();refresh();});
        JButton edit=button("Editar",AppIcon.Kind.EDIT,ModernButton.Variant.PRIMARY); edit.addActionListener(e->{selectCurrent();actions.edit();refresh();});
        JButton remove=button("Remover",AppIcon.Kind.DELETE,ModernButton.Variant.DANGER); remove.addActionListener(e->{selectCurrent();actions.remove();refresh();});
        JButton close=button("Fechar",AppIcon.Kind.CLEAR,ModernButton.Variant.SECONDARY); close.addActionListener(e->dispose());
        buttons.add(create);buttons.add(edit);buttons.add(remove);buttons.add(close);footer.add(legend,BorderLayout.CENTER);footer.add(buttons,BorderLayout.SOUTH);return footer;
    }
    private JButton button(String text,AppIcon.Kind icon,ModernButton.Variant variant){return new ModernButton(text,icon,variant,true);}
    private JLabel dot(String letter,String text,Color color){JLabel l=new JLabel(text,new ModernLegend.DotIcon(color,letter),JLabel.LEFT);l.setFont(UiTheme.FONT.deriveFont(11.5f));l.setForeground(UiTheme.TEXT);return l;}
    private void refresh(){for(Map.Entry<Component,PontoTableModel> e:models.entrySet()){List<Ponto> filtered=new ArrayList<>();for(Ponto p:points.get())if(p.getTipo()==types.get(e.getKey()))filtered.add(p);e.getValue().setPontos(filtered);}}
    private void selectCurrent(){Component tab=tabs.getSelectedComponent();JTable table=tables.get(tab);PontoTableModel model=models.get(tab);Ponto point=table==null?null:selected(table,model);if(point!=null)selection.accept(point);}
    private Ponto selected(JTable table,PontoTableModel model){int row=table.getSelectedRow();return row<0?null:model.getPonto(table.convertRowIndexToModel(row));}
}
