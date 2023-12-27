package br.com.drsolutions.util;

import br.com.drsolutions.configuracao.ConfiguracoesDiretorios;
import com.github.javafaker.Faker;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Locale;

/**
 * Gerar alunos fictícios no formato nome,cpf,email e armazenar em um arquivo CSV
 *
 * @author Diego Mendes Rodrigues
 */
public class GerarAlunos {
    private final static Logger logger = Logger.getLogger(GerarAlunos.class);
    private Faker falsoAluno = new Faker(new Locale("pt-BR"));
    RemoverCaracteresEspeciais removerCaracteresEspeciais = new RemoverCaracteresEspeciais();

    /**
     * Gerar alunos no formato nome,cpf,email e armazenar em um arquivo CSV
     *
     * @param quantidadeAlunos int sendo a quantidade de alunos que será gerarda
     * @param arquivo          String sendo o caminho completo com o nome do arquivo CSV
     */
    public GerarAlunos(int quantidadeAlunos, String arquivo) {
        PropertyConfigurator.configure(ConfiguracoesDiretorios.CONFIGURACAOLOGS);

        if (quantidadeAlunos < 1) {
            quantidadeAlunos = 1;
        }

        StringBuilder alunos = new StringBuilder();

        for (int linha = 0; linha < quantidadeAlunos; linha++) {
            alunos.append(gerar());
            alunos.append(System.lineSeparator());
        }

        logger.info("Gerar o arquivo CSV: " + arquivo);
        UtilizarArquivoCSV.escreverArquivoCSV(arquivo, alunos);
    }

    /**
     * Gerar um aluno no formato nome,cpf,email
     *
     * @return StringBuilder sendo o aluno gerado
     */
    private StringBuilder gerar() {
        StringBuilder aluno = new StringBuilder();
        String nome = falsoAluno.name().fullName();
        String[] nomeCompleto = nome.split(" ");
        String email = nomeCompleto[nomeCompleto.length - 1].toLowerCase();
        email = removerCaracteresEspeciais.remover(email) + "@fakemail.io";

        aluno.append(nome)
                .append(",")
                .append(falsoAluno.bothify("###.***.###-##"))
                .append(",")
                .append(email);
        return aluno;
    }
}
