package com.mycompany.sistemacoletaurbana;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Implementação do algoritmo de Dijkstra: encontra o caminho de menor custo
 * de um vértice de origem até todos os demais vértices do grafo.
 *
 * Complexidade: O((V + E) log V) usando uma fila de prioridade (min-heap).
 *
 * Pré-condição: pesos das arestas não-negativos (garantido em Aresta).
 */

public class Dijkstra {

    public static ResultadoDijkstra calcular(Grafo grafo, String nomeOrigem) {
        Vertice origem = grafo.buscarVertice(nomeOrigem);
        
        if (origem == null) {
            throw new IllegalArgumentException("Vértice de origem não existe no grafo: " + nomeOrigem);
        }

        Map<Vertice, Double> distancias = new HashMap<>();
        Map<Vertice, Vertice> predecessores = new HashMap<>();
        Set<Vertice> visitados = new HashSet<>();

        for (Vertice v : grafo.getVertices()) {
            distancias.put(v, Double.POSITIVE_INFINITY);
        }
        distancias.put(origem, 0.0);

        PriorityQueue<Vertice> fila = new PriorityQueue<>((a, b) -> Double.compare(distancias.get(a), distancias.get(b)));
        fila.add(origem);

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();

            if (visitados.contains(atual)) continue;
            visitados.add(atual);

            for (Aresta aresta : atual.getArestasSaindo()) {
                Vertice vizinho = aresta.getFim(); 
                
                if (visitados.contains(vizinho)) continue;

                double novaDistancia = distancias.get(atual) + aresta.getPeso();
                
                if (novaDistancia < distancias.get(vizinho)) {
                    fila.remove(vizinho); 
                    
                    distancias.put(vizinho, novaDistancia);
                    predecessores.put(vizinho, atual);
                    
                    fila.add(vizinho);
                }
            }
        }

        return new ResultadoDijkstra(origem, distancias, predecessores);
    }

    public static double custoMinimo(Grafo grafo, String origem, String destino) {
        Vertice dest = grafo.buscarVertice(destino);
        if (dest == null) return Double.POSITIVE_INFINITY;
        
        return calcular(grafo, origem).getDistancia(dest);
    }
}