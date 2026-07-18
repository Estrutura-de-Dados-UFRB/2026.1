package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jxmapviewer.viewer.GeoPosition;
import util.RouteGeometry;

/**
 * Resultado gerado por um algoritmo de roteamento.
 */
public final class Rota {

    private final List<Ponto> percurso;
    private final List<Aresta> arestas;
    private final String algoritmo;
    private final Ponto origem;
    private final Ponto destino;
    private double distanciaTotal;
    private double tempoTotal;
    private final double pesoTotal;
    private final double custoTotal;
    private List<GeoPosition> geometria;
    private List<Double> distanciasTrechos = new ArrayList<>();
    private boolean trajetoViario;

    public Rota(List<Ponto> percurso, double distanciaTotal, double tempoTotal) {
        this(percurso, new ArrayList<Aresta>(), null, null, "Rota", distanciaTotal, tempoTotal, distanciaTotal, distanciaTotal * 2.5);
    }

    public Rota(List<Ponto> percurso,
                List<Aresta> arestas,
                Ponto origem,
                Ponto destino,
                String algoritmo,
                double distanciaTotal,
                double tempoTotal,
                double pesoTotal,
                double custoTotal) {
        this.percurso = percurso != null ? new ArrayList<>(percurso) : new ArrayList<Ponto>();
        this.arestas = arestas != null ? new ArrayList<>(arestas) : new ArrayList<Aresta>();
        this.origem = origem;
        this.destino = destino;
        this.algoritmo = algoritmo != null ? algoritmo : "Rota";
        this.distanciaTotal = Math.max(distanciaTotal, 0.0);
        this.tempoTotal = Math.max(tempoTotal, 0.0);
        this.pesoTotal = Math.max(pesoTotal, 0.0);
        this.custoTotal = Math.max(custoTotal, 0.0);
        atualizarGeometriaLocal();
    }

    public List<Ponto> getPercurso() {
        return Collections.unmodifiableList(percurso);
    }

    public List<Aresta> getArestas() {
        return Collections.unmodifiableList(arestas);
    }

    public String getAlgoritmo() {
        return algoritmo;
    }

    public Ponto getOrigem() {
        return origem;
    }

    public Ponto getDestino() {
        return destino;
    }

    public double getDistanciaTotal() {
        return distanciaTotal;
    }

    public double getTempoTotal() {
        return tempoTotal;
    }

    public double getPesoTotal() {
        return pesoTotal;
    }

    public double getCustoTotal() {
        return custoTotal;
    }

    public List<GeoPosition> getGeometria() { return Collections.unmodifiableList(geometria); }

    public List<Double> getDistanciasTrechos() { return Collections.unmodifiableList(distanciasTrechos); }

    /** Aplica o resultado viário; chamado após o algoritmo definir a ordem das paradas. */
    public void definirTrajetoViario(List<GeoPosition> geometria, List<Double> distanciasTrechos,
                                     double distanciaTotal, double tempoTotal) {
        if (geometria == null || geometria.size() < 2) return;
        this.geometria = new ArrayList<>(geometria);
        this.distanciasTrechos = distanciasTrechos == null ? new ArrayList<>() : new ArrayList<>(distanciasTrechos);
        this.distanciaTotal = Math.max(0.0, distanciaTotal);
        this.tempoTotal = Math.max(0.0, tempoTotal);
        this.trajetoViario = true;
    }

    public boolean isTrajetoViario() { return trajetoViario; }

    /** Recalcula imediatamente quando alguma coordenada editável é alterada. */
    public void atualizarGeometriaLocal() {
        this.geometria = RouteGeometry.criar(this.percurso);
        this.distanciasTrechos = new ArrayList<>();
        double total = 0.0;
        for (int i = 1; i < percurso.size(); i++) {
            Ponto a = percurso.get(i - 1);
            Ponto b = percurso.get(i);
            double distancia = util.GeoUtils.haversine(a.getLatitude(), a.getLongitude(),
                    b.getLatitude(), b.getLongitude());
            distanciasTrechos.add(distancia);
            total += distancia;
        }
        this.distanciaTotal = total;
        this.tempoTotal = util.GeoUtils.estimarTempo(total, 40.0);
        this.trajetoViario = false;
    }

    public int getNumeroParadas() {
        return percurso.size();
    }

    public int calcularTotalAlunos() {
        int total = 0;
        Set<Ponto> contabilizados = new HashSet<>();
        for (Ponto ponto : percurso) {
            if (contabilizados.add(ponto)) {
                total += ponto.getQuantidadeAlunos();
            }
        }
        return total;
    }

    public String formatarResumo() {
        StringBuilder sb = new StringBuilder();
        sb.append("ROTA CALCULADA").append('\n');
        sb.append("Algoritmo utilizado: ").append(algoritmo).append('\n');
        if (origem != null) {
            sb.append("Origem: ").append(origem.getNome()).append('\n');
        }
        if (destino != null) {
            sb.append("Destino: ").append(destino.getNome()).append('\n');
        }
        sb.append("Distancia total: ").append(String.format("%.2f km", distanciaTotal)).append('\n');
        sb.append("Tempo estimado: ").append(String.format("%.0f min", tempoTotal)).append('\n');
        sb.append("Numero de paradas: ").append(getNumeroParadas()).append('\n');
        sb.append("Quantidade de alunos: ").append(calcularTotalAlunos()).append('\n');
        sb.append("Peso total: ").append(String.format("%.2f", pesoTotal)).append('\n');
        sb.append("Custo total: ").append(String.format("R$ %.2f", custoTotal)).append('\n');
        sb.append("Geometria: ").append(trajetoViario ? "OSRM/OpenStreetMap (vias reais)" : "aproximação local").append('\n');
        if (distanciasTrechos.size() == Math.max(0, percurso.size() - 1)) {
            sb.append("Distâncias entre paradas:").append('\n');
            for (int i = 0; i < distanciasTrechos.size(); i++) {
                sb.append("  ").append(percurso.get(i).getNome()).append(" → ")
                        .append(percurso.get(i + 1).getNome()).append(": ")
                        .append(String.format("%.2f km", distanciasTrechos.get(i))).append('\n');
            }
        }
        sb.append('\n');
        int ordem = 1;
        Set<Ponto> atendidos = new HashSet<>();
        for (Ponto ponto : percurso) {
            boolean primeiraPassagem = atendidos.add(ponto);
            sb.append(ordem++).append(". ").append(ponto.getNome()).append(" - ")
                    .append(ponto.getTipo().getRotulo()).append(" - ")
                    .append(ponto.getBairro());
            if (primeiraPassagem) {
                sb.append(" | alunos: ").append(ponto.getQuantidadeAlunos());
            } else {
                sb.append(" | retorno: alunos ja atendidos");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
