package br.uel.bd.avaliacoes.dao;

import br.uel.bd.avaliacoes.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Acesso à tabela USUARIO via JDBC (JdbcTemplate).
 * Todo SQL do sistema fica nos DAOs — nunca nos controllers ou nas telas.
 */
@Repository
public class UsuarioDAO {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Usuario> MAPPER = (rs, i) -> new Usuario(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("perfil"));

    public UsuarioDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Usuario> listarTodos() {
        return jdbc.query("SELECT ID, NOME, EMAIL, PERFIL FROM USUARIO ORDER BY NOME", MAPPER);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return jdbc.query("SELECT ID, NOME, EMAIL, PERFIL FROM USUARIO WHERE EMAIL = ?", MAPPER, email)
                .stream().findFirst();
    }
}
