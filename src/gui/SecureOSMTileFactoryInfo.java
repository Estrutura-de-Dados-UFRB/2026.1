package gui;

import org.jxmapviewer.OSMTileFactoryInfo;

/**
 * Usa HTTPS para baixar tiles do OpenStreetMap.
 */
public class SecureOSMTileFactoryInfo extends OSMTileFactoryInfo {

    public SecureOSMTileFactoryInfo() {
        super();
    }

    @Override
    public String getTileUrl(int x, int y, int zoom) {
        return super.getTileUrl(x, y, zoom).replace("http://", "https://");
    }
}
