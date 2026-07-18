package util;

import model.Ponto;
import model.Rota;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Cria a geometria contínua usada tanto pelo desenho quanto pela animação. */
public final class RouteGeometry {

    private static final int AMOSTRAS_POR_TRECHO = 24;

    private RouteGeometry() {
    }

    /**
     * Interpola os pontos com uma curva Catmull-Rom centrípeta simplificada.
     * A curva passa por todas as paradas e evita quinas e saltos visuais.
     */
    public static List<GeoPosition> criar(List<Ponto> percurso) {
        if (percurso == null || percurso.isEmpty()) {
            return Collections.emptyList();
        }
        if (percurso.size() == 1) {
            Ponto unico = percurso.get(0);
            return Collections.singletonList(new GeoPosition(unico.getLatitude(), unico.getLongitude()));
        }

        List<GeoPosition> resultado = new ArrayList<>();
        for (int trecho = 0; trecho < percurso.size() - 1; trecho++) {
            Ponto p0 = percurso.get(Math.max(0, trecho - 1));
            Ponto p1 = percurso.get(trecho);
            Ponto p2 = percurso.get(trecho + 1);
            Ponto p3 = percurso.get(Math.min(percurso.size() - 1, trecho + 2));
            for (int i = 0; i < AMOSTRAS_POR_TRECHO; i++) {
                double t = i / (double) AMOSTRAS_POR_TRECHO;
                resultado.add(new GeoPosition(
                        catmullRom(p0.getLatitude(), p1.getLatitude(), p2.getLatitude(), p3.getLatitude(), t),
                        catmullRom(p0.getLongitude(), p1.getLongitude(), p2.getLongitude(), p3.getLongitude(), t)));
            }
        }
        Ponto ultimo = percurso.get(percurso.size() - 1);
        resultado.add(new GeoPosition(ultimo.getLatitude(), ultimo.getLongitude()));
        return Collections.unmodifiableList(resultado);
    }

    public static List<GeoPosition> criar(Rota rota) {
        if (rota == null) return Collections.emptyList();
        List<GeoPosition> geometry = rota.getGeometria();
        return geometry.size() >= 2 ? geometry : criar(rota.getPercurso());
    }

    private static double catmullRom(double p0, double p1, double p2, double p3, double t) {
        double t2 = t * t;
        double t3 = t2 * t;
        return 0.5 * ((2.0 * p1) + (-p0 + p2) * t
                + (2.0 * p0 - 5.0 * p1 + 4.0 * p2 - p3) * t2
                + (-p0 + 3.0 * p1 - 3.0 * p2 + p3) * t3);
    }

    public static double distanciaKm(GeoPosition a, GeoPosition b) {
        return GeoUtils.haversine(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude());
    }
}
