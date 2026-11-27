-- Consultas dos relatórios (RF7)
-- Os valores fixos abaixo (datas, aluno, texto buscado) são exemplos:
-- no sistema eles vêm da tela, como parâmetros (?) no DAO.


-- =====================================================================
-- R1: Média de cada avaliação e quantos alunos ainda não responderam
-- Recursos: JOIN, LEFT JOIN, subconsulta (no FROM), COUNT, AVG, SUM,
--           GROUP BY, HAVING, WHERE com datas (BETWEEN), ORDER BY
-- =====================================================================
SELECT A.ID,
       A.TITULO,
       A.DATA,
       COUNT(AU.USUARIO_ID)                      AS DESIGNADOS,
       COUNT(T.USUARIO_ID)                       AS RESPONDERAM,
       COUNT(AU.USUARIO_ID) - COUNT(T.USUARIO_ID) AS PENDENTES,
       ROUND(AVG(T.NOTA_TOTAL), 2)               AS MEDIA
FROM AVALIACAO A
JOIN AVALIACAO_USUARIO AU ON AU.AVALIACAO_ID = A.ID
LEFT JOIN (SELECT AVALIACAO_ID, USUARIO_ID, SUM(NOTA) AS NOTA_TOTAL
           FROM RESPOSTA
           GROUP BY AVALIACAO_ID, USUARIO_ID) T
       ON T.AVALIACAO_ID = AU.AVALIACAO_ID AND T.USUARIO_ID = AU.USUARIO_ID
WHERE A.DATA BETWEEN '2026-09-01' AND '2026-12-31'
GROUP BY A.ID, A.TITULO, A.DATA
HAVING COUNT(AU.USUARIO_ID) > 0
ORDER BY A.DATA;

-- R1 (segunda consulta): quem ainda não respondeu cada avaliação
-- Recursos: JOIN, LEFT JOIN (... IS NULL), ORDER BY
SELECT A.TITULO, U.NOME
FROM AVALIACAO_USUARIO AU
JOIN AVALIACAO A ON A.ID = AU.AVALIACAO_ID
JOIN USUARIO U ON U.ID = AU.USUARIO_ID
LEFT JOIN RESPOSTA R ON R.AVALIACAO_ID = AU.AVALIACAO_ID AND R.USUARIO_ID = AU.USUARIO_ID
WHERE R.ID IS NULL
GROUP BY A.ID, A.TITULO, A.DATA, U.NOME
ORDER BY A.DATA, U.NOME;


-- =====================================================================
-- R2: Evolução de um aluno comparada com a média dos demais
-- Recursos: JOIN, SUM, GROUP BY, subconsulta correlacionada (média dos
--           demais alunos na mesma avaliação), ORDER BY data
-- =====================================================================
SELECT A.ID,
       A.TITULO,
       A.DATA,
       SUM(R.NOTA) AS NOTA_ALUNO,
       (SELECT ROUND(AVG(X.TOTAL), 2)
        FROM (SELECT R2.USUARIO_ID, SUM(R2.NOTA) AS TOTAL
              FROM RESPOSTA R2
              WHERE R2.AVALIACAO_ID = A.ID AND R2.USUARIO_ID <> 2
              GROUP BY R2.USUARIO_ID) X) AS MEDIA_DEMAIS
FROM RESPOSTA R
JOIN AVALIACAO A ON A.ID = R.AVALIACAO_ID
WHERE R.USUARIO_ID = 2
GROUP BY A.ID, A.TITULO, A.DATA
ORDER BY A.DATA;


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