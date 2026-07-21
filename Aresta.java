package com.mycompany.sistemacoletaurbana;

public class Aresta {
    
    private String nomeRua;
    private Vertice inicio;
    private Vertice fim;
    private Double volumeLixo;
    private Double distancia;
    private Double peso;
    
    public Aresta(String nomeRua, Vertice inicio, Vertice fim, Double distancia, Double volumeLixo) {
        this.nomeRua = nomeRua;
        this.inicio = inicio;
        this.fim = fim;
        this.distancia = distancia;
        this.volumeLixo = volumeLixo;
        
        calcularPeso(distancia, volumeLixo);
    } 

    public Double calcularPeso(Double distancia, Double volumeLixo) {
        if (volumeLixo == 0){
            volumeLixo = 0.1;
        }
        this.peso = distancia/volumeLixo;
        return peso;
    }

    public String getNomeRua() {
        return nomeRua;
    }

    public void setNomeRua(String nomeRua) {
        this.nomeRua = nomeRua;
    }

    public Vertice getInicio() {
        return inicio;
    }

    public void setInicio(Vertice inicio) {
        this.inicio = inicio;
    }

    public Vertice getFim() {
        return fim;
    }

    public void setFim(Vertice fim) {
        this.fim = fim;
    }

    public Double getVolumeLixo() {
        return volumeLixo;
    }

    public void setVolumeLixo(Double volumeLixo) {
        this.volumeLixo = volumeLixo;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }
    
    
}
