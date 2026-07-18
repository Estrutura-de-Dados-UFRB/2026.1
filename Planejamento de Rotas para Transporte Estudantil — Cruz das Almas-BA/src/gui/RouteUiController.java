package gui;

import model.Grafo;
import model.Ponto;
import model.Rota;
import services.RouteService;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Orquestra interações dos algoritmos sem implementar ou alterar qualquer algoritmo. */
final class RouteUiController {
    record Result(Rota route,RouteOperation operation,Ponto origin,Ponto destination,Color color,String message) { }
    private final Component owner; private final Grafo graph; private final RouteService service;
    private final JProgressBar progress; private final JLabel status; private final Consumer<Result> resultConsumer;

    RouteUiController(Component owner,Grafo graph,RouteService service,JProgressBar progress,JLabel status,Consumer<Result> resultConsumer){
        this.owner=owner;this.graph=graph;this.service=service;this.progress=progress;this.status=status;this.resultConsumer=resultConsumer;
    }
    void dijkstra(){Ponto a=RouteDialogs.choosePoint(owner,graph,"Selecionar origem");if(a==null)return;Ponto b=RouteDialogs.choosePoint(owner,graph,"Selecionar destino");if(b==null)return;
        run(()->service.executarDijkstra(a,b),RouteOperation.DIJKSTRA,a,b,Color.BLUE,"Dijkstra calculado");}
    void prim(){List<Ponto> p=points("Selecionar pontos para Prim");if(p==null)return;Ponto a=start("Selecionar ponto inicial do Prim",p);if(a==null)return;
        run(()->{service.executarPrim(a,p);return service.getRotaAtual();},RouteOperation.PRIM,a,null,new Color(30,150,60),"Prim calculado para "+p.size()+" pontos");}
    void kruskal(){List<Ponto> p=points("Selecionar pontos para Kruskal");if(p==null)return;Ponto a=start("Selecionar ponto inicial do Kruskal",p);if(a==null)return;
        List<Ponto> ordered=new ArrayList<>();ordered.add(a);for(Ponto point:p)if(!point.equals(a))ordered.add(point);
        run(()->{service.executarKruskal(ordered);return service.getRotaAtual();},RouteOperation.KRUSKAL,a,null,new Color(210,140,40),"Kruskal calculado a partir de "+a.getNome());}
    void bfs(){traversal("Selecionar pontos para BFS","Selecionar ponto inicial do BFS",RouteOperation.BFS,Color.CYAN.darker());}
    void dfs(){traversal("Selecionar pontos para DFS","Selecionar ponto inicial do DFS",RouteOperation.DFS,Color.PINK.darker());}
    void greedy(){traversal("Selecionar pontos para Guloso","Selecionar ponto inicial do Guloso",RouteOperation.GREEDY,new Color(140,60,170));}
    void tsp(){traversal("Selecionar pontos para TSP","Selecionar ponto inicial do TSP",RouteOperation.TSP,Color.RED);}

    private void traversal(String title,String startTitle,RouteOperation operation,Color color){List<Ponto> p=points(title);if(p==null)return;Ponto a=start(startTitle,p);if(a==null)return;
        run(()->switch(operation){case BFS->service.executarBFS(a,p);case DFS->service.executarDFS(a,p);case GREEDY->service.executarGuloso(a,p);case TSP->service.executarTSP(a,p);default->null;},
                operation,a,null,color,label(operation)+" calculado para "+p.size()+" pontos");}
    private List<Ponto> points(String title){return RouteDialogs.chooseOrAll(owner,graph,title,2);}
    private Ponto start(String title,List<Ponto> points){return RouteDialogs.chooseFrom(owner,title,points);}
    private String label(RouteOperation operation){return switch(operation){case BFS->"BFS";case DFS->"DFS";case GREEDY->"Guloso";case TSP->"Caixeiro viajante";default->operation.name();};}
    private void run(java.util.concurrent.Callable<Rota> task,RouteOperation op,Ponto origin,Ponto destination,Color color,String message){
        progress.setIndeterminate(true);status.setText("Processando...");
        new SwingWorker<Rota,Void>(){@Override protected Rota doInBackground() throws Exception{return task.call();}
            @Override protected void done(){progress.setIndeterminate(false);try{Rota route=get();progress.setValue(100);resultConsumer.accept(new Result(route,op,origin,destination,color,message));}
                catch(Exception ex){JOptionPaneHelper.error(owner,"Erro ao calcular rota",ex);}}}.execute();
    }
}
