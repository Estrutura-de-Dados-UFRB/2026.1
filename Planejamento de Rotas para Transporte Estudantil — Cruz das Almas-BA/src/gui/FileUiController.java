package gui;

import model.Grafo;
import model.Ponto;
import model.Rota;
import services.ProjetoService;
import util.CSVExporter;
import util.PDFExporter;
import util.TXTExporter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Centraliza apenas diálogos e exportações de arquivo da camada de interface. */
final class FileUiController {
    private final Component owner; private final Grafo graph; private final ProjetoService projects;
    private final Supplier<Rota> route; private final Supplier<String> algorithm; private final Consumer<Grafo> graphLoader; private final Consumer<String> status;
    FileUiController(Component owner,Grafo graph,ProjetoService projects,Supplier<Rota> route,Supplier<String> algorithm,
                     Consumer<Grafo> graphLoader,Consumer<String> status){this.owner=owner;this.graph=graph;this.projects=projects;this.route=route;this.algorithm=algorithm;this.graphLoader=graphLoader;this.status=status;}
    void open(){JFileChooser c=chooser("Projeto de rotas (*.rotas)","rotas","txt");if(c.showOpenDialog(owner)!=JFileChooser.APPROVE_OPTION)return;
        try{graphLoader.accept(projects.carregar(c.getSelectedFile()));status.accept("Projeto carregado: "+c.getSelectedFile().getName());}catch(IOException ex){error("Erro ao abrir projeto",ex);}}
    void save(){JFileChooser c=chooser("Projeto de rotas (*.rotas)","rotas","txt");if(c.showSaveDialog(owner)!=JFileChooser.APPROVE_OPTION)return;File f=extension(c.getSelectedFile(),"rotas");
        try{projects.salvar(graph,f);status.accept("Projeto salvo: "+f.getName());}catch(IOException ex){error("Erro ao salvar projeto",ex);}}
    void csv(){export("CSV (*.csv)","csv",(r,f)->CSVExporter.exportarResultado(r,algorithm.get(),f));}
    void pdf(){export("PDF (*.pdf)","pdf",(r,f)->PDFExporter.exportarRelatorio(r,algorithm.get(),new ArrayList<>(graph.getPontos()),f));}
    void txt(){export("TXT (*.txt)","txt",(r,f)->TXTExporter.exportar(r,algorithm.get(),new ArrayList<>(graph.getPontos()),f));}
    private void export(String description,String ext,Exporter exporter){Rota r=route.get();if(r==null){JOptionPane.showMessageDialog(owner,"Calcule uma rota antes de exportar.");return;}
        JFileChooser c=chooser(description,ext);if(c.showSaveDialog(owner)!=JFileChooser.APPROVE_OPTION)return;File f=extension(c.getSelectedFile(),ext);
        try{exporter.write(r,f);status.accept(ext.toUpperCase()+" exportado: "+f.getName());}catch(IOException ex){error("Erro ao exportar "+ext.toUpperCase(),ex);}}
    private JFileChooser chooser(String description,String...extensions){JFileChooser c=new JFileChooser();c.setFileFilter(new FileNameExtensionFilter(description,extensions));return c;}
    private File extension(File file,String ext){return file.getName().toLowerCase().endsWith("."+ext)?file:new File(file.getAbsolutePath()+"."+ext);}
    private void error(String context,Exception ex){JOptionPaneHelper.error(owner,context,ex);}
    @FunctionalInterface private interface Exporter{void write(Rota route,File file)throws IOException;}
}
