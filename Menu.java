package com.mycompany.sistemacoletaurbana;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private final Scanner entrada = new Scanner(System.in);
    private final Grafo mapa;
    private final SimuladorVias simulador;

    /** O grafo ja chega pronto da carga de cenario (Pessoa 1 / Main). */
    public Menu(Grafo mapa) {
        this.mapa = mapa;
        this.simulador = new SimuladorVias(mapa);
    }

    public void executar() {
        boolean rodando = true;
        while (rodando) {
            imprimirOpcoes();
            String opcao = entrada.nextLine().trim();
            System.out.println();

            switch (opcao) {
                case "1":  verMapa();                break;
                case "2":  calcularRotaMinima();     break;
                case "3":  compararRotas();          break;
                case "4":  verificarAlcance();       break;
                case "5":  gerarMalhaEconomica();    break;
                case "6":  rotaCarteiroChines();     break;
                case "7":  bloquearVia();            break;
                case "8":  desbloquearVia();         break;
                case "9":  alterarVia();             break;
                case "10": gerenciarBloqueios();     break;
                case "0":  rodando = false;          break;
                default:   System.out.println("Opcao invalida.");
            }
        }
        System.out.println("Encerrando o simulador.");
    }

    private void imprimirOpcoes() {
        System.out.println("\n=================================================");
        System.out.println(" SIMULADOR DE COLETA DE LIXO URBANO - " + mapa.getNome());
        System.out.println("=================================================");
        System.out.println(" 1. Ver mapa/grafo carregado");
        System.out.println(" 2. Calcular rota de menor custo (Dijkstra)");
        System.out.println(" 3. Comparar rota atual x rota otimizada");
        System.out.println(" 4. Verificar se todos os pontos sao alcancaveis (BFS/DFS)");
        System.out.println(" 5. Gerar malha economica de vias (Prim/Kruskal)");
        System.out.println(" 6. Rota cobrindo todas as ruas (Carteiro Chines)");
        System.out.println(" 7. Simular bloqueio de via");
        System.out.println(" 8. Desbloquear via");
        System.out.println(" 9. Alterar peso/distancia/volume de uma via");
        System.out.println("10. Listar bloqueios ativos / desbloquear tudo");
        System.out.println(" 0. Sair");
        System.out.print("Escolha uma opcao: ");
    }

    // ------------------------------------------------------------------
    // 1 - VER MAPA
    // ------------------------------------------------------------------
    private void verMapa() {
        System.out.println(simulador.exibirMapa());
    }

    // ------------------------------------------------------------------
    // 2 - DIJKSTRA 
    // ------------------------------------------------------------------
    private void calcularRotaMinima() {
        String origem = perguntarVertice("Ponto de origem: ");
        if (origem == null) return;
        String destino = perguntarVertice("Ponto de destino: ");
        if (destino == null) return;

        ResultadoDijkstra resultado = Dijkstra.calcular(mapa, origem);
        Vertice vDestino = mapa.buscarVertice(destino);

        if (!resultado.isAlcancavel(vDestino)) {
            System.out.println(">> Nao existe rota de " + origem + " ate " + destino
                    + " (verifique se ha vias bloqueadas no caminho).");
            return;
        }

        List<Vertice> caminho = resultado.reconstruirCaminho(vDestino);
        System.out.println(">> Rota otima: " + formatarCaminho(caminho));
        System.out.printf(">> Custo total: %.2f%n", resultado.getDistancia(vDestino));
    }

    // ------------------------------------------------------------------
    // 3 - COMPARAR ROTA ATUAL x OTIMIZADA 
    // ------------------------------------------------------------------
    private void compararRotas() {
        System.out.println("Informe a rota ATUAL do caminhao (pontos separados por virgula).");
        System.out.println("Ex.: Deposito,Centro,Bairro A,Aterro");
        System.out.print("Rota atual: ");
        String linha = entrada.nextLine();

        List<Vertice> rotaAtual = new ArrayList<>();
        for (String nome : linha.split(",")) {
            Vertice v = mapa.buscarVertice(nome.trim());
            if (v == null) {
                System.out.println(">> Ponto inexistente: " + nome.trim());
                return;
            }
            rotaAtual.add(v);
        }
        if (rotaAtual.size() < 2) {
            System.out.println(">> Informe pelo menos 2 pontos.");
            return;
        }

        Double custoAtual = custoDaRota(rotaAtual);
        if (custoAtual == null) {
            System.out.println(">> A rota atual e invalida: existe trecho sem via direta ou bloqueado.");
            return;
        }

        Vertice origem = rotaAtual.get(0);
        Vertice destino = rotaAtual.get(rotaAtual.size() - 1);
        ResultadoDijkstra resultado = Dijkstra.calcular(mapa, origem.getNome());

        if (!resultado.isAlcancavel(destino)) {
            System.out.println(">> Nao ha rota otimizada disponivel entre origem e destino.");
            return;
        }

        double custoOtimo = resultado.getDistancia(destino);
        List<Vertice> rotaOtima = resultado.reconstruirCaminho(destino);

        System.out.println("Rota atual....: " + formatarCaminho(rotaAtual));
        System.out.printf("Custo atual...: %.2f%n", custoAtual);
        System.out.println("Rota otimizada: " + formatarCaminho(rotaOtima));
        System.out.printf("Custo otimo...: %.2f%n", custoOtimo);

        double economia = custoAtual - custoOtimo;
        if (economia <= 0.0001) {
            System.out.println(">> A rota atual ja e otima.");
        } else {
            System.out.printf(">> Economia: %.2f (%.1f%% de reducao)%n",
                    economia, (economia / custoAtual) * 100);
        }
    }

    /** Soma o peso de cada trecho da rota informada. null = trecho inexistente/bloqueado. */
    private Double custoDaRota(List<Vertice> rota) {
        double total = 0.0;
        for (int i = 0; i < rota.size() - 1; i++) {
            Aresta a = mapa.buscarAresta(rota.get(i), rota.get(i + 1));
            if (a == null || SimuladorVias.estaBloqueada(a)) {
                return null;
            }
            total += a.getPeso();
        }
        return total;
    }

    private void verificarAlcance() {
        // TODO PESSOA 3: substituir pela chamada real, ex.:
        //     String origem = perguntarVertice("Ponto de partida (deposito): ");
        //     BuscaGrafos.verificarAlcance(mapa, origem);
        System.out.println(">> Modulo BFS/DFS (Pessoa 3) ainda nao implementado.");
        System.out.println("   Enquanto isso, o Dijkstra ja indica pontos inalcancaveis (opcao 2).");
    }

    private void gerarMalhaEconomica() {
        //     Prim.gerarArvoreGeradoraMinima(mapa);
        System.out.println(">> Modulo Prim/Kruskal (Pessoa 3) ainda nao implementado.");
    }

    // ------------------------------------------------------------------
    // 6 - CARTEIRO CHINES 
    // ------------------------------------------------------------------
    private void rotaCarteiroChines() {
        System.out.println("ATENCAO: este algoritmo DUPLICA arestas no grafo (efeito permanente).");
        System.out.print("Deseja continuar? (s/n): ");
        if (!entrada.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Operacao cancelada.");
            return;
        }

        CarteiroChines carteiro = new CarteiroChines(mapa);
        ArrayList<Vertice> impares = carteiro.verticesImpares(mapa.getVertices());
        System.out.println("Pontos de grau impar encontrados: " + impares.size());

        carteiro.calcularDistancia(new ArrayList<>(impares));   // copia: o metodo consome a lista

        List<Vertice> circuito = new MontarCaminho(mapa).Montagem();
        System.out.println(">> Rota de coleta: " + formatarCaminho(circuito));
        System.out.println(">> Total de trechos percorridos: " + Math.max(circuito.size() - 1, 0));
    }

    private void bloquearVia() {
        String origem = perguntarVertice("Via bloqueada - ponto de inicio: ");
        if (origem == null) return;
        String destino = perguntarVertice("Via bloqueada - ponto de fim: ");
        if (destino == null) return;
        boolean ambos = perguntarMaoDupla();

        int qtd = simulador.bloquearVia(origem, destino, ambos);
        if (qtd == 0) {
            System.out.println(">> Nenhuma via foi bloqueada (via inexistente ou ja bloqueada).");
        } else {
            System.out.println(">> Via bloqueada com sucesso (" + qtd + " aresta(s)).");
            System.out.println("   Rode novamente as opcoes 2 a 6 para ver as rotas mudarem.");
        }
    }

    private void desbloquearVia() {
        String origem = perguntarVertice("Via liberada - ponto de inicio: ");
        if (origem == null) return;
        String destino = perguntarVertice("Via liberada - ponto de fim: ");
        if (destino == null) return;
        boolean ambos = perguntarMaoDupla();

        int qtd = simulador.desbloquearVia(origem, destino, ambos);
        System.out.println(qtd == 0
                ? ">> Essa via nao estava bloqueada."
                : ">> Via liberada (" + qtd + " aresta(s)), peso original restaurado.");
    }

    private void alterarVia() {
        String origem = perguntarVertice("Ponto de inicio da via: ");
        if (origem == null) return;
        String destino = perguntarVertice("Ponto de fim da via: ");
        if (destino == null) return;

        System.out.println("O que deseja alterar?");
        System.out.println("  1. Peso diretamente (ex.: transito, obra)");
        System.out.println("  2. Distancia (recalcula o peso)");
        System.out.println("  3. Volume de lixo (recalcula o peso)");
        System.out.print("Opcao: ");
        String tipo = entrada.nextLine().trim();

        Double valor = perguntarNumero("Novo valor: ");
        if (valor == null) return;

        int qtd;
        switch (tipo) {
            case "1": qtd = simulador.alterarPeso(origem, destino, valor);        break;
            case "2": qtd = simulador.alterarDistancia(origem, destino, valor);   break;
            case "3": qtd = simulador.alterarVolumeLixo(origem, destino, valor);  break;
            default:
                System.out.println(">> Opcao invalida.");
                return;
        }
        System.out.println(qtd == 0 ? ">> Via nao encontrada." : ">> Via atualizada (" + qtd + " aresta(s)).");
    }

    private void gerenciarBloqueios() {
        System.out.println(simulador.exibirBloqueios());
        if (simulador.listarBloqueadas().isEmpty()) return;

        System.out.print("Deseja desbloquear TODAS as vias? (s/n): ");
        if (entrada.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println(">> " + simulador.desbloquearTudo() + " via(s) liberada(s).");
        }
    }

    // ------------------------------------------------------------------
    // AUXILIARES
    // ------------------------------------------------------------------
    private String perguntarVertice(String pergunta) {
        System.out.print(pergunta);
        String nome = entrada.nextLine().trim();
        if (mapa.buscarVertice(nome) == null) {
            System.out.println(">> Ponto inexistente: " + nome);
            return null;
        }
        return nome;
    }

    private Double perguntarNumero(String pergunta) {
        System.out.print(pergunta);
        try {
            return Double.parseDouble(entrada.nextLine().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println(">> Valor numerico invalido.");
            return null;
        }
    }

    private boolean perguntarMaoDupla() {
        System.out.print("A rua e de mao dupla? (s/n): ");
        return entrada.nextLine().trim().equalsIgnoreCase("s");
    }

    private String formatarCaminho(List<Vertice> caminho) {
        if (caminho == null || caminho.isEmpty()) return "(vazio)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < caminho.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(caminho.get(i).getNome());
        }
        return sb.toString();
    }

}