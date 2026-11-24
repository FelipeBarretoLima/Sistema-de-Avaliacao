package br.uel.bd.avaliacoes.controller;

import br.uel.bd.avaliacoes.model.Usuario;
import br.uel.bd.avaliacoes.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Tela -> controller -> service -> DAO -> banco. */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Map<String, String> corpo) {
        return usuarioService.login(corpo.get("email"))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
