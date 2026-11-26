-- Consultas dos relatórios (RF7)
-- Os valores fixos abaixo (datas, aluno, texto buscado) são exemplos:
-- no sistema eles vêm da tela, como parâmetros (?) no DAO.


-- =====================================================================
-- R3: Ranking das questões de múltipla escolha com mais erros
-- Recursos: LEFT JOIN, COUNT, SUM, GROUP BY, HAVING, RANK(),
--           ORDER BY, FETCH FIRST, filtros IN e LIKE
-- =====================================================================
SELECT RANK() OVER (ORDER BY T.PERCENTUAL_ACERTO) AS POSICAO,
       T.*
FROM (SELECT Q.ID,
             Q.ENUNCIADO,
             COUNT(R.ID)                                         AS RESPOSTAS,
             SUM(CASE WHEN AL.CORRETA THEN 0 ELSE 1 END)         AS ERROS,
             ROUND(100.0 * SUM(CASE WHEN AL.CORRETA THEN 1 ELSE 0 END) / COUNT(R.ID), 1) AS PERCENTUAL_ACERTO
      FROM QUESTAO Q
      LEFT JOIN RESPOSTA R     ON R.QUESTAO_ID = Q.ID
      LEFT JOIN ALTERNATIVA AL ON AL.QUESTAO_ID = R.QUESTAO_ID AND AL.LETRA = R.LETRA
      WHERE Q.TIPO IN ('MULTIPLA')
        AND UPPER(Q.ENUNCIADO) LIKE UPPER('%%')
      GROUP BY Q.ID, Q.ENUNCIADO
      HAVING COUNT(R.ID) > 0) T
ORDER BY POSICAO, T.ID
FETCH FIRST 10 ROWS ONLY;

-- R3 (segunda consulta): questões que nunca foram usadas em nenhuma avaliação
-- Recursos: LEFT JOIN (... IS NULL), ORDER BY
SELECT Q.ID, Q.ENUNCIADO, Q.TIPO
FROM QUESTAO Q
LEFT JOIN AVALIACAO_QUESTAO AQ ON AQ.QUESTAO_ID = Q.ID
WHERE AQ.QUESTAO_ID IS NULL
ORDER BY Q.ID;