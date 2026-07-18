package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Grafo nao direcionado com pesos de distancia, tempo e custo.
 */
public class Grafo {

    private final Map<Ponto, List<Aresta>> adjacencias = new LinkedHashMap<>();

    public void adicionarPonto(Ponto ponto) {
        if (ponto == null) {
            return;
        }
        adjacencias.putIfAbsent(ponto, new ArrayList<Aresta>());
    }

    public void adicionarPontos(Collection<Ponto> pontos) {
        if (pontos == null) {
            return;
        }
        for (Ponto ponto : pontos) {
            adicionarPonto(ponto);
        }
    }

    public void adicionarAresta(Ponto origem, Ponto destino, double distancia) {
        adicionarAresta(origem, destino, distancia, 40.0);
    }

    public void adicionarAresta(Ponto origem, Ponto destino, double distancia, double velocidadeMedia) {
        if (origem == null || destino == null || origem.equals(destino)) {
            return;
        }
        adicionarPonto(origem);
        adicionarPonto(destino);
        removerAresta(origem, destino);
        Aresta ida = new Aresta(origem, destino, distancia, velocidadeMedia);
        Aresta volta = new Aresta(destino, origem, distancia, velocidadeMedia);
        adjacencias.get(origem).add(ida);
        adjacencias.get(destino).add(volta);
    }

    public void removerAresta(Ponto origem, Ponto destino) {
        if (origem == null || destino == null) {
            return;
        }
        List<Aresta> vizinhosOrigem = adjacencias.get(origem);
        if (vizinhosOrigem != null) {
            vizinhosOrigem.removeIf(aresta -> aresta.getDestino().equals(destino));
        }
        List<Aresta> vizinhosDestino = adjacencias.get(destino);
        if (vizinhosDestino != null) {
            vizinhosDestino.removeIf(aresta -> aresta.getDestino().equals(origem));
        }
    }

    public void removerPonto(Ponto ponto) {
        if (ponto == null) {
            return;
        }
        adjacencias.remove(ponto);
        for (List<Aresta> arestas : adjacencias.values()) {
            arestas.removeIf(aresta -> aresta.getDestino().equals(ponto));
        }
    }

    public List<Aresta> getVizinhos(Ponto ponto) {
        return adjacencias.getOrDefault(ponto, new ArrayList<Aresta>());
    }

    public Set<Ponto> getPontos() {
        return Collections.unmodifiableSet(adjacencias.keySet());
    }

    public List<Aresta> getArestas() {
        List<Aresta> arestas = new ArrayList<>();
        Set<String> vistas = new LinkedHashSet<>();
        for (List<Aresta> lista : adjacencias.values()) {
            for (Aresta aresta : lista) {
                String chave = chaveAresta(aresta.getOrigem(), aresta.getDestino());
                if (vistas.add(chave)) {
                    arestas.add(aresta);
                }
            }
        }
        return arestas;
    }

    public Aresta obterAresta(Ponto origem, Ponto destino) {
        for (Aresta aresta : getVizinhos(origem)) {
            if (aresta.getDestino().equals(destino)) {
                return aresta;
            }
        }
        return null;
    }

    public double obterDistanciaDireta(Ponto origem, Ponto destino) {
        Aresta aresta = obterAresta(origem, destino);
        return aresta != null ? aresta.getDistancia() : Double.MAX_VALUE;
    }

    public double obterTempoDireto(Ponto origem, Ponto destino) {
        Aresta aresta = obterAresta(origem, destino);
        return aresta != null ? aresta.getTempoEstimado() : Double.MAX_VALUE;
    }

    public Map<Ponto, List<Aresta>> getAdjacencias() {
        return Collections.unmodifiableMap(adjacencias);
    }

    public void limpar() {
        adjacencias.clear();
    }

    private String chaveAresta(Ponto origem, Ponto destino) {
        String a = origem.getNome();
        String b = destino.getNome();
        return a.compareToIgnoreCase(b) < 0 ? a + "|" + b : b + "|" + a;
    }
}
