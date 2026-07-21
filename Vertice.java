package com.mycompany.sistemacoletaurbana;

import java.util.ArrayList;

public class Vertice {
    
    private String nome;
    private ArrayList<Aresta> arestasEntrando;
    private ArrayList<Aresta> arestasSaindo;
    private int grau;
    
    public Vertice (String nome){
        this.nome = nome;
        this.arestasEntrando = new ArrayList<Aresta>();
        this.arestasSaindo = new ArrayList<Aresta>();
        this.grau = 0;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<Aresta> getArestasEntrando() {
        return arestasEntrando;
    }
    
    public ArrayList<Aresta> getArestasSaindo() {
        return arestasSaindo;
    }

    public int getGrau() {
        grau = this.arestasEntrando.size() + this.arestasSaindo.size();
        return grau;
    }
    
    public void adicionarArestaEntrando (Aresta arestaEntrando){
        this.arestasEntrando.add(arestaEntrando);
    }
    
    public void adicionarArestaSaindo (Aresta arestaSaindo){
        this.arestasSaindo.add(arestaSaindo);
    }
    
    public void removerArestaEntrando (Aresta arestaEntrando){
        this.arestasEntrando.remove(arestaEntrando);
    }
    
    public void removerArestaSaindo (Aresta arestaSaindo){
        this.arestasSaindo.remove(arestaSaindo);
    }
}
