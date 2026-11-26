package br.uel.bd.avaliacoes.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.uel.bd.avaliacoes.dao.RelatorioDAO;
import br.uel.bd.avaliacoes.model.QuestaoErrada;
import br.uel.bd.avaliacoes.model.QuestaoNuncaUsada;

/** Rotas dos relatórios: a tela chama o controller, que usa o DAO (todo o SQL fica no DAO). */
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioDAO relatorioDAO;

    public RelatorioController(RelatorioDAO relatorioDAO) {
        this.relatorioDAO = relatorioDAO;
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