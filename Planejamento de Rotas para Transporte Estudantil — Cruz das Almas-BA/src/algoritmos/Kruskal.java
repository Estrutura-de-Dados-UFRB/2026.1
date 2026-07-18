package algoritmos;

import model.Aresta;
import model.Grafo;
import model.Ponto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Kruskal {

    public List<Aresta> executar(Grafo grafo) {
        return executar(grafo, grafo != null ? grafo.getPontos() : null);
    }

    public List<Aresta> executar(Grafo grafo, Collection<Ponto> pontosEscolhidos) {
        if (grafo == null) {
            throw new IllegalArgumentException("Grafo nao pode ser nulo.");
        }
        if (pontosEscolhidos == null || pontosEscolhidos.isEmpty()) {
            throw new IllegalArgumentException("Selecione pelo menos um ponto.");
        }

        Set<Ponto> permitidos = new HashSet<>(pontosEscolhidos);
        List<Aresta> arestas = new ArrayList<>(grafo.getArestas());
        arestas.removeIf(aresta -> !permitidos.contains(aresta.getOrigem())
                || !permitidos.contains(aresta.getDestino()));
        arestas.sort(Comparator.comparingDouble(Aresta::getPeso));

        List<Ponto> pontos = new ArrayList<>(permitidos);
        Map<Ponto, Integer> indice = new HashMap<>();
        for (int i = 0; i < pontos.size(); i++) {
            indice.put(pontos.get(i), i);
        }

        UnionFind uf = new UnionFind(pontos.size());
        List<Aresta> mst = new ArrayList<>();
        for (Aresta aresta : arestas) {
            Integer u = indice.get(aresta.getOrigem());
            Integer v = indice.get(aresta.getDestino());
            if (u == null || v == null) {
                continue;
            }
            if (uf.find(u) != uf.find(v)) {
                uf.union(u, v);
                mst.add(aresta);
            }
            if (mst.size() == Math.max(0, pontos.size() - 1)) {
                break;
            }
        }
        return mst;
    }

    private static class UnionFind {
        private final int[] parent;
        private final int[] rank;

        private UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        private int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        private void union(int x, int y) {
            int rx = find(x);
            int ry = find(y);
            if (rx == ry) {
                return;
            }
            if (rank[rx] < rank[ry]) {
                parent[rx] = ry;
            } else if (rank[rx] > rank[ry]) {
                parent[ry] = rx;
            } else {
                parent[ry] = rx;
                rank[rx]++;
            }
        }
    }
}
