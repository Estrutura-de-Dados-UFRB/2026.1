package algoritmos;

import model.Grafo;
import model.Ponto;
import model.Rota;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class BFS {

    public Rota executar(Grafo grafo, Ponto origem) {
        if (grafo == null || origem == null) {
            throw new IllegalArgumentException("Grafo e origem sao obrigatorios.");
        }

        List<Ponto> ordem = new ArrayList<>();
        Set<Ponto> visitados = new HashSet<>();
        Queue<Ponto> fila = new ArrayDeque<>();

        fila.add(origem);
        visitados.add(origem);

        while (!fila.isEmpty()) {
            Ponto atual = fila.poll();
            ordem.add(atual);
            for (var aresta : grafo.getVizinhos(atual)) {
                Ponto vizinho = aresta.getDestino();
                if (visitados.add(vizinho)) {
                    fila.add(vizinho);
                }
            }
        }

        double distancia = AlgoritmoUtils.calcularDistancia(grafo, ordem);
        return new Rota(ordem, null, origem, ordem.isEmpty() ? null : ordem.get(ordem.size() - 1),
                "BFS", distancia, AlgoritmoUtils.calcularTempo(distancia),
                distancia, AlgoritmoUtils.calcularCusto(distancia));
    }
}
