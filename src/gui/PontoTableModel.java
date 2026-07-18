package gui;

import model.*;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * TableModel para exibição dos pontos cadastrados.
 */
@SuppressWarnings("serial")
public class PontoTableModel extends AbstractTableModel {

    private final List<Ponto> pontos = new ArrayList<>();
    private final String[] colunas = {"Nome", "Tipo", "Alunos", "Latitude", "Longitude"};

    public void setPontos(Collection<Ponto> novosPontos) {
        pontos.clear();
        pontos.addAll(novosPontos);
        fireTableDataChanged();
    }

    public void adicionarPonto(Ponto ponto) {
        pontos.add(ponto);
        fireTableRowsInserted(pontos.size() - 1, pontos.size() - 1);
    }

    public void removerPonto(int indice) {
        if (indice >= 0 && indice < pontos.size()) {
            pontos.remove(indice);
            fireTableRowsDeleted(indice, indice);
        }
    }

    public void atualizarPonto(int indice) {
        if (indice >= 0 && indice < pontos.size()) {
            fireTableRowsUpdated(indice, indice);
        }
    }

    public Ponto getPonto(int indice) {
        return (indice >= 0 && indice < pontos.size()) ? pontos.get(indice) : null;
    }

    public List<Ponto> getPontos() {
        return new ArrayList<>(pontos);
    }

    @Override
    public int getRowCount() { return pontos.size(); }

    @Override
    public int getColumnCount() { return colunas.length; }

    @Override
    public String getColumnName(int col) { return colunas[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Ponto p = pontos.get(row);
        return switch (col) {
            case 0 -> p.getNome();
            case 1 -> p.getTipo().getRotulo();
            case 2 -> p.getQuantidadeAlunos();
            case 3 -> String.format("%.6f", p.getLatitude());
            case 4 -> String.format("%.6f", p.getLongitude());
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 2;
    }

    @Override
    public void setValueAt(Object valor, int row, int col) {
        if (col == 2 && valor != null) {
            try {
                int alunos = Integer.parseInt(valor.toString());
                pontos.get(row).setQuantidadeAlunos(alunos);
                fireTableCellUpdated(row, col);
            } catch (NumberFormatException ignored) {}
        }
    }
}
