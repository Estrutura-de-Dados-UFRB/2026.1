package view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import util.Lista;
import util.Node;
import util.Pedras2;

public class GerenciadorMesa {

    private static class PecaDesenho {
        Pedras2 pedra;
        int x;
        int y;
        boolean vertical;
        boolean invertida;

        PecaDesenho(Pedras2 pedra, int x, int y, boolean vertical, boolean invertida) {
            this.pedra = pedra;
            this.x = x;
            this.y = y;
            this.vertical = vertical;
            this.invertida = invertida;
        }
    }

    public static void desenharMesa(Graphics2D g2d, Lista<Pedras2> lista, Image[] imgFaces,
                                    int larguraJanela, int alturaJanela) {
        Node<Pedras2> atual = lista.getHead().getNext();
        
        if (atual == null) {
            return;
        }

        int tam = 60;
        int larguraLinha = Math.max(500, larguraJanela - 120);

        List<PecaDesenho> pecas = calcularPecas(atual, tam, larguraLinha);
        Rectangle limites = calcularLimites(pecas, tam);

        int deslocamentoX = (larguraJanela - limites.width) / 2 - limites.x;
        int deslocamentoY = (alturaJanela - limites.height) / 2 - limites.y;

        for (PecaDesenho peca : pecas) {
            desenharPeca(
                    g2d,
                    imgFaces,
                    peca.pedra,
                    peca.x + deslocamentoX,
                    peca.y + deslocamentoY,
                    tam,
                    peca.vertical,
                    peca.invertida
            );
        }
    }

    private static List<PecaDesenho> calcularPecas(Node<Pedras2> inicio, int tam, int larguraLinha) {
        List<PecaDesenho> pecas = new ArrayList<>();
        Node<Pedras2> atual = inicio;

        int gap = 4;
        boolean descendo = false;
        int pedrasDescendo = 0;
        int direcao = 1; 

        int cursorX = -gap;
        int cursorY = 0;

        int lastLeft = 0, lastRight = 0, lastTop = 0, lastBottom = 0;

        while (atual != null) {
            Pedras2 pedra = atual.getValue();
            boolean bucha = pedra.getLadoA() == pedra.getLadoB();
            
            // 1. Verifica se precisa virar a "cobra"
            if (!descendo) {
                int larguraNecessaria = bucha ? tam : tam * 2;
                if (direcao == 1 && cursorX + gap + larguraNecessaria > larguraLinha) {
                    descendo = true;
                    pedrasDescendo = 2;
                } else if (direcao == -1 && cursorX - gap - larguraNecessaria < 0) {
                    descendo = true;
                    pedrasDescendo = 2;
                }
            }

            // 2. Lógica de Descida (Curva)
            if (descendo) {
                // AQUI A MÁGICA: A bucha perde a propriedade de virar e fica vertical como as outras
                boolean vertical = true; 
                int dW = tam;      // Largura de peça em pé
                int dH = tam * 2;  // Altura de peça em pé

                int drawX = (direcao == 1) ? cursorX + gap : cursorX - gap - tam;
                int drawY = cursorY;

                pecas.add(new PecaDesenho(pedra, drawX, drawY, vertical, direcao == -1));

                lastLeft = drawX;
                lastRight = drawX + dW;
                lastTop = drawY;
                lastBottom = drawY + dH;

                cursorY += dH + gap;
                pedrasDescendo--;

                if (pedrasDescendo == 0) {
                    descendo = false;
                    cursorY = lastTop + tam; // Alinha para a próxima linha horizontal

                    if (direcao == 1) {
                        direcao = -1;
                        cursorX = lastLeft;
                    } else {
                        direcao = 1;
                        cursorX = lastRight;
                    }
                }
                atual = atual.getNext();
                continue;
            }

            // 3. Linha Horizontal (Bucha volta a ser transversal/90 graus)
            int drawY = bucha ? cursorY - tam / 2 : cursorY;
            int drawX;
            int W = bucha ? tam : tam * 2;

            if (direcao == 1) {
                drawX = cursorX + gap;
                cursorX = drawX + W;
            } else {
                drawX = cursorX - gap - W;
                cursorX = drawX;
            }

            pecas.add(new PecaDesenho(pedra, drawX, drawY, bucha, direcao == -1));
            atual = atual.getNext();
        }

        return pecas;
    }

    private static Rectangle calcularLimites(List<PecaDesenho> pecas, int tam) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (PecaDesenho peca : pecas) {
            int largura = peca.vertical ? tam : tam * 2;
            int altura = peca.vertical ? tam * 2 : tam;

            minX = Math.min(minX, peca.x);
            minY = Math.min(minY, peca.y);
            maxX = Math.max(maxX, peca.x + largura);
            maxY = Math.max(maxY, peca.y + altura);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    private static void desenharPeca(Graphics2D g2d, Image[] imgFaces, Pedras2 pedra,
                                     int x, int y, int tam, boolean vertical, boolean invertida) {
        int ladoA = invertida ? pedra.getLadoB() : pedra.getLadoA();
        int ladoB = invertida ? pedra.getLadoA() : pedra.getLadoB();

        if (vertical) {
            desenharFace(g2d, imgFaces[ladoA], ladoA, x, y, tam, true);
            desenharFace(g2d, imgFaces[ladoB], ladoB, x, y + tam, tam, true);
        } else {
            desenharFace(g2d, imgFaces[ladoA], ladoA, x, y, tam, false);
            desenharFace(g2d, imgFaces[ladoB], ladoB, x + tam, y, tam, false);
        }
    }

    private static void desenharFace(Graphics2D g2d, Image img, int valor, int x, int y, int tam, boolean vertical) {
        if (!vertical && valor == 6) {
            Graphics2D g = (Graphics2D) g2d.create();
            g.translate(x + tam / 2.0, y + tam / 2.0);
            g.rotate(Math.toRadians(90));
            g.drawImage(img, -tam / 2, -tam / 2, tam, tam, null);
            g.dispose();
        } else {
            g2d.drawImage(img, x, y, tam, tam, null);
        }
    }
}