package model;

/**
 * Aresta do grafo com metadados de rota.
 */
public class Aresta {

    private final Ponto origem;
    private final Ponto destino;
    private double distancia;
    private double tempoEstimado;
    private double peso;

    public Aresta(Ponto origem, Ponto destino, double distancia) {
        this(origem, destino, distancia, 40.0);
    }

    public Aresta(Ponto origem, Ponto destino, double distancia, double velocidadeMedia) {
        if (origem == null || destino == null) {
            throw new IllegalArgumentException("Origem e destino sao obrigatorios.");
        }
        this.origem = origem;
        this.destino = destino;
        this.distancia = Math.max(distancia, 0.0);
        this.tempoEstimado = velocidadeMedia > 0 ? (this.distancia / velocidadeMedia) * 60.0 : 0.0;
        this.peso = this.distancia;
    }

    public Ponto getOrigem() {
        return origem;
    }

    public Ponto getDestino() {
        return destino;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = Math.max(distancia, 0.0);
    }

    public double getTempoEstimado() {
        return tempoEstimado;
    }

    public void setTempoEstimado(double tempoEstimado) {
        this.tempoEstimado = Math.max(tempoEstimado, 0.0);
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = Math.max(peso, 0.0);
    }

    @Override
    public String toString() {
        return origem.getNome() + " -> " + destino.getNome() + " (" + String.format("%.2f", distancia) + " km)";
    }
}
