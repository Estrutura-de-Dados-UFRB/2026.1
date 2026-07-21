package com.mycompany.sistemacoletaurbana;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimuladorVias {

    /** Peso atribuido a uma via bloqueada. */
    public static final Double PESO_BLOQUEADO = Double.POSITIVE_INFINITY;

    private final Grafo mapa;

    /** Backup: aresta bloqueada -> peso que ela tinha antes do bloqueio. */
    private final Map<Aresta, Double> pesosOriginais = new HashMap<>();

    public SimuladorVias(Grafo mapa) {
        this.mapa = mapa;
    }

    /** Uma via esta bloqueada quando seu peso e infinito. */
    public static boolean estaBloqueada(Aresta aresta) {
        return aresta.getPeso() == null || aresta.getPeso().isInfinite();
    }

    /**
     * Retorna todas as arestas que ligam inicio -> fim (inclusive as duplicadas
     * pelo Carteiro Chines). Lista vazia se a via nao existir.
     */
    public List<Aresta> buscarVias(String nomeInicio, String nomeFim) {
        List<Aresta> encontradas = new ArrayList<>();
        Vertice inicio = mapa.buscarVertice(nomeInicio);
        Vertice fim = mapa.buscarVertice(nomeFim);

        if (inicio == null || fim == null) {
            return encontradas;
        }
        for (Aresta a : mapa.getArestas()) {
            if (a.getInicio() == inicio && a.getFim() == fim) {
                encontradas.add(a);
            }
        }
        return encontradas;
    }

    // ------------------------------------------------------------------
    // BLOQUEIO / DESBLOQUEIO
    // ------------------------------------------------------------------

    public int bloquearVia(String nomeInicio, String nomeFim, boolean ambosSentidos) {
        int total = bloquearSentido(nomeInicio, nomeFim);
        if (ambosSentidos) {
            total += bloquearSentido(nomeFim, nomeInicio);
        }
        return total;
    }

    private int bloquearSentido(String nomeInicio, String nomeFim) {
        int total = 0;
        for (Aresta a : buscarVias(nomeInicio, nomeFim)) {
            if (estaBloqueada(a)) continue;          // ja bloqueada, nao sobrescreve o backup
            pesosOriginais.put(a, a.getPeso());
            a.setPeso(PESO_BLOQUEADO);
            total++;
        }
        return total;
    }

    /**
     * Desbloqueia a via inicio -> fim, devolvendo o peso original.
     * @return quantidade de arestas restauradas.
     */
    public int desbloquearVia(String nomeInicio, String nomeFim, boolean ambosSentidos) {
        int total = desbloquearSentido(nomeInicio, nomeFim);
        if (ambosSentidos) {
            total += desbloquearSentido(nomeFim, nomeInicio);
        }
        return total;
    }

    private int desbloquearSentido(String nomeInicio, String nomeFim) {
        int total = 0;
        for (Aresta a : buscarVias(nomeInicio, nomeFim)) {
            Double pesoOriginal = pesosOriginais.remove(a);
            if (pesoOriginal != null) {
                a.setPeso(pesoOriginal);
                total++;
            }
        }
        return total;
    }

    /** Desbloqueia todas as vias bloqueadas de uma vez. */
    public int desbloquearTudo() {
        int total = pesosOriginais.size();
        for (Map.Entry<Aresta, Double> entrada : pesosOriginais.entrySet()) {
            entrada.getKey().setPeso(entrada.getValue());
        }
        pesosOriginais.clear();
        return total;
    }

    public List<Aresta> listarBloqueadas() {
        return new ArrayList<>(pesosOriginais.keySet());
    }

    // ------------------------------------------------------------------
    // ALTERACAO DE PESO / DISTANCIA / VOLUME
    // ------------------------------------------------------------------

    /**
     * Altera diretamente o peso da via (ex.: transito, obra, via lenta).
     * Se a via estiver bloqueada, atualiza o backup em vez do peso atual,
     * para que o desbloqueio devolva o valor novo.
     * @return quantidade de arestas alteradas.
     */
    public int alterarPeso(String nomeInicio, String nomeFim, Double novoPeso) {
        int total = 0;
        for (Aresta a : buscarVias(nomeInicio, nomeFim)) {
            if (estaBloqueada(a) && pesosOriginais.containsKey(a)) {
                pesosOriginais.put(a, novoPeso);
            } else {
                a.setPeso(novoPeso);
            }
            total++;
        }
        return total;
    }

    /** Altera a distancia da via e recalcula o peso (peso = distancia / volumeLixo). */
    public int alterarDistancia(String nomeInicio, String nomeFim, Double novaDistancia) {
        int total = 0;
        for (Aresta a : buscarVias(nomeInicio, nomeFim)) {
            a.setDistancia(novaDistancia);
            recalcular(a);
            total++;
        }
        return total;
    }

    /** Altera o volume de lixo da via e recalcula o peso. */
    public int alterarVolumeLixo(String nomeInicio, String nomeFim, Double novoVolume) {
        int total = 0;
        for (Aresta a : buscarVias(nomeInicio, nomeFim)) {
            a.setVolumeLixo(novoVolume);
            recalcular(a);
            total++;
        }
        return total;
    }

    /** Recalcula o peso respeitando um eventual bloqueio ativo. */
    private void recalcular(Aresta a) {
        boolean bloqueada = pesosOriginais.containsKey(a);
        Double novoPeso = a.calcularPeso(a.getDistancia(), a.getVolumeLixo());
        if (bloqueada) {
            pesosOriginais.put(a, novoPeso);
            a.setPeso(PESO_BLOQUEADO);   // calcularPeso sobrescreveu o peso: rebloqueia
        }
    }

    // ------------------------------------------------------------------
    // VISUALIZACAO
    // ------------------------------------------------------------------

    /** Texto do mapa completo, marcando as vias bloqueadas. */
    public String exibirMapa() {
        StringBuilder sb = new StringBuilder();
        sb.append("MAPA: ").append(mapa.getNome())
          .append("  (").append(mapa.getVertices().size()).append(" pontos, ")
          .append(mapa.getArestas().size()).append(" vias)\n");

        for (Vertice v : mapa.getVertices()) {
            sb.append("\n[").append(v.getNome()).append("] grau=").append(v.getGrau()).append("\n");
            if (v.getArestasSaindo().isEmpty()) {
                sb.append("   (sem vias saindo)\n");
            }
            for (Aresta a : v.getArestasSaindo()) {
                sb.append(String.format("   -> %-12s via %-16s dist=%6.1f  lixo=%5.1f  peso=%s%s%n",
                        a.getFim().getNome(),
                        a.getNomeRua(),
                        a.getDistancia(),
                        a.getVolumeLixo(),
                        estaBloqueada(a) ? "INF" : String.format("%.2f", a.getPeso()),
                        estaBloqueada(a) ? "   [BLOQUEADA]" : ""));
            }
        }
        return sb.toString();
    }

    /** Relatorio curto das vias atualmente bloqueadas. */
    public String exibirBloqueios() {
        if (pesosOriginais.isEmpty()) {
            return "Nenhuma via bloqueada no momento.";
        }
        StringBuilder sb = new StringBuilder("Vias bloqueadas:\n");
        for (Map.Entry<Aresta, Double> e : pesosOriginais.entrySet()) {
            Aresta a = e.getKey();
            sb.append(String.format("   %s -> %s (%s) | peso original: %.2f%n",
                    a.getInicio().getNome(), a.getFim().getNome(), a.getNomeRua(), e.getValue()));
        }
        return sb.toString();
    }
}