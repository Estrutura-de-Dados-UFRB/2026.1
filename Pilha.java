package com.mycompany.sistemacoletaurbana;

public class Pilha {
    private ListaSimplesmenteEncadeada lista;
    
    public Pilha(){
        this.lista = new ListaSimplesmenteEncadeada();
    }
    
    public void adicionar(Vertice novoVertice){
        this.lista.adicionarComeco(novoVertice);
    }
    
    public void remover(){
        this.lista.remover(this.getPrimeiro());
    }
    
    public Vertice getPrimeiro(){
    if (this.lista.getPrimeiro() == null) {
        return null;
    }
    return this.lista.getPrimeiro().getVertice();
    }
}
