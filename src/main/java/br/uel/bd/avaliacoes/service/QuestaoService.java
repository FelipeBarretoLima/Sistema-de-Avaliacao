package br.uel.bd.avaliacoes.service;

import br.uel.bd.avaliacoes.dao.QuestaoDAO;
import br.uel.bd.avaliacoes.model.Alternativa;
import br.uel.bd.avaliacoes.model.Questao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Regras do cadastro de questões (RF1). */
@Service
public class QuestaoService {

    private final QuestaoDAO questaoDAO;

    public QuestaoService(QuestaoDAO questaoDAO) {
        this.questaoDAO = questaoDAO;
    }

    public List<Questao> listar() {
        return questaoDAO.listarTodas();
    }

    public Optional<Questao> buscar(int id) {
        return questaoDAO.buscarPorId(id);
    }

    /**
     * Grava a questão e as alternativas numa única transação:
     * se qualquer INSERT falhar, nada é gravado (nem a questão).
     */
    @Transactional
    public Questao salvar(Questao questao) {
        validar(questao);

        int id = questaoDAO.inserir(questao.enunciado().trim(), questao.tipo());

        for (Alternativa alternativa : alternativasDe(questao)) {
            questaoDAO.inserirAlternativa(new Alternativa(
                    id, alternativa.letra(), alternativa.texto().trim(), alternativa.correta()));
        }

        return questaoDAO.buscarPorId(id).orElseThrow();
    }

    private void validar(Questao questao) {
        if (questao.enunciado() == null || questao.enunciado().isBlank()) {
            throw new IllegalArgumentException("Informe o enunciado da questão.");
        }

        List<Alternativa> alternativas = alternativasDe(questao);

        if ("TEXTO".equals(questao.tipo())) {
            if (!alternativas.isEmpty()) {
                throw new IllegalArgumentException("Questão de texto não tem alternativas.");
            }
        } else if ("MULTIPLA".equals(questao.tipo())) {
            if (alternativas.size() < 2) {
                throw new IllegalArgumentException("Questão de múltipla escolha precisa de pelo menos 2 alternativas.");
            }
            for (Alternativa alternativa : alternativas) {
                if (alternativa.texto() == null || alternativa.texto().isBlank()) {
                    throw new IllegalArgumentException("Preencha o texto da alternativa " + alternativa.letra() + ".");
                }
            }
            long corretas = alternativas.stream().filter(Alternativa::correta).count();
            if (corretas != 1) {
                throw new IllegalArgumentException("Marque exatamente uma alternativa correta.");
            }
        } else {
            throw new IllegalArgumentException("Tipo de questão inválido: use MULTIPLA ou TEXTO.");
        }
    }

    private List<Alternativa> alternativasDe(Questao questao) {
        return questao.alternativas() == null ? List.of() : questao.alternativas();
    }
}
