package br.uel.bd.avaliacoes.model;

import java.math.BigDecimal;

/** Linha do relatório R3: posição no ranking das questões com mais erros. */
public record QuestaoErrada(int posicao, Integer id, String enunciado, int respostas, int erros,
                            BigDecimal percentualAcerto) {
}