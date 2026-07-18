package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public final class ResourceLoader {
    private ResourceLoader() {
    }

    public static URL getResource(String caminho) {
        String normalizado = normalizar(caminho);
        URL url = ResourceLoader.class.getResource("/" + normalizado);
        if (url != null) {
            return url;
        }

        File arquivo = localizarArquivo(normalizado);
        if (!arquivo.exists()) {
            return null;
        }

        try {
            return arquivo.toURI().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static InputStream getResourceAsStream(String caminho) {
        String normalizado = normalizar(caminho);
        InputStream stream = ResourceLoader.class.getResourceAsStream("/" + normalizado);
        if (stream != null) {
            return stream;
        }

        File arquivo = localizarArquivo(normalizado);
        if (!arquivo.exists()) {
            return null;
        }

        try {
            return new FileInputStream(arquivo);
        } catch (java.io.FileNotFoundException e) {
            return null;
        }
    }

    private static String normalizar(String caminho) {
        String normalizado = caminho.replace('\\', '/');
        while (normalizado.startsWith("/")) {
            normalizado = normalizado.substring(1);
        }
        return normalizado;
    }

    private static File localizarArquivo(String caminho) {
        File arquivo = new File(caminho);
        if (arquivo.exists()) {
            return arquivo;
        }
        return new File("src", caminho);
    }
}
