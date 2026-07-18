package util;

// Utilitário de mesa não usado no fluxo atual; mantém peças jogadas em lista encadeada.
public class Mesa {

    private Lista<Pedras2> mesa = new Lista<>();

    public void setPedraMesa(Pedras2 pedra) {
        this.mesa.addFim(pedra);
        pedra.setDisponivel(false);
    }

    public void mostraMesa() {
        System.out.println("Mostrando a Mesa");
        this.mesa.showListFim();
    }
}