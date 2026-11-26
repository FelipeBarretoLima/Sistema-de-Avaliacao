package br.uel.bd.avaliacoes.model;

import java.math.BigDecimal;

/** Linha da tabela RESPOSTA. LETRA fica nula nas questões de texto; NOTA nula = ainda não corrigida. */
public record Resposta(Integer id, Integer avaliacaoId, Integer questaoId, Integer usuarioId,
                       String letra, String textoResposta, BigDecimal nota) {
}