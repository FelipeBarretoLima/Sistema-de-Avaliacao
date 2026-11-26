package br.uel.bd.avaliacoes.model;

import java.math.BigDecimal;
import java.util.List;

/** Questão dentro de uma avaliação (agregação AVALIACAO_QUESTAO), com o valor e a ordem. */
public record ItemAvaliacao(Integer questaoId, Integer ordem, String enunciado, String tipo,
                            BigDecimal valor, List<Alternativa> alternativas) {
}