package br.uel.bd.avaliacoes.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Linha do relatório R1: média da avaliação e quantos alunos ainda não responderam. */
public record MediaAvaliacao(Integer id, String titulo, LocalDate data, int designados,
                             int responderam, int pendentes, BigDecimal media) {
}