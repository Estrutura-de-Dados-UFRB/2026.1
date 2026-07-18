package services;

import model.Aresta;
import model.Grafo;
import model.Ponto;
import model.TipoPonto;
import util.GeoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia alterações no grafo e notifica a interface.
 */
public class SimulationService {

    private final Grafo grafo;
    private final List<Runnable> observadores = new ArrayList<>();

    public SimulationService(Grafo grafo) {
        this.grafo = grafo;
    }

    public void carregarPadrao() {
        grafo.adicionarPontos(DadosIniciaisService.criarPontosPadrao());
        for (Aresta aresta : DadosIniciaisService.criarArestasPadrao(grafo)) {
            grafo.adicionarAresta(aresta.getOrigem(), aresta.getDestino(), aresta.getDistancia());
        }
        notificar();
    }

    public void adicionarObservador(Runnable callback) {
        if (callback != null) {
            observadores.add(callback);
        }
    }

    private void notificar() {
        for (Runnable callback : observadores) {
            callback.run();
        }
    }

    public Ponto adicionarPonto(String nome,
                                String bairro,
                                double lat,
                                double lon,
                                int alunos,
                                int capacidade,
                                int prioridade,
                                String turno,
                                TipoPonto tipo) {
        Ponto ponto = new Ponto(nome, bairro, lat, lon, alunos, capacidade, prioridade, turno, tipo);
        grafo.adicionarPonto(ponto);
        conectarAutomaticamente(ponto);
        notificar();
        return ponto;
    }

    public void removerPonto(Ponto ponto) {
        grafo.removerPonto(ponto);
        notificar();
    }

    public void atualizarPonto(Ponto ponto) {
        if (ponto == null) {
            return;
        }
        reconnectPoint(ponto);
        notificar();
    }

    public void alterarAlunos(Ponto ponto, int novaQuantidade) {
        ponto.setQuantidadeAlunos(novaQuantidade);
        notificar();
    }

    public void alterarVelocidadeMedia(double novaVelocidade) {
        for (Ponto ponto : grafo.getPontos()) {
            for (Aresta aresta : grafo.getVizinhos(ponto)) {
                aresta.setTempoEstimado(GeoUtils.estimarTempo(aresta.getDistancia(), novaVelocidade));
            }
        }
        notificar();
    }

    public Grafo getGrafo() {
        return grafo;
    }

    private void conectarAutomaticamente(Ponto ponto) {
        Ponto maisProximo = null;
        double menorDist = Double.MAX_VALUE;
        for (Ponto existente : grafo.getPontos()) {
            if (existente.equals(ponto)) {
                continue;
            }
            double dist = GeoUtils.haversine(
                    ponto.getLatitude(), ponto.getLongitude(),
                    existente.getLatitude(), existente.getLongitude());
            if (dist < menorDist) {
                menorDist = dist;
                maisProximo = existente;
            }
        }
        if (maisProximo != null) {
            grafo.adicionarAresta(ponto, maisProximo, menorDist);
        }
    }

    private void reconnectPoint(Ponto ponto) {
        List<Ponto> outros = new ArrayList<>(grafo.getPontos());
        grafo.removerPonto(ponto);
        grafo.adicionarPonto(ponto);
        for (Ponto outro : outros) {
            if (!outro.equals(ponto)) {
                double dist = GeoUtils.haversine(
                        ponto.getLatitude(), ponto.getLongitude(),
                        outro.getLatitude(), outro.getLongitude());
                if (dist < 2.5) {
                    grafo.adicionarAresta(ponto, outro, dist);
                }
            }
        }
    }
}
