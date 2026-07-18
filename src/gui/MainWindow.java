package gui;

import model.Grafo;
import model.Ponto;
import model.Rota;
import model.TipoPonto;
import services.ProjetoService;
import services.RouteService;
import services.SimulationService;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@SuppressWarnings({"serial", "this-escape"})
public class MainWindow extends JFrame {

    private final Grafo grafo = new Grafo();
    private final SimulationService simulationService = new SimulationService(grafo);
    private final RouteService routeService = new RouteService(grafo);
    private final ProjetoService projetoService = new ProjetoService();

    private final MapPanel mapPanel = new MapPanel();
    private final NavigationTreePanel treePanel = new NavigationTreePanel();
    private final DetailsPanel detailsPanel = new DetailsPanel();
    private final BottomRoutePanel bottomRoutePanel = new BottomRoutePanel();
    private final StatisticsPanel statisticsPanel = new StatisticsPanel();
    private final JTabbedPane telas = new JTabbedPane();
    private final JLabel statusLabel = new JLabel("Pronto");
    private final JProgressBar progressBar = new JProgressBar();
    private final RouteUiController routeController = new RouteUiController(this, grafo, routeService,
            progressBar, statusLabel, this::aceitarResultadoRota);
    private final AppHeader appHeader = new AppHeader(this::executarComandoUi);
    private final MainContentView contentView = new MainContentView(mapPanel, statisticsPanel, bottomRoutePanel,
            statusLabel, progressBar, telas);

    private final EnumMap<TipoPonto, Boolean> tiposVisiveis = new EnumMap<>(TipoPonto.class);

    private RouteOperation ultimaOperacao = RouteOperation.NONE;
    private Ponto ultimaOrigem;
    private Ponto ultimoDestino;
    private Rota rotaAtual;
    private Ponto pontoSelecionado;
    private final FileUiController fileController = new FileUiController(this, grafo, projetoService,
            () -> rotaAtual, this::algoritmoAtivo, this::substituirGrafo, this::setStatus);

    public MainWindow() {
        super("Sistema de Planejamento de Rotas - Cruz das Almas/BA");
        configurarVisual();
        configurarMenu();
        configurarLayout();
        configurarEventos();

        for (TipoPonto tipo : TipoPonto.values()) {
            tiposVisiveis.put(tipo, Boolean.TRUE);
        }

        simulationService.adicionarObservador(this::atualizarTudo);
        simulationService.carregarPadrao();
        mapPanel.centralizarCruzDasAlmas();
    }

    private void configurarVisual() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 720));
        getContentPane().setBackground(UiTheme.BACKGROUND);
    }

    private void configurarLayout() {
        setContentPane(contentView.build(appHeader));
    }
    private void executarComandoUi(AppHeader.Command command) {
        switch (command) {
            case DIJKSTRA -> routeController.dijkstra(); case PRIM -> routeController.prim(); case KRUSKAL -> routeController.kruskal();
            case BFS -> routeController.bfs(); case DFS -> routeController.dfs(); case GREEDY -> routeController.greedy(); case TSP -> routeController.tsp();
            case PLAY -> iniciarAnimacao(); case PAUSE -> pausarAnimacao(); case STOP -> pararAnimacao(); case CLEAR -> limparRota();
            case POINTS -> abrirJanelaPontos();
        }
    }

    private void configurarMenu() {
        JMenuBar bar = new JMenuBar();

        JMenu arquivo = new JMenu("Arquivo");
        arquivo.add(item("Novo Projeto", e -> novoProjeto()));
        arquivo.add(item("Abrir", e -> fileController.open()));
        arquivo.add(item("Salvar", e -> fileController.save()));
        arquivo.addSeparator();
        arquivo.add(item("Exportar PDF", e -> fileController.pdf()));
        arquivo.add(item("Exportar CSV", e -> fileController.csv()));
        arquivo.add(item("Exportar TXT", e -> fileController.txt()));
        arquivo.addSeparator();
        arquivo.add(item("Sair", e -> dispose()));

        JMenu visualizar = new JMenu("Visualizar");
        visualizar.add(item("Centralizar mapa", e -> mapPanel.centralizarCruzDasAlmas()));
        visualizar.add(item("Recarregar mapa", e -> mapPanel.recarregarMapa()));
        visualizar.addSeparator();
        visualizar.add(checkItem("Mostrar bairros", TipoPonto.BAIRRO));
        visualizar.add(checkItem("Mostrar escolas", TipoPonto.ESCOLA));
        visualizar.add(checkItem("Mostrar universidades", TipoPonto.UNIVERSIDADE));
        visualizar.add(checkItem("Mostrar pontos", TipoPonto.PONTO_EMBARQUE));

        JMenu navegar = new JMenu("Telas");
        navegar.add(item("Mapa", e -> mostrarMapa()));

        JMenu algoritmos = new JMenu("Algoritmos");
        algoritmos.add(item("Dijkstra", e -> routeController.dijkstra()));
        algoritmos.add(item("Prim", e -> routeController.prim()));
        algoritmos.add(item("Kruskal", e -> routeController.kruskal()));
        algoritmos.add(item("BFS", e -> routeController.bfs()));
        algoritmos.add(item("DFS", e -> routeController.dfs()));
        algoritmos.add(item("Guloso", e -> routeController.greedy()));
        algoritmos.add(item("Caixeiro Viajante", e -> routeController.tsp()));

        JMenu ajuda = new JMenu("Ajuda");
        ajuda.add(item("Sobre", e -> JOptionPane.showMessageDialog(this,
                "Sistema de Planejamento de Rotas\nCruz das Almas - BA\nJava Swing + JXMapViewer2",
                "Sobre", JOptionPane.INFORMATION_MESSAGE)));

        bar.add(arquivo);
        bar.add(navegar);
        bar.add(visualizar);
        bar.add(algoritmos);
        bar.add(ajuda);
        setJMenuBar(bar);
    }

    private JMenuItem item(String texto, java.awt.event.ActionListener listener) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(listener);
        return item;
    }

    private JCheckBoxMenuItem checkItem(String texto, TipoPonto tipo) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(texto, true);
        item.addActionListener(e -> {
            tiposVisiveis.put(tipo, item.isSelected());
            atualizarTudo();
        });
        return item;
    }

    private void configurarEventos() {
        mapPanel.setOnPontoSelecionado(ponto -> {
            pontoSelecionado = ponto;
            mapPanel.selecionarPonto(ponto);
            mostrarDetalhes(ponto);
        });
        treePanel.setOnSelecionado(ponto -> {
            pontoSelecionado = ponto;
            mapPanel.selecionarPonto(ponto);
            mostrarDetalhes(ponto);
        });
        mapPanel.setOnAnimationProgress(progresso -> {
            int percentual = (int) Math.round(progresso * 100.0);
            progressBar.setIndeterminate(false);
            progressBar.setValue(percentual);
            statusLabel.setText("Veículo em movimento: " + percentual + "% da rota");
        });
        mapPanel.setOnTipoLegendaAlternado(tipo -> {
            boolean atual = Boolean.TRUE.equals(tiposVisiveis.get(tipo));
            tiposVisiveis.put(tipo, !atual);
            atualizarTudo();
        });
    }

    private void iniciarAnimacao() {
        if (rotaAtual == null || rotaAtual.getPercurso().size() < 2) {
            JOptionPane.showMessageDialog(this, "Calcule uma rota com pelo menos dois pontos antes de iniciar.");
            return;
        }
        mostrarMapa();
        mapPanel.iniciarAnimacao(appHeader.speedValue());
    }

    private void pausarAnimacao() {
        mapPanel.pausarAnimacao();
        setStatus("Animação pausada");
    }

    private void pararAnimacao() {
        mapPanel.pararAnimacao();
        progressBar.setValue(0);
        setStatus("Animação reiniciada");
    }

    private void mostrarMapa() {
        telas.setSelectedIndex(0);
    }

    private void mostrarDetalhes(Ponto ponto) {
        if (ponto == null) {
            detailsPanel.limpar();
            return;
        }
        detailsPanel.mostrar(ponto, grafo);
    }

    private void atualizarTudo() {
        List<Ponto> pontosVisiveis = pontosVisiveis();
        treePanel.atualizar(pontosVisiveis);
        mapPanel.setPontos(pontosVisiveis);
        if (rotaAtual != null) {
            // Pontos são mutáveis: garante atualização imediata após editar latitude/longitude.
            if (!rotaAtual.isTrajetoViario()) rotaAtual.atualizarGeometriaLocal();
            mapPanel.destacarRota(rotaAtual, corDaOperacao(ultimaOperacao));
            bottomRoutePanel.mostrar(rotaAtual);
            statisticsPanel.atualizar(grafo, rotaAtual, algoritmoAtivo());
            atualizarIndicadoresMapa(rotaAtual);
        } else {
            mapPanel.limparRota();
            bottomRoutePanel.limpar();
            statisticsPanel.limpar();
            statisticsPanel.atualizar(grafo, null, null);
            atualizarIndicadoresMapa(null);
        }
        statusLabel.setText("Pontos: " + grafo.getPontos().size() + " | Arestas: " + grafo.getArestas().size());
        if (pontoSelecionado != null && grafo.getPontos().contains(pontoSelecionado)) {
            mapPanel.selecionarPonto(pontoSelecionado);
            mostrarDetalhes(pontoSelecionado);
        } else if (ultimaOperacao == RouteOperation.NONE) {
            mostrarDetalhes(null);
        }
    }

    private void atualizarIndicadoresMapa(Rota rota) {
        contentView.updateSummary(grafo, rota);
    }

    private List<Ponto> pontosVisiveis() {
        List<Ponto> lista = new ArrayList<>();
        for (Ponto ponto : grafo.getPontos()) {
            if (Boolean.TRUE.equals(tiposVisiveis.get(ponto.getTipo()))) {
                lista.add(ponto);
            }
        }
        for (TipoPonto tipo : TipoPonto.values()) {
            mapPanel.setTipoLegendaVisivel(tipo, Boolean.TRUE.equals(tiposVisiveis.get(tipo)));
        }
        return lista;
    }

    private void novoProjeto() {
        grafo.limpar();
        rotaAtual = null;
        routeService.limpar();
        ultimaOperacao = RouteOperation.NONE;
        ultimaOrigem = null;
        ultimoDestino = null;
        pontoSelecionado = null;
        simulationService.carregarPadrao();
    }

    private void restaurarMapaPadrao() {
        if (JOptionPane.showConfirmDialog(this,
                "Restaurar mapa e dados padrao de 1500 alunos?",
                "Restaurar mapa",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        grafo.limpar();
        rotaAtual = null;
        routeService.limpar();
        ultimaOperacao = RouteOperation.NONE;
        ultimaOrigem = null;
        ultimoDestino = null;
        pontoSelecionado = null;
        simulationService.carregarPadrao();
        mapPanel.centralizarCruzDasAlmas();
        setStatus("Mapa restaurado com dados padrao");
    }

    private void novoPonto() {
        PointFormDialog.Data result = PointFormDialog.show(this, null);
        if (result == null) {
            return;
        }
        try {
            Ponto novo = simulationService.adicionarPonto(result.nome(), result.bairro(), result.latitude(), result.longitude(),
                    result.alunos(), result.capacidade(), result.prioridade(), result.turno(), result.tipo());
            novo.setQuantidadeDesembarque(0);
            pontoSelecionado = novo;
            mapPanel.selecionarPonto(novo);
            mostrarDetalhes(novo);
            setStatus("Ponto adicionado: " + result.nome());
        } catch (IllegalArgumentException ex) {
            erro("Erro ao adicionar ponto", ex);
        }
    }

    private void editarPontoSelecionado() {
        Ponto selecionado = pontoSelecionado();
        if (selecionado == null) {
            return;
        }
        PointFormDialog.Data result = PointFormDialog.show(this, selecionado);
        if (result == null) {
            return;
        }
        try {
            selecionado.setNome(result.nome());
            selecionado.setBairro(result.bairro());
            selecionado.setLatitude(result.latitude());
            selecionado.setLongitude(result.longitude());
            selecionado.setQuantidadeAlunos(result.alunos());
            selecionado.setQuantidadeDesembarque(0);
            selecionado.setCapacidade(result.capacidade());
            selecionado.setPrioridade(result.prioridade());
            selecionado.setTurno(result.turno());
            selecionado.setTipo(result.tipo());
            if (rotaAtual != null) rotaAtual.atualizarGeometriaLocal();
            simulationService.atualizarPonto(selecionado);
            atualizarRotaViariaEmSegundoPlano();
            setStatus("Ponto atualizado: " + selecionado.getNome());
        } catch (IllegalArgumentException ex) {
            erro("Erro ao editar ponto", ex);
        }
    }

    private void atualizarRotaViariaEmSegundoPlano() {
        Rota rota = rotaAtual;
        if (rota == null || rota.getPercurso().size() < 2) return;
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override protected Void doInBackground() {
                routeService.atualizarTrajetoViario(rota);
                return null;
            }
            @Override protected void done() {
                if (rotaAtual == rota) atualizarTudo();
            }
        };
        worker.execute();
    }

    private void removerPontoSelecionado() {
        Ponto selecionado = pontoSelecionado();
        if (selecionado == null) {
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Remover \"" + selecionado.getNome() + "\"?", "Confirmar",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        pontoSelecionado = null;
        rotaAtual = null;
        ultimaOperacao = RouteOperation.NONE;
        ultimaOrigem = null;
        ultimoDestino = null;
        routeService.limpar();
        simulationService.removerPonto(selecionado);
        mapPanel.selecionarPonto(null);
        detailsPanel.limpar();
        atualizarTudo();
        setStatus("Ponto removido: " + selecionado.getNome());
    }

    private Ponto pontoSelecionado() {
        if (pontoSelecionado != null && grafo.getPontos().contains(pontoSelecionado)) {
            return pontoSelecionado;
        }
        Ponto escolhido = RouteDialogs.choosePoint(this, grafo, "Selecione um ponto");
        if (escolhido != null) {
            pontoSelecionado = escolhido;
            mapPanel.selecionarPonto(escolhido);
            mostrarDetalhes(escolhido);
        }
        return escolhido;
    }

    private void abrirJanelaPontos() {
        PointManagerDialog dialog = new PointManagerDialog(this, () -> new ArrayList<>(grafo.getPontos()),
                ponto -> { pontoSelecionado = ponto; mapPanel.selecionarPonto(ponto); mostrarDetalhes(ponto); mostrarMapa(); },
                new PointManagerDialog.Actions() {
                    @Override public void create() { novoPonto(); }
                    @Override public void edit() { editarPontoSelecionado(); }
                    @Override public void remove() { removerPontoSelecionado(); }
                });
        dialog.open();
    }

    private void aceitarResultadoRota(RouteUiController.Result result) {
        rotaAtual = result.route(); ultimaOperacao = result.operation(); ultimaOrigem = result.origin(); ultimoDestino = result.destination();
        mapPanel.destacarRota(rotaAtual, result.color()); bottomRoutePanel.mostrar(rotaAtual);
        atualizarTudo(); mostrarMapa(); setStatus(result.message());
    }

    private void limparRota() {
        mapPanel.pararAnimacao();
        routeService.limpar();
        rotaAtual = null;
        ultimaOperacao = RouteOperation.NONE;
        ultimaOrigem = null;
        ultimoDestino = null;
        pontoSelecionado = null;
        atualizarTudo();
        setStatus("Rota limpa");
    }

    private void substituirGrafo(Grafo novoGrafo) {
        List<Ponto> atuais = new ArrayList<>(grafo.getPontos());
        for (Ponto ponto : atuais) {
            grafo.removerPonto(ponto);
        }
        for (Ponto ponto : new ArrayList<>(novoGrafo.getPontos())) {
            grafo.adicionarPonto(ponto);
        }
        for (var aresta : novoGrafo.getArestas()) {
            grafo.adicionarAresta(aresta.getOrigem(), aresta.getDestino(), aresta.getDistancia());
        }
        rotaAtual = null;
        routeService.limpar();
        pontoSelecionado = null;
        atualizarTudo();
    }

    private String algoritmoAtivo() {
        return routeService.getAlgoritmoAtual() != null ? routeService.getAlgoritmoAtual() : "-";
    }

    private Color corDaOperacao(RouteOperation operacao) {
        switch (operacao) {
            case DIJKSTRA:
                return Color.BLUE;
            case PRIM:
                return new Color(30, 150, 60);
            case KRUSKAL:
                return new Color(210, 140, 40);
            case BFS:
                return Color.CYAN.darker();
            case DFS:
                return Color.PINK.darker();
            case GREEDY:
                return new Color(140, 60, 170);
            case TSP:
                return Color.RED;
            default:
                return Color.BLUE;
        }
    }

    private void setStatus(String texto) {
        statusLabel.setText(texto + " | Cruz das Almas - BA");
    }

    private void erro(String contexto, Exception ex) {
        JOptionPane.showMessageDialog(this, contexto + ":\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }

}
