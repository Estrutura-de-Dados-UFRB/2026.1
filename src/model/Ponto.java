package model;

import java.util.Objects;
import java.util.UUID;

/**
 * Vertice do grafo e item exibido no mapa.
 */
public class Ponto {

    private final String id;
    private String nome;
    private String bairro;
    private double latitude;
    private double longitude;
    private int quantidadeAlunos;
    private int quantidadeDesembarque;
    private int capacidade;
    private int prioridade;
    private String turno;
    private double distanciaMedia;
    private double tempoMedio;
    private TipoPonto tipo;

    public Ponto(String nome,
                 String bairro,
                 double latitude,
                 double longitude,
                 int quantidadeAlunos,
                 int capacidade,
                 int prioridade,
                 String turno,
                 TipoPonto tipo) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do ponto nao pode ser vazio.");
        }
        if (quantidadeAlunos < 0) {
            throw new IllegalArgumentException("Quantidade de alunos invalida.");
        }
        this.id = UUID.randomUUID().toString();
        this.nome = nome.trim();
        this.bairro = bairro != null ? bairro.trim() : "";
        this.latitude = latitude;
        this.longitude = longitude;
        this.quantidadeAlunos = quantidadeAlunos;
        this.quantidadeDesembarque = 0;
        this.capacidade = Math.max(capacidade, 0);
        this.prioridade = Math.max(prioridade, 0);
        this.turno = turno != null ? turno.trim() : "";
        this.tipo = tipo != null ? tipo : TipoPonto.OUTRO;
    }

    public Ponto(String nome,
                 String bairro,
                 double latitude,
                 double longitude,
                 int quantidadeAlunos,
                 TipoPonto tipo) {
        this(nome, bairro, latitude, longitude, quantidadeAlunos, quantidadeAlunos, 1, "", tipo);
    }

    public Ponto(String nome, double latitude, double longitude, int quantidadeAlunos, TipoPonto tipo) {
        this(nome, "", latitude, longitude, quantidadeAlunos, quantidadeAlunos, 1, "", tipo);
    }

    public Ponto(String nome, double latitude, double longitude, int quantidadeAlunos) {
        this(nome, "", latitude, longitude, quantidadeAlunos, quantidadeAlunos, 1, "", TipoPonto.PONTO_EMBARQUE);
    }

    public String getNome() {
        return nome;
    }

    public String getId() {
        return id;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do ponto nao pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro != null ? bairro.trim() : "";
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getQuantidadeAlunos() {
        return quantidadeAlunos;
    }

    public void setQuantidadeAlunos(int quantidadeAlunos) {
        if (quantidadeAlunos < 0) {
            throw new IllegalArgumentException("Quantidade invalida.");
        }
        this.quantidadeAlunos = quantidadeAlunos;
    }

    public int getQuantidadeDesembarque() {
        return quantidadeDesembarque;
    }

    public void setQuantidadeDesembarque(int quantidadeDesembarque) {
        if (quantidadeDesembarque < 0) {
            throw new IllegalArgumentException("Quantidade de desembarque invalida.");
        }
        this.quantidadeDesembarque = quantidadeDesembarque;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = Math.max(capacidade, 0);
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = Math.max(prioridade, 0);
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno != null ? turno.trim() : "";
    }

    public double getDistanciaMedia() {
        return distanciaMedia;
    }

    public void setDistanciaMedia(double distanciaMedia) {
        this.distanciaMedia = Math.max(distanciaMedia, 0.0);
    }

    public double getTempoMedio() {
        return tempoMedio;
    }

    public void setTempoMedio(double tempoMedio) {
        this.tempoMedio = Math.max(tempoMedio, 0.0);
    }

    public TipoPonto getTipo() {
        return tipo;
    }

    public void setTipo(TipoPonto tipo) {
        this.tipo = tipo != null ? tipo : TipoPonto.OUTRO;
    }

    public String getDescricaoCompleta() {
        return String.format("%s | %s | %s", nome, tipo.getRotulo(), bairro);
    }

    @Override
    public String toString() {
        return getDescricaoCompleta();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Ponto)) {
            return false;
        }
        Ponto outro = (Ponto) obj;
        return Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
