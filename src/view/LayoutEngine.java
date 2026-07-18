package view;

// Classe utilitária de layout para posicionar peças automaticamente (não utilizada no fluxo atual).
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.Pedras2;

class Tabuleiro {
    private int largura;
    private int altura;
    private int[][] grid;

    public Tabuleiro(int largura, int altura) {
        this.largura = largura;
        this.altura = altura;
        this.grid = new int[altura][largura];
    }

    // Verifica se uma área está livre + margem
    public boolean podeColocar(int x, int y, int w, int h) {
        // margem de 1 célula
        int startX = x - 1;
        int startY = y - 1;
        int endX = x + w;
        int endY = y + h;

        // verificar limites
        if (startX < 0 || startY < 0 || endX >= largura || endY >= altura) {
            return false;
        }

        // verificar colisão
        for (int i = startY; i <= endY; i++) {
            for (int j = startX; j <= endX; j++) {
                if (grid[i][j] != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    // Marca a peça no grid
    public void colocar(int x, int y, int w, int h) {
        for (int i = y; i < y + h; i++) {
            for (int j = x; j < x + w; j++) {
                grid[i][j] = 1;
            }
        }
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }
}

class PecaLayout {
    Pedras2 pedra;
    boolean horizontal;

    public PecaLayout(Pedras2 pedra, boolean horizontal) {
        this.pedra = pedra;
        this.horizontal = horizontal;
    }

    public int getLargura() {
        return horizontal ? 4 : 2;
    }

    public int getAltura() {
        return horizontal ? 2 : 4;
    }
}

class Posicao {
    int x, y;

    public Posicao(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class LayoutEngine {

    public static Map<PecaLayout, Posicao> autoOrganizar(
            List<PecaLayout> pecas,
            Tabuleiro tab
    ) {
        Map<PecaLayout, Posicao> resultado = new HashMap<>();

        int x = 1; // margem
        int y = 1;
        int alturaLinha = 0;

        for (PecaLayout p : pecas) {
            int w = p.getLargura();
            int h = p.getAltura();

            // se não cabe na linha → quebra linha
            if (x + w + 1 >= tab.getLargura()) {
                x = 1;
                y += alturaLinha + 1; // espaço vertical
                alturaLinha = 0;
            }

            // tentar encontrar posição válida
            boolean colocado = false;

            while (!colocado) {
                if (tab.podeColocar(x, y, w, h)) {
                    tab.colocar(x, y, w, h);
                    resultado.put(p, new Posicao(x, y));

                    x += w + 1; // espaço horizontal
                    alturaLinha = Math.max(alturaLinha, h);

                    colocado = true;
                } else {
                    // tenta empurrar para direita
                    x++;

                    // se estourar linha → quebra
                    if (x + w + 1 >= tab.getLargura()) {
                        x = 1;
                        y += alturaLinha + 1;
                        alturaLinha = 0;
                    }
                }
            }
        }

        return resultado;
    }
}