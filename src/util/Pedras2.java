package util;

// Modelo de uma peça de dominó com lados A/B e estado de disponibilidade.
public class Pedras2 {
    private int ladoA;
    private int ladoB;
    // Se já foi cavada
    private boolean disponivel = true;
    // Se já foi posta na mesa
    private boolean jogada = false;

    public void setPedra(int ladoA, int ladoB) {
        this.ladoA = ladoA;
        this.ladoB = ladoB;
        this.disponivel = true;
    }

    public int getLadoA() {
        return ladoA;
    }

    public int getLadoB() {
        return ladoB;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setJogada(boolean jogada){
        this.jogada = jogada;
    }

    public boolean isJogada(){
        return jogada;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public void swap(){
        int aux = this.ladoA;
        this.ladoA = this.ladoB;
        this.ladoB = aux;
    }

    @Override
    public String toString() {
        return "[" + ladoA + "|" + ladoB + "]";
    }
}
