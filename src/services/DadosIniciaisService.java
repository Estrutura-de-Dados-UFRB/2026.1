package services;

import model.Aresta;
import model.Grafo;
import model.Ponto;
import model.TipoPonto;
import util.GeoUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DadosIniciaisService {

    private static final String ARQUIVO_VERTICES = "dados/vertices.csv";
    private static final int TOTAL_ALUNOS_PADRAO = 1500;

    private DadosIniciaisService() {
    }

    public static List<Ponto> criarPontosPadrao() {
        List<Ponto> pontos = criarPontosHardcoded();
        Map<String, VerticeCsv> verticesCsv = carregarVerticesCsv();
        if (!verticesCsv.isEmpty()) {
            for (Ponto ponto : pontos) {
                VerticeCsv dados = verticesCsv.get(normalizarNome(ponto.getNome()));
                if (dados != null) {
                    ponto.setLatitude(dados.latitude);
                    ponto.setLongitude(dados.longitude);
                    ponto.setQuantidadeAlunos(dados.estudantes);
                    ponto.setTipo(dados.tipo);
                }
            }
        }
        aplicarDistribuicaoAlunosPadrao(pontos);
        return pontos;
    }

    private static void aplicarDistribuicaoAlunosPadrao(List<Ponto> pontos) {
        distribuirAlunos(pontos, TOTAL_ALUNOS_PADRAO);
    }

    private static void distribuirAlunos(List<Ponto> pontos, int total) {
        int somaPesos = 0;
        for (Ponto ponto : pontos) {
            somaPesos += Math.max(1, ponto.getQuantidadeAlunos());
        }

        int restante = total;
        for (int i = 0; i < pontos.size(); i++) {
            Ponto ponto = pontos.get(i);
            int valor;
            if (i == pontos.size() - 1) {
                valor = restante;
            } else {
                int peso = Math.max(1, ponto.getQuantidadeAlunos());
                valor = Math.max(0, (int) Math.round((peso * (double) total) / Math.max(1, somaPesos)));
                valor = Math.min(valor, restante);
            }
            ponto.setQuantidadeAlunos(valor);
            ponto.setQuantidadeDesembarque(0);
            restante -= valor;
        }
    }

    private static List<Ponto> criarPontosHardcoded() {
        List<Ponto> pontos = new ArrayList<>();
        // Objetos reais do OpenStreetMap, datum WGS84, consultados em 12/07/2026.
        pontos.add(new Ponto("CEAT - Centro Educacional Alberto Torres", "Centro", -12.6616899, -39.0993240, 420, 500, 5, "Manhã/Tarde", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Colégio Montessori", "Centro", -12.6691435, -39.1013517, 260, 300, 3, "Manhã", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Centro Educacional Cruzalmense", "Centro", -12.6685736, -39.1060494, 310, 360, 4, "Tarde", TipoPonto.ESCOLA));
        pontos.add(new Ponto("CCAA", "Centro", -12.6722576, -39.1043217, 80, 120, 3, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("CEMAM - Centro Educacional Maria Milza", "Centro", -12.6719797, -39.1009655, 180, 240, 4, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Colégio Contemporâneo", "Centro", -12.6765230, -39.0995860, 180, 240, 3, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Colégio Cruz das Almas", "Centro", -12.6709379, -39.1089355, 220, 300, 4, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Colégio Municipal Jorge Guerra", "Centro", -12.6689885, -39.1133049, 240, 320, 4, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Colégio Vigildásio Sena", "Centro", -12.6680751, -39.1051911, 210, 280, 4, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Creche Municipal do Vilarejo", "Vilarejo", -12.6710945, -39.1141004, 90, 130, 3, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Escola Comendador Themistocles", "Centro", -12.6719023, -39.1042140, 200, 280, 4, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Escola Lavoisier", "Centro", -12.6641959, -39.1011136, 160, 220, 3, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Escola Municipal Joaquim Medeiros", "UFRB", -12.6598308, -39.0927302, 190, 260, 4, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Escola Recanto Feliz", "Centro", -12.6683263, -39.1055505, 120, 170, 3, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Faculdade Batista", "Centro", -12.6717048, -39.1040406, 160, 220, 3, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Fisk", "Centro", -12.6688085, -39.1046441, 70, 100, 2, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Escola José Batista da Fonseca", "Centro", -12.6652431, -39.1017923, 180, 250, 4, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("Líder Cursos", "Centro", -12.6699347, -39.1025360, 80, 120, 2, "", TipoPonto.ESCOLA));
        pontos.add(new Ponto("UFRB - Universidade Federal do Recôncavo da Bahia", "UFRB", -12.6631654, -39.0799470, 1800, 2200, 5, "Integral", TipoPonto.UNIVERSIDADE));
        pontos.add(new Ponto("Alberto Passos", "Alberto Passos", -12.6769831, -39.0958146, 70, 140, 3, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("Assembléia", "Assembléia", -12.6696420, -39.1108140, 75, 130, 4, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("Cajá", "Cajá", -12.6557446, -39.1260175, 55, 110, 3, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("Centro", "Centro", -12.6745075, -39.1016142, 120, 200, 5, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("INOCOOP", "Inocoop", -12.6576407, -39.1034474, 95, 170, 4, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("Tabela", "Tabela", -12.6517875, -39.0996903, 70, 150, 3, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("Itapicuru", "Itapicuru", -12.6562736, -39.1185154, 60, 120, 3, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("Toquinha", "Toquinha", -12.6597906, -39.1274811, 55, 110, 3, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("Coplan", "Coplan", -12.6605714, -39.1189434, 80, 160, 4, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("Embira", "Embira", -12.6826227, -39.1433214, 50, 100, 2, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("Loteamento Santo Antônio", "Loteamento Santo Antônio", -12.6526301, -39.1175541, 65, 120, 3, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("Loteamento Tancredo Neves", "Loteamento Tancredo Neves", -12.6626230, -39.1339088, 70, 130, 3, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("Primavera", "Primavera", -12.6672260, -39.0988773, 85, 150, 4, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("Sapucaia", "Sapucaia", -12.6617792, -39.0627825, 45, 90, 2, "", TipoPonto.BAIRRO));
        pontos.add(new Ponto("Rodoviária de Cruz das Almas", "Centro", -12.6642076, -39.1129109, 140, 220, 5, "", TipoPonto.PONTO_EMBARQUE));
        pontos.add(new Ponto("Praça Senador Temístocles", "Centro", -12.6729805, -39.1013486, 160, 240, 5, "", TipoPonto.PONTO_EMBARQUE));
        pontos.add(new Ponto("Portaria UFRB", "UFRB", -12.6639377, -39.0999366, 220, 260, 5, "", TipoPonto.PONTO_EMBARQUE));

        return pontos;
    }

    private static Map<String, VerticeCsv> carregarVerticesCsv() {
        Map<String, VerticeCsv> vertices = new HashMap<>();
        File arquivo = new File(ARQUIVO_VERTICES);
        if (!arquivo.exists()) {
            return vertices;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
            String linha = reader.readLine();
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }
                String[] campos = linha.split(",", -1);
                if (campos.length < 6) {
                    continue;
                }
                String nome = campos[1].trim();
                TipoPonto tipo = parseTipo(campos[2].trim());
                double latitude = Double.parseDouble(campos[3].trim());
                double longitude = Double.parseDouble(campos[4].trim());
                int estudantes = Integer.parseInt(campos[5].trim());
                vertices.put(normalizarNome(nome), new VerticeCsv(latitude, longitude, estudantes, tipo));
            }
        } catch (IOException | NumberFormatException ex) {
            return new HashMap<>();
        }
        return vertices;
    }

    private static String normalizarNome(String valor) {
        return valor == null ? "" : java.text.Normalizer.normalize(valor.trim().toLowerCase(),
                java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    private static TipoPonto parseTipo(String valor) {
        String tipo = valor == null ? "" : valor.trim().toUpperCase();
        if ("ESCOLA".equals(tipo)) {
            return TipoPonto.ESCOLA;
        }
        if ("UNIVERSIDADE".equals(tipo)) {
            return TipoPonto.UNIVERSIDADE;
        }
        if ("BAIRRO".equals(tipo)) {
            return TipoPonto.BAIRRO;
        }
        return TipoPonto.PONTO_EMBARQUE;
    }

    public static List<Aresta> criarArestasPadrao(Grafo grafo) {
        List<Aresta> arestas = new ArrayList<>();
        List<Ponto> pontos = new ArrayList<>(grafo.getPontos());
        for (int i = 0; i < pontos.size(); i++) {
            for (int j = i + 1; j < pontos.size(); j++) {
                Ponto a = pontos.get(i);
                Ponto b = pontos.get(j);
                double dist = GeoUtils.haversine(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude());
                if (dist <= 1.8) {
                    arestas.add(new Aresta(a, b, dist));
                }
            }
        }
        adicionarConexoesBase(arestas, grafo);
        garantirConectividade(arestas, pontos);
        return arestas;
    }

    /** Liga pontos isolados ao vizinho geográfico mais próximo sem alterar as demais conexões. */
    private static void garantirConectividade(List<Aresta> arestas, List<Ponto> pontos) {
        for (Ponto ponto : pontos) {
            boolean conectado = arestas.stream().anyMatch(a -> a.getOrigem().equals(ponto) || a.getDestino().equals(ponto));
            if (conectado) continue;
            Ponto maisProximo = null;
            double menorDistancia = Double.MAX_VALUE;
            for (Ponto candidato : pontos) {
                if (ponto.equals(candidato)) continue;
                double distancia = GeoUtils.haversine(ponto.getLatitude(), ponto.getLongitude(),
                        candidato.getLatitude(), candidato.getLongitude());
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    maisProximo = candidato;
                }
            }
            conectarSePossivel(arestas, ponto, maisProximo);
        }
    }

    private static void adicionarConexoesBase(List<Aresta> arestas, Grafo grafo) {
        Ponto centro = encontrar(grafo, "CEAT - Centro Educacional Alberto Torres");
        Ponto ufrb = encontrar(grafo, "UFRB - Universidade Federal do Recôncavo da Bahia");
        Ponto rodoviaria = encontrar(grafo, "Rodoviária de Cruz das Almas");
        Ponto praca = encontrar(grafo, "Praça Senador Temístocles");
        Ponto coplan = encontrar(grafo, "Coplan");
        Ponto inocoop = encontrar(grafo, "Inocoop");

        conectarSePossivel(arestas, centro, praca);
        conectarSePossivel(arestas, centro, rodoviaria);
        conectarSePossivel(arestas, centro, ufrb);
        conectarSePossivel(arestas, centro, coplan);
        conectarSePossivel(arestas, inocoop, ufrb);
    }

    private static void conectarSePossivel(List<Aresta> arestas, Ponto a, Ponto b) {
        if (a == null || b == null) {
            return;
        }
        double dist = GeoUtils.haversine(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude());
        arestas.add(new Aresta(a, b, dist));
    }

    private static Ponto encontrar(Grafo grafo, String nome) {
        for (Ponto ponto : grafo.getPontos()) {
            if (ponto.getNome().equals(nome)) {
                return ponto;
            }
        }
        return null;
    }

    private static final class VerticeCsv {
        private final double latitude;
        private final double longitude;
        private final int estudantes;
        private final TipoPonto tipo;

        private VerticeCsv(double latitude, double longitude, int estudantes, TipoPonto tipo) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.estudantes = estudantes;
            this.tipo = tipo;
        }
    }
}
