package br.uel.bd.avaliacoes.model;

import java.math.BigDecimal;

/** Resposta de texto que ainda não recebeu nota do professor. */
public record CorrecaoPendente(Integer respostaId, String avaliacao, String enunciado,
                               BigDecimal valor, String aluno, String textoResposta) {
}