package com.mycompany.sistemacoletaurbana;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length >= 2 && args[0].equals("--exemplo")) {
            gerarExemplo(args[1]);
            return;
        }

        boolean modoConsole = false;
        String[] restantes = args;
        if (args.length > 0 && args[0].equals("--console")) {
            modoConsole = true;
            restantes = new String[args.length - 1];
            System.arraycopy(args, 1, restantes, 0, restantes.length);
        }

        Grafo mapa = montarMapa(restantes);
        if (mapa == null) {
            return;
        }

        System.out.println("Cenario carregado: " + mapa.getNome());
        System.out.println("Pontos de coleta: " + mapa.getVertices().size()
                + " | Vias: " + mapa.getArestas().size());

        if (modoConsole) {
            new Menu(mapa).executar();      // interface de console (alternativa)
        } else {
            JanelaPrincipal.abrir(mapa);    // interface grafica (padrao)
        }
    }

    /** Decide entre carga por arquivo e cenario padrao. Retorna null se a carga falhar. */
    private static Grafo montarMapa(String[] args) {
        if (args.length == 0) {
            return CarregadorCenario.carregarCenarioComAnel();
        }
        try {
            return CarregadorCenario.carregarDeArquivo(args[0]);
        } catch (IOException e) {
            System.out.println("Falha ao carregar o cenario: " + e.getMessage());
            System.out.println("Encerrando. Rode sem argumentos para usar o cenario padrao.");
            return null;
        }
    }

    private static void gerarExemplo(String caminho) {
        try {
            CarregadorCenario.gerarArquivoExemplo(caminho);
            System.out.println("Arquivo de exemplo gerado em: " + caminho);
        } catch (IOException e) {
            System.out.println("Nao foi possivel gerar o arquivo: " + e.getMessage());
        }
    }
}