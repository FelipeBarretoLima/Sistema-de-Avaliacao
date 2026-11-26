package br.uel.bd.avaliacoes.model;

import java.time.LocalDate;
import java.time.LocalTime;

/** Avaliação para a qual o aluno foi designado, e se ele já respondeu. */
public record AvaliacaoDoAluno(Integer id, String titulo, LocalDate data, LocalTime horaInicio,
                               LocalTime horaFim, boolean jaRespondeu) {
}