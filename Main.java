import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final int PEDRAS_INICIAIS = 7;

    public static void main(String[] args) {
        new JogoDomino().iniciar();
    }

    private static class JogoDomino {
        private final Scanner scanner = new Scanner(System.in);
        private final List<Peca> monte = new ArrayList<>();
        private final List<Peca> jogador = new ArrayList<>();
        private final List<Peca> computador = new ArrayList<>();
        private final List<Peca> mesa = new ArrayList<>();

        void iniciar() {
            prepararJogo();

            System.out.println("=== Domino ===");
            System.out.println("Digite o numero da peca para jogar, C para comprar ou S para sair.");

            boolean vezDoJogador = true;
            int passesSeguidos = 0;

            while (true) {
                if (jogador.isEmpty() || computador.isEmpty()) {
                    mostrarResultado();
                    return;
                }

                if (!existeJogada(jogador) && !existeJogada(computador) && monte.isEmpty()) {
                    System.out.println("\nJogo fechado!");
                    mostrarResultado();
                    return;
                }

                boolean jogou;
                if (vezDoJogador) {
                    jogou = turnoJogador();
                } else {
                    jogou = turnoComputador();
                }

                passesSeguidos = jogou ? 0 : passesSeguidos + 1;
                if (passesSeguidos >= 2 && monte.isEmpty()) {
                    System.out.println("\nNinguem consegue jogar. Jogo fechado!");
                    mostrarResultado();
                    return;
                }

                vezDoJogador = !vezDoJogador;
            }
        }

        private void prepararJogo() {
            for (int esquerda = 0; esquerda <= 6; esquerda++) {
                for (int direita = esquerda; direita <= 6; direita++) {
                    monte.add(new Peca(esquerda, direita));
                }
            }

            Collections.shuffle(monte, new Random());
            for (int i = 0; i < PEDRAS_INICIAIS; i++) {
                jogador.add(comprar());
                computador.add(comprar());
            }
        }

        private boolean turnoJogador() {
            while (true) {
                mostrarMesa();
                mostrarMao();

                System.out.print("Sua jogada: ");
                String entrada = scanner.nextLine().trim();

                if (entrada.equalsIgnoreCase("S")) {
                    System.out.println("Jogo encerrado.");
                    System.exit(0);
                }

                if (entrada.equalsIgnoreCase("C")) {
                    if (monte.isEmpty()) {
                        System.out.println("Monte vazio. Voce passou a vez.");
                        return false;
                    }

                    Peca comprada = comprar();
                    jogador.add(comprada);
                    System.out.println("Voce comprou " + comprada + ".");
                    if (!podeJogar(comprada)) {
                        return false;
                    }
                    continue;
                }

                try {
                    int indice = Integer.parseInt(entrada) - 1;
                    if (indice < 0 || indice >= jogador.size()) {
                        System.out.println("Escolha uma peca valida.");
                        continue;
                    }

                    Peca peca = jogador.get(indice);
                    if (!podeJogar(peca)) {
                        System.out.println("Essa peca nao encaixa na mesa.");
                        continue;
                    }

                    jogarPeca(jogador.remove(indice));
                    return true;
                } catch (NumberFormatException e) {
                    System.out.println("Entrada invalida. Use o numero da peca, C ou S.");
                }
            }
        }

        private boolean turnoComputador() {
            System.out.println("\nVez do computador...");

            for (int i = 0; i < computador.size(); i++) {
                Peca peca = computador.get(i);
                if (podeJogar(peca)) {
                    jogarPeca(computador.remove(i));
                    System.out.println("Computador jogou " + peca + ".");
                    return true;
                }
            }

            while (!monte.isEmpty()) {
                Peca comprada = comprar();
                computador.add(comprada);

                if (podeJogar(comprada)) {
                    computador.remove(computador.size() - 1);
                    jogarPeca(comprada);
                    System.out.println("Computador comprou e jogou uma peca.");
                    return true;
                }
            }

            System.out.println("Computador passou a vez.");
            return false;
        }

        private Peca comprar() {
            return monte.remove(monte.size() - 1);
        }

        private boolean existeJogada(List<Peca> mao) {
            for (Peca peca : mao) {
                if (podeJogar(peca)) {
                    return true;
                }
            }
            return false;
        }

        private boolean podeJogar(Peca peca) {
            return mesa.isEmpty()
                    || peca.temValor(extremoEsquerdo())
                    || peca.temValor(extremoDireito());
        }

        private void jogarPeca(Peca peca) {
            if (mesa.isEmpty()) {
                mesa.add(peca);
                return;
            }

            int esquerda = extremoEsquerdo();
            int direita = extremoDireito();

            if (peca.direita == esquerda) {
                mesa.add(0, peca);
            } else if (peca.esquerda == esquerda) {
                mesa.add(0, peca.invertida());
            } else if (peca.esquerda == direita) {
                mesa.add(peca);
            } else if (peca.direita == direita) {
                mesa.add(peca.invertida());
            }
        }

        private int extremoEsquerdo() {
            return mesa.get(0).esquerda;
        }

        private int extremoDireito() {
            return mesa.get(mesa.size() - 1).direita;
        }

        private void mostrarMesa() {
            System.out.println("\nMesa: " + (mesa.isEmpty() ? "(vazia)" : mesa));
            System.out.println("Monte: " + monte.size() + " pecas | Computador: " + computador.size() + " pecas");
        }

        private void mostrarMao() {
            System.out.println("Sua mao:");
            for (int i = 0; i < jogador.size(); i++) {
                System.out.println((i + 1) + " - " + jogador.get(i));
            }
        }

        private void mostrarResultado() {
            int pontosJogador = somarPontos(jogador);
            int pontosComputador = somarPontos(computador);

            mostrarMesa();
            System.out.println("\nPontuacao restante:");
            System.out.println("Voce: " + pontosJogador);
            System.out.println("Computador: " + pontosComputador);

            if (jogador.isEmpty() || pontosJogador < pontosComputador) {
                System.out.println("Voce venceu!");
            } else if (computador.isEmpty() || pontosComputador < pontosJogador) {
                System.out.println("Computador venceu!");
            } else {
                System.out.println("Empate!");
            }
        }

        private int somarPontos(List<Peca> mao) {
            int total = 0;
            for (Peca peca : mao) {
                total += peca.esquerda + peca.direita;
            }
            return total;
        }
    }

    private static class Peca {
        private final int esquerda;
        private final int direita;

        Peca(int esquerda, int direita) {
            this.esquerda = esquerda;
            this.direita = direita;
        }

        boolean temValor(int valor) {
            return esquerda == valor || direita == valor;
        }

        Peca invertida() {
            return new Peca(direita, esquerda);
        }

        @Override
        public String toString() {
            return "[" + esquerda + "|" + direita + "]";
        }
    }
}
