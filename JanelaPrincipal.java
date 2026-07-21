package com.mycompany.sistemacoletaurbana;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class JanelaPrincipal extends JFrame {

    private Grafo mapa;
    private SimuladorVias simulador;
    private PainelGrafo painelGrafo;
    private JPanel areaCentral;
    private JPanel faixaContadores;

    private final JComboBox<String> comboOrigem = Estilo.combo();
    private final JComboBox<String> comboDestino = Estilo.combo();
    private final JTextField campoRotaAtual = Estilo.campo();

    private final JComboBox<String> comboViaInicio = Estilo.combo();
    private final JComboBox<String> comboViaFim = Estilo.combo();
    private final JCheckBox checkMaoDupla = Estilo.check("Rua de mao dupla (os dois sentidos)", true);
    private final JComboBox<String> comboAtributo = Estilo.combo();
    private final JTextField campoValor = Estilo.campo();

    private final JTextArea areaLog = new JTextArea(7, 20);
    private final JLabel rotuloCenario = new JLabel();

    private Vertice origemSelecionada;
    private Vertice destinoSelecionado;

    public JanelaPrincipal(Grafo mapa) {
        super("Simulador de Coleta de Lixo Urbano");
        this.mapa = mapa;
        this.simulador = new SimuladorVias(mapa);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Estilo.ASFALTO);
        montarInterface();
        atualizarCombos();
        registrar("Cenario carregado: " + mapa.getNome());
        setMinimumSize(new Dimension(1024, 680));
        pack();
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------------------------
    // MONTAGEM DA TELA
    // ------------------------------------------------------------------

    private void montarInterface() {
        painelGrafo = new PainelGrafo(mapa, simulador);
        painelGrafo.setAoClicarVertice(this::verticeClicado);

        areaCentral = new JPanel(new BorderLayout(0, 10));
        areaCentral.setOpaque(false);
        areaCentral.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 6));
        areaCentral.add(envolverEmCartao(painelGrafo), BorderLayout.CENTER);
        areaCentral.add(criarDiario(), BorderLayout.SOUTH);

        add(criarCabecalho(), BorderLayout.NORTH);
        add(areaCentral, BorderLayout.CENTER);
        add(criarTrilhaControles(), BorderLayout.EAST);
    }

    /** Moldura arredondada em volta do mapa. */
    private JComponent envolverEmCartao(Component conteudo) {
        Estilo.PainelArredondado cartao = new Estilo.PainelArredondado(Estilo.ASFALTO, 10);
        cartao.setLayout(new BorderLayout());
        cartao.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cartao.add(conteudo, BorderLayout.CENTER);
        return cartao;
    }

    private JComponent criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(14, 14, 12, 12));

        JPanel titulos = new JPanel();
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        titulos.setOpaque(false);

        JLabel titulo = new JLabel("SIMULADOR DE COLETA URBANA");
        titulo.setFont(Estilo.placa(19f, Font.BOLD));
        titulo.setForeground(Estilo.TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        rotuloCenario.setFont(Estilo.texto(11.5f, Font.PLAIN));
        rotuloCenario.setForeground(Estilo.TEXTO_SUAVE);
        rotuloCenario.setAlignmentX(Component.LEFT_ALIGNMENT);

        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(rotuloCenario);

        faixaContadores = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        faixaContadores.setOpaque(false);

        JPanel direita = new JPanel(new BorderLayout(12, 0));
        direita.setOpaque(false);
        direita.add(faixaContadores, BorderLayout.CENTER);
        direita.add(criarBotoesCenario(), BorderLayout.EAST);

        cabecalho.add(titulos, BorderLayout.WEST);
        cabecalho.add(direita, BorderLayout.EAST);
        return cabecalho;
    }

    private JComponent criarBotoesCenario() {
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botoes.setOpaque(false);
        botoes.add(Estilo.botao("Cenario padrao", Estilo.Variante.NEUTRO,
                e -> trocarCenario(CarregadorCenario.carregarCenarioComAnel())));
        botoes.add(Estilo.botao("Carregar CSV", Estilo.Variante.NEUTRO, e -> carregarCsv()));
        botoes.add(Estilo.botao("Reorganizar", Estilo.Variante.NEUTRO, e -> painelGrafo.reorganizar()));
        return botoes;
    }

    private JComponent criarDiario() {
        Estilo.PainelArredondado cartao = new Estilo.PainelArredondado(Estilo.PAINEL, 10);
        cartao.setLayout(new BorderLayout(0, 6));
        cartao.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        cartao.setPreferredSize(new Dimension(10, 150));

        areaLog.setEditable(false);
        areaLog.setBackground(Estilo.PAINEL);
        areaLog.setForeground(Estilo.TEXTO_SUAVE);
        areaLog.setFont(Estilo.dados(12f));
        areaLog.setBorder(null);

        cartao.add(Estilo.eyebrow("Diario de operacao"), BorderLayout.NORTH);
        cartao.add(Estilo.rolagem(areaLog), BorderLayout.CENTER);
        return cartao;
    }

    private JComponent criarTrilhaControles() {
        JPanel trilha = new JPanel();
        trilha.setLayout(new BoxLayout(trilha, BoxLayout.Y_AXIS));
        trilha.setOpaque(false);
        trilha.setBorder(BorderFactory.createEmptyBorder(0, 6, 12, 12));

        trilha.add(criarCartaoRotas());
        trilha.add(Box.createVerticalStrut(10));
        trilha.add(criarCartaoSimulacao());
        trilha.add(Box.createVerticalStrut(10));
        trilha.add(criarCartaoPendentes());
        trilha.add(Box.createVerticalGlue());

        JComponent rolagem = Estilo.rolagem(trilha);
        rolagem.setPreferredSize(new Dimension(330, 0));
        return rolagem;
    }

    /** Dijkstra, comparacao de custos e Carteiro Chines. */
    private JPanel criarCartaoRotas() {
        JPanel cartao = Estilo.cartao("Rotas");

        cartao.add(criarLinha("Origem", comboOrigem));
        cartao.add(Box.createVerticalStrut(5));
        cartao.add(criarLinha("Destino", comboDestino));
        cartao.add(Box.createVerticalStrut(9));
        cartao.add(Estilo.botao("Calcular rota de menor custo", Estilo.Variante.ACENTO,
                e -> calcularRotaMinima()));
        cartao.add(Box.createVerticalStrut(12));
        cartao.add(Estilo.divisoria());
        cartao.add(Box.createVerticalStrut(10));

        JLabel dica = Estilo.rotulo("Rota atual do caminhao, pontos por virgula");
        dica.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartao.add(dica);
        cartao.add(Box.createVerticalStrut(4));
        campoRotaAtual.setToolTipText("Ex.: Deposito,Centro,BairroSul,Aterro");
        campoRotaAtual.setAlignmentX(Component.LEFT_ALIGNMENT);
        Estilo.travarAltura(campoRotaAtual, 28);
        cartao.add(campoRotaAtual);
        cartao.add(Box.createVerticalStrut(6));
        cartao.add(Estilo.botao("Comparar com a rota otimizada", Estilo.Variante.NEUTRO,
                e -> compararRotas()));
        cartao.add(Box.createVerticalStrut(12));
        cartao.add(Estilo.divisoria());
        cartao.add(Box.createVerticalStrut(10));

        cartao.add(Estilo.botao("Cobrir todas as ruas (Carteiro Chines)", Estilo.Variante.NEUTRO,
                e -> rotaCarteiroChines()));
        cartao.add(Box.createVerticalStrut(6));
        cartao.add(Estilo.botao("Limpar destaque", Estilo.Variante.NEUTRO,
                e -> painelGrafo.limparDestaque()));
        return cartao;
    }

    /** Bloqueio, liberacao e alteracao de vias. */
    private JPanel criarCartaoSimulacao() {
        JPanel cartao = Estilo.cartao("Simulacao de vias");

        cartao.add(criarLinha("Inicio", comboViaInicio));
        cartao.add(Box.createVerticalStrut(5));
        cartao.add(criarLinha("Fim", comboViaFim));
        cartao.add(Box.createVerticalStrut(6));
        cartao.add(checkMaoDupla);
        cartao.add(Box.createVerticalStrut(8));

        JPanel dupla = new JPanel(new GridLayout(1, 2, 6, 0));
        dupla.setOpaque(false);
        dupla.setAlignmentX(Component.LEFT_ALIGNMENT);
        Estilo.travarAltura(dupla, 32);
        dupla.add(Estilo.botao("Interditar", Estilo.Variante.PERIGO, e -> bloquearVia()));
        dupla.add(Estilo.botao("Liberar", Estilo.Variante.NEUTRO, e -> desbloquearVia()));
        cartao.add(dupla);
        cartao.add(Box.createVerticalStrut(6));
        cartao.add(Estilo.botao("Liberar todas as vias", Estilo.Variante.NEUTRO, e -> desbloquearTudo()));
        cartao.add(Box.createVerticalStrut(12));
        cartao.add(Estilo.divisoria());
        cartao.add(Box.createVerticalStrut(10));

        comboAtributo.setModel(new DefaultComboBoxModel<>(
                new String[]{"Peso", "Distancia", "Volume de lixo"}));
        cartao.add(criarLinha("Alterar", comboAtributo));
        cartao.add(Box.createVerticalStrut(5));
        cartao.add(criarLinha("Valor", campoValor));
        cartao.add(Box.createVerticalStrut(8));
        cartao.add(Estilo.botao("Aplicar alteracao", Estilo.Variante.NEUTRO, e -> alterarVia()));
        return cartao;
    }

    /** Modulo da Pessoa 3: os controles ja existem, aguardando o algoritmo. */
    private JPanel criarCartaoPendentes() {
        JPanel cartao = Estilo.cartao("Conectividade e malha");

        JButton botaoBusca = Estilo.botao("Verificar alcance (BFS)", Estilo.Variante.NEUTRO,
                e -> verificarAlcance());
        JButton botaoMalha = Estilo.botao("Gerar malha economica (Kruskal)", Estilo.Variante.NEUTRO,
                e -> gerarMalhaEconomica());
        botaoBusca.setToolTipText("Parte do ponto escolhido em Origem");
        botaoMalha.setToolTipText("Arvore geradora minima sobre o mapa atual");

        cartao.add(botaoBusca);
        cartao.add(Box.createVerticalStrut(6));
        cartao.add(botaoMalha);
        return cartao;
    }

    // ------------------------------------------------------------------
    // ACOES - ROTAS
    // ------------------------------------------------------------------

    private void calcularRotaMinima() {
        Vertice origem = verticeDoCombo(comboOrigem);
        Vertice destino = verticeDoCombo(comboDestino);
        if (origem == null || destino == null || origem == destino) {
            registrar("Escolha uma origem e um destino diferentes.");
            return;
        }

        ResultadoDijkstra resultado = Dijkstra.calcular(mapa, origem.getNome());
        if (!resultado.isAlcancavel(destino)) {
            painelGrafo.destacarCaminho(null);
            registrar("Sem rota de " + origem.getNome() + " ate " + destino.getNome()
                    + ": o destino ficou isolado pelas interdicoes.");
            return;
        }

        List<Vertice> caminho = resultado.reconstruirCaminho(destino);
        painelGrafo.destacarCaminho(caminho);
        registrar(String.format("Rota otima: %s  |  custo %.2f",
                formatarCaminho(caminho), resultado.getDistancia(destino)));
    }

    private void compararRotas() {
        String texto = campoRotaAtual.getText().trim();
        if (texto.isEmpty()) {
            registrar("Informe a rota atual do caminhao, ex.: Deposito,Centro,BairroSul,Aterro");
            return;
        }

        List<Vertice> rotaAtual = new ArrayList<>();
        for (String nome : texto.split(",")) {
            Vertice v = mapa.buscarVertice(nome.trim());
            if (v == null) {
                registrar("Ponto inexistente na rota informada: " + nome.trim());
                return;
            }
            rotaAtual.add(v);
        }
        if (rotaAtual.size() < 2) {
            registrar("Informe pelo menos 2 pontos na rota atual.");
            return;
        }

        Double custoAtual = custoDaRota(rotaAtual);
        if (custoAtual == null) {
            registrar("Rota atual invalida: ha trecho sem via direta ou interditado.");
            return;
        }

        Vertice origem = rotaAtual.get(0);
        Vertice destino = rotaAtual.get(rotaAtual.size() - 1);
        ResultadoDijkstra resultado = Dijkstra.calcular(mapa, origem.getNome());
        if (!resultado.isAlcancavel(destino)) {
            registrar("Nao ha rota otimizada entre " + origem.getNome() + " e " + destino.getNome() + ".");
            return;
        }

        List<Vertice> rotaOtima = resultado.reconstruirCaminho(destino);
        double custoOtimo = resultado.getDistancia(destino);
        painelGrafo.destacarCaminho(rotaOtima);

        registrar(String.format("Rota atual....: %s  |  custo %.2f", formatarCaminho(rotaAtual), custoAtual));
        registrar(String.format("Rota otimizada: %s  |  custo %.2f", formatarCaminho(rotaOtima), custoOtimo));

        double economia = custoAtual - custoOtimo;
        if (economia <= 0.0001) {
            registrar("A rota atual ja e otima.");
        } else {
            registrar(String.format("Economia: %.2f  (%.1f%% de reducao)", economia, economia / custoAtual * 100));
        }
    }

    /** Soma o peso de cada trecho da rota informada. null = trecho inexistente ou interditado. */
    private Double custoDaRota(List<Vertice> rota) {
        double total = 0.0;
        for (int i = 0; i < rota.size() - 1; i++) {
            Aresta a = mapa.buscarAresta(rota.get(i), rota.get(i + 1));
            if (a == null || SimuladorVias.estaBloqueada(a)) {
                return null;
            }
            total += a.getPeso();
        }
        return total;
    }

    private void rotaCarteiroChines() {
        // Pre-condicao: so faz sentido procurar um circuito que passa por todas as
        // ruas se o grafo for conexo. Se algum ponto nao for alcancavel, nao existe
        // esse circuito - entao verificamos a conectividade (Pessoa 3) antes de rodar.
        ConectividadeService conectividade = new ConectividadeService();
        Vertice partida = mapa.getVertices().isEmpty() ? null : mapa.getVertices().get(0);
        if (partida == null || !conectividade.verificarConectividade(mapa, partida)) {
            List<Vertice> inalcancaveis = partida == null
                    ? new ArrayList<>()
                    : conectividade.verticesInalcancaveis(mapa, partida);
            registrar("Carteiro Chines nao executado: o mapa nao e conexo.");
            if (!inalcancaveis.isEmpty()) {
                StringBuilder nomes = new StringBuilder();
                for (Vertice v : inalcancaveis) {
                    if (nomes.length() > 0) {
                        nomes.append(", ");
                    }
                    nomes.append(v.getNome());
                }
                registrar("Pontos sem ligacao a partir de " + partida.getNome() + ": " + nomes);
                painelGrafo.destacarInalcancaveis(inalcancaveis);
            }
            registrar("Nao existe rota que passe por todas as ruas num mapa desconexo.");
            return;
        }

        int opcao = JOptionPane.showConfirmDialog(this,
                "O Carteiro Chines duplica arestas no grafo, e isso altera o mapa de forma permanente.\n"
                + "Deseja executar assim mesmo?",
                "Cobrir todas as ruas", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            CarteiroChines carteiro = new CarteiroChines(mapa);
            ArrayList<Vertice> impares = carteiro.verticesImpares(mapa.getVertices());
            registrar("Pontos de grau impar: " + impares.size());

            carteiro.calcularDistancia(new ArrayList<>(impares));   // copia: o metodo consome a lista
            List<Vertice> circuito = new MontarCaminho(mapa).Montagem();

            painelGrafo.destacarCaminho(circuito);
            registrar("Rota de coleta: " + formatarCaminho(circuito));
            registrar("Trechos percorridos: " + Math.max(circuito.size() - 1, 0));
        } catch (RuntimeException e) {
            registrar("O modulo da Pessoa 4 falhou: " + e);
        }
        atualizarContadores();
        painelGrafo.repaint();
    }

    /** BFS da Pessoa 3: quais pontos o caminhao alcanca saindo da origem escolhida. */
    private void verificarAlcance() {
        Vertice partida = verticeDoCombo(comboOrigem);
        if (partida == null) {
            registrar("Escolha em Origem o ponto de partida do caminhao.");
            return;
        }

        ConectividadeService servico = new ConectividadeService();
        List<Vertice> inalcancaveis = servico.verticesInalcancaveis(mapa, partida);
        painelGrafo.destacarArestas(null);
        painelGrafo.destacarInalcancaveis(inalcancaveis);

        int total = mapa.getVertices().size();
        if (inalcancaveis.isEmpty()) {
            registrar("Alcance a partir de " + partida.getNome() + ": todos os "
                    + total + " pontos sao atendidos.");
        } else {
            StringBuilder nomes = new StringBuilder();
            for (Vertice v : inalcancaveis) {
                if (nomes.length() > 0) {
                    nomes.append(", ");
                }
                nomes.append(v.getNome());
            }
            registrar("Alcance a partir de " + partida.getNome() + ": "
                    + (total - inalcancaveis.size()) + " de " + total + " pontos. "
                    + "Ficam sem coleta: " + nomes);
        }
        conferirBfsContraDijkstra(partida, inalcancaveis.size());
    }

    /**
     * O BFS da Pessoa 3 percorre as arestas sem olhar o peso, entao ele atravessa
     * vias interditadas. Enquanto isso nao for tratado no modulo dela, a divergencia
     * e apontada aqui em vez de passar despercebida.
     */
    private void conferirBfsContraDijkstra(Vertice partida, int inalcancaveisBfs) {
        if (simulador.listarBloqueadas().isEmpty()) {
            return;
        }
        ResultadoDijkstra resultado = Dijkstra.calcular(mapa, partida.getNome());
        int isoladosDeVerdade = 0;
        for (Vertice v : mapa.getVertices()) {
            if (v != partida && !resultado.isAlcancavel(v)) {
                isoladosDeVerdade++;
            }
        }
        if (isoladosDeVerdade > inalcancaveisBfs) {
            registrar("Atencao: o BFS ignora interdicoes e contou "
                    + (isoladosDeVerdade - inalcancaveisBfs) + " ponto(s) a mais como atendidos. "
                    + "Pelo Dijkstra, " + isoladosDeVerdade + " estao isolados.");
        }
    }

    /** Kruskal da Pessoa 3: malha minima que mantem todos os pontos ligados. */
    private void gerarMalhaEconomica() {
        MalhaEconomica malha = new MalhaEconomica();
        List<Aresta> arvore = malha.kruskal(mapa);

        painelGrafo.destacarInalcancaveis(null);
        painelGrafo.destacarArestas(arvore);

        registrar(String.format("Malha economica: %d vias  |  custo %.2f  |  %.1f km de ruas",
                arvore.size(), malha.custoTotal(arvore), malha.distanciaTotal(arvore)));

        int esperado = mapa.getVertices().size() - 1;
        if (arvore.size() < esperado) {
            registrar("O mapa esta desconexo: a malha ficou com " + arvore.size()
                    + " vias, seriam necessarias " + esperado + " para ligar todos os pontos.");
        }

        int interditadas = 0;
        for (Aresta a : arvore) {
            if (SimuladorVias.estaBloqueada(a)) {
                interditadas++;
            }
        }
        if (interditadas > 0) {
            registrar("Atencao: a malha inclui " + interditadas
                    + " via(s) interditada(s) - o Kruskal nao filtra interdicoes.");
        }
    }

    // ------------------------------------------------------------------
    // ACOES - SIMULACAO DE VIAS
    // ------------------------------------------------------------------

    private void bloquearVia() {
        Vertice inicio = verticeDoCombo(comboViaInicio);
        Vertice fim = verticeDoCombo(comboViaFim);
        if (inicio == null || fim == null || inicio == fim) {
            registrar("Escolha dois pontos diferentes para a via.");
            return;
        }

        int qtd = simulador.bloquearVia(inicio.getNome(), fim.getNome(), checkMaoDupla.isSelected());
        if (qtd == 0) {
            registrar("Nada a interditar em " + inicio.getNome() + " -> " + fim.getNome()
                    + ": a via nao existe ou ja esta interditada.");
        } else {
            registrar("Interditada a via " + inicio.getNome() + " -> " + fim.getNome()
                    + " (" + qtd + " sentido(s)). Recalcule a rota para ver o desvio.");
        }
        atualizarContadores();
        painelGrafo.repaint();
    }

    private void desbloquearVia() {
        Vertice inicio = verticeDoCombo(comboViaInicio);
        Vertice fim = verticeDoCombo(comboViaFim);
        if (inicio == null || fim == null) {
            return;
        }

        int qtd = simulador.desbloquearVia(inicio.getNome(), fim.getNome(), checkMaoDupla.isSelected());
        registrar(qtd == 0
                ? "Essa via nao estava interditada."
                : "Liberada a via " + inicio.getNome() + " -> " + fim.getNome()
                  + " (" + qtd + " sentido(s)), peso original restaurado.");
        atualizarContadores();
        painelGrafo.repaint();
    }

    private void desbloquearTudo() {
        registrar(simulador.desbloquearTudo() + " via(s) liberada(s).");
        atualizarContadores();
        painelGrafo.repaint();
    }

    private void alterarVia() {
        Vertice inicio = verticeDoCombo(comboViaInicio);
        Vertice fim = verticeDoCombo(comboViaFim);
        if (inicio == null || fim == null || inicio == fim) {
            registrar("Escolha dois pontos diferentes para a via.");
            return;
        }

        Double valor;
        try {
            valor = Double.parseDouble(campoValor.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            registrar("Valor numerico invalido: '" + campoValor.getText().trim() + "'");
            return;
        }

        String atributo = (String) comboAtributo.getSelectedItem();
        int qtd;
        if ("Distancia".equals(atributo)) {
            qtd = simulador.alterarDistancia(inicio.getNome(), fim.getNome(), valor);
        } else if ("Volume de lixo".equals(atributo)) {
            qtd = simulador.alterarVolumeLixo(inicio.getNome(), fim.getNome(), valor);
        } else {
            qtd = simulador.alterarPeso(inicio.getNome(), fim.getNome(), valor);
        }

        registrar(qtd == 0
                ? "Via nao encontrada: " + inicio.getNome() + " -> " + fim.getNome()
                : atributo + " de " + inicio.getNome() + " -> " + fim.getNome()
                  + " agora e " + valor + " (" + qtd + " sentido(s)).");
        painelGrafo.repaint();
    }

    // ------------------------------------------------------------------
    // CENARIO
    // ------------------------------------------------------------------

    private void carregarCsv() {
        JFileChooser seletor = new JFileChooser(new File("."));
        seletor.setFileFilter(new FileNameExtensionFilter("Cenario CSV (*.csv, *.txt)", "csv", "txt"));
        if (seletor.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            trocarCenario(CarregadorCenario.carregarDeArquivo(seletor.getSelectedFile().getAbsolutePath()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Falha ao carregar o cenario",
                    JOptionPane.ERROR_MESSAGE);
            registrar("Falha ao carregar o cenario: " + e.getMessage());
        }
    }

    private void trocarCenario(Grafo novoMapa) {
        this.mapa = novoMapa;
        this.simulador = new SimuladorVias(novoMapa);
        this.origemSelecionada = null;
        this.destinoSelecionado = null;

        // o mapa depende do simulador: recria o painel e troca no centro
        PainelGrafo novoPainel = new PainelGrafo(novoMapa, simulador);
        novoPainel.setAoClicarVertice(this::verticeClicado);
        areaCentral.remove(painelGrafo.getParent());
        areaCentral.add(envolverEmCartao(novoPainel), BorderLayout.CENTER);
        painelGrafo = novoPainel;
        areaCentral.revalidate();
        areaCentral.repaint();

        atualizarCombos();
        registrar("Cenario carregado: " + novoMapa.getNome());
    }

    /** Clique no mapa: 1o define a origem, 2o o destino, 3o recomeca. */
    private void verticeClicado(Vertice v) {
        if (origemSelecionada == null) {
            origemSelecionada = v;
            destinoSelecionado = null;
        } else if (destinoSelecionado == null && v != origemSelecionada) {
            destinoSelecionado = v;
        } else {
            origemSelecionada = v;
            destinoSelecionado = null;
        }

        if (origemSelecionada != null) {
            comboOrigem.setSelectedItem(origemSelecionada.getNome());
            comboViaInicio.setSelectedItem(origemSelecionada.getNome());
        }
        if (destinoSelecionado != null) {
            comboDestino.setSelectedItem(destinoSelecionado.getNome());
            comboViaFim.setSelectedItem(destinoSelecionado.getNome());
        }
        painelGrafo.setOrigemDestino(origemSelecionada, destinoSelecionado);
    }

    private void atualizarCombos() {
        String[] nomes = Estilo.nomes(mapa.getVertices());
        comboOrigem.setModel(new DefaultComboBoxModel<>(nomes));
        comboDestino.setModel(new DefaultComboBoxModel<>(nomes));
        comboViaInicio.setModel(new DefaultComboBoxModel<>(nomes));
        comboViaFim.setModel(new DefaultComboBoxModel<>(nomes));

        if (nomes.length > 1) {
            comboDestino.setSelectedIndex(nomes.length - 1);
            comboViaFim.setSelectedIndex(1);
        }
        rotuloCenario.setText(mapa.getNome());
        atualizarContadores();
    }

    /** Contadores do cabecalho: refletem o estado real do grafo a cada acao. */
    private void atualizarContadores() {
        int bloqueios = simulador.listarBloqueadas().size();
        faixaContadores.removeAll();
        faixaContadores.add(Estilo.chip(String.valueOf(mapa.getVertices().size()), "pontos", Estilo.TEXTO));
        faixaContadores.add(Estilo.chip(String.valueOf(mapa.getArestas().size()), "vias", Estilo.TEXTO));
        faixaContadores.add(Estilo.chip(String.valueOf(bloqueios), "interditadas",
                bloqueios > 0 ? Estilo.VERMELHO : Estilo.TEXTO_SUAVE));
        faixaContadores.revalidate();
        faixaContadores.repaint();
    }

    private JComponent criarLinha(String rotuloTexto, JComponent campo) {
        JPanel linha = new JPanel(new BorderLayout(8, 0));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rotulo = Estilo.rotulo(rotuloTexto);
        rotulo.setPreferredSize(new Dimension(58, 26));
        linha.add(rotulo, BorderLayout.WEST);
        linha.add(campo, BorderLayout.CENTER);
        Estilo.travarAltura(linha, 28);
        return linha;
    }

    private Vertice verticeDoCombo(JComboBox<String> combo) {
        Object selecionado = combo.getSelectedItem();
        return selecionado == null ? null : mapa.buscarVertice(selecionado.toString());
    }

    private String formatarCaminho(List<Vertice> caminho) {
        if (caminho == null || caminho.isEmpty()) {
            return "(vazio)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < caminho.size(); i++) {
            if (i > 0) {
                sb.append(" > ");
            }
            sb.append(caminho.get(i).getNome());
        }
        return sb.toString();
    }

    private void registrar(String mensagem) {
        areaLog.append(mensagem + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    /** Abre a interface grafica com o cenario informado. */
    public static void abrir(Grafo mapa) {
        SwingUtilities.invokeLater(() -> {
            // deixa dialogos e tooltips no mesmo tom da janela
            UIManager.put("OptionPane.background", Estilo.PAINEL);
            UIManager.put("Panel.background", Estilo.PAINEL);
            UIManager.put("OptionPane.messageForeground", Estilo.TEXTO);
            UIManager.put("ToolTip.background", Estilo.ELEVADO);
            UIManager.put("ToolTip.foreground", Estilo.TEXTO);
            new JanelaPrincipal(mapa).setVisible(true);
        });
    }
}