package br.uel.bd.avaliacoes.controller;

import br.uel.bd.avaliacoes.model.Questao;
import br.uel.bd.avaliacoes.service.QuestaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Rotas do cadastro de questões: tela -> controller -> service -> DAO -> banco. */
@RestController
@RequestMapping("/api/questoes")
public class QuestaoController {

    private final QuestaoService questaoService;

    public QuestaoController(QuestaoService questaoService) {
        this.questaoService = questaoService;
    }

    @GetMapping
    public List<Questao> listar() {
        return questaoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Questao> buscar(@PathVariable int id) {
        return questaoService.buscar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Questao> cadastrar(@RequestBody Questao questao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questaoService.salvar(questao));
    }

    /** Erros de validação do service voltam para a tela como 400 com a mensagem. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> erroDeValidacao(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
    }
}
