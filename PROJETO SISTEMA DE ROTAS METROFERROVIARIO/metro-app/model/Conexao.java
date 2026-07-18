package model;

import java.util.Objects;

public class Conexao {
    private Estacao origem;
    private Estacao destino;
    private double peso;
    private boolean bidirecional;

    public Conexao(Estacao origem, Estacao destino, double peso) {
        this(origem, destino, peso, true);
    }

    public Conexao(Estacao origem, Estacao destino, double peso, boolean bidirecional) {
        this.origem = origem;
        this.destino = destino;
        this.peso = peso;
        this.bidirecional = bidirecional;
    }

    public Estacao getOrigem() {
        return origem;
    }

    public Estacao getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

    public boolean isBidirecional() {
        return bidirecional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conexao conexao = (Conexao) o;
        return Objects.equals(origem, conexao.origem) && Objects.equals(destino, conexao.destino);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origem, destino);
    }

    @Override
    public String toString() {
        return "Conexao{origem='%s', destino='%s', peso=%.1f, bidirecional=%s}"
                .formatted(origem.getNome(), destino.getNome(), peso, bidirecional);
    }
}