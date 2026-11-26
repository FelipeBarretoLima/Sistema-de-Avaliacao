package br.uel.bd.avaliacoes.model;

import java.util.List;

/** Linha da tabela QUESTAO, com as suas alternativas (vazia nas questões de texto). */
public record Questao(Integer id, String enunciado, String tipo, List<Alternativa> alternativas) {
}
