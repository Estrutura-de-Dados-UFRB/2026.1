package com.mycompany.sistemacoletaurbana;

import java.util.ArrayList;
import java.util.List;

public class CarteiroChines {
    private Grafo mapa;
    
    public CarteiroChines (Grafo mapa) {
        this.mapa = mapa;
    }
    
    // Passo 1: Achar quais vertices são ímpares
    public ArrayList<Vertice> verticesImpares(ArrayList<Vertice> vertices){
        ArrayList<Vertice> verticesImpares = new ArrayList<Vertice>();
        if(!vertices.isEmpty()){
            for(int i=0; i<vertices.size(); i++){
                Vertice atual = vertices.get(i);
                if((atual.getGrau() % 2) != 0) {
                    verticesImpares.add(atual);
                }
            }
        }
        
        return verticesImpares;
    }
    
    // Passo 2: Calcular a distância entre os pontos da lista
    public void calcularDistancia (ArrayList<Vertice> listaImpares) {
        while(!listaImpares.isEmpty()){
            Vertice v1 = listaImpares.get(0);
            listaImpares.remove(0);
            String nomeV1 = v1.getNome();
            
            ResultadoDijkstra analiseV1 = Dijkstra.calcular(this.mapa, nomeV1);
            
            Double menorDistancia = Double.POSITIVE_INFINITY;
            Vertice v2 = null;
            int indiceV2 = -1;
            
            for (int i = 0; i < listaImpares.size(); i++) {
                Vertice atual = listaImpares.get(i);
                Double distanciaAtual = analiseV1.getDistancia(atual);
                if (distanciaAtual < menorDistancia){
                    menorDistancia = distanciaAtual;
                    v2 = atual;
                    indiceV2 = i;
                }
            }
            
            if ((v2 != null) && (indiceV2 != -1)) { 
                List<Vertice> caminhoV1paraV2 = analiseV1.reconstruirCaminho(v2);
                duplicarArestasV1paraV2(caminhoV1paraV2);
                listaImpares.remove(indiceV2);
            }
        }
    }
    
    public void duplicarArestasV1paraV2 (List<Vertice> caminho) {
        for (int i = 0; i < caminho.size() - 1; i++){
            Vertice v1 = caminho.get(i);
            Vertice v2 = caminho.get(i+1);
            
            Aresta arestaV1paraV2 = mapa.buscarAresta(v1,v2);
            
            String nomeRua = arestaV1paraV2.getNomeRua();
            String nomeInicio = arestaV1paraV2.getInicio().getNome();
            String nomeFim = arestaV1paraV2.getFim().getNome();
            Double volumeLixo = arestaV1paraV2.getVolumeLixo();
            Double distancia = arestaV1paraV2.getDistancia();
            
            mapa.adicionarAresta(nomeRua, nomeInicio, nomeFim, distancia, volumeLixo);
        }
    }
}
