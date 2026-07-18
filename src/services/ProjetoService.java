package services;

import model.Aresta;
import model.Grafo;
import model.Ponto;
import model.TipoPonto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProjetoService {

    public void salvar(Grafo grafo, File arquivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write("# SISTEMA_DE_ROTAS\n");
            for (Ponto ponto : grafo.getPontos()) {
                writer.write(String.join("|",
                        "PONTO",
                        escape(ponto.getNome()),
                        escape(ponto.getBairro()),
                        String.valueOf(ponto.getLatitude()),
                        String.valueOf(ponto.getLongitude()),
                        String.valueOf(ponto.getQuantidadeAlunos()),
                        String.valueOf(ponto.getCapacidade()),
                        String.valueOf(ponto.getPrioridade()),
                        escape(ponto.getTurno()),
                        ponto.getTipo().name()));
                writer.write("\n");
            }
            for (Aresta aresta : grafo.getArestas()) {
                writer.write(String.join("|",
                        "ARESTA",
                        escape(aresta.getOrigem().getNome()),
                        escape(aresta.getDestino().getNome()),
                        String.valueOf(aresta.getDistancia())));
                writer.write("\n");
            }
        }
    }

    public Grafo carregar(File arquivo) throws IOException {
        Map<String, Ponto> pontos = new LinkedHashMap<>();
        List<String[]> arestas = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty() || linha.startsWith("#")) {
                    continue;
                }
                String[] partes = linha.split("\\|", -1);
                if (partes.length == 0) {
                    continue;
                }
                if ("PONTO".equals(partes[0]) && partes.length >= 10) {
                    boolean formatoNovo = partes.length >= 11;
                    Ponto ponto = new Ponto(
                            unescape(partes[1]),
                            unescape(partes[2]),
                            Double.parseDouble(partes[3]),
                            Double.parseDouble(partes[4]),
                            Integer.parseInt(partes[5]),
                            Integer.parseInt(partes[formatoNovo ? 7 : 6]),
                            Integer.parseInt(partes[formatoNovo ? 8 : 7]),
                            unescape(partes[formatoNovo ? 9 : 8]),
                            TipoPonto.valueOf(partes[formatoNovo ? 10 : 9]));
                    if (formatoNovo) {
                        ponto.setQuantidadeDesembarque(Integer.parseInt(partes[6]));
                    }
                    pontos.put(ponto.getNome(), ponto);
                } else if ("ARESTA".equals(partes[0]) && partes.length >= 4) {
                    arestas.add(partes);
                }
            }
        }

        Grafo grafo = new Grafo();
        for (Ponto ponto : pontos.values()) {
            grafo.adicionarPonto(ponto);
        }
        for (String[] partes : arestas) {
            Ponto origem = pontos.get(unescape(partes[1]));
            Ponto destino = pontos.get(unescape(partes[2]));
            double distancia = Double.parseDouble(partes[3]);
            if (origem != null && destino != null) {
                grafo.adicionarAresta(origem, destino, distancia);
            }
        }
        return grafo;
    }

    private String escape(String valor) {
        return valor == null ? "" : valor.replace("|", "/");
    }

    private String unescape(String valor) {
        return valor == null ? "" : valor.replace("/", "|");
    }
}
