package gui;

import javax.swing.JOptionPane;
import java.awt.Component;

final class JOptionPaneHelper {
    private JOptionPaneHelper(){ }
    static void error(Component owner,String context,Exception ex){
        Throwable cause=ex.getCause()!=null?ex.getCause():ex;
        JOptionPane.showMessageDialog(owner,context+":\n"+cause.getMessage(),"Erro",JOptionPane.ERROR_MESSAGE);
    }
}
