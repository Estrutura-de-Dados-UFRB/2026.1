package algoritmos;

import model.Grafo;
import model.Ponto;
import model.Rota;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DFS {

    public Rota executar(Grafo grafo, Ponto origem) {
        if (grafo == null || origem == null) {
            throw new IllegalArgumentException("Grafo e origem sao obrigatorios.");
        }
        List<Ponto> ordem = new ArrayList<>();
        Set<Ponto> visitados = new HashSet<>();
        visitar(grafo, origem, visitados, ordem);
        double distancia = AlgoritmoUtils.calcularDistancia(grafo, ordem);
        return new Rota(ordem, null, origem, ordem.isEmpty() ? null : ordem.get(ordem.size() - 1),
                "DFS", distancia, AlgoritmoUtils.calcularTempo(distancia),
                distancia, AlgoritmoUtils.calcularCusto(distancia));
    }

    private void visitar(Grafo grafo, Ponto atual, Set<Ponto> visitados, List<Ponto> ordem) {
        if (!visitados.add(atual)) {
            return;
        }
        ordem.add(atual);
        for (var aresta : grafo.getVizinhos(atual)) {
            visitar(grafo, aresta.getDestino(), visitados, ordem);
        }
    }
}
