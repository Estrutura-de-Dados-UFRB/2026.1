package model;

import java.util.Objects;

public class Estacao {
    private String id;
    private String nome;
    private String linha;
    private boolean ativa;

    public Estacao(String id, String nome, String linha) {
        this.id = id;
        this.nome = nome;
        this.linha = linha;
        this.ativa = true;
    }

    public Estacao(String id, String nome, String linha, boolean ativa) {
        this.id = id;
        this.nome = nome;
        this.linha = linha;
        this.ativa = ativa;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLinha() {
        return linha;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estacao estacao = (Estacao) o;
        return Objects.equals(id, estacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Estacao{id='%s', nome='%s', linha='%s', ativa=%s}".formatted(id, nome, linha, ativa);
    }
}