package br.uel.bd.avaliacoes.model;

/** Resposta de uma questão enviada pela tela: a letra (múltipla escolha) ou o texto (texto livre). */
public record RespostaAluno(Integer questaoId, String letra, String texto) {
}