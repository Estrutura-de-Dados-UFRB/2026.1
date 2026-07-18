import model.Ponto;
import services.DadosIniciaisService;
import services.OsrmRouteService;

import java.util.List;

/** Teste opcional de integração; requer acesso à internet. */
public class OsrmIntegrationTest {
    public static void main(String[] args) throws Exception {
        List<Ponto> points = DadosIniciaisService.criarPontosPadrao();
        OsrmRouteService.Result result = new OsrmRouteService().rotear(points.subList(0, 3));
        if (result.getGeometry().size() < 3) throw new AssertionError("Geometria viária vazia");
        if (result.getLegDistancesKm().size() != 2 || result.getTotalDistanceKm() <= 0.0) {
            throw new AssertionError("Distâncias viárias inválidas");
        }
        System.out.printf("OsrmIntegrationTest OK: %.2f km, %d coordenadas%n",
                result.getTotalDistanceKm(), result.getGeometry().size());
    }
}
