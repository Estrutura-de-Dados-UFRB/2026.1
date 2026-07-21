package com.mycompany.sistemacoletaurbana;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Resultado de uma execução do Dijkstra a partir de um vértice de origem:
 * a menor distância até cada vértice alcançável, e o predecessor de cada um
 * no caminho mínimo (usado para reconstruir a rota completa).
 */

public class ResultadoDijkstra {

    private final Map<Vertice, Double> distancias;
    private final Map<Vertice, Vertice> predecessores;
    private final Vertice origem;

    public ResultadoDijkstra(Vertice origem, Map<Vertice, Double> distancias, Map<Vertice, Vertice> predecessores) {
        this.origem = origem;
        this.distancias = distancias;
        this.predecessores = predecessores;
    }
    
    
    /** Custo do menor caminho da origem até o destino, ou infinito se inalcançável. */
    public double getDistancia(Vertice destino) {
        return distancias.getOrDefault(destino, Double.POSITIVE_INFINITY);
    }

    public boolean isAlcancavel(Vertice destino) {
        return distancias.containsKey(destino) && distancias.get(destino) < Double.POSITIVE_INFINITY;
    }
    
    /** Reconstrói a sequência de vértices do caminho mínimo até o destino. */
    public List<Vertice> reconstruirCaminho(Vertice destino) {
        List<Vertice> caminho = new ArrayList<>();

        if (!isAlcancavel(destino) && destino != origem) {
            return caminho;
        }

        Vertice atual = destino;
        while (atual != null) {
            caminho.add(atual);
            if (atual == origem) break;
            atual = predecessores.get(atual);
        }
        Collections.reverse(caminho);
        return caminho;
    }

    public Map<Vertice, Double> getTodasDistancias() {
        return distancias;
    }
}

