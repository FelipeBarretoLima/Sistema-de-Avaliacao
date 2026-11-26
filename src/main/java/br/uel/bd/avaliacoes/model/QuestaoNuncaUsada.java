package br.uel.bd.avaliacoes.model;

/** Linha do relatório R3: questão que nunca entrou em nenhuma avaliação. */
public record QuestaoNuncaUsada(Integer id, String enunciado, String tipo) {
}