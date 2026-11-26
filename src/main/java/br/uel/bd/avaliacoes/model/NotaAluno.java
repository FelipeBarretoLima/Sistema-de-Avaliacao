package br.uel.bd.avaliacoes.model;

import java.math.BigDecimal;

/** Nota total de um aluno numa avaliação (SUM das notas) e quantas questões de texto faltam corrigir. */
public record NotaAluno(Integer usuarioId, String nome, BigDecimal notaTotal, int questoesSemCorrecao) {
}