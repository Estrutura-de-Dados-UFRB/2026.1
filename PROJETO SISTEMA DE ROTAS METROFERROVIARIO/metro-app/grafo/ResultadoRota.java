package grafo;

import java.util.Collections;
import java.util.List;
import model.Estacao;

/**
 * Resultado de uma consulta de rota.
 */
public class ResultadoRota {
    private final List<Estacao> caminho; // sequência de estações da origem ao destino
    private final double custoTotal;     // tempo ou distância total
    private final int baldeacoes;        // número de trocas de linha

    public ResultadoRota(List<Estacao> caminho, double custoTotal, int baldeacoes) {
        this.caminho = Collections.unmodifiableList(caminho);
        this.custoTotal = custoTotal;
        this.baldeacoes = baldeacoes;
    }

    public List<Estacao> getCaminho() {
        return caminho;
    }

    public double getCustoTotal() {
        return custoTotal;
    }

    public int getBaldeacoes() {
        return baldeacoes;
    }

    public boolean éVazio() {
        return caminho == null || caminho.isEmpty();
    }

    @Override
    public String toString() {
        if (éVazio()) {
            return "Sem rota disponível";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Rota: ");
        for (int i = 0; i < caminho.size(); i++) {
            sb.append(caminho.get(i).getNome());
            if (i < caminho.size() - 1) {
                sb.append(" → ");
            }
        }
        sb.append(String.format("\nCusto total: %.1f min", custoTotal));
        sb.append(String.format("\nBaldeações: %d", baldeacoes));
        return sb.toString();
    }
}