package algoritmos;

import grafo.RedeMetro;
import model.Conexao;
import model.Estacao;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação da Busca em Largura (BFS) para encontar o caminho
 * com menor número de estações (arestas) entre duas estações.
 */
public class BFS {

    /**
     * Encontra o caminho com o menor número de estações entre origem e destino.
     *
     * @param rede        rede de metrô contendo todas as estações e conexões
     * @param origemId    id da estação de origem (deve estar ativa)
     * @param destinoId   id da estação de destino (deve estar ativa)
     * @return lista de ids das estações no caminho, inclusive origem e destino.
     *         Retorna lista vazia se não houver caminho ou se alguma das
     *         estações não estiver ativa.
     */
    public static List<String> caminhoMenorSaltos(RedeMetro rede, String origemId, String destinoId) {
        // Validação rápida
        Estacao origem = rede.getEstacao(origemId);
        Estacao destino = rede.getEstacao(destinoId);
        if (origem == null || destino == null || !origem.isAtiva() || !destino.isAtiva()) {
            return Collections.emptyList();
        }
        if (origemId.equals(destinoId)) {
            return Collections.singletonList(origemId);
        }

        Set<String> visitados = new HashSet<>();
        Map<String, String> predecessor = new HashMap<>();
        Queue<String> fila = new LinkedList<>();

        visitados.add(origemId);
        fila.add(origemId);

        while (!fila.isEmpty()) {
            String uId = fila.poll();
            Estacao u = rede.getEstacao(uId);
            if (u == null || !u.isAtiva()) continue;

            // Percorre todas as conexões de u (listagem de Conexao)
            for (Conexao c : rede.getConexoes(uId)) {
                Estacao v = c.getDestino();
                String vId = v.getId();
                if (!visitados.contains(vId) && v.isAtiva()) {
                    visitados.add(vId);
                    predecessor.put(vId, uId);
                    if (vId.equals(destinoId)) {
                        // Reconstroi o caminho
                        List<String> path = new ArrayList<>();
                        String cur = vId;
                        while (cur != null) {
                            path.add(cur);
                            cur = predecessor.get(cur);
                        }
                        Collections.reverse(path);
                        return path;
                    }
                    fila.add(vId);
                }
            }
        }
        // Destino não reachable
        return Collections.emptyList();
    }
}
