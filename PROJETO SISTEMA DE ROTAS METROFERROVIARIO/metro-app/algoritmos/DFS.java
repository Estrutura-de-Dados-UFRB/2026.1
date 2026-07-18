package algoritmos;

import grafo.RedeMetro;
import model.Conexao;
import model.Estacao;

import java.util.*;

/**
 * Implementação da Busca em Profundidade (DFS) para verificar
 * conectividade e detectar ciclos na rede de metrô.
 */
public class DFS {

    /**
     * Verifica se existe um caminho entre duas estações ativas.
     *
     * @param rede        rede de metrô
     * @param origemId    id da estação de origem
     * @param destinoId   id da estação de destino
     * @return true se houver caminho, false caso contrário
     */
    public static boolean estaConectada(RedeMetro rede, String origemId, String destinoId) {
        Estacao origem = rede.getEstacao(origemId);
        Estacao destino = rede.getEstacao(destinoId);
        if (origem == null || destino == null || !origem.isAtiva() || !destino.isAtiva()) {
            return false;
        }

        Set<String> visitados = new HashSet<>();
        Stack<String> pilha = new Stack<>();
        pilha.push(origemId);
        visitados.add(origemId);

        while (!pilha.isEmpty()) {
            String uId = pilha.pop();
            if (uId.equals(destinoId)) {
                return true;
            }
            Estacao u = rede.getEstacao(uId);
            if (u == null || !u.isAtiva()) continue;

            for (Conexao c : rede.getConexoes(uId)) {
                Estacao v = c.getDestino();
                String vId = v.getId();
                if (!visitados.contains(vId) && v.isAtiva()) {
                    visitados.add(vId);
                    pilha.push(vId);
                }
            }
        }
        return false;
    }

    /**
     * Verifica se a rede (considerando apenas estações ativas) é conexa.
     *
     * @param rede rede de metrô
     * @return true se todas as estações ativas estiverem conectadas entre si
     */
    public static boolean redeConectada(RedeMetro rede) {
        // Encontra primeira estação ativa
        Estacao primeira = null;
        for (Estacao e : rede.getEstacoes()) {
            if (e.isAtiva()) {
                primeira = e;
                break;
            }
        }
        if (primeira == null) return true; // rede vazia

        Set<String> visitados = new HashSet<>();
        Stack<String> pilha = new Stack<>();
        pilha.push(primeira.getId());
        visitados.add(primeira.getId());

        while (!pilha.isEmpty()) {
            String uId = pilha.pop();
            Estacao u = rede.getEstacao(uId);
            if (u == null || !u.isAtiva()) continue;

            for (Conexao c : rede.getConexoes(uId)) {
                Estacao v = c.getDestino();
                String vId = v.getId();
                if (!visitados.contains(vId) && v.isAtiva()) {
                    visitados.add(vId);
                    pilha.push(vId);
                }
            }
        }

        // Conta estações ativas
        long ativas = rede.getEstacoes().stream().filter(Estacao::isAtiva).count();
        return visitados.size() == ativas;
    }

    /**
     * Detecta se existe ciclo na rede (considerando apenas estações ativas).
     *
     * @param rede rede de metrô
     * @return true se houver pelo menos um ciclo
     */
    public static boolean temCiclo(RedeMetro rede) {
        Set<String> visitados = new HashSet<>();
        Set<String> emRecursao = new HashSet<>();

        for (Estacao e : rede.getEstacoes()) {
            if (!e.isAtiva() || visitados.contains(e.getId())) continue;
            if (dfsCiclo(rede, e.getId(), visitados, emRecursao, null)) {
                return true;
            }
        }
        return false;
    }

    private static boolean dfsCiclo(RedeMetro rede, String uId,
                                    Set<String> visitados,
                                    Set<String> emRecursao,
                                    String pai) {
        visitados.add(uId);
        emRecursao.add(uId);

        Estacao u = rede.getEstacao(uId);
        if (u == null || !u.isAtiva()) {
            emRecursao.remove(uId);
            return false;
        }

        for (Conexao c : rede.getConexoes(uId)) {
            Estacao v = c.getDestino();
            String vId = v.getId();
            if (!v.isAtiva()) continue;
            if (!visitados.contains(vId)) {
                if (dfsCiclo(rede, vId, visitados, emRecursao, uId)) {
                    return true;
                }
            } else if (emRecursao.contains(vId) && !vId.equals(pai)) {
                // Encontrou back-edge para ancestral diferente do pai
                return true;
            }
        }
        emRecursao.remove(uId);
        return false;
    }
}