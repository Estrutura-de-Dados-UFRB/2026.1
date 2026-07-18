package view;

import java.io.File;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// Frame que exibe o histórico de partidas extraído de XML.
public class Historico extends JFrame {

    private JTextArea areaTexto;

    public Historico() {
        super("Historico de Jogadas");

        areaTexto = new JTextArea(20, 45);
        areaTexto.setEditable(false);

        JScrollPane scroll = new JScrollPane(areaTexto);
        add(scroll);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        carregarHistorico();
        setVisible(true);
    }

    private void carregarHistorico() {
        File arquivo = new File("historico.xml");

        if (!arquivo.exists() || arquivo.length() == 0) {
            areaTexto.setText("Nenhuma jogada registrada ainda.");
            return;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(arquivo);

            doc.getDocumentElement().normalize();

            NodeList jogadas = doc.getElementsByTagName("jogada");
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < jogadas.getLength(); i++) {
                Element jogada = (Element) jogadas.item(i);

                String jogador = jogada.getAttribute("jogador");
                String pedra = jogada.getAttribute("pedra");
                String lado = jogada.getAttribute("lado");

                sb.append("Jogador ")
                  .append(jogador)
                  .append(" jogou a pedra ")
                  .append(pedra)
                  .append(" no lado ")
                  .append(lado)
                  .append("\n");
            }

            areaTexto.setText(
                    sb.length() == 0 ? "Nenhuma jogada registrada ainda." : sb.toString()
            );

        } catch (Exception e) {
            areaTexto.setText("Nenhuma jogada registrada ainda.");
        }
    }
}
