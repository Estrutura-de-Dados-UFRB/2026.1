package com.mycompany.sistemacoletaurbana;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CarregadorCenario {

    private static final String SEPARADOR = ";";

    private CarregadorCenario() {
    }

    public static Grafo carregarCenarioPadrao() {
        Grafo mapa = new Grafo("Cenario Urbano Padrao");
        mapa.adicionarVertice("Deposito");
        mapa.adicionarVertice("Centro");
        mapa.adicionarVertice("Praca");
        mapa.adicionarVertice("BairroNorte");
        mapa.adicionarVertice("BairroSul");
        mapa.adicionarVertice("Feira");
        mapa.adicionarVertice("Escola");
        mapa.adicionarVertice("Aterro");
        adicionarRuaMaoDupla(mapa, "Av. Principal",    "Deposito",    "Centro",      4.0, 2.0);
        adicionarRuaMaoDupla(mapa, "Rua das Flores",   "Centro",      "Praca",       2.0, 3.0);
        adicionarRuaMaoDupla(mapa, "Av. Norte",        "Centro",      "BairroNorte", 5.0, 4.0);
        adicionarRuaMaoDupla(mapa, "Rua do Comercio",  "Praca",       "Feira",       3.0, 8.0);
        adicionarRuaMaoDupla(mapa, "Travessa Escolar", "Praca",       "Escola",      1.5, 1.0);
        adicionarRuaMaoDupla(mapa, "Rua da Feira",     "Feira",       "BairroSul",   2.5, 6.0);
        adicionarRuaMaoDupla(mapa, "Av. Sul",          "Centro",      "BairroSul",   6.0, 1.5);
        adicionarRuaMaoDupla(mapa, "Estrada Velha",    "BairroNorte", "Aterro",      8.0, 0.5);
        adicionarRuaMaoDupla(mapa, "Rodovia do Lixo",  "BairroSul",   "Aterro",      7.0, 5.0);
        adicionarRuaMaoDupla(mapa, "Contorno Leste",   "BairroNorte", "Escola",      4.5, 2.0);
        return mapa;
    }

    public static Grafo carregarCenarioComAnel() {
        Grafo mapa = new Grafo("Cenario com Anel Viario");
        mapa.adicionarVertice("Deposito");
        mapa.adicionarVertice("Centro");
        mapa.adicionarVertice("Praca");
        mapa.adicionarVertice("BairroNorte");
        mapa.adicionarVertice("BairroSul");
        mapa.adicionarVertice("Feira");
        mapa.adicionarVertice("Escola");
        mapa.adicionarVertice("Aterro");
        adicionarRuaMaoDupla(mapa, "Av. Principal",    "Deposito",    "Centro",      4.0, 2.0);
        adicionarRuaMaoDupla(mapa, "Av. Sul",          "Centro",      "BairroSul",   6.0, 1.5);
        adicionarRuaMaoDupla(mapa, "Rua do Comercio",  "Praca",       "Feira",       3.0, 8.0);
        adicionarRuaMaoDupla(mapa, "Rua da Feira",     "Feira",       "BairroSul",   2.5, 6.0);
        adicionarRuaMaoDupla(mapa, "Estrada Velha",    "BairroNorte", "Aterro",      8.0, 0.5);
        adicionarRuaMaoDupla(mapa, "Rodovia do Lixo",  "BairroSul",   "Aterro",      7.0, 5.0);
        adicionarRuaMaoUnica(mapa, "Rua das Flores",   "Centro",      "Praca",       2.0, 3.0);
        adicionarRuaMaoUnica(mapa, "Travessa Escolar", "Praca",       "Escola",      1.5, 1.0);
        adicionarRuaMaoUnica(mapa, "Contorno Leste",   "Escola",      "BairroNorte", 4.5, 2.0);
        adicionarRuaMaoUnica(mapa, "Av. Norte",        "BairroNorte", "Centro",      5.0, 4.0);
        return mapa;
    }

    private static void adicionarRuaMaoDupla(Grafo mapa, String nomeRua, String inicio,
                                             String fim, Double distancia, Double volumeLixo) {
        mapa.adicionarAresta(nomeRua, inicio, fim, distancia, volumeLixo);
        mapa.adicionarAresta(nomeRua, fim, inicio, distancia, volumeLixo);
    }

    private static void adicionarRuaMaoUnica(Grafo mapa, String nomeRua, String inicio,
                                             String fim, Double distancia, Double volumeLixo) {
        mapa.adicionarAresta(nomeRua, inicio, fim, distancia, volumeLixo);
    }

    public static Grafo carregarDeArquivo(String caminho) throws IOException {
        Grafo mapa = new Grafo("Cenario: " + caminho);
        int numeroLinha = 0;
        try (BufferedReader leitor = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) continue;
                processarLinha(mapa, linha, numeroLinha);
            }
        }
        if (mapa.getVertices().isEmpty())
            throw new IOException("Cenario invalido: o arquivo nao declarou nenhum ponto (linha 'V;...').");
        return mapa;
    }

    private static void processarLinha(Grafo mapa, String linha, int numeroLinha) throws IOException {
        String[] campos = linha.split(SEPARADOR);
        String tipo = campos[0].trim().toUpperCase();
        if (tipo.equals("V")) {
            if (campos.length < 2) throw new IOException("Linha " + numeroLinha + ": vertice sem nome. Use V;NomeDoPonto");
            String nome = campos[1].trim();
            if (mapa.buscarVertice(nome) != null) throw new IOException("Linha " + numeroLinha + ": ponto duplicado '" + nome + "'.");
            mapa.adicionarVertice(nome);
        } else if (tipo.equals("A")) {
            if (campos.length < 6) throw new IOException("Linha " + numeroLinha + ": aresta incompleta. Use A;Rua;Inicio;Fim;distancia;volumeLixo[;s/n]");
            String nomeRua = campos[1].trim(), inicio = campos[2].trim(), fim = campos[3].trim();
            if (mapa.buscarVertice(inicio) == null) throw new IOException("Linha " + numeroLinha + ": ponto '" + inicio + "' nao foi declarado antes.");
            if (mapa.buscarVertice(fim) == null) throw new IOException("Linha " + numeroLinha + ": ponto '" + fim + "' nao foi declarado antes.");
            Double distancia = lerNumero(campos[4], numeroLinha, "distancia");
            Double volumeLixo = lerNumero(campos[5], numeroLinha, "volumeLixo");
            boolean maoDupla = campos.length >= 7 && campos[6].trim().equalsIgnoreCase("s");
            if (maoDupla) adicionarRuaMaoDupla(mapa, nomeRua, inicio, fim, distancia, volumeLixo);
            else mapa.adicionarAresta(nomeRua, inicio, fim, distancia, volumeLixo);
        } else {
            throw new IOException("Linha " + numeroLinha + ": tipo desconhecido '" + tipo + "'. Use 'V' ou 'A'.");
        }
    }

    private static Double lerNumero(String texto, int numeroLinha, String campo) throws IOException {
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IOException("Linha " + numeroLinha + ": campo '" + campo + "' nao e um numero valido ('" + texto.trim() + "').");
        }
    }

    public static void gerarArquivoExemplo(String caminho) throws IOException {
        Grafo padrao = carregarCenarioPadrao();
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(caminho))) {
            escritor.write("# Cenario urbano - Sistema de Coleta de Lixo"); escritor.newLine();
            escritor.write("# V;NomeDoPonto"); escritor.newLine();
            escritor.write("# A;Rua;Inicio;Fim;distancia;volumeLixo;maoDupla(s/n)"); escritor.newLine();
            escritor.newLine();
            for (Vertice v : padrao.getVertices()) { escritor.write("V" + SEPARADOR + v.getNome()); escritor.newLine(); }
            escritor.newLine();
            for (Aresta a : padrao.getArestas()) {
                escritor.write("A" + SEPARADOR + a.getNomeRua() + SEPARADOR + a.getInicio().getNome()
                        + SEPARADOR + a.getFim().getNome() + SEPARADOR + a.getDistancia()
                        + SEPARADOR + a.getVolumeLixo() + SEPARADOR + "n");
                escritor.newLine();
            }
        }
    }
}