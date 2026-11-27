package br.uel.bd.avaliacoes.controller;

import br.uel.bd.avaliacoes.dao.RelatorioDAO;
import br.uel.bd.avaliacoes.model.AlunoPendente;
import br.uel.bd.avaliacoes.model.EvolucaoAluno;
import br.uel.bd.avaliacoes.model.MediaAvaliacao;
import br.uel.bd.avaliacoes.model.QuestaoErrada;
import br.uel.bd.avaliacoes.model.QuestaoNuncaUsada;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** Rotas dos relatórios: a tela chama o controller, que usa o DAO (todo o SQL fica no DAO). */
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioDAO relatorioDAO;

    public RelatorioController(RelatorioDAO relatorioDAO) {
        this.relatorioDAO = relatorioDAO;
    }

    @GetMapping("/media-por-avaliacao")
    public List<MediaAvaliacao> mediaPorAvaliacao(@RequestParam(defaultValue = "2000-01-01") LocalDate inicio,
                                                  @RequestParam(defaultValue = "2100-12-31") LocalDate fim) {
        return relatorioDAO.mediaPorAvaliacao(inicio, fim);
    }

    @GetMapping("/alunos-pendentes")
    public List<AlunoPendente> alunosPendentes(@RequestParam(defaultValue = "2000-01-01") LocalDate inicio,
                                               @RequestParam(defaultValue = "2100-12-31") LocalDate fim) {
        return relatorioDAO.alunosPendentes(inicio, fim);
    }

    @GetMapping("/evolucao-do-aluno")
    public List<EvolucaoAluno> evolucaoDoAluno(@RequestParam int usuarioId) {
        return relatorioDAO.evolucaoDoAluno(usuarioId);
    }

    @GetMapping("/questoes-mais-erradas")
    public List<QuestaoErrada> questoesMaisErradas(@RequestParam(defaultValue = "") String busca) {
        return relatorioDAO.questoesMaisErradas(busca);
    }

    @GetMapping("/questoes-nunca-usadas")
    public List<QuestaoNuncaUsada> questoesNuncaUsadas() {
        return relatorioDAO.questoesNuncaUsadas();
    }
}