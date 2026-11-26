package br.uel.bd.avaliacoes.service;

import br.uel.bd.avaliacoes.dao.AvaliacaoDAO;
import br.uel.bd.avaliacoes.model.Avaliacao;
import br.uel.bd.avaliacoes.model.QuestaoNaAvaliacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/** Regras do cadastro de avaliações (RF2, RF3 e RF4). */
@Service
public class AvaliacaoService {

    private final AvaliacaoDAO avaliacaoDAO;

    public AvaliacaoService(AvaliacaoDAO avaliacaoDAO) {
        this.avaliacaoDAO = avaliacaoDAO;
    }

    public List<Avaliacao> listar() {
        return avaliacaoDAO.listarTodas();
    }

    public Optional<Avaliacao> buscar(int id) {
        return avaliacaoDAO.buscarPorId(id);
    }

    /**
     * Grava a avaliação, as questões escolhidas e os alunos designados numa única transação:
     * mexe em AVALIACAO, AVALIACAO_QUESTAO e AVALIACAO_USUARIO; se qualquer INSERT falhar, nada é gravado.
     */
    @Transactional
    public Avaliacao salvar(Avaliacao avaliacao) {
        validar(avaliacao);

        int id = avaliacaoDAO.inserir(avaliacao);

        // A ordem das questões é a ordem em que vieram da tela (1, 2, 3...)
        List<QuestaoNaAvaliacao> questoes = avaliacao.questoes();
        for (int i = 0; i < questoes.size(); i++) {
            QuestaoNaAvaliacao questao = questoes.get(i);
            avaliacaoDAO.inserirQuestao(id, new QuestaoNaAvaliacao(questao.questaoId(), questao.valor(), i + 1));
        }

        for (Integer alunoId : avaliacao.alunos()) {
            avaliacaoDAO.designar(id, alunoId);
        }

        return avaliacaoDAO.buscarPorId(id).orElseThrow();
    }

    private void validar(Avaliacao avaliacao) {
        if (avaliacao.titulo() == null || avaliacao.titulo().isBlank()) {
            throw new IllegalArgumentException("Informe o título da avaliação.");
        }
        if (avaliacao.data() == null || avaliacao.horaInicio() == null || avaliacao.horaFim() == null) {
            throw new IllegalArgumentException("Informe a data e os horários de início e de fim.");
        }
        if (!avaliacao.horaFim().isAfter(avaliacao.horaInicio())) {
            throw new IllegalArgumentException("O horário de fim deve ser depois do horário de início.");
        }

        List<QuestaoNaAvaliacao> questoes = avaliacao.questoes();
        if (questoes == null || questoes.isEmpty()) {
            throw new IllegalArgumentException("Escolha pelo menos uma questão.");
        }
        HashSet<Integer> questoesVistas = new HashSet<>();
        for (QuestaoNaAvaliacao questao : questoes) {
            if (questao.valor() == null || questao.valor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Informe um valor maior que zero para a questão " + questao.questaoId() + ".");
            }
            if (!questoesVistas.add(questao.questaoId())) {
                throw new IllegalArgumentException("A questão " + questao.questaoId() + " foi escolhida mais de uma vez.");
            }
        }

        List<Integer> alunos = avaliacao.alunos();
        if (alunos == null || alunos.isEmpty()) {
            throw new IllegalArgumentException("Escolha pelo menos um aluno.");
        }
        if (new HashSet<>(alunos).size() != alunos.size()) {
            throw new IllegalArgumentException("Um aluno foi escolhido mais de uma vez.");
        }
    }
}