package model;

/**
 * Tipos de pontos exibidos no sistema.
 */
public enum TipoPonto {
    ESCOLA("Escola"),
    UNIVERSIDADE("Universidade"),
    BAIRRO("Bairro"),
    PONTO_EMBARQUE("Ponto de embarque"),
    OUTRO("Outro");

    private final String rotulo;

    TipoPonto(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
