package testes;

import dados.DadosMetroSP;
import grafo.RedeMetro;
import algoritmos.DFS;

public class TesteConexao {
    public static void main(String[] args) {
        RedeMetro rede = DadosMetroSP.carregar();
        System.out.println("Rede conectada? " + DFS.redeConectada(rede));
    }
}