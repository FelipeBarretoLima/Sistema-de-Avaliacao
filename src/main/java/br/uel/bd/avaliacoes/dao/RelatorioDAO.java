package br.uel.bd.avaliacoes.dao;

import br.uel.bd.avaliacoes.model.AlunoPendente;
import br.uel.bd.avaliacoes.model.EvolucaoAluno;
import br.uel.bd.avaliacoes.model.MediaAvaliacao;
import br.uel.bd.avaliacoes.model.QuestaoErrada;
import br.uel.bd.avaliacoes.model.QuestaoNuncaUsada;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/** Consultas dos relatórios (RF7). As mesmas consultas estão comentadas em database/relatorios.sql. */
@Repository
public class RelatorioDAO {

    private final JdbcTemplate jdbc;

    public RelatorioDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** R1: média de cada avaliação do período e quantos alunos ainda não responderam. */
    public List<MediaAvaliacao> mediaPorAvaliacao(LocalDate inicio, LocalDate fim) {
        return jdbc.query("""
                SELECT A.ID,
                       A.TITULO,
                       A.DATA,
                       COUNT(AU.USUARIO_ID)                       AS DESIGNADOS,
                       COUNT(T.USUARIO_ID)                        AS RESPONDERAM,
                       COUNT(AU.USUARIO_ID) - COUNT(T.USUARIO_ID) AS PENDENTES,
                       ROUND(AVG(T.NOTA_TOTAL), 2)                AS MEDIA
                FROM AVALIACAO A
                JOIN AVALIACAO_USUARIO AU ON AU.AVALIACAO_ID = A.ID
                LEFT JOIN (SELECT AVALIACAO_ID, USUARIO_ID, SUM(NOTA) AS NOTA_TOTAL
                           FROM RESPOSTA
                           GROUP BY AVALIACAO_ID, USUARIO_ID) T
                       ON T.AVALIACAO_ID = AU.AVALIACAO_ID AND T.USUARIO_ID = AU.USUARIO_ID
                WHERE A.DATA BETWEEN ? AND ?
                GROUP BY A.ID, A.TITULO, A.DATA
                HAVING COUNT(AU.USUARIO_ID) > 0
                ORDER BY A.DATA
                """,
                (rs, i) -> new MediaAvaliacao(
                        rs.getInt("ID"),
                        rs.getString("TITULO"),
                        rs.getObject("DATA", LocalDate.class),
                        rs.getInt("DESIGNADOS"),
                        rs.getInt("RESPONDERAM"),
                        rs.getInt("PENDENTES"),
                        rs.getBigDecimal("MEDIA")),
                inicio, fim);
    }

    /** R1: quem ainda não respondeu cada avaliação do período. */
    public List<AlunoPendente> alunosPendentes(LocalDate inicio, LocalDate fim) {
        return jdbc.query("""
                SELECT A.TITULO, U.NOME
                FROM AVALIACAO_USUARIO AU
                JOIN AVALIACAO A ON A.ID = AU.AVALIACAO_ID
                JOIN USUARIO U ON U.ID = AU.USUARIO_ID
                LEFT JOIN RESPOSTA R ON R.AVALIACAO_ID = AU.AVALIACAO_ID AND R.USUARIO_ID = AU.USUARIO_ID
                WHERE R.ID IS NULL AND A.DATA BETWEEN ? AND ?
                GROUP BY A.ID, A.TITULO, A.DATA, U.NOME
                ORDER BY A.DATA, U.NOME
                """,
                (rs, i) -> new AlunoPendente(rs.getString("TITULO"), rs.getString("NOME")),
                inicio, fim);
    }

    /** R2: nota do aluno em cada avaliação comparada com a média dos demais alunos (subconsulta). */
    public List<EvolucaoAluno> evolucaoDoAluno(int usuarioId) {
        return jdbc.query("""
                SELECT A.ID,
                       A.TITULO,
                       A.DATA,
                       SUM(R.NOTA) AS NOTA_ALUNO,
                       (SELECT ROUND(AVG(X.TOTAL), 2)
                        FROM (SELECT R2.USUARIO_ID, SUM(R2.NOTA) AS TOTAL
                              FROM RESPOSTA R2
                              WHERE R2.AVALIACAO_ID = A.ID AND R2.USUARIO_ID <> ?
                              GROUP BY R2.USUARIO_ID) X) AS MEDIA_DEMAIS
                FROM RESPOSTA R
                JOIN AVALIACAO A ON A.ID = R.AVALIACAO_ID
                WHERE R.USUARIO_ID = ?
                GROUP BY A.ID, A.TITULO, A.DATA
                ORDER BY A.DATA
                """,
                (rs, i) -> new EvolucaoAluno(
                        rs.getInt("ID"),
                        rs.getString("TITULO"),
                        rs.getObject("DATA", LocalDate.class),
                        rs.getBigDecimal("NOTA_ALUNO"),
                        rs.getBigDecimal("MEDIA_DEMAIS")),
                usuarioId, usuarioId);
    }

    /** R3: ranking das questões de múltipla escolha com mais erros (RANK, FETCH FIRST, IN, LIKE). */
    public List<QuestaoErrada> questoesMaisErradas(String busca) {
        return jdbc.query("""
                SELECT RANK() OVER (ORDER BY T.PERCENTUAL_ACERTO) AS POSICAO,
                       T.*
                FROM (SELECT Q.ID,
                             Q.ENUNCIADO,
                             COUNT(R.ID)                                 AS RESPOSTAS,
                             SUM(CASE WHEN AL.CORRETA THEN 0 ELSE 1 END) AS ERROS,
                             ROUND(100.0 * SUM(CASE WHEN AL.CORRETA THEN 1 ELSE 0 END) / COUNT(R.ID), 1) AS PERCENTUAL_ACERTO
                      FROM QUESTAO Q
                      LEFT JOIN RESPOSTA R     ON R.QUESTAO_ID = Q.ID
                      LEFT JOIN ALTERNATIVA AL ON AL.QUESTAO_ID = R.QUESTAO_ID AND AL.LETRA = R.LETRA
                      WHERE Q.TIPO IN ('MULTIPLA')
                        AND UPPER(Q.ENUNCIADO) LIKE UPPER(?)
                      GROUP BY Q.ID, Q.ENUNCIADO
                      HAVING COUNT(R.ID) > 0) T
                ORDER BY POSICAO, T.ID
                FETCH FIRST 10 ROWS ONLY
                """,
                (rs, i) -> new QuestaoErrada(
                        rs.getInt("POSICAO"),
                        rs.getInt("ID"),
                        rs.getString("ENUNCIADO"),
                        rs.getInt("RESPOSTAS"),
                        rs.getInt("ERROS"),
                        rs.getBigDecimal("PERCENTUAL_ACERTO")),
                "%" + (busca == null ? "" : busca) + "%");
    }

    /** R3: questões que nunca foram usadas em nenhuma avaliação (LEFT JOIN ... IS NULL). */
    public List<QuestaoNuncaUsada> questoesNuncaUsadas() {
        return jdbc.query("""
                SELECT Q.ID, Q.ENUNCIADO, Q.TIPO
                FROM QUESTAO Q
                LEFT JOIN AVALIACAO_QUESTAO AQ ON AQ.QUESTAO_ID = Q.ID
                WHERE AQ.QUESTAO_ID IS NULL
                ORDER BY Q.ID
                """,
                (rs, i) -> new QuestaoNuncaUsada(rs.getInt("ID"), rs.getString("ENUNCIADO"), rs.getString("TIPO")));
    }
}