package br.com.drsolutions;

import br.com.drsolutions.certificado.GerarCertificado;
import br.com.drsolutions.configuracao.ConfiguracoesDiretorios;
import br.com.drsolutions.email.EnviarEmail;
import br.com.drsolutions.util.GerarAlunos;
import br.com.drsolutions.util.UtilizarArquivoCSV;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Gerar uma lista de alunos em um arquivo CSV (nome,cpf,email), depois ler esse arquivo CSV e gerar certificados
 * no formato PDF para cada aluno.
 * Enviar uma mensagem de e-mail para cada aluno, parabenizando ele pela conclusão do curso, com o certificado
 * anexado ao e-mail.
 * O envio de testes está sendo realizado através do MailHog (https://github.com/mailhog/MailHog).
 *
 * @author Diego Mendes Rodrigues
 * @version 1.0
 * @since 12/27/2023
 */
public class Main {
    private final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        /*
         * Logs
         */
        PropertyConfigurator.configure(ConfiguracoesDiretorios.CONFIGURACAOLOGS);
        logger.info("Iniciar o sistema");

        /*
         * Verificar se os diretórios existem
         */
        if(!Files.exists(Path.of(ConfiguracoesDiretorios.LOCALARQUIVOCSV.split("/")[0]))) {
            Files.createDirectory(Path.of(ConfiguracoesDiretorios.LOCALARQUIVOCSV.split("/")[0]));
            logger.warn("Diretório " + ConfiguracoesDiretorios.LOCALARQUIVOCSV.split("/")[0] + " criado");
        }
        if(!Files.exists(Path.of(ConfiguracoesDiretorios.LOCALCERTIFICADOS))) {
            Files.createDirectory(Path.of(ConfiguracoesDiretorios.LOCALCERTIFICADOS));
            logger.warn("Diretório " + ConfiguracoesDiretorios.LOCALCERTIFICADOS + " criado");
        }
        if(!Files.exists(Path.of(ConfiguracoesDiretorios.FUNDOCERTIFICADO.split("/")[0]))) {
            Files.createDirectory(Path.of(ConfiguracoesDiretorios.FUNDOCERTIFICADO.split("/")[0]));
            logger.warn("Diretório " + ConfiguracoesDiretorios.FUNDOCERTIFICADO.split("/")[0] + " criado");
        }
        if(!Files.exists(Path.of(ConfiguracoesDiretorios.LOCALLOGS))) {
            Files.createDirectory(Path.of(ConfiguracoesDiretorios.LOCALLOGS));
            logger.warn("Diretório " + ConfiguracoesDiretorios.LOCALLOGS + " criado");
        }
        if(!Files.exists(Path.of(ConfiguracoesDiretorios.CONFIGURACAOLOGS.split("/")[0]))) {
            Files.createDirectory(Path.of(ConfiguracoesDiretorios.CONFIGURACAOLOGS.split("/")[0]));
            logger.warn("Diretório " + ConfiguracoesDiretorios.CONFIGURACAOLOGS.split("/")[0] + " criado");
        }

        /*
         * Limpar os certificados gerados anteriormente
         */
        logger.info("Limpar os certificados anteriores");
        String caminhoDoProjeto = System.getProperty("user.dir");
        Runtime.getRuntime()
                .exec(String.format("cmd.exe /c del " + caminhoDoProjeto + "\\" + ConfiguracoesDiretorios.LOCALCERTIFICADOS
                        .replace("/", "\\") + "*.pdf"));

        /*
         * Gerar alunos para os certificados e salvar em arquivo CSV
         * */
        logger.info("Gerar os alunos fictícios e salvar em um arquivo CVS");
        GerarAlunos gerarAlunos = new GerarAlunos(10, ConfiguracoesDiretorios.LOCALARQUIVOCSV);


        /*
         * Ler os alunos que estão no arquivo CSV
         * */
        logger.info("Ler os alunos do arquivo CSV");
        ArrayList<String> alunos = UtilizarArquivoCSV.lerArquivoCSV(ConfiguracoesDiretorios.LOCALARQUIVOCSV);
        logger.info("Arquivo CSV lido");

        /*
         * Gerar os certificados dos alunos no formato PDF
         * */
        logger.info("Gerar os certificados dos alunos no formato PDF");
        GerarCertificado gerarCertificado = new GerarCertificado(ConfiguracoesDiretorios.LOCALCERTIFICADOS);
        for (String aluno : alunos) {
            String[] informacoesAlunos = aluno.split(",");
            gerarCertificado.gerar(informacoesAlunos[0], informacoesAlunos[1]);
        }

        /*
         * Enviar os e-mails utilizando o MailHog
         * */
        logger.info("Enviar os e-mails para os alunos");
        EnviarEmail enviarEmail = new EnviarEmail();
        enviarEmail.conectarSMTP();

        String corpoDoEmail;
        for (String aluno : alunos) {
            String[] informacoesAlunos = aluno.split(",");
            corpoDoEmail = "Prezado(a) " + informacoesAlunos[0] + ",<br/><br/>" +
                    "Parabéns por concluir o curso <b>Fundamentos da Linguagem Java</b> com a duração " +
                    "de 40 horas!<br/><br/>" +
                    "Seu certificado está anexado ao e-mail.<br/><br/>n" +
                    "Caso precise de mais alguma informação, estamos à disposição.<br/><br/>" +
                    "Atenciosamente,<br/>" +
                    "Equipe DR Treinamentos";

            String cpfArquivo = informacoesAlunos[1].replace(".", "")
                    .replace("*", "")
                    .replace("-", "");
            String arquivoCertificado = ConfiguracoesDiretorios.LOCALCERTIFICADOS + "Certificado-" + cpfArquivo + ".pdf";
            enviarEmail.enviarComAnexo(informacoesAlunos[2], "", "",
                    "Certificado de Conclusão", corpoDoEmail, arquivoCertificado);
        }

        logger.info("Encerrar o sistema");
    }
}
