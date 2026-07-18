package util;

// Lista encadeada utilizada para armazenar peças e jogadores.
public class Lista<T> {

    public Node<T> head = new Node<>();
    public Node<T> tail = new Node<>();

    public Node<T> getHead() {
        return head;
    }

    public Node<T> getTail() {
        return tail;
    }

    public void addFim(T valor) {
        // A lista usa um no cabeca; o primeiro valor real fica em head.next.
        if (head.getNext() == null) {
            tail.setValue(valor);
            tail.setNext(null);
            head.setNext(tail);
        } else {
            Node<T> atual = new Node<>();
            atual.setValue(valor);
            atual.setNext(null);

            tail.setNext(atual);
            tail = atual;
        }
    }

    public void addInicio(T valor) {
        // Insere logo apos o no cabeca para representar a ponta esquerda da mesa.
        if (head.getNext() == null) {
            tail.setValue(valor);
            tail.setNext(null);
            head.setNext(tail);
        } else {
            Node<T> atual = new Node<>();
            atual.setValue(valor);

            Node<T> antigoProx = head.getNext();

            head.setNext(atual);
            atual.setNext(antigoProx);
        }
    }

    public void showListFim() {
        if (head.getNext() == null) {
            System.out.println("Lista vazia...");
        } else {
            Node<T> aux = head.getNext();

            if (aux.getValue() instanceof Pedras2) {
                while (aux != null) {
                    Pedras2 pedra = (Pedras2) aux.getValue();

                    System.out.print("-[" + pedra.getLadoA() + "|" + pedra.getLadoB() + "]-");

                    aux = aux.getNext();
                }

                System.out.println();
            } else {
                System.out.println("Imprimindo a Lista");

                while (aux != null) {
                    System.out.println(aux.getValue());
                    aux = aux.getNext();
                }
            }
        }
    }

    public String mostraMesa() {
        String mensagem = "";

        Node<T> headAux = this.head;

        if (headAux.getNext() == null) {
            System.out.println("Lista vazia...");
        } else {
            Node<T> aux = headAux.getNext();

            if (aux.getValue() instanceof Pedras2) {
                while (aux != null) {
                    Pedras2 pedra = (Pedras2) aux.getValue();

                    mensagem += "-[" + pedra.getLadoA() + "|" + pedra.getLadoB() + "]-";

                    aux = aux.getNext();
                }

                System.out.println();
            }
        }

        return mensagem;
    }
}
