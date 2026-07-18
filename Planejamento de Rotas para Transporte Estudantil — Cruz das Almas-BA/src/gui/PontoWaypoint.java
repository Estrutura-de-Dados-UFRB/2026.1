package gui;

import model.Ponto;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class PontoWaypoint extends DefaultWaypoint {

    private final Ponto ponto;

    public PontoWaypoint(Ponto ponto) {
        super(new GeoPosition(ponto.getLatitude(), ponto.getLongitude()));
        this.ponto = ponto;
    }

    public Ponto getPonto() {
        return ponto;
    }
}
