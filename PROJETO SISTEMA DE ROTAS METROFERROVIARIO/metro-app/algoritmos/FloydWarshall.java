package algoritmos;

import grafo.RedeMetro;
import model.*;

import java.util.*;
import java.util.stream.Collectors;

public class FloydWarshall {

    /**
     * Executa o algoritmo de Floyd‑Warshall na rede fornecida.
     *
     * @param rede rede de metró contendo somente estaçōes ativas
     * @return objeto {@link Resultado} com as matrizes de distâncias
     *         e predecessentes
     */
    public static Resultado executar(RedeMetro rede) {
        // ---------- 1) Obter todas as estações ativas ----------
        List<Estacao> estacoes = rede.getAtivas().stream().toList();
        int n = estacoes.size();
        if (n == 0) {
            return new Resultado(new double[0][0], new int[0][0]);
        }

        // ---------- 2) Mapear cada estação para um índice ----------
        Map<Estacao, Integer> indice = new HashMap<>();
        for (int i = 0; i < n; i++) {
            indice.put(estacoes.get(i), i);
        }

        // ---------- 3) Inicializar as matrizes ----------
        double INF = Double.POSITIVE_INFINITY;
        double[][] dist = new double[n][n];
        int[][] prox = new int[n][n];

        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
            prox[i][i] = i;
        }

        // ---------- 4) Preencher de arestas diretas ----------
        for (String id : rede.getIdsAtivos()) {
            Estacao u = rede.getEstacao(id);
            if (!u.isAtiva()) continue;
            int i = indice.get(u);
            for (Conexao c : rede.getConexoes(id)) {
                Estacao v = c.getDestino();
                if (!v.isAtiva()) continue;
                int j = indice.get(v);
                double peso = c.getPeso();
                if (peso < dist[i][j]) {
                    dist[i][j] = peso;
                    prox[i][j] = j;
                }
            }
        }

        // ---------- 5) Execução do Floyd‑Warshall ----------
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (Double.isInfinite(dist[i][k])) continue;
                for (int j = 0; j < n; j++) {
                    double nd = dist[i][k] + dist[k][j];
                    if (nd < dist[i][j]) {
                        dist[i][j] = nd;
                        prox[i][j] = prox[i][k];
                    }
                }
            }
        }

        return new Resultado(dist, prox);
    }

    /**
     * Reconstrução do caminho a partir das matrizes {@code dist} e {@code prox}.
     *
     * @param prox     matriz de predecessentes gerada por {@link #executar(RedeMetro)}
     * @param estacoes lista de estações na ordem dos índices usados nas matrizes
     * @param origemId id da estação de origem
     * @param destinoId id da estação de destino
     * @return lista com os nomes das estações no caminho, ou lista vazia se indisponível
     */
    public static List<String> reconstruirCaminho(Resultado resultado,
                                                  List<Estacao> estacoes,
                                                  String origemId,
                                                  String destinoId) {
        int n = estacoes.size();
        // procurar índices
        Integer i = null, j = null;
        for (int idx = 0; idx < n; idx++) {
            if (estacoes.get(idx).getId().equals(origemId)) i = idx;
            if (estacoes.get(idx).getId().equals(destinoId)) j = idx;
        }
        if (i == null || j == null) return Collections.emptyList();
        if (Double.isInfinite(resultado.distancias[i][j])) return Collections.emptyList();

        LinkedList<String> path = new LinkedList<>();
        while (i != j) {
            path.addFirst(estacoes.get(i).getId());
            int next = resultado.prox[i][j];
            if (next == j) break;
            i = next;
        }
        path.addFirst(estacoes.get(j).getId());
        return path;
    }

    /**
     * Resultado encapsulado do algoritmo.
     */
    public static class Resultado {
        public final double[][] distancias; // matriz de menores distâncias
        public final int[][] prox;          // matriz de predecessentes (prox[i][j] = primeiro nó após i no caminho a j)

        public Resultado(double[][] distancias, int[][] prox) {
            this.distancias = distancias;
            this.prox = prox;
        }
    }
}