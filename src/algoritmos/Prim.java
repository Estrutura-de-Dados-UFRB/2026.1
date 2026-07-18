package algoritmos;

import model.Aresta;
import model.Grafo;
import model.Ponto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Prim {

    public List<Aresta> executar(Grafo grafo, Ponto inicio) {
        if (grafo == null || inicio == null) {
            throw new IllegalArgumentException("Grafo e inicio sao obrigatorios.");
        }

        List<Aresta> arvore = new ArrayList<>();
        Set<Ponto> visitados = new HashSet<>();
        PriorityQueue<Aresta> fila = new PriorityQueue<>((a, b) -> Double.compare(a.getPeso(), b.getPeso()));

        visitados.add(inicio);
        fila.addAll(grafo.getVizinhos(inicio));

        while (!fila.isEmpty() && visitados.size() < grafo.getPontos().size()) {
            Aresta aresta = fila.poll();
            if (visitados.contains(aresta.getDestino())) {
                continue;
            }
            arvore.add(aresta);
            visitados.add(aresta.getDestino());
            for (Aresta vizinha : grafo.getVizinhos(aresta.getDestino())) {
                if (!visitados.contains(vizinha.getDestino())) {
                    fila.add(vizinha);
                }
            }
        }

        return arvore;
    }
}
