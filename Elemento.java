package com.mycompany.sistemacoletaurbana;

public class Elemento {
    private Vertice vertice;
    private Elemento proximo;
    
    public Elemento(Vertice novoValor){
        this.vertice = novoValor;
    }

    public Vertice getVertice() {
        return vertice;
    }

    public void setVertice(Vertice valor) {
        this.vertice = valor;
    }

    public Elemento getProximo() {
        return proximo;
    }

    public void setProximo(Elemento proximo) {
        this.proximo = proximo;
    }
}
