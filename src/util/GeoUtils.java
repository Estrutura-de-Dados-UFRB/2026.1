package util;

/**
 * Utilitários para cálculos geográficos.
 */
public class GeoUtils {

    private static final double RAIO_TERRA_KM = 6371.0;

    /**
     * Calcula a distância entre dois pontos geográficos
     * usando a fórmula de Haversine.
     *
     * @return distância em quilômetros
     */
    public static double haversine(
            double lat1, double lon1,
            double lat2, double lon2
    ) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAIO_TERRA_KM * c;
    }

    /**
     * Estima o tempo de viagem com base na distância
     * e velocidade média.
     *
     * @param distanciaKm     distância em km
     * @param velocidadeMedia velocidade em km/h
     * @return tempo em minutos
     */
    public static double estimarTempo(double distanciaKm, double velocidadeMedia) {
        if (velocidadeMedia <= 0) return 0;
        return (distanciaKm / velocidadeMedia) * 60.0;
    }

    /**
     * Estima o consumo de combustível.
     *
     * @param distanciaKm distância total em km
     * @param kmPorLitro  eficiência do veículo (km/L)
     * @return litros estimados
     */
    public static double estimarCombustivel(double distanciaKm, double kmPorLitro) {
        if (kmPorLitro <= 0) return 0;
        return distanciaKm / kmPorLitro;
    }
}
