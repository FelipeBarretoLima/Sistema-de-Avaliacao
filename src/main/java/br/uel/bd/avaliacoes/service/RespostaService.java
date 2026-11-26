package br.uel.bd.avaliacoes.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.uel.bd.avaliacoes.dao.RespostaDAO;
import br.uel.bd.avaliacoes.model.Alternativa;
import br.uel.bd.avaliacoes.model.AvaliacaoDoAluno;
import br.uel.bd.avaliacoes.model.CorrecaoPendente;
import br.uel.bd.avaliacoes.model.EnvioRespostas;
import br.uel.bd.avaliacoes.model.ItemAvaliacao;
import br.uel.bd.avaliacoes.model.NotaAluno;
import br.uel.bd.avaliacoes.model.Resposta;
import br.uel.bd.avaliacoes.model.RespostaAluno;

/** Regras de responder a avaliação (RF5) e da nota por questão (RF6). */
@Service
public class RespostaService {

    private final RespostaDAO respostaDAO;

    public RespostaService(RespostaDAO respostaDAO) {
        this.respostaDAO = respostaDAO;
    }

    public List<AvaliacaoDoAluno> avaliacoesDoAluno(int usuarioId) {
        return respostaDAO.listarAvaliacoesDoAluno(usuarioId);
    }

    /** Questões da avaliação para o aluno responder, sem revelar qual alternativa é a correta. */
    public List<ItemAvaliacao> questoesParaResponder(int avaliacaoId, int usuarioId) {
        verificarSePodeResponder(avaliacaoId, usuarioId);
        return respostaDAO.listarItens(avaliacaoId).stream()
                .map(item -> new ItemAvaliacao(item.questaoId(), item.ordem(), item.enunciado(), item.tipo(),
                        item.valor(),
                        item.alternativas().stream()
                                .map(a -> new Alternativa(a.questaoId(), a.letra(), a.texto(), false))
                                .toList()))
                .toList();
    }

    /**
     * Grava todas as respostas do aluno numa única transação.
     * Múltipla escolha recebe nota na hora (acertou = valor da questão, errou = 0);
     * texto livre fica com nota nula até o professor corrigir.
     */
    @Transactional
    public NotaAluno enviar(EnvioRespostas envio) {
        verificarSePodeResponder(envio.avaliacaoId(), envio.usuarioId());

        Map<Integer, RespostaAluno> respostas = new HashMap<>();
        if (envio.respostas() != null) {
            for (RespostaAluno r : envio.respostas()) {
                respostas.put(r.questaoId(), r);
            }
        }

        for (ItemAvaliacao item : respostaDAO.listarItens(envio.avaliacaoId())) {
            RespostaAluno resposta = respostas.get(item.questaoId());

            if ("MULTIPLA".equals(item.tipo())) {
                if (resposta == null || resposta.letra() == null || resposta.letra().isBlank()) {
                    throw new IllegalArgumentException("Responda a questão " + item.ordem() + ".");
                }
                boolean acertou = item.alternativas().stream()
                        .anyMatch(a -> a.correta() && a.letra().equals(resposta.letra()));
                BigDecimal nota = acertou ? item.valor() : BigDecimal.ZERO;
                respostaDAO.inserir(new Resposta(null, envio.avaliacaoId(), item.questaoId(), envio.usuarioId(),
                        resposta.letra(), null, nota));
            } else {
                if (resposta == null || resposta.texto() == null || resposta.texto().isBlank()) {
                    throw new IllegalArgumentException("Responda a questão " + item.ordem() + ".");
                }
                respostaDAO.inserir(new Resposta(null, envio.avaliacaoId(), item.questaoId(), envio.usuarioId(),
                        null, resposta.texto().trim(), null));
            }
        }

        return respostaDAO.notaDoAluno(envio.avaliacaoId(), envio.usuarioId()).orElseThrow();
    }

    public List<CorrecaoPendente> correcoesPendentes() {
        return respostaDAO.listarCorrecoesPendentes();
    }

    /** O professor dá a nota de uma resposta de texto: entre 0 e o valor da questão na avaliação. */
    @Transactional
    public void corrigir(int respostaId, BigDecimal nota) {
        BigDecimal valor = respostaDAO.valorDaRespostaDeTexto(respostaId)
                .orElseThrow(() -> new IllegalArgumentException("Resposta de texto não encontrada."));
        if (nota == null || nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(valor) > 0) {
            throw new IllegalArgumentException("A nota deve ficar entre 0 e " + valor + ".");
        }
        respostaDAO.atualizarNota(respostaId, nota);
    }

    public List<NotaAluno> notasDaAvaliacao(int avaliacaoId) {
        return respostaDAO.listarNotasDaAvaliacao(avaliacaoId);
    }

    private void verificarSePodeResponder(Integer avaliacaoId, Integer usuarioId) {
        if (avaliacaoId == null || usuarioId == null) {
            throw new IllegalArgumentException("Informe a avaliação e o aluno.");
        }
        if (!respostaDAO.estaDesignado(avaliacaoId, usuarioId)) {
            throw new IllegalArgumentException("Você não foi designado para esta avaliação.");
        }
        if (respostaDAO.jaRespondeu(avaliacaoId, usuarioId)) {
            throw new IllegalArgumentException("Você já respondeu esta avaliação.");
        }
    }
}