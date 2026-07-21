package com.mycompany.sistemacoletaurbana;

import java.util.ArrayList;

public class MontarCaminho {
    private Grafo mapa;
    private ArrayList<Aresta> arestasMarcadas;
    private ArrayList<Aresta> arestasDesmarcadas;
    private ArrayList<Vertice> caminho;
            
    public MontarCaminho (Grafo mapa) {
        this.mapa = mapa;
    }
    
    public ArrayList<Vertice> Montagem(){
        arestasMarcadas = new ArrayList<>();
        caminho = new ArrayList<>();
        
        arestasDesmarcadas = new ArrayList<>(mapa.getArestas()); 
        
        Vertice origem = mapa.getPrimeiro();
        Pilha listaPilha = new Pilha();
        listaPilha.adicionar(origem);
        
        // O loop roda enquanto a pilha não estiver vazia
        while (listaPilha.getPrimeiro() != null) {
            
            Vertice atual = listaPilha.getPrimeiro(); 
            Double menorPeso = Double.POSITIVE_INFINITY;
            
            Aresta proximaAresta = null;
            for (int k = 0; k < atual.getArestasSaindo().size(); k++) {
                Aresta a = atual.getArestasSaindo().get(k);
                if (!arestasMarcadas.contains(a)) {
                    
                    if(a.getPeso() < menorPeso){
                        proximaAresta = a;
                        menorPeso = a.getPeso();
                    }
                }
            }

            if (proximaAresta != null) {
                // CASO A: Achou uma rua não visitada
                arestasMarcadas.add(proximaAresta); 
                arestasDesmarcadas.remove(proximaAresta);
                
                Vertice fim = proximaAresta.getFim();
                listaPilha.adicionar(fim);
            } else { 
                // CASO B: Todas as ruas próximas já foram visitadas
                caminho.add(atual); 
                listaPilha.remover(); 
            }
        }
        
        java.util.Collections.reverse(this.caminho);
        
        return this.caminho;
    }
}

