package br.com.drsolutions.util;

/**
 * Remover caracteres especiais, incluindo letras acentuadas
 *
 * @author Diego Mendes Rodrigues
 */
public class RemoverCaracteresEspeciais {
    public String remover(String entrada) {
        return entrada == null ? null : entrada.replaceAll("[^a-zA-Z0-9]+", "x");
    }
}
