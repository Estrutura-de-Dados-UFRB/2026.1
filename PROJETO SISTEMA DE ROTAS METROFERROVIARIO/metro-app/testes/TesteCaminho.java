package testes;

import grafo.RedeMetro;
import dados.DadosMetroSP;
import algoritmos.BFS;
import java.util.List;

public class TesteCaminho {
    public static void main(String[] args) {
        RedeMetro rede = DadosMetroSP.carregar();

        List<String> ida = BFS.caminhoMenorSaltos(rede, "L1_AZUL:Tucuruvi", "L1_AZUL:Jabaquara");
        List<String> volta = BFS.caminhoMenorSaltos(rede, "L1_AZUL:Jabaquara", "L1_AZUL:Tucuruvi");
        System.out.println("Ida: " + ida);
        System.out.println("Volta: " + volta);
    }
}