package com.mycompany.sistemacoletaurbana;

import java.util.Set;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.LinkedHashSet;
import java.util.ArrayList;

public class ConectividadeService {

    // percorre o grafo a partir de um vertice
    public Set<Vertice> bfs(Vertice inicio) {
        Set<Vertice> visitados = new LinkedHashSet<>();
        Queue<Vertice> fila = new LinkedList<>();
        fila.add(inicio);
        visitados.add(inicio);

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();
            for (Aresta aresta : atual.getArestasSaindo()) {
                Vertice vizinho = aresta.getFim();
                if (!visitados.contains(vizinho)) {
                    visitados.add(vizinho);
                    fila.add(vizinho);
                }
            }
        }
        return visitados;
    }

    // verifica se a partir de um ponto de partida o caminhao alcanca todos os vertice
    public boolean verificarConectividade(Grafo grafo, Vertice partida) {
        Set<Vertice> alcancados = bfs(partida);
        return alcancados.size() == grafo.getVertices().size();
    }

    // sobrecarga: se nao for informado um ponto de partida especifico vai
    // usa o primeiro vertice cadastrado no grafo
    public boolean verificarConectividade(Grafo grafo) {
        if (grafo.getVertices().isEmpty()) 
            return true;
        Vertice partida = grafo.getVertices().get(0);
        return verificarConectividade(grafo, partida);
    }

    // retorna quais vertices ficariam sem coleta, se tiver algum
    public List<Vertice> verticesInalcancaveis(Grafo grafo, Vertice partida) {
        Set<Vertice> alcancados = bfs(partida);
        List<Vertice> inalcancaveis = new ArrayList<>();
        for (Vertice v : grafo.getVertices()) {
            if (!alcancados.contains(v)) {
                inalcancaveis.add(v);
            }
        }
        return inalcancaveis;
    }
}