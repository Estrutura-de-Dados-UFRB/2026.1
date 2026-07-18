package gui;

import model.Ponto;
import model.TipoPonto;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class PontoWaypointRenderer implements WaypointRenderer<PontoWaypoint> {

    private final Ponto pontoSelecionado;
    private final Ponto origem;
    private final Ponto destino;

    public PontoWaypointRenderer(Ponto pontoSelecionado) {
        this(pontoSelecionado, null, null);
    }

    public PontoWaypointRenderer(Ponto pontoSelecionado, Ponto origem, Ponto destino) {
        this.pontoSelecionado = pontoSelecionado;
        this.origem = origem;
        this.destino = destino;
    }

    @Override
    public void paintWaypoint(Graphics2D g, JXMapViewer map, PontoWaypoint waypoint) {
        Ponto ponto = waypoint.getPonto();
        Point2D point = ajustar(map, waypoint);
        boolean selecionado = pontoSelecionado != null && pontoSelecionado.equals(ponto);
        boolean ehOrigem = origem != null && origem.equals(ponto);
        boolean ehDestino = destino != null && destino.equals(ponto);
        RectangleHelper.drawPoint(g, point, corDoTipo(ponto.getTipo()), selecionado, ehOrigem, ehDestino,
                textoDoPonto(ponto, ehOrigem, ehDestino));
    }

    private Point2D ajustar(JXMapViewer map, PontoWaypoint waypoint) {
        Point2D point = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom());
        java.awt.Rectangle viewport = map.getViewportBounds();
        return new Point2D.Double(point.getX() - viewport.getX(), point.getY() - viewport.getY());
    }

    private Color corDoTipo(TipoPonto tipo) {
        if (tipo == TipoPonto.ESCOLA) {
            return new Color(210, 50, 50);
        }
        if (tipo == TipoPonto.UNIVERSIDADE) {
            return new Color(45, 90, 220);
        }
        if (tipo == TipoPonto.BAIRRO) {
            return new Color(190, 140, 30);
        }
        if (tipo == TipoPonto.PONTO_EMBARQUE) {
            return new Color(50, 160, 70);
        }
        return new Color(120, 120, 120);
    }

    private String textoDoPonto(Ponto ponto, boolean ehOrigem, boolean ehDestino) {
        if (ehOrigem) {
            return "S";
        }
        if (ehDestino) {
            return "D";
        }
        return abreviacao(ponto.getTipo());
    }

    private String abreviacao(TipoPonto tipo) {
        switch (tipo) {
            case ESCOLA:
                return "E";
            case UNIVERSIDADE:
                return "U";
            case BAIRRO:
                return "B";
            case PONTO_EMBARQUE:
                return "P";
            default:
                return "?";
        }
    }

    private static final class RectangleHelper {
        private static void drawPoint(Graphics2D g, Point2D point, Color color, boolean selected,
                                      boolean origem, boolean destino, String text) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int x = (int) Math.round(point.getX());
            int y = (int) Math.round(point.getY());
            int size = selected ? 26 : (origem || destino ? 24 : 18);
            g2.setColor(origem ? new Color(40, 120, 220) : destino ? new Color(230, 60, 60) : selected ? color.brighter() : color);
            g2.fill(new Ellipse2D.Double(x - size / 2.0, y - size / 2.0, size, size));
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new Ellipse2D.Double(x - size / 2.0, y - size / 2.0, size, size));
            g2.setFont(UiTheme.FONT_BOLD.deriveFont(10f));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, x - fm.stringWidth(text) / 2, y + fm.getAscent() / 2 - 1);
            g2.dispose();
        }
    }
}
