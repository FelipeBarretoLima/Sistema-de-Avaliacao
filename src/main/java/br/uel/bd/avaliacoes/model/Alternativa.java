package br.uel.bd.avaliacoes.model;

/** Linha da tabela ALTERNATIVA (entidade fraca de QUESTAO). */
public record Alternativa(Integer questaoId, String letra, String texto, boolean correta) {
}
