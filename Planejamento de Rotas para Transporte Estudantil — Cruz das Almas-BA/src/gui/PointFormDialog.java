package gui;

import model.Ponto;
import model.TipoPonto;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/** Formulário visual reutilizável para cadastro e edição de pontos. */
final class PointFormDialog {
    record Data(String nome, String bairro, double latitude, double longitude, int alunos,
                int capacidade, int prioridade, String turno, TipoPonto tipo) { }

    private PointFormDialog() { }

    static Data show(Component owner, Ponto point) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UiTheme.SURFACE);
        form.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = UiTheme.scaledInsets(5, 5, 5, 5); c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        JTextField name = field(point != null ? point.getNome() : "");
        JTextField district = field(point != null ? point.getBairro() : "");
        JTextField latitude = field(point != null ? String.valueOf(point.getLatitude()) : "-12.6700");
        JTextField longitude = field(point != null ? String.valueOf(point.getLongitude()) : "-39.1000");
        JTextField students = field(point != null ? String.valueOf(point.getQuantidadeAlunos()) : "0");
        JTextField capacity = field(point != null ? String.valueOf(point.getCapacidade()) : "0");
        JTextField priority = field(point != null ? String.valueOf(point.getPrioridade()) : "1");
        JTextField shift = field(point != null ? point.getTurno() : "");
        JComboBox<TipoPonto> type = new JComboBox<>(TipoPonto.values());
        type.setSelectedItem(point != null ? point.getTipo() : TipoPonto.PONTO_EMBARQUE);
        type.setFont(UiTheme.FONT); type.setBackground(UiTheme.SURFACE); type.setForeground(UiTheme.TEXT); type.setPreferredSize(UiTheme.scaledSize(260, 36));
        JComponent[] fields={name,district,latitude,longitude,students,capacity,priority,shift,type};
        String[] labels={"Nome","Bairro","Latitude","Longitude","Alunos","Capacidade","Prioridade","Turno","Tipo"};
        for(int row=0;row<labels.length;row++) addRow(form,c,row,labels[row],fields[row]);
        int confirm=JOptionPane.showConfirmDialog(owner,form,point==null?"Novo ponto":"Editar ponto",
                JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(confirm!=JOptionPane.OK_OPTION)return null;
        try {
            return new Data(name.getText().trim(),district.getText().trim(),Double.parseDouble(latitude.getText().trim()),
                    Double.parseDouble(longitude.getText().trim()),Integer.parseInt(students.getText().trim()),
                    Integer.parseInt(capacity.getText().trim()),Integer.parseInt(priority.getText().trim()),
                    shift.getText().trim(),(TipoPonto)type.getSelectedItem());
        } catch(NumberFormatException ex) {
            JOptionPane.showMessageDialog(owner,"Valores numéricos inválidos.","Dados inválidos",JOptionPane.ERROR_MESSAGE); return null;
        }
    }

    private static JTextField field(String value) {
        JTextField field=new JTextField(value); field.setFont(UiTheme.FONT); field.setForeground(UiTheme.TEXT); field.setBackground(UiTheme.SURFACE);
        field.setPreferredSize(UiTheme.scaledSize(260,36)); field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER_STRONG),BorderFactory.createEmptyBorder(6,10,6,10))); return field;
    }
    private static void addRow(JPanel form,GridBagConstraints c,int row,String text,JComponent field) {
        c.gridx=0;c.gridy=row;c.weightx=0;JLabel label=new JLabel(text);label.setFont(UiTheme.FONT_BOLD.deriveFont(12f));label.setForeground(UiTheme.TEXT_MUTED);form.add(label,c);
        c.gridx=1;c.weightx=1;form.add(field,c);
    }
}
