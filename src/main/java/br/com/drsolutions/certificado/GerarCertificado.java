package br.com.drsolutions.certificado;

import br.com.drsolutions.configuracao.ConfiguracoesDiretorios;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Gerar um certificado para um aluno no formato PDF
 *
 * @author Diego Mendes Rodrigues
 */
public class GerarCertificado {
    private final static Logger logger = Logger.getLogger(GerarCertificado.class);
    private String localCertificado;

    /**
     * Gerar um certificado para um aluno no formato PDF
     *
     * @param localCertificado String sendo o local onde o certificado será armazenado
     */
    public GerarCertificado(String localCertificado) {
        this.localCertificado = localCertificado;
        PropertyConfigurator.configure(ConfiguracoesDiretorios.CONFIGURACAOLOGS);
    }

    /**
     * Gerar um certificado para um aluno no formato PDF
     *
     * @param nome String sendo o nome do aluno
     * @param cpf  String sendo o CPF do aluno
     * @throws IOException erro caso o arquivo em PDF do certificado não possa ser salvo no sistema
     */
    public void gerar(String nome, String cpf) throws IOException {
        PageSize pageSize = new PageSize(420.0F, 298.0F);
        String cpfArquivo = cpf.replace(".", "")
                .replace("*", "")
                .replace("-", "");
        String arquivo = localCertificado + "Certificado-" + cpfArquivo + ".pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(arquivo));
        Document doc = new Document(pdfDoc, pageSize);
        doc.setMargins(92, 20, 20, 20);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(ConfiguracoesDiretorios.FUNDOCERTIFICADO),
                pageSize, false);

        PdfFont fonteNome = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont fonteCPF = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        Style estiloNome = new Style()
                .setFont(fonteNome)
                .setFontSize(13)
                .setFontColor(ColorConstants.BLACK);

        Style estiloCPF = new Style()
                .setFont(fonteCPF)
                .setFontSize(8)
                .setFontColor(ColorConstants.BLACK);

        Paragraph textoNome = new Paragraph();
        textoNome.add(new Text(nome).addStyle(estiloNome));
        textoNome.setTextAlignment(TextAlignment.CENTER);
        doc.add(textoNome);

        Paragraph textoCpf = new Paragraph();
        textoCpf.add(new Text(cpf).addStyle(estiloCPF));
        textoCpf.setTextAlignment(TextAlignment.CENTER);
        doc.add(textoCpf);

        doc.close();

        logger.info("Certificado gerado: " + nome + " - " + cpf);
    }
}
