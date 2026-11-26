package br.uel.bd.avaliacoes.model;

import java.util.List;

/** O que a tela envia quando o aluno termina a avaliação. */
public record EnvioRespostas(Integer avaliacaoId, Integer usuarioId, List<RespostaAluno> respostas) {
}