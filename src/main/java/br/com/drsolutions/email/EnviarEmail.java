package br.com.drsolutions.email;

import br.com.drsolutions.configuracao.ConfiguracoesDiretorios;
import br.com.drsolutions.configuracao.ConfiguracoesEmail;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * Envio de e-mails com autenticação TTLS/SSL, utilizando a API Java Mail
 * As mensagens são enviadas no formato HTML com a codificação UTF-8, para aceitar os caracteres latino americanos
 *
 * @author Diego Mendes Rodrigues
 */
public class EnviarEmail {
    private final static Logger logger = Logger.getLogger(EnviarEmail.class);
    private Session secao;

    private String mensagemDeErro;

    public EnviarEmail() {
        PropertyConfigurator.configure(ConfiguracoesDiretorios.CONFIGURACAOLOGS);
    }

    /**
     * Realizar a conexão e a autenticação com o servidor SMTP
     */
    public void conectarSMTP() {
        logger.info("Realizar autenticação com o servidor SMTP");

        Properties props = new Properties();
        props.put("mail.smtp.host", ConfiguracoesEmail.SMTP);
        props.put("mail.smtp.port", ConfiguracoesEmail.PORTA);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ConfiguracoesEmail.USUARIO, ConfiguracoesEmail.TOKEN);
            }
        };

        secao = Session.getInstance(props, auth);
    }

    /**
     * Enviar um e-mail no formato HTML com um anexo
     *
     * @param emailDestinatario String sendo o destinatário do e-mail
     * @param emailCC           String sendo quem receberá uma cópia do e-mail
     * @param emailCCO          String sendo quem receberá uma cópia oculta do e-mail
     * @param assunto           String sendo o assunto do e-mail
     * @param corpo             String sendo a mensagem no formato HTML
     * @param caminhoDoAnexo    String sendo o caminho do anexo no sistema operacional
     * @return boolean true caso o e-mail seja enviado, false caso contrário
     */
    public boolean enviarComAnexo(String emailDestinatario, String emailCC, String emailCCO, String assunto,
                                  String corpo, String caminhoDoAnexo) {
        try {
            logger.info("Enviar e-mail para o aluno");

            /* Montar o cabeçalho da mensagem */
            MimeMessage mensagem = new MimeMessage(secao);
            mensagem.addHeader("Content-type", "text/HTML; charset=UTF-8");
            mensagem.addHeader("format", "flowed");
            mensagem.addHeader("Content-Transfer-Encoding", "8bit");

            /* Definir o e-mail de origem */
            mensagem.setFrom(new InternetAddress(ConfiguracoesEmail.EMAIL, ConfiguracoesEmail.NOME));
            mensagem.setReplyTo(InternetAddress.parse(ConfiguracoesEmail.EMAIL, false));

            /* Incluir o assunto na mensagem */
            mensagem.setSubject(assunto, "UTF-8");
            mensagem.setSentDate(new Date());

            /* Definir o destinatário */
            mensagem.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestinatario, false));

            if (!emailCC.isEmpty())
                mensagem.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailCC, false));

            if (!emailCCO.isEmpty())
                mensagem.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(emailCCO, false));

            /* Mensagem com várias partes para enviar o anexo */
            Multipart multipart = new MimeMultipart();

            /* Corpo da mensagem em HTML */
            MimeBodyPart mensagemHTML = new MimeBodyPart();
            mensagemHTML.setContent(corpo, "text/html; charset=UTF-8");
            multipart.addBodyPart(mensagemHTML);

            /* Arquivo anexado */
            if (!caminhoDoAnexo.isEmpty()) {
                File file = new File(caminhoDoAnexo);

                /* Verificar se o arquivo do anexo existe e pode ser lido */
                if (!file.exists() || !file.isFile()) {
                    this.mensagemDeErro = "O arquivo selecionado para ser anexado não existe, ou não pode " +
                            "ser acessado.";
                    logger.error("O arquivo selecionado para ser anexado não existe, ou não pode ser acessado.");
                    return false;
                }

                /* Verificar se o tamanho em MB do anexo é maior do que o permitido */
                if (((double) file.length() / (1024 * 1024)) > ConfiguracoesEmail.TAMANHOMAXIMOANEXOMB) {
                    this.mensagemDeErro = "O arquivo selecionado para ser anexado possui o tamanho superior a " +
                            ConfiguracoesEmail.TAMANHOMAXIMOANEXOMB + " MB, não podendo ser anexado.";
                    logger.error("O arquivo selecionado para ser anexado possui o tamanho superior a " +
                            ConfiguracoesEmail.TAMANHOMAXIMOANEXOMB + " MB, não podendo ser anexado.");
                    return false;
                }

                MimeBodyPart anexo = new MimeBodyPart();
                anexo.setDataHandler(new DataHandler(new FileDataSource(file)));
                anexo.setFileName(file.getName());
                multipart.addBodyPart(anexo);
            }

            /* Incluir o corpo e o anexo na mensagem */
            mensagem.setContent(multipart);

            /* Enviar a mensagem */
            Transport.send(mensagem);

            logger.info("E-mail enviado: " + emailDestinatario);
        } catch (AuthenticationFailedException e) {
            logger.error("Falhar ao enviar o e-mail - Autenticação");
            logger.error("Falhar ao enviar o e-mail: " + e.getMessage());
//            e.printStackTrace();
            this.mensagemDeErro = "Os dados de autenticação no servidor SMTP são inválidos.\n" +
                    "Ajuste o USUARIO e o TOKEN na classe 'Configuracoes'.";
            return false;
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Falhar ao enviar o e-mail: " + e.getMessage());
//            e.printStackTrace();
            this.mensagemDeErro = e.getMessage();
            return false;
        }
        return true;
    }

    public String getMensagemDeErro() {
        return mensagemDeErro;
    }
}
