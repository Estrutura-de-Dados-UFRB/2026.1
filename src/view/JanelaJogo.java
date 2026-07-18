package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.*;
import util.*;

/**
 * JanelaJogo: O coração do Dominó.
 * Esta classe junta a Interface Gráfica (Swing) com as Estruturas de Dados (Listas Encadeadas).
 * É ela quem dita as regras, de quem é a vez, e quem ganha.
 */
public class JanelaJogo extends JPanel {

    // =======================================================
    // 1. VARIÁVEIS DE INTERFACE GRÁFICA (VISUAL)
    // =======================================================
    private MainFrame frame;         // Referência à janela principal (para poder trocar de tela)
    private PainelMesa painelMesa;   // Painel central onde as pedras jogadas são desenhadas
    private PainelMao painelMao;     // Painel inferior que mostra as peças do jogador da vez
    private PainelStatus painelStatus; // Barra superior com o texto "Jogador X", "Sua vez!"
    private JButton btnComprar;      // Botão para pegar pedras da cava
    private JLabel lblPopupAviso;    // O aviso amarelo flutuante ("Jogador passou a vez")

    // =======================================================
    // 2. VARIÁVEIS DE CONTROLE DE FLUXO (ESTADO DO JOGO)
    // =======================================================
    private boolean podeComprar = false; // Define se o botão de comprar fica verde ou cinza
    private boolean podePassar = false;  // Define se o jogo deve pular o jogador automaticamente
    private boolean processandoPassagem = false; // TRAVA: Impede que o jogador clique enquanto a mensagem de "passou" está na tela

    // =======================================================
    // 3. ESTRUTURAS DE DADOS (A MÁGICA DA MATÉRIA)
    // =======================================================
    private Cava cava;                 // Array/Objeto com as 28 peças do dominó
    private Lista<Pedras2> mesa;       // LISTA ENCADEADA: Guarda a ordem das pedras jogadas na mesa
    private Lista<Jogador2> jogadores; // LISTA ENCADEADA: Guarda a roda de jogadores

    // =======================================================
    // 4. REGRAS DA PARTIDA
    // =======================================================
    private int qtdJogadores = 2;       // Padrão de 2 jogadores
    private int indiceAtual = 0;        // Marca de quem é a vez (0 = Jogador 1, 1 = Jogador 2, etc)
    private int passesConsecutivos = 0; // Conta quantos passaram a vez seguidos (se chegar no total de jogadores, o jogo tranca)
    private int ultimoJogadorQueJogou = -1;

    /**
     * Construtor: Chamado quando você clica em "Iniciar Jogo" no Menu.
     */
    public JanelaJogo(MainFrame frame, int qtdJogadores) {
        this.frame = frame;
        this.qtdJogadores = validarQuantidadeJogadores(qtdJogadores); // Garante que ninguém bugue o menu
        
        configurarJanela(); // Ajusta cor de fundo
        iniciarJogo();      // Cria as listas, embaralha e distribui as peças
        montarInterface();  // Coloca os painéis e botões na tela
        atualizarTela();    // Desenha tudo pela primeira vez
    
    // =======================================================
    // MÚSICA DE FUNDO DURANTE A PARTIDA
    // =======================================================
    // ta comentado até eu achar uma musica legal nessa merda
        
        SoundPlayer.tocar("undertale.wav", true);

    }

    /**
     * Método Utilitário: Deixa os textos com a fonte Alegre Sans e as letras mais juntas.
     */
    private void aplicarEstiloAlegre(JComponent componente, float tamanho, boolean negrito) {
        Font fonteBase = new Font("Alegre Sans", negrito ? Font.BOLD : Font.PLAIN, (int)tamanho);
        Map<TextAttribute, Object> atributos = new HashMap<>();
        atributos.put(TextAttribute.TRACKING, -0.05); // -0.05 aproxima as letras (tracking)
        componente.setFont(fonteBase.deriveFont(atributos));
    }

    /**
     * Trava de segurança: Se vier um número maluco, força para ser 2 jogadores.
     */
    private int validarQuantidadeJogadores(int qtdJogadores) {
        if (qtdJogadores < 2 || qtdJogadores > 4) return 2;
        return qtdJogadores;
    }

    /**
     * Quando o jogo acaba, percorre a lista de jogadores e soma os pontos das peças que sobraram na mão.
     */
    private void mostrarPecasRestantes() {
        StringBuilder sb = new StringBuilder("Peças restantes:\n");
        
        // Loop pela quantidade de jogadores para montar o relatório
        for (int i = 0; i < qtdJogadores; i++) {
            Jogador2 j = getJogadorPorIndice(i);
            sb.append("Jogador ").append(j.getId()).append(": ");
            
            List<Pedras2> restantes = j.getPedrasDisponiveis();
            if (restantes.isEmpty()) {
                sb.append("Nenhuma - total = 0 pontos.\n"); // Quem bateu
            } else {
                for (Pedras2 p : restantes) {
                    sb.append("-[").append(p.getLadoA()).append("|").append(p.getLadoB()).append("]- ");
                }
                sb.append("total = ").append(j.getPontuacaoRestante()).append(" pontos.\n");
            }
        }

        // Mostra o que sobrou na mesa para jogos de 2 ou 3 pessoas
        if (qtdJogadores != 4) {
            sb.append("\nSobras no cava:\n");
            boolean temSobras = false;
            for (Pedras2 p : cava.getDomino()) {
                if (p.isDisponivel()) {
                    sb.append("-[").append(p.getLadoA()).append("|").append(p.getLadoB()).append("]- ");
                    temSobras = true;
                }
            }
            if (!temSobras) sb.append("Nenhuma");
            sb.append("\n");
        }

        // Cria a janelinha (Popup) para mostrar o texto final
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 300)); 

        JOptionPane.showMessageDialog(this, scrollPane, "Contagem de Peças", JOptionPane.INFORMATION_MESSAGE);
    }

    private void configurarJanela() {
        setLayout(new BorderLayout()); // Layout de bordas (Norte, Sul, Centro)
        setBackground(Color.BLACK);
    }

    /**
     * Instancia as Listas Encadeadas e distribui as peças.
     */
    private void iniciarJogo() {
        HistoricoXML.limpar(); 
        
        cava = new Cava();
        cava.criarDomino(); // Cria as 28 pedras do jogo

        mesa = new Lista<>(); // Inicializa a lista encadeada da mesa vazia
        jogadores = new Lista<>(); // Inicializa a lista encadeada de jogadores

        // Adiciona os jogadores na lista
        for (int i = 0; i < qtdJogadores; i++) {
            Jogador2 jogador = new Jogador2();
            jogador.setId(i + 1);
            jogadores.addFim(jogador); // Método da sua estrutura de dados!
        }

        distribuirPedras(); 
    }

    /**
     * Dá 7 pedras sorteadas para cada jogador da lista.
     */
    private void distribuirPedras() {
        // Pega o primeiro nó útil da lista de jogadores (pulando a Head/Sentinela)
        Node<Jogador2> aux = jogadores.getHead().getNext();
        
        for (int j = 0; j < qtdJogadores; j++) {
            Jogador2 jogador = aux.getValue();
            for (int i = 0; i < 7; i++) {
                Pedras2 pedra = sortearPedra();
                jogador.setPedra(pedra);
                pedra.setDisponivel(false); // Marca que a pedra saiu da cava
            }
            aux = aux.getNext(); // Avança para o próximo jogador da lista encadeada
        }
    }

    /**
     * Fica sorteando aleatoriamente até achar uma pedra que ainda está na cava.
     */
    private Pedras2 sortearPedra() {
        Random random = new Random();
        Pedras2[] domino = cava.getDomino();
        int indice;
        do {
            indice = random.nextInt(domino.length);
        } while (!domino[indice].isDisponivel()); // Repete se a pedra já foi pega
        return domino[indice];
    }

    // =======================================================
    // HELPERS PARA ACHAR DE QUEM É A VEZ
    // =======================================================
    private Jogador2 getJogadorAtual() { return getJogadorPorIndice(indiceAtual); }

    public PainelMao getPainelMao() {
        return painelMao;
    }

    public int getQtdJogadores() {
        return qtdJogadores;
    }

    // Percorre a lista encadeada até achar o jogador na posição desejada
    private Jogador2 getJogadorPorIndice(int indice) {
        Node<Jogador2> aux = jogadores.getHead().getNext();
        for (int i = 0; i < indice; i++) aux = aux.getNext();
        return aux.getValue();
    }

    private int getIdJogadorAtual() { return getJogadorAtual().getId(); }

    // Passa a vez usando Matemática Modular (se for 2 jogadores: 0 vira 1, 1 vira 0)
    private void proximoJogador() {
        indiceAtual = (indiceAtual + 1) % qtdJogadores;
    }

    /**
     * Monta onde cada botão e painel vai ficar na tela.
     */
    private void montarInterface() {
        painelStatus = new PainelStatus(this); 
        painelMesa = new PainelMesa();
        painelMao = new PainelMao(getMaoJogadorAtual());
        
        // Avisa que quando o jogador clicar numa peça, deve chamar o método "tentarJogarPedra"
        painelMao.addPecaClickListener(this::tentarJogarPedra);

        // Barra de rolagem para quando a mão tiver muitas peças (10+)
        JScrollPane scrollMao = new JScrollPane(
                painelMao,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollMao.setBorder(null);
        scrollMao.getViewport().setBackground(new Color(16, 18, 16));

        btnComprar = criarBotaoBase("COMPRAR");
        btnComprar.addActionListener(e -> acaoComprar());

        // Se tiver 4 jogadores, as 28 peças já foram distribuídas (7x4), então esconde o botão
        if (qtdJogadores == 4) {
            btnComprar.setVisible(false);
        }

        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.setBackground(new Color(16, 18, 16));

        JPanel wrapperComprar = new JPanel(new GridBagLayout());
        wrapperComprar.setOpaque(false);
        wrapperComprar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));
        wrapperComprar.add(btnComprar);

        painelInferior.add(scrollMao, BorderLayout.CENTER);
        painelInferior.add(wrapperComprar, BorderLayout.EAST);

        // Configuração do Popup amarelo que flutua na tela
        lblPopupAviso = new JLabel("", SwingConstants.CENTER);
        aplicarEstiloAlegre(lblPopupAviso, 24f, true); 
        lblPopupAviso.setForeground(Color.YELLOW);
        lblPopupAviso.setBackground(new Color(0, 0, 0, 150));
        lblPopupAviso.setOpaque(true);
        lblPopupAviso.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        lblPopupAviso.setVisible(false); // Começa invisível

        add(painelStatus, BorderLayout.NORTH);
        
        // O GridBagLayout permite empurrar o aviso lá pro teto da mesa (NORTH)
        painelMesa.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH; 
        gbc.weighty = 1.0; 
        gbc.insets = new Insets(20, 0, 0, 0); 
        
        painelMesa.add(lblPopupAviso, gbc);

        add(painelMesa, BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);
    }
    
    private JButton criarBotaoBase(String texto) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(140, 46));
        aplicarEstiloAlegre(btn, 20f, true); 
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }

    /**
     * Mostra o aviso e esconde sozinho depois de 1.8 segundos.
     */
    private void exibirPopup(String mensagem) {
        lblPopupAviso.setText("  " + mensagem + "  ");
        lblPopupAviso.setVisible(true);
        
        Timer timer = new Timer(1800, e -> {
            lblPopupAviso.setVisible(false);
            ((Timer)e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Animação que faz o botão de comprar balançar quando você clica mas não pode comprar.
     */
    private void tremerBotao(JButton btn) {
        Point original = btn.getLocation();
        int[] offsets = {8, -8, 6, -6, 4, -4, 2, -2, 0};
        int[] index = {0};

        Timer timer = new Timer(25, e -> {
            if (index[0] < offsets.length) {
                btn.setLocation(original.x + offsets[index[0]], original.y);
                index[0]++;
            } else {
                btn.setLocation(original); 
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    private void acaoComprar() {
        if (!podeComprar) {
            SoundPlayer.tocar("erro.wav", false);
            tremerBotao(btnComprar); // Dá a resposta visual do erro
            return;
        }
        SoundPlayer.tocar("click.wav", false);
        comprarPedra();
    }

    private List<Pedras2> getMaoJogadorAtual() {
        return new ArrayList<>(getJogadorAtual().getPedrasDisponiveis());
    }

    /**
     * Chamado toda vez que o turno muda. Sincroniza o visual com as listas.
     */
    private void atualizarTela() {
        painelMesa.limparMesa();
        
        // Percorre a lista encadeada da mesa para redesenhar todas as peças na ordem correta
        Node<Pedras2> atual = mesa.getHead().getNext();
        while (atual != null) {
            painelMesa.adicionarPedra(atual.getValue(), false);
            atual = atual.getNext();
        }

        // Atualiza os componentes visuais
        painelMao.setMao(getMaoJogadorAtual());
        painelStatus.atualizarJogador(getIdJogadorAtual());
        painelStatus.atualizarVez(true);
        painelStatus.atualizarMensagem("Cava: " + contarDisponiveis() + " pedras");
        
        atualizarControles(); // Verifica o que o botão comprar deve fazer agora
        verificarPassagemAutomatica(); // Confere se o jogador travou
    }

    /**
     * Regras booleanas para decidir se o jogador trava, compra ou joga.
     */
    private void atualizarControles() {
        Jogador2 jogadorAtual = getJogadorAtual();
        boolean temJogada = podeJogar(jogadorAtual);
        boolean cavaVazia = contarDisponiveis() == 0;
        boolean mesaVazia = mesa.getHead().getNext() == null;

        podeComprar = !temJogada && !cavaVazia && !mesaVazia;
        
        if (qtdJogadores == 4) {
            podePassar = !temJogada && !mesaVazia;
        } else {
            podePassar = !temJogada && cavaVazia && !mesaVazia;
        }

        // Pinta o botão dependendo do estado
        if (podeComprar) {
            btnComprar.setBackground(new Color(20, 130, 55)); 
            btnComprar.setForeground(Color.WHITE);
            btnComprar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            btnComprar.setBackground(new Color(40, 40, 40));  
            btnComprar.setForeground(new Color(100, 100, 100));
            btnComprar.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Se não tem o que fazer, o jogo passa a vez automaticamente após 2 segundos.
     */
    private void verificarPassagemAutomatica() {
        if (podePassar && !processandoPassagem) {
            processandoPassagem = true; // Liga a trava para este jogador
            
            exibirPopup("Jogador " + getIdJogadorAtual() + " passou a vez!");
            
            Timer autoPassTimer = new Timer(2000, e -> {
                
                // O SEGREDO ESTÁ AQUI: Libera a trava ANTES de rodar o próximo turno!
                processandoPassagem = false; 
                
                if (podePassar) { 
                    // Agora, quando o passarVez rodar o atualizarTela() para o próximo jogador,
                    // a trava já vai estar desligada e ele vai conseguir passar de novo se precisar.
                    passarVez();
                    painelMao.limparSelecao();
                }
                
                ((Timer)e.getSource()).stop();
            });
            
            autoPassTimer.setRepeats(false);
            autoPassTimer.start();
        }
    }

    private int contarDisponiveis() {
        int contador = 0;
        for (Pedras2 pedra : cava.getDomino()) {
            if (pedra.isDisponivel()) contador++;
        }
        return contador;
    }

    /**
     * Verifica se o clique do jogador na peça é válido e decide o lado da mesa.
     */
    private void tentarJogarPedra(int indice) {
        // Trava: se o jogo estiver processando uma passagem de vez, ignora os cliques afobados!
        if (processandoPassagem) return; 

        Jogador2 jogadorAtual = getJogadorAtual();
        List<Pedras2> disponiveis = jogadorAtual.getPedrasDisponiveis();
        
        if (indice < 0 || indice >= disponiveis.size()) return;
        
        Pedras2 pedra = disponiveis.get(indice);

        // Se a lista da mesa está vazia, joga e sai do método
        if (mesa.getHead().getNext() == null) {
            jogarPedra(pedra, true);
            return;
        }

        // Pega as extremidades da lista encadeada (Cabeça = esquerda, Cauda = Direita)
        Pedras2 cabeca = mesa.getHead().getNext().getValue();
        Pedras2 cauda = mesa.getTail().getValue();

        boolean encEsq = encaixaEsquerda(pedra, cabeca);
        boolean encDir = encaixaDireita(pedra, cauda);

        // Se encaixa nos dois lados (exemplo: a mesa tem 4 nas duas pontas e vc quer jogar um 4/4)
        if (encEsq && encDir) {
            Object[] options = {"Esquerda", "Direita"};
            int n = JOptionPane.showOptionDialog(this,
                    "A pedra encaixa nos dois lados. Onde quer jogar?",
                    "Escolha o Lado",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            
            if (n == JOptionPane.YES_OPTION) jogarPedra(pedra, true);
            else if (n == JOptionPane.NO_OPTION) jogarPedra(pedra, false);
            else painelMao.limparSelecao(); // Se ele cancelar a janela
            
        } else if (encEsq) {
            jogarPedra(pedra, true);
        } else if (encDir) {
            jogarPedra(pedra, false);
        } else {
            // Se a pedra não serve em lugar nenhum
            SoundPlayer.tocar("erro.wav", false);
            JOptionPane.showMessageDialog(this, "Essa pedra não encaixa na mesa!");
            painelMao.limparSelecao();
        }
    }

    /**
     * Adiciona a pedra fisicamente na Lista Encadeada da mesa e gira ela se necessário.
     */
    public void jogarPedra(Pedras2 pedra, boolean esquerda) {
        Jogador2 jogadorAtual = getJogadorAtual();

        // 1ª Peça do jogo
        if (mesa.getHead().getNext() == null) {
            mesa.addFim(pedra);
            painelMesa.adicionarPedraAnimada(pedra, false);
            HistoricoXML.salvarJogada(getIdJogadorAtual(), pedra, "inicio");
            finalizarJogada(jogadorAtual, pedra);
            return;
        }

        Pedras2 cabeca = mesa.getHead().getNext().getValue();
        Pedras2 cauda = mesa.getTail().getValue();

        if (esquerda) {
            // Se o lado da peça não combinar com o lado exposto, inverte a peça (swap)
            if (pedra.getLadoB() != cabeca.getLadoA()) pedra.swap();
            mesa.addInicio(pedra); // Insere no começo da Lista Encadeada
            painelMesa.adicionarPedraAnimada(pedra, true);
            HistoricoXML.salvarJogada(getIdJogadorAtual(), pedra, "esquerda");
        } else {
            if (pedra.getLadoA() != cauda.getLadoB()) pedra.swap();
            mesa.addFim(pedra); // Insere no final da Lista Encadeada
            painelMesa.adicionarPedraAnimada(pedra, false);
            HistoricoXML.salvarJogada(getIdJogadorAtual(), pedra, "direita");
        }

        finalizarJogada(jogadorAtual, pedra);
    }

    /**
     * Finaliza o turno atual e verifica se a partida acabou.
     */
    private void finalizarJogada(Jogador2 jogadorAtual, Pedras2 pedra) {
        pedra.setJogada(true);
        passesConsecutivos = 0; // Zera a contagem de bloqueio porque alguém jogou
        ultimoJogadorQueJogou = getIdJogadorAtual();
        SoundPlayer.tocar("jogada_correta.wav", false);

        // Se o array do jogador ficou vazio (Bateu)
        if (venceu(jogadorAtual)) {
            SoundPlayer.tocar("vitoria.wav", false);
            JOptionPane.showMessageDialog(this, "Jogador " + getIdJogadorAtual() + " venceu!");
            mostrarPecasRestantes(); 
            novoJogo(); // Reinicia
            return;
        }

        proximoJogador();
        atualizarTela();
    }

    public void comprarPedra() {
        if (processandoPassagem) return; // Trava contra cliques indesejados

        Jogador2 jogadorAtual = getJogadorAtual();
        Pedras2 comprada = sortearPedra();
        comprada.setDisponivel(false);
        jogadorAtual.setPedra(comprada); // Aumenta a mão do jogador
        passesConsecutivos = 0;

        SoundPlayer.tocar("peca_recebida.wav", false);
        atualizarTela();
    }

    public void passarVez() {
        SoundPlayer.tocar("passar.wav", false);
        passesConsecutivos++;

        // Condição de "Jogo Trancado": cava vazia e TODOS os jogadores passaram seguidos
        if (contarDisponiveis() == 0 && passesConsecutivos >= qtdJogadores) {
            finalizarJogoTrancado();
            return;
        }

        proximoJogador();
        atualizarTela();
    }

    // =======================================================
    // REGRAS BOOLEANAS DO DOMINÓ
    // =======================================================
    private boolean podeJogar(Jogador2 jogador) {
        if (mesa.getHead().getNext() == null) return true;
        Pedras2 cabeca = mesa.getHead().getNext().getValue();
        Pedras2 cauda = mesa.getTail().getValue();
        
        // Verifica se pelo menos uma pedra da mão encaixa
        for (Pedras2 p : jogador.getPedrasDisponiveis()) {
            if (encaixa(p, cabeca, cauda)) return true;
        }
        return false;
    }

    private boolean venceu(Jogador2 jogador) { return jogador.getPedrasDisponiveis().isEmpty(); }

    /**
     * Jogo trancado: percorre a lista de jogadores pra ver quem somou menos pontos nas peças restantes.
     */
    private void finalizarJogoTrancado() {
        Jogador2 vencedor = getJogadorPorIndice(0);
        for (int i = 1; i < qtdJogadores; i++) {
            Jogador2 j = getJogadorPorIndice(i);
            if (j.getPontuacaoRestante() < vencedor.getPontuacaoRestante()) vencedor = j;
        }
        
        SoundPlayer.tocar("game_over.wav", false);
        JOptionPane.showMessageDialog(this, "Jogo trancado! Jogador " + vencedor.getId() + " venceu por pontos.");
        mostrarPecasRestantes(); 
        novoJogo();
    }

    private boolean encaixa(Pedras2 p, Pedras2 cab, Pedras2 cau) {
        return encaixaEsquerda(p, cab) || encaixaDireita(p, cau);
    }

    private boolean encaixaEsquerda(Pedras2 p, Pedras2 cab) {
        return p.getLadoA() == cab.getLadoA() || p.getLadoB() == cab.getLadoA();
    }

    private boolean encaixaDireita(Pedras2 p, Pedras2 cau) {
        return p.getLadoA() == cau.getLadoB() || p.getLadoB() == cau.getLadoB();
    }

    /**
     * Troca o painel visível na tela pelo painel fornecido.
     */
    public void trocarPainel(JPanel novoPainel) {
        this.removeAll();
        this.add(novoPainel, BorderLayout.CENTER);
        this.revalidate(); // Avisa ao Java que a estrutura visual mudou
        this.repaint();    // Pinta a tela com a mudança
    }

    /**
     * Zera o jogo atual e volta pro Menu Inicial.
     */
    public void novoJogo() {
        try {
            SoundPlayer.parar(); // Mata todos os sons atuais
            if (frame != null) {
                frame.trocarPainel(new MenuPanel(frame)); // Recria o menu do zero
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
