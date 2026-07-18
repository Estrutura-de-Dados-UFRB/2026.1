package algoritmos;

import model.Aresta;
import model.Grafo;
import model.Ponto;
import util.GeoUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public final class AlgoritmoUtils {

    private AlgoritmoUtils() {
    }

    public static List<Ponto> reconstruirCaminho(Map<Ponto, Ponto> anteriores, Ponto origem, Ponto destino) {
        List<Ponto> caminho = new ArrayList<>();
        Ponto atual = destino;
        while (atual != null) {
            caminho.add(atual);
            if (atual.equals(origem)) {
                Collections.reverse(caminho);
                return caminho;
            }
            atual = anteriores.get(atual);
        }
        return new ArrayList<Ponto>();
    }

    public static Aresta melhorAresta(Grafo grafo, Ponto origem, Ponto destino) {
        return grafo.obterAresta(origem, destino);
    }

    public static double calcularDistancia(Grafo grafo, List<Ponto> percurso) {
        if (percurso == null || percurso.size() < 2) {
            return 0.0;
        }
        double total = 0.0;
        for (int i = 1; i < percurso.size(); i++) {
            double dist = grafo.obterDistanciaDireta(percurso.get(i - 1), percurso.get(i));
            if (dist == Double.MAX_VALUE) {
                dist = GeoUtils.haversine(
                        percurso.get(i - 1).getLatitude(),
                        percurso.get(i - 1).getLongitude(),
                        percurso.get(i).getLatitude(),
                        percurso.get(i).getLongitude());
            }
            total += dist;
        }
        return total;
    }

    public static double calcularTempo(double distanciaKm) {
        return GeoUtils.estimarTempo(distanciaKm, 40.0);
    }

    public static double calcularCusto(double distanciaKm) {
        return distanciaKm * 2.5;
    }

    public static double calcularPeso(List<Aresta> arestas) {
        double peso = 0.0;
        if (arestas != null) {
            for (Aresta aresta : arestas) {
                peso += aresta.getPeso();
            }
        }
        return peso;
    }

    public static Map<Ponto, Double> mapaDeDistancias(Grafo grafo) {
        Map<Ponto, Double> distancias = new HashMap<>();
        for (Ponto ponto : grafo.getPontos()) {
            distancias.put(ponto, Double.MAX_VALUE);
        }
        return distancias;
    }
}
