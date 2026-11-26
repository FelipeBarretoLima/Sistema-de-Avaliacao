package br.uel.bd.avaliacoes.model;

import java.math.BigDecimal;

/** Linha da tabela AVALIACAO_QUESTAO: a questão escolhida, o valor dela na avaliação e a ordem. */
public record QuestaoNaAvaliacao(Integer questaoId, BigDecimal valor, Integer ordem) {
}