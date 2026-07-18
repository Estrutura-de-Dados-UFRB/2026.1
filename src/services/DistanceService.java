package services;

import model.Ponto;
import util.GeoUtils;

/**
 * Serviço centralizado para cálculos de distância e tempo.
 * Mantém a lógica geográfica fora da interface e dos algoritmos.
 */
public class DistanceService {

    public double calcularDistancia(
            Ponto origem,
            Ponto destino
    ) {
        if (origem == null || destino == null) {
            throw new IllegalArgumentException("Origem e destino são obrigatórios.");
        }

        return GeoUtils.haversine(
                origem.getLatitude(),
                origem.getLongitude(),
                destino.getLatitude(),
                destino.getLongitude()
        );
    }

    public double estimarTempo(
            double distanciaKm,
            double velocidadeMedia
    ) {
        return GeoUtils.estimarTempo(distanciaKm, velocidadeMedia);
    }

    public double estimarCombustivel(
            double distanciaKm,
            double kmPorLitro
    ) {
        return GeoUtils.estimarCombustivel(distanciaKm, kmPorLitro);
    }
}
