package algoritmos;

import model.Grafo;
import model.Ponto;
import model.Rota;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Guloso {

    public Rota executar(Grafo grafo, Ponto origem) {
        if (grafo == null || origem == null) {
            throw new IllegalArgumentException("Grafo e origem sao obrigatorios.");
        }

        List<Ponto> percurso = new ArrayList<>();
        Set<Ponto> visitados = new HashSet<>();
        Ponto atual = origem;
        percurso.add(atual);
        visitados.add(atual);

        while (visitados.size() < grafo.getPontos().size()) {
            Ponto melhor = null;
            double melhorScore = Double.NEGATIVE_INFINITY;
            List<Ponto> melhorCaminho = new ArrayList<>();

            for (Ponto candidato : grafo.getPontos()) {
                if (visitados.contains(candidato)) {
                    continue;
                }
                List<Ponto> caminho = Dijkstra.encontrarCaminho(grafo, atual, candidato);
                if (caminho.isEmpty()) {
                    continue;
                }
                double distancia = AlgoritmoUtils.calcularDistancia(grafo, caminho);
                double prioridade = candidato.getPrioridade() + candidato.getQuantidadeAlunos();
                double score = prioridade / Math.max(distancia, 0.1);
                if (score > melhorScore) {
                    melhorScore = score;
                    melhor = candidato;
                    melhorCaminho = caminho;
                }
            }

            if (melhor == null) {
                break;
            }

            for (int i = 1; i < melhorCaminho.size(); i++) {
                Ponto ponto = melhorCaminho.get(i);
                if (visitados.add(ponto)) {
                    percurso.add(ponto);
                }
            }
            atual = melhor;
        }

        double distancia = AlgoritmoUtils.calcularDistancia(grafo, percurso);
        return new Rota(percurso, null, origem, percurso.isEmpty() ? null : percurso.get(percurso.size() - 1),
                "Guloso", distancia, AlgoritmoUtils.calcularTempo(distancia),
                distancia, AlgoritmoUtils.calcularCusto(distancia));
    }
}
