package util;

import java.io.File;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

// Gerencia leitura e gravação de histórico de jogo em XML.
public class HistoricoXML {

    private static final String NOME_DO_ARQUIVO = "historico.xml";

    public static void salvarJogada(int jogador, Pedras2 pedra, String lado) {
        try {
            File arquivo = new File(NOME_DO_ARQUIVO);

            // Reaproveita o XML existente quando valido; caso contrario cria a raiz do historico.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = carregarOuCriarDocumento(arquivo, builder);

            // Cada jogada fica registrada como um elemento com jogador, pedra e lado escolhido.
            Element jogada = doc.createElement("jogada");
            jogada.setAttribute("jogador", String.valueOf(jogador));
            jogada.setAttribute("pedra", pedra.getLadoA() + "-" + pedra.getLadoB());
            jogada.setAttribute("lado", lado);

            doc.getDocumentElement().appendChild(jogada);

            // Grava o DOM completo no arquivo para manter o historico consultavel pela tela.
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(new DOMSource(doc), new StreamResult(arquivo));

        } catch (Exception e) {
            System.out.println("Erro ao registrar jogada no XML: " + e.getMessage());
        }
    }

    private static Document carregarOuCriarDocumento(File arquivo, DocumentBuilder builder) throws Exception {
        if (arquivo.exists() && arquivo.length() > 0) {
            try {
                Document doc = builder.parse(arquivo);
                doc.getDocumentElement().normalize();

                // So reutiliza arquivos com a raiz esperada para evitar misturar formatos invalidos.
                if ("historico".equals(doc.getDocumentElement().getNodeName())) {
                    return doc;
                }
            } catch (Exception e) {
                System.out.println("Historico invalido. Um novo arquivo sera criado.");
            }
        }

        Document doc = builder.newDocument();
        Element raiz = doc.createElement("historico");
        doc.appendChild(raiz);
        return doc;
    }

    public static void limpar() {
        File arquivo = new File(NOME_DO_ARQUIVO);

        if (arquivo.exists()) {
            arquivo.delete();
        }
    }
}
