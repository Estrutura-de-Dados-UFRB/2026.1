import model.Grafo;
import model.Ponto;
import model.Rota;
import model.TipoPonto;
import org.jxmapviewer.viewer.GeoPosition;
import services.DadosIniciaisService;
import services.RouteService;
import util.RouteGeometry;

import java.util.List;

public class RouteSmokeTest {
    public static void main(String[] args) {
        Grafo grafo = new Grafo();
        grafo.adicionarPontos(DadosIniciaisService.criarPontosPadrao());
        DadosIniciaisService.criarArestasPadrao(grafo).forEach(a ->
                grafo.adicionarAresta(a.getOrigem(), a.getDestino(), a.getDistancia()));
        if (grafo.getPontos().size() < 10 || grafo.getArestas().isEmpty()) {
            throw new AssertionError("Grafo padrão incompleto");
        }
        long escolas = grafo.getPontos().stream().filter(p -> p.getTipo() == TipoPonto.ESCOLA).count();
        long bairros = grafo.getPontos().stream().filter(p -> p.getTipo() == TipoPonto.BAIRRO).count();
        if (escolas != 18 || bairros != 14) {
            throw new AssertionError("Esperados 18 escolas e 14 bairros, encontrados " + escolas + " e " + bairros);
        }
        for (Ponto ponto : grafo.getPontos()) {
            if (ponto.getLatitude() < -90 || ponto.getLatitude() > 90
                    || ponto.getLongitude() < -180 || ponto.getLongitude() > 180) {
                throw new AssertionError("Coordenada inválida: " + ponto.getNome());
            }
        }

        List<Ponto> pontos = List.copyOf(grafo.getPontos());
        RouteService service = new RouteService(grafo);
        Rota rota = service.executarDijkstra(pontos.get(0), pontos.get(pontos.size() - 1));
        validarRota(rota);
        service.executarPrim(pontos.get(0));
        validarRota(service.getRotaAtual());
        service.executarKruskal();
        validarRota(service.getRotaAtual());
        validarRota(service.executarBFS(pontos.get(0)));
        validarRota(service.executarDFS(pontos.get(0)));
        validarRota(service.executarGuloso(pontos.get(0)));
        validarRota(service.executarTSP(pontos.get(0)));
        System.out.println("RouteSmokeTest OK");
    }

    private static void validarRota(Rota rota) {
        if (rota == null || rota.getPercurso().isEmpty()) throw new AssertionError("Rota vazia");
        List<GeoPosition> geometria = RouteGeometry.criar(rota.getPercurso());
        if (geometria.isEmpty()) throw new AssertionError("Geometria vazia");
        Ponto inicio = rota.getPercurso().get(0);
        Ponto fim = rota.getPercurso().get(rota.getPercurso().size() - 1);
        if (RouteGeometry.distanciaKm(geometria.get(0), new GeoPosition(inicio.getLatitude(), inicio.getLongitude())) > 0.001
                || RouteGeometry.distanciaKm(geometria.get(geometria.size() - 1),
                new GeoPosition(fim.getLatitude(), fim.getLongitude())) > 0.001) {
            throw new AssertionError("Geometria não passa pelas extremidades");
        }
        for (int i = 1; i < geometria.size(); i++) {
            double passo = RouteGeometry.distanciaKm(geometria.get(i - 1), geometria.get(i));
            if (!Double.isFinite(passo) || passo > 0.25) throw new AssertionError("Salto na geometria: " + passo);
        }
        for (int i = 1; i < rota.getPercurso().size(); i++) {
            double trecho = RouteGeometry.distanciaKm(
                    new GeoPosition(rota.getPercurso().get(i - 1).getLatitude(), rota.getPercurso().get(i - 1).getLongitude()),
                    new GeoPosition(rota.getPercurso().get(i).getLatitude(), rota.getPercurso().get(i).getLongitude()));
            if (trecho > 2.6) throw new AssertionError("Salto entre pontos da rota: " + trecho);
        }
    }
}
