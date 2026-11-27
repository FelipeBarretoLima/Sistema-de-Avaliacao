package br.uel.bd.avaliacoes.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Linha do relatório R2: nota do aluno numa avaliação e a média dos demais alunos. */
public record EvolucaoAluno(Integer id, String titulo, LocalDate data, BigDecimal notaAluno, BigDecimal mediaDemais) {
}