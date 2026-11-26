package br.uel.bd.avaliacoes.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.uel.bd.avaliacoes.model.QuestaoErrada;
import br.uel.bd.avaliacoes.model.QuestaoNuncaUsada;

/** Consultas dos relatórios (RF7). As mesmas consultas estão comentadas em database/relatorios.sql. */
@Repository
public class RelatorioDAO {

    private final JdbcTemplate jdbc;

    public RelatorioDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
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