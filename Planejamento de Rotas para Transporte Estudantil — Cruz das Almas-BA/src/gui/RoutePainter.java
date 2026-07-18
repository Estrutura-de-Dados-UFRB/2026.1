package gui;

import model.Aresta;
import model.Ponto;
import model.Rota;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;
import util.RouteGeometry;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class RoutePainter implements Painter<JXMapViewer> {

    private final List<GeoPosition> geometria;
    private final List<Aresta> arestas;
    private final Color cor;
    private final List<Ponto> percurso;
    private final List<Double> distanciasTrechos;

    public RoutePainter(Rota rota, Color cor) {
        this.geometria = RouteGeometry.criar(rota);
        this.percurso = new ArrayList<>(rota.getPercurso());
        this.distanciasTrechos = new ArrayList<>(rota.getDistanciasTrechos());
        this.arestas = new ArrayList<>(rota.getArestas());
        this.cor = cor != null ? cor : Color.BLUE;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
        if (geometria.size() < 2) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Point anterior = ajustar(map, geometria.get(0));
        for (int i = 1; i < geometria.size(); i++) {
            GeoPosition b = geometria.get(i);
            Point pb = ajustar(map, b);
            g2.drawLine(anterior.x, anterior.y, pb.x, pb.y);
            if (i % 24 == 12) {
                desenharSeta(g2, anterior, pb);
            }
            anterior = pb;
        }
        desenharDistancias(g2, map);
        g2.dispose();
    }

    private void desenharDistancias(Graphics2D g2, JXMapViewer map) {
        if (distanciasTrechos.size() != percurso.size() - 1) return;
        g2.setFont(UiTheme.FONT_BOLD.deriveFont(11f));
        for (int i = 0; i < distanciasTrechos.size(); i++) {
            Ponto a = percurso.get(i);
            Ponto b = percurso.get(i + 1);
            Point pa = ajustar(map, new GeoPosition(a.getLatitude(), a.getLongitude()));
            Point pb = ajustar(map, new GeoPosition(b.getLatitude(), b.getLongitude()));
            String label = String.format("%.2f km", distanciasTrechos.get(i));
            int x = (pa.x + pb.x) / 2;
            int y = (pa.y + pb.y) / 2;
            int width = g2.getFontMetrics().stringWidth(label) + 8;
            g2.setColor(new Color(255, 255, 255, 225));
            g2.fillRoundRect(x - width / 2, y - 16, width, 18, 8, 8);
            g2.setColor(cor.darker());
            g2.drawString(label, x - width / 2 + 4, y - 3);
        }
    }

    private Point ajustar(JXMapViewer map, GeoPosition position) {
        java.awt.geom.Point2D p = map.getTileFactory().geoToPixel(position, map.getZoom());
        java.awt.Rectangle viewport = map.getViewportBounds();
        return new Point((int) Math.round(p.getX() - viewport.getX()), (int) Math.round(p.getY() - viewport.getY()));
    }

    private void desenharSeta(Graphics2D g2, Point from, Point to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double angle = Math.atan2(dy, dx);
        int size = 9;
        int mx = (from.x + to.x) / 2;
        int my = (from.y + to.y) / 2;
        int[] xPoints = {
                (int) (mx + size * Math.cos(angle + Math.PI - 0.4)),
                mx,
                (int) (mx + size * Math.cos(angle + Math.PI + 0.4))
        };
        int[] yPoints = {
                (int) (my + size * Math.sin(angle + Math.PI - 0.4)),
                my,
                (int) (my + size * Math.sin(angle + Math.PI + 0.4))
        };
        g2.fillPolygon(xPoints, yPoints, 3);
    }
}
