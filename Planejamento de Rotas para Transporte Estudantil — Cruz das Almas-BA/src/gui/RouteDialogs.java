package gui;

import model.Grafo;
import model.Ponto;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

/** Diálogos de seleção compartilhados pelos algoritmos. */
final class RouteDialogs {
    private RouteDialogs() { }
    static Ponto choosePoint(Component owner,Grafo graph,String title){return chooseFrom(owner,title,new ArrayList<>(graph.getPontos()));}
    static List<Ponto> chooseOrAll(Component owner,Grafo graph,String title,int minimum){
        List<Ponto> all=new ArrayList<>(graph.getPontos());
        if(all.size()<minimum){JOptionPane.showMessageDialog(owner,"Cadastre pelo menos "+minimum+" pontos.");return null;}
        Object[] options={"Todos","Escolher pontos","Cancelar"};
        int choice=JOptionPane.showOptionDialog(owner,"Quais pontos deseja usar neste algoritmo?",title,JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
        if(choice==0)return all;if(choice!=1)return null;
        List<Ponto> selected=chooseMany(owner,title,all);
        if(selected!=null&&selected.size()<minimum){JOptionPane.showMessageDialog(owner,"Selecione pelo menos "+minimum+" pontos.");return null;}
        return selected;
    }
    static Ponto chooseFrom(Component owner,String title,List<Ponto> points){
        if(points==null||points.isEmpty()){JOptionPane.showMessageDialog(owner,"Não há pontos cadastrados.");return null;}
        Object selected=JOptionPane.showInputDialog(owner,title,title,JOptionPane.QUESTION_MESSAGE,null,points.toArray(),points.get(0));
        return selected instanceof Ponto point?point:null;
    }
    private static List<Ponto> chooseMany(Component owner,String title,List<Ponto> points){
        JList<Ponto> list=new JList<>(points.toArray(new Ponto[0]));list.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        int[] indices=new int[points.size()];for(int i=0;i<indices.length;i++)indices[i]=i;list.setSelectedIndices(indices);
        JScrollPane scroll=new JScrollPane(list);scroll.setPreferredSize(UiTheme.scaledSize(480,340));scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        gui.components.ModernCard panel=new gui.components.ModernCard("Pontos da rota");
        panel.content().add(new JLabel("Selecione os pontos que o algoritmo deve conectar:"),BorderLayout.NORTH);
        panel.content().add(scroll,BorderLayout.CENTER);
        int confirm=JOptionPane.showConfirmDialog(owner,panel,title,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        return confirm==JOptionPane.OK_OPTION?list.getSelectedValuesList():null;
    }
}
