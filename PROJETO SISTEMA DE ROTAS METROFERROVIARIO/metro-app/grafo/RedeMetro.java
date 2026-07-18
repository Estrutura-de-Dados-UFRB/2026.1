package grafo;

import model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Estrutura de dados que representa a rede de metrô como um grafo
 * usando lista de adjacência. As estações são mantidas ativas/inativas
 * para simulação de interdições.
 */
public class RedeMetro {

    /** mapeia id da estação → objeto Estação */
    private final Map<String, Estacao> estacoes = new HashMap<>();

    /** lista de adjacência: id da estação → lista de conexões partindo dela */
    private final Map<String, List<Conexao>> adjacencias = new HashMap<>();

    /**
     * Adiciona uma estação à rede (se ainda não existir).
     *
     * @param e estação a ser adicionada
     */
    public void adicionarEstacao(Estacao e) {
        if (!estacoes.containsKey(e.getId())) {
            estacoes.put(e.getId(), e);
            // garante lista vazia para a nova estação
            adjacencias.putIfAbsent(e.getId(), new ArrayList<>());
        }
    }

    /**
     * Remove uma estação da rede (opcional, caso precise). Não usada no guia,
     * mas útil para testes.
     *
     * @param id identificador da estação a remover
     */
    public void removerEstacao(String id) {
        estacoes.remove(id);
        adjacencias.remove(id);
        // remove referências nas listas de adjacência de outras estações
        for (List<Conexao> lst : adjacencias.values()) {
            lst.removeIf(c -> c.getDestino().getId().equals(id));
        }
    }

    /**
     * Retorna a estação pelo id, ou null se não existir.
     *
     * @param id identificador da estação
     * @return objeto Estacao ou null
     */
    public Estacao getEstacao(String id) {
        return estacoes.get(id);
    }

    /**
     * Adiciona uma conexão (aresta) entre duas estações já existentes.
     *
     * @param origemId   id da estação de origem
     * @param destinoId  id da estação de destino
     * @param peso       peso da aresta (tempo em minutos)
     */
    public void adicionarConexao(String origemId, String destinoId, double peso) {
        Estacao origem = estacoes.get(origemId);
        Estacao destino = estacoes.get(destinoId);
        if (origem == null || destino == null) {
            throw new IllegalArgumentException("Estações não encontradas: " + origemId + " ou " + destinoId);
        }
        Conexao c = new Conexao(origem, destino, peso);
        adjacencias.computeIfAbsent(origemId, k -> new ArrayList<>()).add(c);
        // Não adiciona o contrário aqui; se a conexão for bidirecional,
        // chame o método duas vezes ou ajuste o parâmetro bidirecional.
        // (O modelo de Conexao já tem flag bidirecional, mas a lista de adjacência
        //  pode ser tratada como dirigida. Para simplificar, deixamos como dirigida.)
    }

    /**
     * @return lista imutável de todas as conexões que saem da estação com o dado id
     */
    public List<Conexao> getConexoes(String id) {
        return Collections.unmodifiableList(adjacencias.getOrDefault(id, Collections.emptyList()));
    }

    /**
     * Marca a estação como inativa (interdição).
     *
     * @param id identificador da estação a interditar
     */
    public void interditar(String id) {
        Estacao e = estacoes.get(id);
        if (e != null) {
            e.setAtiva(false);
        }
    }

    /**
     * Restaura a estação para estado ativo.
     *
     * @param id identificador da estação a reativar
     */
    public void reativar(String id) {
        Estacao e = estacoes.get(id);
        if (e != null) {
            e.setAtiva(true);
        }
    }

    /** @return coleção não modificável de todas as estações cadastradas */
    public Collection<Estacao> getEstacoes() {
        return Collections.unmodifiableCollection(estacoes.values());
    }

    /** @return conjunto de ids de todas as estações cadastradas */
    public Set<String> getIds() {
        return Collections.unmodifiableSet(estacoes.keySet());
    }

    /** @return lista apenas das estações atualmente ativas */
    public List<Estacao> getAtivas() {
        return estacoes.values().stream()
                .filter(Estacao::isAtiva)
                .collect(Collectors.toList());
    }

    /** @return conjunto de ids das estações ativas */
    public Set<String> getIdsAtivos() {
        return estacoes.entrySet().stream()
                .filter(e -> e.getValue().isAtiva())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /** Verifica se há ao menos uma estação ativa na rede. */
    public boolean temEstacoesAtivas() {
        return !getAtivas().isEmpty();
    }

    /** (Opcional) método de utilidade para imprimir a rede (debug). */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RedeMetro{");
        sb.append(" estacoes: ").append(estacoes.size()).append(", ativas: ").append(getAtivas().size()).append('}');
        return sb.toString();
    }
}