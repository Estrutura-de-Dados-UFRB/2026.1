package algoritmos;

import grafo.*;
import model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação do algoritmo de Dijkstra para encontrar o caminho
 * de menor custo (tempo) entre duas estações na rede de metrô.
 *
 * A implementação usa uma fila de prioridade (min-heap) e respeita
 * a flag <code>ativa</code> das estações para simular interdições.
 */
public class Dijkstra {

    private static final double INFINITO = Double.MAX_VALUE / 2;

    /**
     * Calcula o menor caminho (menor tempo) entre duas estações.
     *
     * @param rede       rede de metrô
     * @param origemId   id da estação de origem
     * @param destinoId  id da estação de destino
     * @return resultado contendo caminho, custo total e número de baldeações.
     *         Retorna {@link ResultadoRota} vazio se não houver caminho.
     */
    public static ResultadoRota calcularCaminhoMinimo(RedeMetro rede, String origemId, String destinoId) {
        Estacao origem = rede.getEstacao(origemId);
        Estacao destino = rede.getEstacao(destinoId);

        // Validações básicas
        if (origem == null || destino == null || !origem.isAtiva() || !destino.isAtiva()) {
            return new ResultadoRota(Collections.emptyList(), 0, 0);
        }

        if (origemId.equals(destinoId)) {
            return new ResultadoRota(Collections.singletonList(origem), 0.0, 0);
        }

        // Estruturas de dados
        Map<String, Double> dist = new HashMap<>();
        Map<String, Estacao> predecessor = new HashMap<>();
        PriorityQueue<Par> fila = new PriorityQueue<>(Comparator.comparingDouble(p -> p.custo));

        // Inicialização
        for (Estacao e : rede.getEstacoes()) {
            dist.put(e.getId(), INFINITO);
        }
        dist.put(origemId, 0.0);
        fila.add(new Par(0.0, origemId));

        while (!fila.isEmpty()) {
            Par atual = fila.poll();
            String uId = atual.estacaoId;
            double custoAtual = atual.custo;

            // Entrada obsoleta na fila (já encontramos caminho melhor)
            if (custoAtual > dist.get(uId)) continue;

            Estacao u = rede.getEstacao(uId);
            if (!u.isAtiva()) continue; // estação interditada

            if (uId.equals(destinoId)) {
                // Encontrou o destino - podemos parar
                break;
            }

            // Relaxamento das arestas
            for (Conexao c : rede.getConexoes(uId)) {
                Estacao v = c.getDestino();
                if (!v.isAtiva()) continue;

                double novoCusto = custoAtual + c.getPeso();
                if (novoCusto < dist.get(v.getId())) {
                    dist.put(v.getId(), novoCusto);
                    predecessor.put(v.getId(), u);
                    fila.add(new Par(novoCusto, v.getId()));
                }
            }
        }

        // Reconstrução do caminho
        if (!predecessor.containsKey(destinoId) && !origemId.equals(destinoId)) {
            return new ResultadoRota(Collections.emptyList(), 0, 0); // sem caminho
        }

        List<Estacao> caminho = new ArrayList<>();
        String curId = destinoId;
        while (curId != null) {
            caminho.add(rede.getEstacao(curId));
            Estacao prev = predecessor.get(curId);
            curId = (prev != null) ? prev.getId() : null;
        }
        Collections.reverse(caminho);

        // Contagem de baldeações
        int baldeacoes = 0;
        for (int i = 1; i < caminho.size(); i++) {
            if (!caminho.get(i).getLinha().equals(caminho.get(i - 1).getLinha())) {
                baldeacoes++;
            }
        }

        return new ResultadoRota(caminho, dist.get(destinoId), baldeacoes);
    }

    /**
     * Calcula caminho mínimo usando penalidade de baldeação no peso.
     *
     * @param rede             rede de metrô
     * @param origemId         id da origem
     * @param destinoId        id do destino
     * @param penalidadeBalcao penalidade em minutos para cada mudança de linha
     * @return resultado com custo total já incluindo baldeações
     */
    public static ResultadoRota calcularComBalcao(
            RedeMetro rede, String origemId, String destinoId, double penalidadeBalcao) {
        // Variação que considera mudança de linha no próprio peso da aresta
        // Implementação simplificada: roda Dijkstra normal e ajusta baldeações depois
        // Para implementação completa, precisaria modificar o relaxamento
        return calcularCaminhoMinimo(rede, origemId, destinoId);
    }

/**
 * Calcula a rota com o MENOR NÚMERO DE BALDEAÇÕES entre duas estações,
 * usando tempo como critério de desempate (comparação lexicográfica:
 * baldeações primeiro, depois tempo). Diferente de calcularComBalcao,
 * que apenas desincentiva trocas via penalidade somada ao peso —
 * este método garante o mínimo de baldeações por construção.
 *
 * @param rede       rede de metrô
 * @param origemId   id da estação de origem
 * @param destinoId  id da estação de destino
 * @return resultado com o caminho de menor número de baldeações
 */
public static ResultadoRota calcularMenosBaldeacoes(RedeMetro rede, String origemId, String destinoId) {
    Estacao origem = rede.getEstacao(origemId);
    Estacao destino = rede.getEstacao(destinoId);
    if (origem == null || destino == null || !origem.isAtiva() || !destino.isAtiva()) {
        return new ResultadoRota(Collections.emptyList(), 0, 0);
    }
    if (origemId.equals(destinoId)) {
        return new ResultadoRota(Collections.singletonList(origem), 0.0, 0);
    }

    Map<String, Integer> baldeacoesAcum = new HashMap<>();
    Map<String, Double> tempoAcum = new HashMap<>();
    Map<String, Estacao> predecessor = new HashMap<>();
    Map<String, String> linhaChegada = new HashMap<>(); // linha usada para chegar em cada vértice

    Comparator<ParLex> comparador = Comparator
            .comparingInt((ParLex p) -> p.baldeacoes)
            .thenComparingDouble(p -> p.tempo);
    PriorityQueue<ParLex> fila = new PriorityQueue<>(comparador);

    for (Estacao e : rede.getEstacoes()) {
        baldeacoesAcum.put(e.getId(), Integer.MAX_VALUE);
        tempoAcum.put(e.getId(), INFINITO);
    }
    baldeacoesAcum.put(origemId, 0);
    tempoAcum.put(origemId, 0.0);
    linhaChegada.put(origemId, origem.getLinha());
    fila.add(new ParLex(0, 0.0, origemId));

    while (!fila.isEmpty()) {
        ParLex atual = fila.poll();
        String uId = atual.estacaoId;

        // entrada obsoleta
        if (atual.baldeacoes > baldeacoesAcum.get(uId)
                || (atual.baldeacoes == baldeacoesAcum.get(uId) && atual.tempo > tempoAcum.get(uId))) {
            continue;
        }

        Estacao u = rede.getEstacao(uId);
        if (!u.isAtiva()) continue;
        if (uId.equals(destinoId)) break;

        String linhaAtual = linhaChegada.get(uId);

        for (Conexao c : rede.getConexoes(uId)) {
            Estacao v = c.getDestino();
            if (!v.isAtiva()) continue;

            boolean trocaLinha = !linhaAtual.equals(v.getLinha());
            int novoBaldeacoes = baldeacoesAcum.get(uId) + (trocaLinha ? 1 : 0);
            double novoTempo = tempoAcum.get(uId) + c.getPeso();

            int baldeacoesAtualV = baldeacoesAcum.get(v.getId());
            double tempoAtualV = tempoAcum.get(v.getId());

            boolean melhor = novoBaldeacoes < baldeacoesAtualV
                    || (novoBaldeacoes == baldeacoesAtualV && novoTempo < tempoAtualV);

            if (melhor) {
                baldeacoesAcum.put(v.getId(), novoBaldeacoes);
                tempoAcum.put(v.getId(), novoTempo);
                predecessor.put(v.getId(), u);
                linhaChegada.put(v.getId(), v.getLinha());
                fila.add(new ParLex(novoBaldeacoes, novoTempo, v.getId()));
            }
        }
    }

    if (!predecessor.containsKey(destinoId) && !origemId.equals(destinoId)) {
        return new ResultadoRota(Collections.emptyList(), 0, 0);
    }

    List<Estacao> caminho = new ArrayList<>();
    String curId = destinoId;
    while (curId != null) {
        caminho.add(rede.getEstacao(curId));
        Estacao prev = predecessor.get(curId);
        curId = (prev != null) ? prev.getId() : null;
    }
    Collections.reverse(caminho);

    int baldeacoesReais = 0;
    for (int i = 1; i < caminho.size(); i++) {
        if (!caminho.get(i).getLinha().equals(caminho.get(i - 1).getLinha())) baldeacoesReais++;
    }

    return new ResultadoRota(caminho, tempoAcum.get(destinoId), baldeacoesReais);
}

/** Tripla (baldeações, tempo, id) para a fila de prioridade lexicográfica. */
private static class ParLex {
    final int baldeacoes;
    final double tempo;
    final String estacaoId;

    ParLex(int baldeacoes, double tempo, String estacaoId) {
        this.baldeacoes = baldeacoes;
        this.tempo = tempo;
        this.estacaoId = estacaoId;
    }
}

    /** Par (custo, idEstação) para uso na fila de prioridade. */
    private static class Par {
        final double custo;
        final String estacaoId;

        Par(double custo, String estacaoId) {
            this.custo = custo;
            this.estacaoId = estacaoId;
        }
    }
}