package gui;

import model.Ponto;
import model.TipoPonto;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings({"serial", "this-escape"})
public class NavigationTreePanel extends gui.components.ModernCard {

    private final JTree tree;
    private Consumer<Ponto> onSelecionado;

    public NavigationTreePanel() {
        super("Pontos cadastrados");
        tree = new JTree(new DefaultMutableTreeNode("Carregando..."));
        tree.setBackground(UiTheme.SURFACE);
        tree.setRowHeight(24);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setBackgroundNonSelectionColor(UiTheme.SURFACE);
        renderer.setBackgroundSelectionColor(new java.awt.Color(218, 233, 248));
        renderer.setTextSelectionColor(UiTheme.PRIMARY_DARK);
        renderer.setTextNonSelectionColor(UiTheme.TEXT);
        tree.setCellRenderer(renderer);
        tree.addTreeSelectionListener(this::selecionar);
        JScrollPane scroll = new JScrollPane(tree);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        content().add(scroll, BorderLayout.CENTER);
    }

    public void setOnSelecionado(Consumer<Ponto> onSelecionado) {
        this.onSelecionado = onSelecionado;
    }

    public void atualizar(List<Ponto> pontos) {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Pontos");
        Map<TipoPonto, List<Ponto>> porTipo = pontos.stream()
                .collect(Collectors.groupingBy(Ponto::getTipo));

        adicionarGrupo(raiz, "Escolas", porTipo.get(TipoPonto.ESCOLA));
        adicionarGrupo(raiz, "Universidades", porTipo.get(TipoPonto.UNIVERSIDADE));
        adicionarGrupo(raiz, "Bairros", porTipo.get(TipoPonto.BAIRRO));
        adicionarGrupo(raiz, "Pontos de embarque", porTipo.get(TipoPonto.PONTO_EMBARQUE));
        adicionarGrupo(raiz, "Outros", porTipo.get(TipoPonto.OUTRO));

        tree.setModel(new DefaultTreeModel(raiz));
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    private void adicionarGrupo(DefaultMutableTreeNode raiz, String nome, List<Ponto> pontos) {
        DefaultMutableTreeNode grupo = new DefaultMutableTreeNode(nome);
        if (pontos != null) {
            List<Ponto> ordenados = new ArrayList<>(pontos);
            ordenados.sort(Comparator.comparing(Ponto::getNome));
            for (Ponto ponto : ordenados) {
                grupo.add(new DefaultMutableTreeNode(ponto));
            }
        }
        raiz.add(grupo);
    }

    private void selecionar(TreeSelectionEvent e) {
        TreePath path = e.getPath();
        Object node = path.getLastPathComponent();
        if (node instanceof DefaultMutableTreeNode) {
            Object user = ((DefaultMutableTreeNode) node).getUserObject();
            if (user instanceof Ponto && onSelecionado != null) {
                onSelecionado.accept((Ponto) user);
            }
        }
    }
}
