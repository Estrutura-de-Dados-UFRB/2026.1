package algoritmos;

import model.Aresta;
import model.Grafo;
import model.Ponto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Dijkstra {

    public static Map<Ponto, Double> calcular(Grafo grafo, Ponto origem) {
        Resultado resultado = executar(grafo, origem);
        return resultado.distancias;
    }

    public static List<Ponto> encontrarCaminho(Grafo grafo, Ponto origem, Ponto destino) {
        Resultado resultado = executar(grafo, origem);
        return reconstruirCaminho(resultado, origem, destino);
    }

    private static Resultado executar(Grafo grafo, Ponto origem) {
        if (grafo == null || origem == null) {
            throw new IllegalArgumentException("Grafo e origem sao obrigatorios.");
        }

        Map<Ponto, Double> distancias = new LinkedHashMap<>();
        Map<Ponto, Ponto> anteriores = new HashMap<>();
        for (Ponto ponto : grafo.getPontos()) {
            distancias.put(ponto, Double.MAX_VALUE);
        }
        if (!distancias.containsKey(origem)) {
            distancias.put(origem, Double.MAX_VALUE);
        }

        PriorityQueue<No> fila = new PriorityQueue<>();
        distancias.put(origem, 0.0);
        fila.add(new No(origem, 0.0));

        while (!fila.isEmpty()) {
            No atual = fila.poll();
            if (atual.distancia > distancias.getOrDefault(atual.ponto, Double.MAX_VALUE)) {
                continue;
            }

            for (Aresta aresta : grafo.getVizinhos(atual.ponto)) {
                Ponto vizinho = aresta.getDestino();
                double novaDistancia = atual.distancia + aresta.getDistancia();
                if (novaDistancia < distancias.getOrDefault(vizinho, Double.MAX_VALUE)) {
                    distancias.put(vizinho, novaDistancia);
                    anteriores.put(vizinho, atual.ponto);
                    fila.add(new No(vizinho, novaDistancia));
                }
            }
        }

        return new Resultado(distancias, anteriores);
    }

    private static List<Ponto> reconstruirCaminho(Resultado resultado, Ponto origem, Ponto destino) {
        if (origem == null || destino == null) {
            throw new IllegalArgumentException("Origem e destino sao obrigatorios.");
        }
        if (origem.equals(destino)) {
            List<Ponto> caminho = new ArrayList<>();
            caminho.add(origem);
            return caminho;
        }

        Double distanciaDestino = resultado.distancias.get(destino);
        if (distanciaDestino == null || distanciaDestino == Double.MAX_VALUE) {
            return new ArrayList<>();
        }

        List<Ponto> caminho = new ArrayList<>();
        Ponto atual = destino;
        while (atual != null) {
            caminho.add(atual);
            if (atual.equals(origem)) {
                Collections.reverse(caminho);
                return caminho;
            }
            atual = resultado.anteriores.get(atual);
        }
        return new ArrayList<>();
    }

    private static class Resultado {
        private final Map<Ponto, Double> distancias;
        private final Map<Ponto, Ponto> anteriores;

        private Resultado(Map<Ponto, Double> distancias, Map<Ponto, Ponto> anteriores) {
            this.distancias = distancias;
            this.anteriores = anteriores;
        }
    }

    private static class No implements Comparable<No> {
        private final Ponto ponto;
        private final double distancia;

        private No(Ponto ponto, double distancia) {
            this.ponto = ponto;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(No outro) {
            return Double.compare(this.distancia, outro.distancia);
        }
    }
}
