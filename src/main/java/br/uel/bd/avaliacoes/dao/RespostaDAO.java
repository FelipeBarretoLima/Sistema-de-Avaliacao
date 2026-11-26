package br.uel.bd.avaliacoes.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.uel.bd.avaliacoes.model.Alternativa;
import br.uel.bd.avaliacoes.model.AvaliacaoDoAluno;
import br.uel.bd.avaliacoes.model.CorrecaoPendente;
import br.uel.bd.avaliacoes.model.ItemAvaliacao;
import br.uel.bd.avaliacoes.model.NotaAluno;
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

    private static final RowMapper<NotaAluno> NOTA_MAPPER = (rs, i) -> new NotaAluno(
            rs.getInt("USUARIO_ID"),
            rs.getString("NOME"),
            rs.getBigDecimal("NOTA_TOTAL"),
            rs.getInt("SEM_CORRECAO"));

    /** Nota total de cada aluno na avaliação: SUM das notas (a nota total não é guardada em tabela). */
    public List<NotaAluno> listarNotasDaAvaliacao(int avaliacaoId) {
        return jdbc.query("""
                SELECT U.ID AS USUARIO_ID, U.NOME,
                       COALESCE(SUM(R.NOTA), 0) AS NOTA_TOTAL,
                       COUNT(R.ID) - COUNT(R.NOTA) AS SEM_CORRECAO
                FROM RESPOSTA R
                JOIN USUARIO U ON U.ID = R.USUARIO_ID
                WHERE R.AVALIACAO_ID = ?
                GROUP BY U.ID, U.NOME
                ORDER BY NOTA_TOTAL DESC, U.NOME
                """,
                NOTA_MAPPER, avaliacaoId);
    }

    public Optional<NotaAluno> notaDoAluno(int avaliacaoId, int usuarioId) {
        return jdbc.query("""
                SELECT U.ID AS USUARIO_ID, U.NOME,
                       COALESCE(SUM(R.NOTA), 0) AS NOTA_TOTAL,
                       COUNT(R.ID) - COUNT(R.NOTA) AS SEM_CORRECAO
                FROM RESPOSTA R
                JOIN USUARIO U ON U.ID = R.USUARIO_ID
                WHERE R.AVALIACAO_ID = ? AND R.USUARIO_ID = ?
                GROUP BY U.ID, U.NOME
                """,
                NOTA_MAPPER, avaliacaoId, usuarioId)
                .stream().findFirst();
    }

    /** Respostas de texto ainda sem nota, para o professor corrigir. */
    public List<CorrecaoPendente> listarCorrecoesPendentes() {
        return jdbc.query("""
                SELECT R.ID, A.TITULO, Q.ENUNCIADO, AQ.VALOR, U.NOME, R.TEXTO_RESPOSTA
                FROM RESPOSTA R
                JOIN AVALIACAO A ON A.ID = R.AVALIACAO_ID
                JOIN QUESTAO Q ON Q.ID = R.QUESTAO_ID
                JOIN AVALIACAO_QUESTAO AQ ON AQ.AVALIACAO_ID = R.AVALIACAO_ID AND AQ.QUESTAO_ID = R.QUESTAO_ID
                JOIN USUARIO U ON U.ID = R.USUARIO_ID
                WHERE Q.TIPO = 'TEXTO' AND R.NOTA IS NULL
                ORDER BY A.DATA, U.NOME
                """,
                (rs, i) -> new CorrecaoPendente(
                        rs.getInt("ID"),
                        rs.getString("TITULO"),
                        rs.getString("ENUNCIADO"),
                        rs.getBigDecimal("VALOR"),
                        rs.getString("NOME"),
                        rs.getString("TEXTO_RESPOSTA")));
    }

    /** Valor da questão (na avaliação) a que a resposta de texto se refere. */
    public Optional<BigDecimal> valorDaRespostaDeTexto(int respostaId) {
        return jdbc.query("""
                SELECT AQ.VALOR
                FROM RESPOSTA R
                JOIN AVALIACAO_QUESTAO AQ ON AQ.AVALIACAO_ID = R.AVALIACAO_ID AND AQ.QUESTAO_ID = R.QUESTAO_ID
                JOIN QUESTAO Q ON Q.ID = R.QUESTAO_ID
                WHERE R.ID = ? AND Q.TIPO = 'TEXTO'
                """,
                (rs, i) -> rs.getBigDecimal("VALOR"), respostaId)
                .stream().findFirst();
    }

    public void atualizarNota(int respostaId, BigDecimal nota) {
        jdbc.update("UPDATE RESPOSTA SET NOTA = ? WHERE ID = ?", nota, respostaId);
    }
}