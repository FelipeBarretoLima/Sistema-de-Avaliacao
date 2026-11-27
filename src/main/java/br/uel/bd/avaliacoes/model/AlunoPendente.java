package br.uel.bd.avaliacoes.model;

/** Linha do relatório R1: aluno designado que ainda não respondeu a avaliação. */
public record AlunoPendente(String avaliacao, String aluno) {
}