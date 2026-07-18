package model;

public class Onibus {

    private String placa;
    private int capacidade;
    private Rota rota;

    public Onibus(String placa, int capacidade) {

        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "A placa não pode ser vazia."
            );
        }

        if (capacidade <= 0) {
            throw new IllegalArgumentException(
                    "A capacidade deve ser maior que zero."
            );
        }

        this.placa = placa;
        this.capacidade = capacidade;
    }

    public String getPlaca() {
        return placa;
    }


    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getCapacidade() {
        return capacidade;
    }


    public void setCapacidade(int capacidade) {

        if (capacidade <= 0) {
            throw new IllegalArgumentException(
                    "Capacidade inválida."
            );
        }

        this.capacidade = capacidade;
    }


    public void setRota(Rota rota) {
        this.rota = rota;
    }


    public Rota getRota() {
        return rota;
    }

    @Override
    public String toString() {

        String rotaInfo =
                (rota != null)
                        ? "Rota definida"
                        : "Sem rota";

        return "Ônibus {" +
                "placa='" + placa + '\'' +
                ", capacidade=" + capacidade +
                ", statusRota=" + rotaInfo +
                '}';
    }
}