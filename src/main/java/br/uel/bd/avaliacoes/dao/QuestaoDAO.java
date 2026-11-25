package br.uel.bd.avaliacoes.dao;

import br.uel.bd.avaliacoes.model.Alternativa;
import br.uel.bd.avaliacoes.model.Questao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Acesso às tabelas QUESTAO e ALTERNATIVA via JDBC (JdbcTemplate). */
@Repository
public class QuestaoDAO {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Alternativa> ALTERNATIVA_MAPPER = (rs, i) -> new Alternativa(
            rs.getInt("QUESTAO_ID"),
            rs.getString("LETRA"),
            rs.getString("TEXTO"),
            rs.getBoolean("CORRETA"));

    public QuestaoDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Insere a questão e devolve o ID gerado pelo SERIAL. */
    public int inserir(String enunciado, String tipo) {
        return jdbc.queryForObject(
                "INSERT INTO QUESTAO (ENUNCIADO, TIPO) VALUES (?, ?) RETURNING ID",
                Integer.class, enunciado, tipo);
    }

    public void inserirAlternativa(Alternativa alternativa) {
        jdbc.update(
                "INSERT INTO ALTERNATIVA (QUESTAO_ID, LETRA, TEXTO, CORRETA) VALUES (?, ?, ?, ?)",
                alternativa.questaoId(), alternativa.letra(), alternativa.texto(), alternativa.correta());
    }

    public List<Questao> listarTodas() {
        return jdbc.query("SELECT ID, ENUNCIADO, TIPO FROM QUESTAO ORDER BY ID",
                (rs, i) -> montarQuestao(rs.getInt("ID"), rs.getString("ENUNCIADO"), rs.getString("TIPO")));
    }

    public Optional<Questao> buscarPorId(int id) {
        return jdbc.query("SELECT ID, ENUNCIADO, TIPO FROM QUESTAO WHERE ID = ?",
                        (rs, i) -> montarQuestao(rs.getInt("ID"), rs.getString("ENUNCIADO"), rs.getString("TIPO")),
                        id)
                .stream().findFirst();
    }

    public List<Alternativa> listarAlternativas(int questaoId) {
        return jdbc.query(
                "SELECT QUESTAO_ID, LETRA, TEXTO, CORRETA FROM ALTERNATIVA WHERE QUESTAO_ID = ? ORDER BY LETRA",
                ALTERNATIVA_MAPPER, questaoId);
    }

    private Questao montarQuestao(int id, String enunciado, String tipo) {
        return new Questao(id, enunciado, tipo, listarAlternativas(id));
    }
}
