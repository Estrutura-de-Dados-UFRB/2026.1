package util;

import java.io.*;

public class ArquivoJSON {

    public static void salvar(
            String json,
            String caminho
    ) {

        validarParametros(json, caminho);

        try (BufferedWriter writer =
                     new BufferedWriter(
                             new FileWriter(caminho)
                     )) {

            writer.write(json);

            System.out.println(
                    "Dados salvos com sucesso."
            );

        } catch (IOException e) {

            System.err.println(
                    "Erro ao salvar arquivo JSON."
            );

            e.printStackTrace();
        }
    }

    public static String carregar(
            String caminho
    ) {

        validarCaminho(caminho);

        StringBuilder conteudo =
                new StringBuilder();

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(caminho)
                     )) {

            String linha;

            while ((linha = reader.readLine())
                    != null) {

                conteudo.append(linha)
                        .append("\\n");
            }

        } catch (IOException e) {

            System.err.println(
                    "Erro ao carregar arquivo JSON."
            );

            e.printStackTrace();
        }

        return conteudo.toString();
    }

    public static boolean existeArquivo(
            String caminho
    ) {

        File arquivo =
                new File(caminho);

        return arquivo.exists();
    }

    public static boolean deletarArquivo(
            String caminho
    ) {

        File arquivo =
                new File(caminho);

        if (arquivo.exists()) {

            return arquivo.delete();
        }

        return false;
    }

    public static void criarDiretorio(
            String caminho
    ) {

        File diretorio =
                new File(caminho);

        if (!diretorio.exists()) {

            boolean criado =
                    diretorio.mkdirs();

            if (criado) {

                System.out.println(
                        "Diretório criado com sucesso."
                );
            }
        }
    }

    private static void validarParametros(
            String json,
            String caminho
    ) {

        if (json == null
                || json.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "JSON inválido."
            );
        }

        validarCaminho(caminho);
    }

    private static void validarCaminho(
            String caminho
    ) {

        if (caminho == null
                || caminho.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Caminho inválido."
            );
        }
    }
}