package testes;

import grafo.RedeMetro;
import grafo.ResultadoRota;
import dados.DadosMetroSP;
import algoritmos.Dijkstra;

public class TesteMenosBaldeacoes {

    private static int falhas = 0;
    private static int total = 0;

    public static void main(String[] args) {
        RedeMetro rede = DadosMetroSP.carregar();

        // Caso 1: mesma linha, sem baldeação possível nem necessária
        testarBaldeacoesMenorOuIgual(rede, "L1_AZUL:Tucuruvi", "L1_AZUL:Jabaquara");

        // Caso 2: rota que provavelmente exige baldeação
        testarBaldeacoesMenorOuIgual(rede, "L1_AZUL:Tucuruvi", "L3_VERMELHA:Corinthians-Itaquera");

        // Caso 2: rota que provavelmente exige baldeação
        testarValorExato(rede, "L1_AZUL:Tucuruvi", "L3_VERMELHA:Corinthians-Itaquera", 1, 47.0);

        // Caso 3: hub com múltiplas linhas convergindo
        testarBaldeacoesMenorOuIgual(rede, "L7_RUBI:Jundiaí", "L15_PRATA:São_Mateus");

        // Caso 4: origem = destino
        testarOrigemIgualDestino(rede, "L1_AZUL:Sé");

        // Caso 5: com interdição
        testarComInterdicao(rede, "L1_AZUL:Sé", "L1_AZUL:Tucuruvi", "L3_VERMELHA:Corinthians-Itaquera");

        System.out.println("\nTotal: " + total + " | Falhas: " + falhas);
        if (falhas > 0) {
            System.out.println("HÁ FALHAS — não confiar nesses casos na demo.");
        }
    }

    private static void testarBaldeacoesMenorOuIgual(RedeMetro rede, String origem, String destino) {
        total++;
        ResultadoRota viaTempo = Dijkstra.calcularCaminhoMinimo(rede, origem, destino);
        ResultadoRota viaBaldeacoes = Dijkstra.calcularMenosBaldeacoes(rede, origem, destino);

        System.out.println("--- " + origem + " -> " + destino + " ---");
        System.out.println("Menor tempo:      baldeações=" + viaTempo.getBaldeacoes()
                + " tempo=" + viaTempo.getCustoTotal());
        System.out.println("Menos baldeações: baldeações=" + viaBaldeacoes.getBaldeacoes()
                + " tempo=" + viaBaldeacoes.getCustoTotal());

        if (viaBaldeacoes.getBaldeacoes() > viaTempo.getBaldeacoes()) {
            System.out.println("FALHA: calcularMenosBaldeacoes teve MAIS baldeações que o Dijkstra normal.");
            falhas++;
        }
        if (viaBaldeacoes.éVazio() && !viaTempo.éVazio()) {
            System.out.println("FALHA: calcularMenosBaldeacoes não achou rota, mas Dijkstra normal achou.");
            falhas++;
        }
    }

    private static void testarOrigemIgualDestino(RedeMetro rede, String id) {
        total++;
        ResultadoRota r = Dijkstra.calcularMenosBaldeacoes(rede, id, id);
        if (r.éVazio() || r.getCaminho().size() != 1 || r.getBaldeacoes() != 0) {
            System.out.println("FALHA: origem=destino não retornou caminho trivial.");
            falhas++;
        }
    }

    private static void testarValorExato(RedeMetro rede, String origem, String destino,
                                       int baldeacoesEsperadas, double tempoEsperado) {
        total++;
        ResultadoRota r = Dijkstra.calcularMenosBaldeacoes(rede, origem, destino);
        if (r.getBaldeacoes() != baldeacoesEsperadas || r.getCustoTotal() != tempoEsperado) {
            System.out.println("FALHA: esperado baldeacoes=" + baldeacoesEsperadas
                    + " tempo=" + tempoEsperado
                    + " | obtido baldeacoes=" + r.getBaldeacoes()
                    + " tempo=" + r.getCustoTotal());
            falhas++;
        }
    }

    private static void testarComInterdicao(RedeMetro rede, String idInterditada, String origem, String destino) {
        total++;
        rede.interditar(idInterditada);
        ResultadoRota r = Dijkstra.calcularMenosBaldeacoes(rede, origem, destino);
        System.out.println("--- (interditando " + idInterditada + ") " + origem + " -> " + destino + " ---");
        System.out.println("baldeações=" + r.getBaldeacoes() + " tempo=" + r.getCustoTotal());
        rede.reativar(idInterditada); // reverte pro próximo teste não ser afetado
    }
}