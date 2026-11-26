package br.uel.bd.avaliacoes.controller;

import br.uel.bd.avaliacoes.model.Avaliacao;
import br.uel.bd.avaliacoes.service.AvaliacaoService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Rotas do cadastro de avaliações: tela -> controller -> service -> DAO -> banco. */
@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    public List<Avaliacao> listar() {
        return avaliacaoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> buscar(@PathVariable int id) {
        return avaliacaoService.buscar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Avaliacao> cadastrar(@RequestBody Avaliacao avaliacao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoService.salvar(avaliacao));
    }

    /** Erros de validação do service voltam para a tela como 400 com a mensagem. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> erroDeValidacao(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
    }

    /** O banco recusou (ex.: questão ou aluno que não existe): a transação já foi desfeita. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> erroNoBanco(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().body(Map.of("erro", "O banco recusou os dados: confira as questões e os alunos escolhidos."));
    }
}