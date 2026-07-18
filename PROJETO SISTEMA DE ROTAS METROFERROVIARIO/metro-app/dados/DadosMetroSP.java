package dados;

import enums.Linha;
import grafo.RedeMetro;
import model.Conexao;
import model.Estacao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Carregador de dados da rede de metrô de São Paulo.
 * <p>
 * Fornece métodos estáticos para popular uma {@link RedeMetro} com as estações
 * e conexões do sistema metroviário da cidade de São Paulo, conforme mapa
 * oficial de março/2026.
 *
 * @see Linha
 */
public class DadosMetroSP {

    // Pesos de baldeação (estimados)
    private static final double BALDEACAO_GRATUITA = 3.0;
    private static final double BALDEACAO_HORARIA  = 5.0;

    /**
     * Carrega a rede completa de São Paulo.
     *
     * @return RedeMetro totalmente populada
     */
    public static RedeMetro carregar() {
        RedeMetro rede = new RedeMetro();
        Map<String, Double> pesoTrecho = new HashMap<>();

        // Adicionar estações e conexões linha por linha
        adicionarLinha(rede, Linha.L1_AZUL, List.of(
            "Tucuruvi", "Parada Inglesa", "Jd. São Paulo-Ayrton Senna", "Santana",
            "Carandiru", "Portuguesa-Tietê", "Armênia", "Tiradentes", "Luz",
            "São Bento", "Sé", "Japão-Liberdade", "São Joaquim", "Vergueiro",
            "Paraíso", "Ana Rosa", "Vila Mariana", "Santa Cruz", "Praça da Árvore", "Saúde",
            "São Judas", "Conceição", "Jabaquara"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L2_VERDE, List.of(
            "Vila Madalena", "Sumaré", "Clínicas", "Consolação", "Paulista",
            "Trianon-Masp", "Brigadeiro", "Paraíso", "Ana Rosa",
            "Chácara Klabin", "Alto do Ipiranga", "Santos-Imigrantes",
            "Sacomã", "Vila Prudente", "Tamanduateí"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L3_VERMELHA, List.of(
            "Palmeiras-Barra Funda", "Marechal Deodoro", "Santa Cecília",
            "República", "Anhangabaú", "Sé", "Pedro II", "Brás",
            "Bresser-Mooca", "Belém", "Tatuapé", "Carrão", "Penha",
            "Vila Matilde", "Guilhermina-Esperança", "Patriarca-Vila Ré",
            "Artur Alvim", "Corinthians-Itaquera"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L4_AMARELA, List.of(
            "Taboão da Serra", "Vila Sônia", "São Paulo-Morumbi", "Butantã",
            "Pinheiros", "Faria Lima", "Paulista", "Oscar Freire",
            "Fradique Coutinho", "Consolação", "Higienópolis-Mackenzie",
            "República", "Luz"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L5_LILAS, List.of(
            "Capão Redondo", "Campo Limpo", "Vila das Belezas",
            "Giovanni Gronchi", "Santo Amaro", "Largo Treze",
            "Adolfo Pinheiro", "Alto da Boa Vista", "Borba Gato",
            "Brooklin", "Campo Belo", "Eucaliptos", "Moema",
            "AACD-Servidor", "Hospital São Paulo", "Santa Cruz",
            "Chácara Klabin"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L7_RUBI, List.of(
            "Jundiaí", "Várzea Paulista", "Campo Limpo Paulista", "Botujuru",
            "Francisco Morato", "Baltazar Fidélis", "Franco da Rocha", "Caieiras",
            "Perus", "Vila Aurora", "Jaraguá", "Vila Clarice", "Pirituba",
            "Piqueri", "Lapa", "Água Branca", "Palmeiras-Barra Funda"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L8_DIAMANTE, List.of(
            "Amador Bueno", "Ambuitá", "Santa Rita", "Itapevi", "Engº Cardoso",
            "Sagrado Coração", "Jandira", "Jd. Silveira", "Jd. Belval", "Barueri",
            "Antonio João", "Santa Terezinha", "Carapicuíba", "Gen. Miguel Costa",
            "Quitaúna", "Com. Sampaio", "Osasco", "Pres. Altino",
            "Imperatriz Leopoldina", "Domingos de Moraes", "Lapa", "Palmeiras-Barra Funda", 
            "Júlio Prestes"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L9_ESMERALDA, List.of(
            "Varginha", "Mendes-Vila Natal", "Grajaú", "Primavera-Interlagos",
            "Autódromo", "Jurubatuba", "Socorro", "Santo Amaro", "João Dias", "Granja Julieta",
            "Morumbi", "Berrini", "Vila Olímpia", "Cidade Jardim", "Hebraica-Rebouças",
            "Pinheiros", "Cidade Universitária", "Villa Lobos-Jaguaré", "Ceasa", "Osasco", "Pres. Altino"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L10_TURQUESA, List.of(
           "Rio Grande da Serra", "Ribeirão Pires", "Guapituba", "Mauá", "Capuava",
           "Santo André", "Prefeito Saladino", "Utinga", "São Caetano do Sul", "Tamanduateí",
           "Vila Prudente", "Ipiranga", "Juventus-Mooca", "Brás", "Luz", "Palmeiras-Barra Funda"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L11_CORAL, List.of(
            "Estudantes", "Mogi das Cruzes", "Braz Cubas", "Jundiapeba", "Suzano", "Calmon Viana",
            "Poá", "Ferraz de Vasconcelos", "Antonio Gianetti Neto", "Guaianases", "José Bonifácio",
            "Dom Bosco", "Corinthians-Itaquera", "Tatuapé", "Brás", "Luz", "Palmeiras-Barra Funda"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L12_SAFIRA, List.of(
            "Calmon Viana", "Aracaré", "Itaquaquecetuba", "Engº Manoel Feio", "Jardim Romano", "Itaim Paulista",
            "Jd. Helena-Vila Mara", "São Miguel Paulista", "Comendador Ermelino", "USP Leste", "Engº Goulart",
            "Tatuapé", "Brás"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L13_JADE, List.of(
            "Aeroporto-Guarulhos", "Guarulhos-Cecap", "Engº Goulart"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L15_PRATA, List.of(
            "Jd. Colonial", "São Mateus", "Fazenda da Juta", "Sapopemba", "Jd. Planalto",
            "Vila União", "Vila Tolstói", "Camilo Haddad", "São Lucas", "Oratório", "Vila Prudente"
        ), pesoTrecho);

        adicionarLinha(rede, Linha.L17_OURO, List.of(
            "Aeroporto de Congonhas", "Brooklin Paulista", "Vereador José Diniz", "Campo Belo",
            "Vila Cordeiro", "Chucri Zaidan", "Morumbi"
        ), pesoTrecho);

        // Integrações entre linhas (baldeações gratuitas — 2 linhas)
        integrar(rede, "Sé", List.of("L1_AZUL", "L3_VERMELHA"), BALDEACAO_GRATUITA);
        integrar(rede, "Paraíso", List.of("L1_AZUL", "L2_VERDE"), BALDEACAO_GRATUITA);
        integrar(rede, "Ana Rosa", List.of("L1_AZUL", "L2_VERDE"), BALDEACAO_GRATUITA);
        integrar(rede, "Paulista", List.of("L2_VERDE", "L4_AMARELA"), BALDEACAO_GRATUITA);
        integrar(rede, "Consolação", List.of("L2_VERDE", "L4_AMARELA"), BALDEACAO_GRATUITA);
        integrar(rede, "República", List.of("L3_VERMELHA", "L4_AMARELA"), BALDEACAO_GRATUITA);
        integrar(rede, "Pinheiros", List.of("L4_AMARELA", "L9_ESMERALDA"), BALDEACAO_GRATUITA);
        integrar(rede, "Chácara Klabin", List.of("L2_VERDE", "L5_LILAS"), BALDEACAO_GRATUITA);
        integrar(rede, "Santo Amaro", List.of("L5_LILAS", "L9_ESMERALDA"), BALDEACAO_GRATUITA);
        integrar(rede, "Vila Prudente", List.of("L2_VERDE", "L15_PRATA"), BALDEACAO_GRATUITA);
        integrar(rede, "Tamanduateí", List.of("L2_VERDE", "L10_TURQUESA"), BALDEACAO_GRATUITA);
        integrar(rede, "Osasco", List.of("L8_DIAMANTE", "L9_ESMERALDA"), BALDEACAO_GRATUITA);
        integrar(rede, "Pres. Altino", List.of("L8_DIAMANTE", "L9_ESMERALDA"), BALDEACAO_GRATUITA);
        integrar(rede, "Morumbi", List.of("L9_ESMERALDA", "L17_OURO"), BALDEACAO_GRATUITA);
        integrar(rede, "Campo Belo", List.of("L17_OURO", "L5_LILAS"), BALDEACAO_GRATUITA);
        integrar(rede, "Santa Cruz", List.of("L1_AZUL", "L5_LILAS"), BALDEACAO_GRATUITA);
        integrar(rede, "Palmeiras-Barra Funda", List.of("L7_RUBI", "L8_DIAMANTE",
         "L10_TURQUESA", "L11_CORAL", "L3_VERMELHA"), BALDEACAO_GRATUITA);
        integrar(rede, "Luz", List.of("L1_AZUL", "L11_CORAL", "L10_TURQUESA", "L4_AMARELA"), BALDEACAO_GRATUITA);
        integrar(rede, "Brás", List.of("L12_SAFIRA", "L11_CORAL", "L10_TURQUESA", "L3_VERMELHA"), BALDEACAO_GRATUITA);
        integrar(rede, "Tatuapé", List.of("L12_SAFIRA", "L11_CORAL"), BALDEACAO_GRATUITA);
        integrar(rede, "Engº Goulart", List.of("L12_SAFIRA", "L13_JADE"), BALDEACAO_GRATUITA);
        integrar(rede, "Calmon Viana", List.of("L12_SAFIRA", "L11_CORAL"), BALDEACAO_GRATUITA);

        // Integrações horárias (mais caras)
        integrar(rede, "Tatuapé", List.of("L3_VERMELHA", "L11_CORAL"), BALDEACAO_HORARIA);
        integrar(rede, "Corinthians-Itaquera", List.of("L3_VERMELHA", "L11_CORAL"), BALDEACAO_HORARIA);

        return rede;
    }

    private static void adicionarLinha(RedeMetro rede, Linha linha, List<String> estacoes, Map<String, Double> pesoTrecho) {
        double peso = linha.pesoPadrao;
        for (int i = 0; i < estacoes.size(); i++) {
            String nome = estacoes.get(i);
            String id = idEstacao(nome, linha);
            if (rede.getEstacao(id) == null) {
                rede.adicionarEstacao(new Estacao(id, nome, linha.nome));
            }
            if (i > 0) {
                String idAnt = idEstacao(estacoes.get(i - 1), linha);
                rede.adicionarConexao(idAnt, id, peso);
                rede.adicionarConexao(id, idAnt, peso);
            }
        }
    }

    /**
     * Cria conexões de baldeação entre todas as combinações de estações-nó
     * que representam o mesmo ponto físico em linhas diferentes.
     *
     * @param rede         rede de metrô
     * @param estacaoNome  nome físico da estação de integração
     * @param linhasCod    lista de nomes dos valores do enum Linha (ex: "L1_AZUL")
     * @param peso         custo da baldeação
     */
    private static void integrar(RedeMetro rede, String estacaoNome, List<String> linhasCod, double peso) {
        for (int i = 0; i < linhasCod.size(); i++) {
            for (int j = i + 1; j < linhasCod.size(); j++) {
                String idA = linhasCod.get(i) + ":" + estacaoNome.replace(" ", "_");
                String idB = linhasCod.get(j) + ":" + estacaoNome.replace(" ", "_");
                if (rede.getEstacao(idA) == null || rede.getEstacao(idB) == null) continue;
                rede.adicionarConexao(idA, idB, peso);
                rede.adicionarConexao(idB, idA, peso);
            }
        }
    }

    private static String idEstacao(String nome, Linha linha) {
        return linha.name() + ":" + nome.replace(" ", "_");
    }
}