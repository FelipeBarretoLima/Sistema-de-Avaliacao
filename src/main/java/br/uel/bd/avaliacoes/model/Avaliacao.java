package br.uel.bd.avaliacoes.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Linha da tabela AVALIACAO, com as questões escolhidas (AVALIACAO_QUESTAO)
 * e os IDs dos alunos designados (AVALIACAO_USUARIO).
 */
public record Avaliacao(Integer id, String titulo, String descricao, LocalDate data,
                        LocalTime horaInicio, LocalTime horaFim,
                        List<QuestaoNaAvaliacao> questoes, List<Integer> alunos) {
}