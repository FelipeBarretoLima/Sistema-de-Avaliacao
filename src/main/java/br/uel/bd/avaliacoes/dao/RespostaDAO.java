package br.uel.bd.avaliacoes.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.uel.bd.avaliacoes.model.Alternativa;
import br.uel.bd.avaliacoes.model.AvaliacaoDoAluno;
import br.uel.bd.avaliacoes.model.ItemAvaliacao;
import br.uel.bd.avaliacoes.model.Resposta;

/** Acesso à tabela RESPOSTA e às consultas que a tela de responder precisa. */
@Repository
public class RespostaDAO {

    private final JdbcTemplate jdbc;

    public RespostaDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Avaliações designadas ao aluno (LEFT JOIN: aparecem também as que ele ainda não respondeu). */
    public List<AvaliacaoDoAluno> listarAvaliacoesDoAluno(int usuarioId) {
        return jdbc.query("""
                SELECT A.ID, A.TITULO, A.DATA, A.HORA_INICIO, A.HORA_FIM, COUNT(R.ID) AS RESPONDIDAS
                FROM AVALIACAO_USUARIO AU
                JOIN AVALIACAO A ON A.ID = AU.AVALIACAO_ID
                LEFT JOIN RESPOSTA R ON R.AVALIACAO_ID = AU.AVALIACAO_ID AND R.USUARIO_ID = AU.USUARIO_ID
                WHERE AU.USUARIO_ID = ?
                GROUP BY A.ID, A.TITULO, A.DATA, A.HORA_INICIO, A.HORA_FIM
                ORDER BY A.DATA
                """,
                (rs, i) -> new AvaliacaoDoAluno(
                        rs.getInt("ID"),
                        rs.getString("TITULO"),
                        rs.getObject("DATA", LocalDate.class),
                        rs.getObject("HORA_INICIO", LocalTime.class),
                        rs.getObject("HORA_FIM", LocalTime.class),
                        rs.getInt("RESPONDIDAS") > 0),
                usuarioId);
    }

    public boolean estaDesignado(int avaliacaoId, int usuarioId) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM AVALIACAO_USUARIO WHERE AVALIACAO_ID = ? AND USUARIO_ID = ?",
                Integer.class, avaliacaoId, usuarioId);
        return total != null && total > 0;
    }

    public boolean jaRespondeu(int avaliacaoId, int usuarioId) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM RESPOSTA WHERE AVALIACAO_ID = ? AND USUARIO_ID = ?",
                Integer.class, avaliacaoId, usuarioId);
        return total != null && total > 0;
    }

    /** Questões da avaliação na ordem da prova, com o valor de cada uma e as alternativas. */
    public List<ItemAvaliacao> listarItens(int avaliacaoId) {
        return jdbc.query("""
                SELECT AQ.QUESTAO_ID, AQ.ORDEM, Q.ENUNCIADO, Q.TIPO, AQ.VALOR
                FROM AVALIACAO_QUESTAO AQ
                JOIN QUESTAO Q ON Q.ID = AQ.QUESTAO_ID
                WHERE AQ.AVALIACAO_ID = ?
                ORDER BY AQ.ORDEM
                """,
                (rs, i) -> new ItemAvaliacao(
                        rs.getInt("QUESTAO_ID"),
                        rs.getInt("ORDEM"),
                        rs.getString("ENUNCIADO"),
                        rs.getString("TIPO"),
                        rs.getBigDecimal("VALOR"),
                        listarAlternativas(rs.getInt("QUESTAO_ID"))),
                avaliacaoId);
    }

    private List<Alternativa> listarAlternativas(int questaoId) {
        return jdbc.query(
                "SELECT QUESTAO_ID, LETRA, TEXTO, CORRETA FROM ALTERNATIVA WHERE QUESTAO_ID = ? ORDER BY LETRA",
                (rs, i) -> new Alternativa(rs.getInt("QUESTAO_ID"), rs.getString("LETRA"),
                        rs.getString("TEXTO"), rs.getBoolean("CORRETA")),
                questaoId);
    }

    public void inserir(Resposta resposta) {
        jdbc.update("""
                INSERT INTO RESPOSTA (AVALIACAO_ID, QUESTAO_ID, USUARIO_ID, LETRA, TEXTO_RESPOSTA, NOTA)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                resposta.avaliacaoId(), resposta.questaoId(), resposta.usuarioId(),
                resposta.letra(), resposta.textoResposta(), resposta.nota());
    }
}