package br.uel.bd.avaliacoes.service;

import br.uel.bd.avaliacoes.dao.UsuarioDAO;
import br.uel.bd.avaliacoes.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Regras de negócio. Gravações que mexem em mais de uma tabela
 * devem ficar em métodos anotados com @Transactional nesta camada.
 */
@Service
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public List<Usuario> listar() {
        return usuarioDAO.listarTodos();
    }

    /** Login simples: identifica o usuário pelo e-mail. */
    public Optional<Usuario> login(String email) {
        return usuarioDAO.buscarPorEmail(email);
    }
}
