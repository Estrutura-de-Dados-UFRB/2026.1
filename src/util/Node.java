package util;

// Nó genérico para a lista encadeada personalizada.
public class Node<T> {
    // Atributos do nó
    private T value;
    private Node<T> next;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }
}
