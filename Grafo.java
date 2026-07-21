package com.mycompany.sistemacoletaurbana;

import java.util.ArrayList;

public class Grafo {
    
    private String nome;
    private ArrayList<Aresta> arestas;
    private ArrayList<Vertice> vertices;
    private Vertice primeiro;
    private Vertice ultimo;
    
    public Grafo(String nome){
        this.nome = nome;
        this.arestas = new ArrayList<Aresta>();
        this.vertices = new ArrayList<Vertice>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<Aresta> getArestas() {
        return arestas;
    }

    public ArrayList<Vertice> getVertices() {
        return vertices;
    }

    public Vertice getPrimeiro() {
        return primeiro;
    }

    public Vertice getUltimo() {
        return ultimo;
    }
    
    public void adicionarVertice(String nome){   
        Vertice novoVertice = new Vertice(nome);
        this.vertices.add(novoVertice);
        atualizarExtremos();
    }
    
    /** Mantem primeiro/ultimo sempre coerentes com a lista de vertices. */
    private void atualizarExtremos(){
        if (this.vertices.isEmpty()){
            this.primeiro = null;
            this.ultimo = null;
        } else {
            this.primeiro = this.vertices.get(0);
            this.ultimo = this.vertices.get(this.vertices.size() - 1);
        }
    }
    
    public void removerVertice(String nome){
        Vertice atual = buscarVertice(nome);
        if (atual == null) {
            return;
        }
        
        // Remove as arestas que CHEGAM nele, limpando tambem a lista do vizinho de origem
        for (Aresta a : new ArrayList<Aresta>(atual.getArestasEntrando())) {
            a.getInicio().removerArestaSaindo(a);
            this.arestas.remove(a);
        }
        // Remove as arestas que SAEM dele, limpando tambem a lista do vizinho de destino
        for (Aresta a : new ArrayList<Aresta>(atual.getArestasSaindo())) {
            a.getFim().removerArestaEntrando(a);
            this.arestas.remove(a);
        }
        atual.getArestasEntrando().clear();
        atual.getArestasSaindo().clear();
        
        this.vertices.remove(atual);
        atualizarExtremos();
    }
    
    public int removerAresta(String nomeInicio, String nomeFim){
        Vertice inicio = buscarVertice(nomeInicio);
        Vertice fim = buscarVertice(nomeFim);
        if (inicio == null || fim == null) {
            return 0;
        }
        
        int removidas = 0;
        for (Aresta a : new ArrayList<Aresta>(this.arestas)) {
            if (a.getInicio() == inicio && a.getFim() == fim) {
                inicio.removerArestaSaindo(a);
                fim.removerArestaEntrando(a);
                this.arestas.remove(a);
                removidas++;
            }
        }
        return removidas;
    }
    
    public Vertice buscarVertice(String nome) {
        for (int i = 0; i < this.vertices.size(); i++){
            Vertice atual = this.vertices.get(i);
            if(atual.getNome().equals(nome)){
                return atual;
            }
        }
        return null;
    }
    
    public Aresta buscarAresta(Vertice inicio, Vertice fim) {
        for (int i = 0; i < this.arestas.size(); i++){
            Aresta atual = this.arestas.get(i);
            if((atual.getInicio() == inicio) && (atual.getFim() == fim)) {
                return atual;
            }
        }
        return null;
    }
    
    public void adicionarAresta(String nomeRua, String nomeInicio, String nomeFim, Double distancia, Double volumeLixo) {
        Vertice inicio = buscarVertice(nomeInicio);
        Vertice fim = buscarVertice(nomeFim);
        
        if (inicio!=null && fim!=null) {
            Aresta novaAresta = new Aresta(nomeRua, inicio, fim, distancia, volumeLixo);
            inicio.adicionarArestaSaindo(novaAresta);
            fim.adicionarArestaEntrando(novaAresta);
            this.arestas.add(novaAresta);
        }
    }
    
    
}