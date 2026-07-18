package jogodomino;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import util.Cava;
import util.Jogador2;
import util.Lista;
import util.Node;
import util.Pedras2;

// Versão console do jogo de dominó com regras de 2 a 4 jogadores.
public class JogoDomino {

    public static void main(String[] args) {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        
        Random rdm = new Random();

        try (Scanner input = new Scanner(System.in)) {

            int qtdJogadores = qtdJogadores(input);

            Cava domino = new Cava();
            domino.criarDomino();

            // Monta uma lista circular para percorrer os jogadores em ordem de turno.
            Node<Jogador2> primeiroJogador = null;
            Node<Jogador2> atual = null;

            for (int i = 0; i < qtdJogadores; i++) {
                Jogador2 jogador = new Jogador2();
                jogador.setId(i + 1);

                Node<Jogador2> novoNo = new Node<>();
                novoNo.setValue(jogador);

                if (primeiroJogador == null) {
                    primeiroJogador = novoNo;
                    atual = primeiroJogador;
                } else {
                    atual.setNext(novoNo);
                    atual = novoNo;
                }
            }

            atual.setNext(primeiroJogador);

            Node<Jogador2> distribuidor = primeiroJogador;

            // Distribui 7 pedras aleatorias para cada jogador e remove essas pedras da cava.
            for (int j = 0; j < qtdJogadores; j++) {
                for (int i = 0; i < 7; i++) {
                    int indiceDaPeca;

                    do {
                        indiceDaPeca = rdm.nextInt(28);
                    } while (domino.getDomino()[indiceDaPeca].isDisponivel() == false);

                    distribuidor.getValue().setPedra(domino.getDomino()[indiceDaPeca]);
                    domino.getDomino()[indiceDaPeca].setDisponivel(false);
                }

                distribuidor = distribuidor.getNext();
            }

            Lista<Pedras2> mesa = new Lista<>();

            int continuar = 1;
            int lado;

            Node<Jogador2> turnoAtual = primeiroJogador;

            Node<Jogador2> impressora = primeiroJogador;

            for (int i = 0; i < qtdJogadores; i++) {
                System.out.println("Mão do jogador " + impressora.getValue().getId());
                impressora.getValue().mostraMao();
                impressora = impressora.getNext();
            }
            
            int contadorPassesSeguidos = 0; // Variável para controlar o trancamento (empate)
            int ultimoJogadorQueJogou = -1; // Para desempate em trancamento

            while (continuar == 1) {
                Jogador2 jogadorVez = turnoAtual.getValue();

                if (mesa.head.getNext() == null) {
                    // A primeira pedra apenas inicia a mesa; as proximas precisam encaixar nas pontas.
                    System.out.println("=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~");
                    System.out.println("          Iniciando o jogo");
                    System.out.println("=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~");

                    int indiceMao = entradaJogador(jogadorVez, input);
                    Pedras2 pedraDaMao = jogadorVez.getPedraMao(indiceMao);

                    mesa.addFim(pedraDaMao);
                    pedraDaMao.setJogada(true);

                    contadorPassesSeguidos = 0;// Reinicia o contador de passes seguidos após uma jogada bem-sucedida
                  
                } else {
                    Pedras2 pedraCabeca = mesa.head.getNext().getValue();
                    Pedras2 pedraCauda = mesa.tail.getValue();

                    // Se nao houver peca compativel, o jogador compra da cava enquanto ainda houver pecas.
                    while (!jogadorVez.checaSePodeJogar(pedraCabeca, pedraCauda)) {
                        if (contarDisponiveis(domino) > 0) {
                            Pedras2 comprada = comprarPeca(domino, rdm);
                            jogadorVez.setPedra(comprada);
                            comprada.setDisponivel(false);
                            System.out.println("Jogador " + jogadorVez.getId() + " comprou a peça -[" + comprada.getLadoA() + "|" + comprada.getLadoB() + "]- da cava.");
                        } else {
                            System.out.println("Jogador " + jogadorVez.getId() + " passou a vez!");
                            contadorPassesSeguidos++; // Incrementa o contador de passes seguidos
                            break;
                        }
                    }

                    if (jogadorVez.checaSePodeJogar(pedraCabeca, pedraCauda)) {
                        int indiceMao = entradaJogador(jogadorVez, input);
                        Pedras2 pedraDaMao = jogadorVez.getPedraMao(indiceMao);

                        while (pedraDaMao.getLadoA() != pedraCabeca.getLadoA()
                                && pedraDaMao.getLadoB() != pedraCabeca.getLadoA()
                                && pedraDaMao.getLadoA() != pedraCauda.getLadoB()
                                && pedraDaMao.getLadoB() != pedraCauda.getLadoB()) {

                            System.out.println("Pedra Cabeça: " + pedraCabeca.getLadoA()
                                    + ", Pedra Cauda: " + pedraCauda.getLadoB());

                            System.out.println("Pedra não cola com a mesa!");
                            System.out.println("Selecione uma nova pedra...");
                            System.out.println("Escolha a pedra a ser jogada (pelo índice):");

                            indiceMao = input.nextInt();
                            pedraDaMao = jogadorVez.getPedraMao(indiceMao);
                        }

                        if ((pedraDaMao.getLadoB() == pedraCabeca.getLadoA()
                                && pedraDaMao.getLadoA() == pedraCauda.getLadoB())
                                || (pedraDaMao.getLadoA() == pedraCabeca.getLadoA()
                                && pedraDaMao.getLadoB() == pedraCauda.getLadoB())) {

                            System.out.println("Posição da pedra (0) esquerda / (1) direita:");
                            // Quando a pedra encaixa nos dois lados, o jogador escolhe a ponta.
                            lado = input.nextInt();

                            if (lado == 0) {
                                if (pedraDaMao.getLadoB() != pedraCabeca.getLadoA()) {
                                    pedraDaMao.swap();
                                }

                                mesa.addInicio(pedraDaMao);
                                pedraDaMao.setJogada(true);
                            }

                            if (lado == 1) {
                                if (pedraDaMao.getLadoA() != pedraCauda.getLadoB()) {
                                    pedraDaMao.swap();
                                }

                                mesa.addFim(pedraDaMao);
                                pedraDaMao.setJogada(true);
                            }
                        } else {
                            if (pedraDaMao.getLadoB() == pedraCabeca.getLadoA()) {
                                mesa.addInicio(pedraDaMao);
                                pedraDaMao.setJogada(true);
                            } else if (pedraDaMao.getLadoA() == pedraCabeca.getLadoA()) {
                                pedraDaMao.swap();
                                mesa.addInicio(pedraDaMao);
                                pedraDaMao.setJogada(true);
                            } else if (pedraDaMao.getLadoA() == pedraCauda.getLadoB()) {
                                mesa.addFim(pedraDaMao);
                                pedraDaMao.setJogada(true);
                            } else if (pedraDaMao.getLadoB() == pedraCauda.getLadoB()) {
                                pedraDaMao.swap();
                                mesa.addFim(pedraDaMao);
                                pedraDaMao.setJogada(true);
                            }
                        }

                        contadorPassesSeguidos = 0; // Reinicia o contador de passes seguidos após uma jogada bem-sucedida
                        ultimoJogadorQueJogou = jogadorVez.getId(); // Atualiza o último jogador que jogou
                    }
                }

                if (contadorPassesSeguidos >= qtdJogadores) {
                    exibirRankingVencedorPorPontos(primeiroJogador, qtdJogadores, ultimoJogadorQueJogou);
                    break; 
                }

                System.out.println("Fim da jogada!");
                

                if (jogadorVez.checaVitoria() == true) {
                    if (qtdJogadores == 4) {
                        String dupla = getNomeDupla(jogadorVez.getId(), qtdJogadores);
                        System.out.println("\n===== VITÓRIA DA DUPLA! =====");
                        System.out.println(dupla + " ganharam.");
                    } else {
                        System.out.println("\n===== VITÓRIA! =====");
                        System.out.println("Jogador " + jogadorVez.getId() + " venceu!!!");
                    }
                    mostrarPecasRestantes(primeiroJogador, qtdJogadores);
                    System.out.println("GAMEOVER");
                    break;
                } else {
                    turnoAtual = turnoAtual.getNext();

                    System.out.println("Deseja continuar? (0 = não, 1 = sim)");
                    continuar = input.nextInt();

                    System.out.println("Mão do jogador " + turnoAtual.getValue().getId());
                    turnoAtual.getValue().mostraMao();
                }
            }
        }
    }

    static void exibirRankingVencedorPorPontos(Node<Jogador2> primeiro, int total, int ultimoJogadorQueJogou) {
        System.out.println("\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        System.out.println("   JOGO TRANCADO! NINGUÉM PODE JOGAR.   ");
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

        if (total == 4) {
            int pontDupla1 = calcularPontuacaoDupla(primeiro, total, 1);
            int pontDupla2 = calcularPontuacaoDupla(primeiro, total, 2);
            String vencedorDupla;
            if (pontDupla1 < pontDupla2) {
                vencedorDupla = "Jogadores 1 e 3";
            } else if (pontDupla2 < pontDupla1) {
                vencedorDupla = "Jogadores 2 e 4";
            } else {
                // empate, último jogador que jogou perde
                int duplaUltimo = getDupla(ultimoJogadorQueJogou, total);
                vencedorDupla = (duplaUltimo == 1) ? "Jogadores 2 e 4" : "Jogadores 1 e 3";
            }
            System.out.println("\n===== VITÓRIA DA DUPLA! =====");
            System.out.println(vencedorDupla + " ganharam.");
            System.out.println("Pontuação Dupla 1: " + pontDupla1);
            System.out.println("Pontuação Dupla 2: " + pontDupla2);
        } else {
            List<Jogador2> listaParaRanking = new ArrayList<>();
            Node<Jogador2> aux = primeiro;
            
            // Adiciona todos os jogadores na lista para ordenação
            for (int i = 0; i < total; i++) {
                listaParaRanking.add(aux.getValue());
                aux = aux.getNext();
            }

            // Ordena os jogadores: quem tem MENOS pontos restantes fica em primeiro
            Collections.sort(listaParaRanking, Comparator.comparingInt(Jogador2::getPontuacaoRestante));

            System.out.println("\n--- PONTUAÇÃO FINAL (MENOR PONTUAÇÃO VENCE) ---");
            for (int i = 0; i < listaParaRanking.size(); i++) {
                Jogador2 j = listaParaRanking.get(i);
                System.out.println((i + 1) + "º Lugar: Jogador " + j.getId() + " com " + j.getPontuacaoRestante() + " pontos.");
            }

            System.out.println("\nO VENCEDOR É O JOGADOR " + listaParaRanking.get(0).getId() + "!");
        }

        mostrarPecasRestantes(primeiro, total);
        System.out.println("==============================================");
    }

    static int qtdJogadores(Scanner input) {
        int valido = 0;
        int qtdJogadores;

        // O jogo suporta partidas de 2, 3 ou 4 jogadores.
        do {
            System.out.println("Informe a quantidade de jogadores: ");
            qtdJogadores = input.nextInt();

            if (qtdJogadores != 2 && qtdJogadores != 3 && qtdJogadores != 4) {
                System.out.println("Quantidade informada inválida!!!\n");
            } else {
                valido = 1;
            }
        } while (valido == 0);

        return qtdJogadores;
    }

    static int getDupla(int jogadorId, int qtdJogadores) {
        if (qtdJogadores == 4) {
            if (jogadorId == 1 || jogadorId == 3) return 1;
            if (jogadorId == 2 || jogadorId == 4) return 2;
        }
        return 0; // para 2 e 3 jogadores, não há duplas
    }

    static int calcularPontuacaoDupla(Node<Jogador2> primeiro, int qtdJogadores, int duplaId) {
        int pontuacao = 0;
        Node<Jogador2> aux = primeiro;
        for (int i = 0; i < qtdJogadores; i++) {
            if (getDupla(aux.getValue().getId(), qtdJogadores) == duplaId) {
                pontuacao += aux.getValue().getPontuacaoRestante();
            }
            aux = aux.getNext();
        }
        return pontuacao;
    }

    static String getNomeDupla(int jogadorId, int qtdJogadores) {
        if (qtdJogadores == 4) {
            return (jogadorId == 1 || jogadorId == 3) ? "Jogadores 1 e 3" : "Jogadores 2 e 4";
        }
        return "Jogador " + jogadorId;
    }

    static int contarDisponiveis(Cava domino) {
        int total = 0;
        for (Pedras2 pedra : domino.getDomino()) {
            if (pedra.isDisponivel()) {
                total++;
            }
        }
        return total;
    }

    static Pedras2 comprarPeca(Cava domino, Random rdm) {
        Pedras2[] pecas = domino.getDomino();
        int indice;
        do {
            indice = rdm.nextInt(pecas.length);
        } while (!pecas[indice].isDisponivel());
        pecas[indice].setDisponivel(false);
        return pecas[indice];
    }

    static void mostrarPecasRestantes(Node<Jogador2> primeiro, int qtdJogadores) {
        System.out.println("\n--- PEÇAS RESTANTES ---");
        Node<Jogador2> aux = primeiro;
        for (int i = 0; i < qtdJogadores; i++) {
            Jogador2 j = aux.getValue();
            System.out.print("Jogador " + j.getId() + ": ");
            List<Pedras2> restantes = j.getPedrasDisponiveis();
            if (restantes.isEmpty()) {
                System.out.println("Nenhuma - total = 0 pontos.");
            } else {
                for (Pedras2 p : restantes) {
                    System.out.print("-[" + p.getLadoA() + "|" + p.getLadoB() + "]- ");
                }
                System.out.println("total = " + j.getPontuacaoRestante() + " pontos.");
            }
            aux = aux.getNext();
        }
        System.out.println("=======================");
    }

    static int entradaJogador(Jogador2 jogadorVez, Scanner input) {
        System.out.println("=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~");
        System.out.println("Vez do Jogador " + jogadorVez.getId());
        System.out.println("Escolha a pedra a ser jogada (pelo índice):");

        int indiceMao = input.nextInt();

        Pedras2 pedraDaMao = jogadorVez.getPedraMao(indiceMao);

        // Impede selecionar uma pedra ja usada em rodadas anteriores.
        while (pedraDaMao.isJogada() == true) {
            System.out.println("A pedra selecionada já foi jogada!");
            System.out.println("Selecione uma nova pedra (pelo índice):");

            indiceMao = input.nextInt();
            pedraDaMao = jogadorVez.getPedraMao(indiceMao);
        }

        return indiceMao;
    }
}
