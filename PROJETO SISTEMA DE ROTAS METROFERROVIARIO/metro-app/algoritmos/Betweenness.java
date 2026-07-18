package algoritmos;

import grafo.RedeMetro;
import model.Estacao;

import java.util.*;

/**
 * Aproximação de centralidade de intermediação (betweenness centrality).
 *
 * LIMITAÇÃO CONHECIDA: quando existem múltiplos caminhos mínimos empatados
 * entre um par de estações, esta implementação conta apenas UM deles (o que
 * o Floyd-Warshall retorna via prox[i][j], decidido pela ordem de iteração
 * de k no relaxamento). Isso não é betweenness centrality formal — a
 * definição correta distribui o crédito fracionalmente (1/k) entre todos os
 * k caminhos mínimos empatados. Esta é uma aproximação de caminho único,
 * documentada como tal.
 *
 * A contagem é feita por NOME FÍSICO da estação (getNome()), não por id de
 * vértice, para evitar fragmentar a importância de uma estação de baldeação
 * entre seus múltiplos vértices (um por linha).
 */
public class Betweenness {

    public static Map<String, Integer> calcular(RedeMetro rede) {
        List<Estacao> estacoes = rede.getAtivas();
        FloydWarshall.Resultado resultado = FloydWarshall.executar(rede);

        Map<String, Integer> contagem = new HashMap<>();
        for (Estacao e : estacoes) {
            contagem.putIfAbsent(e.getNome(), 0);
        }

        int n = estacoes.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                String origemId = estacoes.get(i).getId();
                String destinoId = estacoes.get(j).getId();
                List<String> caminho = FloydWarshall.reconstruirCaminho(
                        resultado, estacoes, origemId, destinoId);

                for (int k = 1; k < caminho.size() - 1; k++) {
                    Estacao e = rede.getEstacao(caminho.get(k));
                    contagem.merge(e.getNome(), 1, Integer::sum);
                }
            }
        }
        return contagem;
    }

    public static List<Map.Entry<String, Integer>> top(RedeMetro rede, int limite) {
        Map<String, Integer> contagem = calcular(rede);
        List<Map.Entry<String, Integer>> lista = new ArrayList<>(contagem.entrySet());
        lista.sort((a, b) -> b.getValue() - a.getValue());
        return lista.subList(0, Math.min(limite, lista.size()));
    }
}