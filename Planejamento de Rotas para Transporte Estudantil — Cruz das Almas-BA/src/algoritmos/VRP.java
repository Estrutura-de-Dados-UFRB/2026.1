package algoritmos;

import model.Grafo;
import model.Onibus;
import model.Ponto;
import model.Rota;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VRP {

    public Map<Onibus, Rota> distribuirRotas(Grafo grafo, List<Ponto> pontos, List<Onibus> frota) {
        if (grafo == null || pontos == null || frota == null || frota.isEmpty()) {
            throw new IllegalArgumentException("Parametros invalidos para o VRP.");
        }

        Map<Onibus, Rota> resultado = new LinkedHashMap<>();
        List<Ponto> pendentes = new ArrayList<>(pontos);

        for (Onibus onibus : frota) {
            if (pendentes.isEmpty()) {
                break;
            }
            Ponto origem = pendentes.remove(0);
            List<Ponto> percurso = new ArrayList<>();
            percurso.add(origem);
            double distancia = 0.0;
            Ponto atual = origem;

            while (!pendentes.isEmpty()) {
                Ponto melhor = null;
                double menor = Double.MAX_VALUE;
                List<Ponto> melhorCaminho = new ArrayList<>();
                for (Ponto candidato : pendentes) {
                    List<Ponto> caminho = Dijkstra.encontrarCaminho(grafo, atual, candidato);
                    if (caminho.isEmpty()) {
                        continue;
                    }
                    double dist = AlgoritmoUtils.calcularDistancia(grafo, caminho);
                    if (dist < menor && candidato.getQuantidadeAlunos() <= onibus.getCapacidade()) {
                        menor = dist;
                        melhor = candidato;
                        melhorCaminho = caminho;
                    }
                }
                if (melhor == null) {
                    break;
                }
                for (int i = 1; i < melhorCaminho.size(); i++) {
                    Ponto ponto = melhorCaminho.get(i);
                    if (!percurso.contains(ponto)) {
                        percurso.add(ponto);
                    }
                }
                distancia += menor;
                atual = melhor;
                pendentes.remove(melhor);
            }

            Rota rota = new Rota(percurso, null, origem, atual, "VRP", distancia,
                    AlgoritmoUtils.calcularTempo(distancia), distancia, AlgoritmoUtils.calcularCusto(distancia));
            onibus.setRota(rota);
            resultado.put(onibus, rota);
        }

        return resultado;
    }

    public List<Onibus> criarFrotaPadrao() {
        List<Onibus> frota = new ArrayList<>();
        frota.add(new Onibus("ABC-1234", 50));
        frota.add(new Onibus("DEF-5678", 50));
        return frota;
    }
}
