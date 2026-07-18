package services;

import model.Ponto;
import org.jxmapviewer.viewer.GeoPosition;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Cliente do OSRM que obtém a geometria efetivamente roteável pelas ruas. */
public final class OsrmRouteService {

    private static final String ENDPOINT = "https://router.project-osrm.org/route/v1/driving/";
    private static final Pattern COORDENADA = Pattern.compile("\\[(-?\\d+(?:\\.\\d+)?),(-?\\d+(?:\\.\\d+)?)\\]");
    private static final Pattern DISTANCIA = Pattern.compile("\\\"distance\\\":(-?\\d+(?:\\.\\d+)?)");
    private static final Pattern DURACAO = Pattern.compile("\\\"duration\\\":(-?\\d+(?:\\.\\d+)?)");
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6)).build();

    public Result rotear(List<Ponto> pontos) throws Exception {
        if (pontos == null || pontos.size() < 2) throw new IllegalArgumentException("A rota exige dois pontos.");
        StringBuilder coordinates = new StringBuilder();
        for (Ponto ponto : pontos) {
            if (coordinates.length() > 0) coordinates.append(';');
            coordinates.append(String.format(Locale.US, "%.7f,%.7f", ponto.getLongitude(), ponto.getLatitude()));
        }
        URI uri = URI.create(ENDPOINT + coordinates
                + "?overview=full&geometries=geojson&steps=false&continue_straight=false");
        HttpRequest request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(15))
                .header("User-Agent", "SistemaPlanejamentoRotas/1.0 (projeto educacional)").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 || !response.body().contains("\"code\":\"Ok\"")) {
            throw new IllegalStateException("OSRM não encontrou uma rota válida (HTTP " + response.statusCode() + ").");
        }
        return parse(response.body(), pontos.size() - 1);
    }

    private Result parse(String json, int legCount) {
        int geometryStart = json.indexOf("\"geometry\":{\"coordinates\":[");
        int geometryEnd = json.indexOf("],\"type\":\"LineString\"", geometryStart);
        if (geometryStart < 0 || geometryEnd < 0) throw new IllegalStateException("Geometria inválida do OSRM.");
        List<GeoPosition> geometry = new ArrayList<>();
        Matcher coordinateMatcher = COORDENADA.matcher(json.substring(geometryStart, geometryEnd + 1));
        while (coordinateMatcher.find()) {
            double lon = Double.parseDouble(coordinateMatcher.group(1));
            double lat = Double.parseDouble(coordinateMatcher.group(2));
            geometry.add(new GeoPosition(lat, lon));
        }
        List<Double> distancesMeters = values(DISTANCIA, json.substring(0, geometryStart));
        List<Double> durationsSeconds = values(DURACAO, json.substring(0, geometryStart));
        if (geometry.size() < 2 || distancesMeters.size() < legCount) {
            throw new IllegalStateException("Resposta incompleta do OSRM.");
        }
        List<Double> legsKm = new ArrayList<>();
        for (int i = 0; i < legCount; i++) legsKm.add(distancesMeters.get(i) / 1000.0);
        double totalKm = legsKm.stream().mapToDouble(Double::doubleValue).sum();
        double durationMinutes = durationsSeconds.stream().limit(legCount).mapToDouble(Double::doubleValue).sum() / 60.0;
        return new Result(geometry, legsKm, totalKm, durationMinutes);
    }

    private List<Double> values(Pattern pattern, String text) {
        List<Double> values = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) values.add(Double.parseDouble(matcher.group(1)));
        return values;
    }

    public static final class Result {
        private final List<GeoPosition> geometry;
        private final List<Double> legDistancesKm;
        private final double totalDistanceKm;
        private final double durationMinutes;

        Result(List<GeoPosition> geometry, List<Double> legDistancesKm, double totalDistanceKm, double durationMinutes) {
            this.geometry = Collections.unmodifiableList(new ArrayList<>(geometry));
            this.legDistancesKm = Collections.unmodifiableList(new ArrayList<>(legDistancesKm));
            this.totalDistanceKm = totalDistanceKm;
            this.durationMinutes = durationMinutes;
        }

        public List<GeoPosition> getGeometry() { return geometry; }
        public List<Double> getLegDistancesKm() { return legDistancesKm; }
        public double getTotalDistanceKm() { return totalDistanceKm; }
        public double getDurationMinutes() { return durationMinutes; }
    }
}
