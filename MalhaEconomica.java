package com.mycompany.sistemacoletaurbana;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Comparator;

public class MalhaEconomica {
    //unir os pontos de coleta em grupos e checar se eles já estão interligados
    private static class UnionFind {
        Map<Vertice, Vertice> pai = new HashMap<>();

        UnionFind(Collection<Vertice> vertices) {
            for (Vertice v : vertices) pai.put(v, v);
        }

        Vertice find(Vertice v) {
            if (pai.get(v) != v) {
                pai.put(v, find(pai.get(v))); // compressao de caminho
            }
            return pai.get(v);
        }

        void union(Vertice a, Vertice b) {
            pai.put(find(a), find(b));
        }
    }
    //gerador da rota otimizada
    public List<Aresta> kruskal(Grafo grafo) {
        List<Aresta> resultado = new ArrayList<>();
        List<Aresta> arestas = new ArrayList<>(grafo.getArestas());
        arestas.sort(Comparator.comparingDouble(Aresta::getPeso));

        UnionFind uf = new UnionFind(grafo.getVertices());

        for (Aresta a : arestas) {
            Vertice origem = a.getInicio();
            Vertice destino = a.getFim();
            if (uf.find(origem) != uf.find(destino)) {
                uf.union(origem, destino);
                resultado.add(a);
            }
        }
        return resultado;
    }

    // soma do peso de todas as arestas da malha 
    public double custoTotal(List<Aresta> mst) {
        double soma = 0;
        for (Aresta a : mst) soma += a.getPeso();
        return soma;
    }

    // soma da distancia "real" (em km) da malha, sem o ajuste de lixo 
    public double distanciaTotal(List<Aresta> mst) {
        double soma = 0;
        for (Aresta a : mst) soma += a.getDistancia();
        return soma;
    }
}
