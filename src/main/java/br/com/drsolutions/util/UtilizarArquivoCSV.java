package br.com.drsolutions.util;

import br.com.drsolutions.configuracao.ConfiguracoesDiretorios;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Escrever e ler arquivos CSV
 *
 * @author Diego Mendes Rodrigues
 */
public class UtilizarArquivoCSV {
    private final static Logger logger = Logger.getLogger(UtilizarArquivoCSV.class);

    /**
     * Escrever um arquivo CSV com os alunos gerados
     *
     * @param arquivo      String sendo o caminho completo com o nome do arquivo
     * @param linhasAlunos StringBuilder sendo a lista de alunos no formato nome,cpf
     */
    public static void escreverArquivoCSV(String arquivo, StringBuilder linhasAlunos) {
        PropertyConfigurator.configure(ConfiguracoesDiretorios.CONFIGURACAOLOGS);

        String[] strings = linhasAlunos.toString().split(System.lineSeparator());
        String[] listaDeAlunos = strings;

        try (FileWriter fw = new FileWriter(new File(arquivo), StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(fw)) {
            for (String linha : listaDeAlunos) {
                writer.append(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error("Falhar ao criar o arquivo CSV (" + ConfiguracoesDiretorios.LOCALARQUIVOCSV + "): " +
                    e.getMessage());
//            e.printStackTrace();
        }
        logger.info("Arquivo CSV criado: " + ConfiguracoesDiretorios.LOCALARQUIVOCSV);
    }

    /**
     * Ler um arquivo CSV com os alunos, gerar uma ArrayList<> e devolver essas informações
     *
     * @param arquivo String sendo o caminho completo com o nome do arquivo
     * @return ArrayList<String> com os alunos no formato nome,cpf
     */
    public static ArrayList<String> lerArquivoCSV(String arquivo) {
        PropertyConfigurator.configure(ConfiguracoesDiretorios.CONFIGURACAOLOGS);

        Path caminho = Paths.get(arquivo);
        ArrayList<String> linhas = new ArrayList<>();

        try (FileReader fr = new FileReader(arquivo, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(fr)) {

            String linha;
            while ((linha = reader.readLine()) != null) {
                linhas.add(linha);
            }
        } catch (IOException e) {
            logger.error("Falhar ao ler o arquivo CSV (" + ConfiguracoesDiretorios.LOCALARQUIVOCSV + "): " +
                    e.getMessage());
//            e.printStackTrace();
        }
        logger.info("Arquivo CSV lido: " + ConfiguracoesDiretorios.LOCALARQUIVOCSV);
        return linhas;
    }
}
