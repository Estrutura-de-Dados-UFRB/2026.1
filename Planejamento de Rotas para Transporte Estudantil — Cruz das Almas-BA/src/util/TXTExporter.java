package util;

import model.Ponto;
import model.Rota;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public final class TXTExporter {

    private TXTExporter() {
    }

    public static void exportar(Rota rota, String algoritmo, List<Ponto> pontos, File arquivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write("RELATORIO DE ROTA\n");
            writer.write("Algoritmo: " + algoritmo + "\n");
            writer.write("Distancia total: " + String.format("%.2f km", rota.getDistanciaTotal()) + "\n");
            writer.write("Tempo estimado: " + String.format("%.0f min", rota.getTempoTotal()) + "\n");
            writer.write("Numero de paradas: " + rota.getNumeroParadas() + "\n");
            writer.write("Quantidade de alunos: " + rota.calcularTotalAlunos() + "\n");
            writer.write("Peso total: " + String.format("%.2f", rota.getPesoTotal()) + "\n");
            writer.write("Custo total: " + String.format("R$ %.2f", rota.getCustoTotal()) + "\n\n");

            int ordem = 1;
            for (Ponto ponto : rota.getPercurso()) {
                writer.write(ordem++ + ". " + ponto.getNome() + " | " + ponto.getTipo().getRotulo()
                        + " | " + ponto.getBairro() + "\n");
            }
            writer.write("\nTotal de pontos do projeto: " + (pontos != null ? pontos.size() : 0) + "\n");
        }
    }
}
