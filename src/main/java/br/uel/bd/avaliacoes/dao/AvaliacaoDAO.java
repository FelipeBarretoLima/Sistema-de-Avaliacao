package br.uel.bd.avaliacoes.dao;

import br.uel.bd.avaliacoes.model.Avaliacao;
import br.uel.bd.avaliacoes.model.QuestaoNaAvaliacao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/** Acesso às tabelas AVALIACAO, AVALIACAO_QUESTAO e AVALIACAO_USUARIO via JDBC (JdbcTemplate). */
@Repository
public class AvaliacaoDAO {

    private final JdbcTemplate jdbc;

    public AvaliacaoDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Insere a avaliação e devolve o ID gerado pelo SERIAL. */
    public int inserir(Avaliacao avaliacao) {
        return jdbc.queryForObject("""
                INSERT INTO AVALIACAO (TITULO, DESCRICAO, DATA, HORA_INICIO, HORA_FIM)
                VALUES (?, ?, ?, ?, ?)
                RETURNING ID
                """,
                Integer.class,
                avaliacao.titulo(), avaliacao.descricao(), avaliacao.data(),
                avaliacao.horaInicio(), avaliacao.horaFim());
    }

    /** RF3: coloca uma questão já cadastrada na avaliação, com o valor e a ordem. */
    public void inserirQuestao(int avaliacaoId, QuestaoNaAvaliacao questao) {
        jdbc.update(
                "INSERT INTO AVALIACAO_QUESTAO (AVALIACAO_ID, QUESTAO_ID, VALOR, ORDEM) VALUES (?, ?, ?, ?)",
                avaliacaoId, questao.questaoId(), questao.valor(), questao.ordem());
    }

    /** RF4: indica que o aluno deve fazer a avaliação. */
    public void designar(int avaliacaoId, int usuarioId) {
        jdbc.update(
                "INSERT INTO AVALIACAO_USUARIO (AVALIACAO_ID, USUARIO_ID) VALUES (?, ?)",
                avaliacaoId, usuarioId);
    }

    public List<Avaliacao> listarTodas() {
        return jdbc.query("""
                SELECT ID, TITULO, DESCRICAO, DATA, HORA_INICIO, HORA_FIM
                FROM AVALIACAO
                ORDER BY DATA DESC, ID DESC
                """,
                (rs, i) -> montarAvaliacao(rs));
    }

    public Optional<Avaliacao> buscarPorId(int id) {
        return jdbc.query("""
                SELECT ID, TITULO, DESCRICAO, DATA, HORA_INICIO, HORA_FIM
                FROM AVALIACAO
                WHERE ID = ?
                """,
                (rs, i) -> montarAvaliacao(rs), id)
                .stream().findFirst();
    }

    public List<QuestaoNaAvaliacao> listarQuestoes(int avaliacaoId) {
        return jdbc.query(
                "SELECT QUESTAO_ID, VALOR, ORDEM FROM AVALIACAO_QUESTAO WHERE AVALIACAO_ID = ? ORDER BY ORDEM",
                (rs, i) -> new QuestaoNaAvaliacao(rs.getInt("QUESTAO_ID"), rs.getBigDecimal("VALOR"), rs.getInt("ORDEM")),
                avaliacaoId);
    }

    public List<Integer> listarAlunos(int avaliacaoId) {
        return jdbc.query(
                "SELECT USUARIO_ID FROM AVALIACAO_USUARIO WHERE AVALIACAO_ID = ? ORDER BY USUARIO_ID",
                (rs, i) -> rs.getInt("USUARIO_ID"),
                avaliacaoId);
    }

    private Avaliacao montarAvaliacao(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID");
        return new Avaliacao(
                id,
                rs.getString("TITULO"),
                rs.getString("DESCRICAO"),
                rs.getObject("DATA", LocalDate.class),
                rs.getObject("HORA_INICIO", LocalTime.class),
                rs.getObject("HORA_FIM", LocalTime.class),
                listarQuestoes(id),
                listarAlunos(id));
    }
}