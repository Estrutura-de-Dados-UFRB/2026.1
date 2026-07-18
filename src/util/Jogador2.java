package util;

// Representa um jogador e gerencia sua mão de peças de dominó.
import java.util.ArrayList;
import java.util.List;

public class Jogador2 {

    private Lista<Pedras2> mao = new Lista<>();

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }   

    public void setPedra(Pedras2 pedra) {
        this.mao.addFim(pedra);
    }

    public Pedras2 getPedra(int indiceMao) {
        return getPedraMao(indiceMao);
    }

    public void mostraMao() {
        System.out.println("Mostrando a mão do jogador");

        Node<Pedras2> head = mao.getHead();

        if (head.getNext() == null) {
            System.out.println("Lista vazia...");
        } else {
            Node<Pedras2> aux = head.getNext();

            while (aux != null) {
                Pedras2 pedraAtual = aux.getValue();

                if (!pedraAtual.isJogada()) {
                    System.out.print("-[" + pedraAtual.getLadoA() + "|" + pedraAtual.getLadoB() + "]-");
                } else {
                    System.out.print("-[  ]-");
                }

                aux = aux.getNext();
            }

            System.out.println();
        }

        System.out.println();
    }

    public Pedras2 getPedraMao(int indice) {
        if (indice < 0 || indice >= getQuantidadePedras()) {
            throw new IndexOutOfBoundsException("Indice de pedra invalido: " + indice);
        }

        Node<Pedras2> atual = mao.getHead().getNext();

        for (int x = 0; x < indice; x++) {
            atual = atual.getNext();
        }

        return atual.getValue();
    }

    public int getQuantidadePedras() {
        int quantidade = 0;
        Node<Pedras2> atual = mao.getHead().getNext();

        while (atual != null) {
            quantidade++;
            atual = atual.getNext();
        }

        return quantidade;
    }

    public List<Pedras2> getPedrasDisponiveis() {
        List<Pedras2> pedras = new ArrayList<>();
        Node<Pedras2> atual = mao.getHead().getNext();

        // Retorna apenas as pedras que ainda nao foram colocadas na mesa.
        while (atual != null) {
            Pedras2 pedra = atual.getValue();

            if (!pedra.isJogada()) {
                pedras.add(pedra);
            }

            atual = atual.getNext();
        }

        return pedras;
    }

    public int getPontuacaoRestante() {
        int pontuacao = 0;
        Node<Pedras2> atual = mao.getHead().getNext();

        // Soma os dois lados das pedras restantes, usada para desempate em jogo trancado.
        while (atual != null) {
            Pedras2 pedra = atual.getValue();

            if (!pedra.isJogada()) {
                pontuacao += pedra.getLadoA() + pedra.getLadoB();
            }

            atual = atual.getNext();
        }

        return pontuacao;
    }

    public boolean checaSePodeJogar(Pedras2 cabeca, Pedras2 cauda) {
        int pedraCabeca = cabeca.getLadoA();
        int pedraCauda = cauda.getLadoB();

        Node<Pedras2> atual = mao.getHead().getNext();

        // Verifica se alguma pedra da mao encaixa em uma das pontas atuais da mesa.
        while (atual != null) {
            Pedras2 pedraAtual = atual.getValue();

            if (!pedraAtual.isJogada()) {
                int ladoA = pedraAtual.getLadoA();
                int ladoB = pedraAtual.getLadoB();

                if (ladoA == pedraCabeca || ladoA == pedraCauda ||
                    ladoB == pedraCabeca || ladoB == pedraCauda) {

                    return true;
                }
            }

            atual = atual.getNext();
        }

        return false;
    }

    public boolean checaVitoria() {
        Node<Pedras2> atual = mao.getHead().getNext();

        while (atual != null) {
            Pedras2 pedraAtual = atual.getValue();

            if (!pedraAtual.isJogada()) {
                return false;
            }

            atual = atual.getNext();
        }

        return true;
    }
}
