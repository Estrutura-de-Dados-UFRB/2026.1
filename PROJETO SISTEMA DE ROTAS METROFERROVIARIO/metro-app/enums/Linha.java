package enums;

/**
 * Enumeração das linhas do metrô de São Paulo.
 */
public enum Linha {
    L1_AZUL("Linha 1 - Azul", "METRÔ", 2.0),
    L2_VERDE("Linha 2 - Verde", "METRÔ", 2.0),
    L3_VERMELHA("Linha 3 - Vermelha", "METRÔ", 2.0),
    L4_AMARELA("Linha 4 - Amarela", "VIAQUATRO", 2.5),
    L5_LILAS("Linha 5 - Lilás", "VIAMOBILIDADE", 2.0),
    L7_RUBI("Linha 7 - Rubi", "TIC TRENS", 3.0),
    L8_DIAMANTE("Linha 8 - Diamante", "VIAMOBILIDADE", 3.0),
    L9_ESMERALDA("Linha 9 - Esmeralda", "VIAMOBILIDADE", 2.5),
    L10_TURQUESA("Linha 10 - Turquesa", "CPTM", 3.0),
    L11_CORAL("Linha 11 - Coral", "CPTM", 3.0),
    L12_SAFIRA("Linha 12 - Safira", "CPTM", 3.0),
    L13_JADE("Linha 13 - Jade", "CPTM", 4.0),
    L15_PRATA("Linha 15 - Prata", "METRÔ", 2.0),
    L17_OURO("Linha 17 - Ouro", "METRÔ", 2.0);

    public final String nome;
    public final String operadora;
    public final double pesoPadrao; // minutos entre estações consecutivas

    Linha(String nome, String operadora, double pesoPadrao) {
        this.nome = nome;
        this.operadora = operadora;
        this.pesoPadrao = pesoPadrao;
    }
}