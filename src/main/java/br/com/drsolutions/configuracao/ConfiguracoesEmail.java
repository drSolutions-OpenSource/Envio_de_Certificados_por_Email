package br.com.drsolutions.configuracao;

/**
 * Configurações para o envio de e-mail, utilizando o MailHog
 * Mais informações: https://github.com/mailhog/MailHog
 *
 * @author Diego Mendes Rodrigues
 */
public class ConfiguracoesEmail {
    /* Nome do usuário, exemplo: Diego Rodrigues */
    public static final String NOME = "Diego Rodrigues";

    /* E-mail utilizado, exemplo: diego@gmail.com */
    public static final String EMAIL = "diego@fakemail.io";

    /* No MailHog não é realizada a autenticação */
    public static final String USUARIO = "diego@fakemail.io";

    /* No MailHog não é realizada a autenticação */
    public static final String TOKEN = "";

    /* SMTP do MailHog */
    public static final String SMTP = "localhost";

    /* Porta utilizada na conexão do MailHog, exemplo: 1025 */
    public static final String PORTA = "1025";

    /* Maior tamanho de MB dos arquivos que podem ser anexados ao e-mail, exemplo: 20 */
    public static final int TAMANHOMAXIMOANEXOMB = 20;
}
