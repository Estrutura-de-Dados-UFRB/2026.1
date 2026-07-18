package gui;

import model.Rota;
import org.jxmapviewer.viewer.GeoPosition;
import util.RouteGeometry;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Relógio de animação baseado em distância, independente da taxa de quadros. */
final class RouteAnimation {

    static final class Frame {
        final GeoPosition position;
        final double angle;
        final double progress;

        Frame(GeoPosition position, double angle, double progress) {
            this.position = position;
            this.angle = angle;
            this.progress = progress;
        }
    }

    private static final int FRAME_DELAY_MS = 16;
    // Um segundo real representa um minuto da viagem, mantendo a demonstração visível.
    private static final double SIMULATION_TIME_SCALE = 60.0;

    private final Timer timer;
    private final Consumer<Frame> frameConsumer;
    private final Runnable finishCallback;
    private List<GeoPosition> path = new ArrayList<>();
    private double[] accumulatedKm = new double[0];
    private double totalKm;
    private double travelledKm;
    private double speedKmh = 40.0;
    private long lastNanos;

    RouteAnimation(Consumer<Frame> frameConsumer, Runnable finishCallback) {
        this.frameConsumer = frameConsumer;
        this.finishCallback = finishCallback;
        timer = new Timer(FRAME_DELAY_MS, e -> tick());
        timer.setCoalesce(true);
    }

    void setRoute(Rota route) {
        stop();
        path = RouteGeometry.criar(route);
        accumulatedKm = new double[path.size()];
        totalKm = 0.0;
        for (int i = 1; i < path.size(); i++) {
            totalKm += RouteGeometry.distanciaKm(path.get(i - 1), path.get(i));
            accumulatedKm[i] = totalKm;
        }
        emitFrame();
    }

    void setSpeedKmh(double speedKmh) {
        this.speedKmh = Math.max(5.0, Math.min(120.0, speedKmh));
    }

    void play() {
        if (path.size() < 2) return;
        if (travelledKm >= totalKm) travelledKm = 0.0;
        lastNanos = System.nanoTime();
        timer.start();
    }

    void pause() {
        timer.stop();
    }

    void stop() {
        timer.stop();
        travelledKm = 0.0;
        lastNanos = 0L;
        emitFrame();
    }

    boolean isRunning() {
        return timer.isRunning();
    }

    private void tick() {
        long now = System.nanoTime();
        double elapsedSeconds = Math.min((now - lastNanos) / 1_000_000_000.0, 0.1);
        lastNanos = now;
        travelledKm += speedKmh / 3600.0 * elapsedSeconds * SIMULATION_TIME_SCALE;
        if (travelledKm >= totalKm) {
            travelledKm = totalKm;
            timer.stop();
            emitFrame();
            finishCallback.run();
            return;
        }
        emitFrame();
    }

    private void emitFrame() {
        if (path.size() < 2) {
            if (!path.isEmpty()) {
                frameConsumer.accept(new Frame(path.get(0), 0.0, 0.0));
            }
            return;
        }
        int segment = findSegment(travelledKm);
        int next = Math.min(segment + 1, path.size() - 1);
        double segmentLength = accumulatedKm[next] - accumulatedKm[segment];
        double fraction = segmentLength <= 0.0 ? 0.0
                : (travelledKm - accumulatedKm[segment]) / segmentLength;
        GeoPosition a = path.get(segment);
        GeoPosition b = path.get(next);
        GeoPosition position = new GeoPosition(
                a.getLatitude() + (b.getLatitude() - a.getLatitude()) * fraction,
                a.getLongitude() + (b.getLongitude() - a.getLongitude()) * fraction);
        double angle = Math.atan2(b.getLatitude() - a.getLatitude(), b.getLongitude() - a.getLongitude());
        frameConsumer.accept(new Frame(position, angle, totalKm == 0.0 ? 0.0 : travelledKm / totalKm));
    }

    private int findSegment(double distanceKm) {
        int low = 0;
        int high = Math.max(0, accumulatedKm.length - 2);
        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (accumulatedKm[mid + 1] < distanceKm) low = mid + 1;
            else if (accumulatedKm[mid] > distanceKm) high = mid - 1;
            else return mid;
        }
        return Math.max(0, Math.min(low, accumulatedKm.length - 2));
    }
}
