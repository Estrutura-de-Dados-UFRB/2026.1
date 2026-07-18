package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import model.Ponto;
import model.Rota;
import model.TipoPonto;

/**
 * Responsável por importar e exportar dados em formato CSV.
 */
public class CSVExporter {

    /**
     * Exporta a lista de pontos para um arquivo CSV.
     */
    public static void exportarPontos(List<Ponto> pontos, File arquivo) throws IOException {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(arquivo), StandardCharsets.UTF_8))) {

            // BOM para compatibilidade com Excel
            pw.print('\uFEFF');
            pw.println("Nome,Tipo,Latitude,Longitude,Alunos");

            for (Ponto p : pontos) {
                pw.printf("%s,%s,%.6f,%.6f,%d%n",
                        escapar(p.getNome()),
                        p.getTipo().getRotulo(),
                        p.getLatitude(),
                        p.getLongitude(),
                        p.getQuantidadeAlunos());
            }
        }
    }

    /**
     * Exporta os resultados da rota para CSV.
     */
    public static void exportarResultado(
            Rota rota,
            String algoritmo,
            File arquivo
    ) throws IOException {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(arquivo), StandardCharsets.UTF_8))) {

            pw.print('\uFEFF');
            pw.println("=== RELATÓRIO DE ROTA ===");
            pw.println("Algoritmo: " + algoritmo);
            pw.println("Distância Total (km): " + String.format("%.2f", rota.getDistanciaTotal()));
            pw.println("Tempo Total (min): " + String.format("%.2f", rota.getTempoTotal()));
            pw.println("Estudantes Atendidos: " + rota.calcularTotalAlunos());
            pw.println("Número de Paradas: " + rota.getPercurso().size());
            pw.println();
            pw.println("Ordem,Parada,Tipo,Latitude,Longitude,Alunos");

            int ordem = 1;
            for (Ponto p : rota.getPercurso()) {
                pw.printf("%d,%s,%s,%.6f,%.6f,%d%n",
                        ordem++,
                        escapar(p.getNome()),
                        p.getTipo().getRotulo(),
                        p.getLatitude(),
                        p.getLongitude(),
                        p.getQuantidadeAlunos());
            }
        }
    }

    /**
     * Importa pontos de um arquivo CSV.
     */
    public static List<Ponto> importarPontos(File arquivo) throws IOException {
        List<Ponto> pontos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(arquivo), StandardCharsets.UTF_8))) {

            String linha = br.readLine(); // cabeçalho
            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(",");
                if (campos.length >= 5) {
                    String nome = campos[0].trim();
                    TipoPonto tipo = parseTipo(campos[1].trim());
                    double lat = Double.parseDouble(campos[2].trim());
                    double lon = Double.parseDouble(campos[3].trim());
                    int alunos = Integer.parseInt(campos[4].trim());
                    pontos.add(new Ponto(nome, lat, lon, alunos, tipo));
                }
            }
        }
        return pontos;
    }

    private static TipoPonto parseTipo(String tipo) {
        String valor = tipo.toLowerCase();
        switch (valor) {
            case "escola":
                return TipoPonto.ESCOLA;
            case "universidade":
                return TipoPonto.UNIVERSIDADE;
            case "bairro":
                return TipoPonto.BAIRRO;
            default:
                return TipoPonto.PONTO_EMBARQUE;
        }
    }

    private static String escapar(String valor) {
        if (valor.contains(",") || valor.contains("\"")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
