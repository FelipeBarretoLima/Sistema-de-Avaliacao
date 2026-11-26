package br.uel.bd.avaliacoes.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.uel.bd.avaliacoes.model.AvaliacaoDoAluno;
import br.uel.bd.avaliacoes.model.CorrecaoPendente;
import br.uel.bd.avaliacoes.model.EnvioRespostas;
import br.uel.bd.avaliacoes.model.ItemAvaliacao;
import br.uel.bd.avaliacoes.model.NotaAluno;
import br.uel.bd.avaliacoes.service.RespostaService;

/** Rotas de responder a avaliação e de correção: tela -> controller -> service -> DAO -> banco. */
@RestController
@RequestMapping("/api/respostas")
public class RespostaController {

    private final RespostaService respostaService;

    public RespostaController(RespostaService respostaService) {
        this.respostaService = respostaService;
    }

    @GetMapping("/avaliacoes")
    public List<AvaliacaoDoAluno> avaliacoesDoAluno(@RequestParam int usuarioId) {
        return respostaService.avaliacoesDoAluno(usuarioId);
    }

    @GetMapping("/avaliacoes/{avaliacaoId}/questoes")
    public List<ItemAvaliacao> questoesParaResponder(@PathVariable int avaliacaoId, @RequestParam int usuarioId) {
        return respostaService.questoesParaResponder(avaliacaoId, usuarioId);
    }

    @PostMapping
    public ResponseEntity<NotaAluno> enviar(@RequestBody EnvioRespostas envio) {
        return ResponseEntity.status(HttpStatus.CREATED).body(respostaService.enviar(envio));
    }

    @GetMapping("/correcoes")
    public List<CorrecaoPendente> correcoesPendentes() {
        return respostaService.correcoesPendentes();
    }

    @PutMapping("/correcoes/{respostaId}")
    public ResponseEntity<Map<String, String>> corrigir(@PathVariable int respostaId, @RequestBody Map<String, BigDecimal> corpo) {
        respostaService.corrigir(respostaId, corpo.get("nota"));
        return ResponseEntity.ok(Map.of("mensagem", "Nota registrada."));
    }

    @GetMapping("/avaliacoes/{avaliacaoId}/notas")
    public List<NotaAluno> notasDaAvaliacao(@PathVariable int avaliacaoId) {
        return respostaService.notasDaAvaliacao(avaliacaoId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> erroDeValidacao(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> erroNoBanco(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().body(Map.of("erro", "O banco recusou a resposta: confira as alternativas marcadas."));
    }
}