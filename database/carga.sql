-- Dados de teste (rodar depois do schema.sql)

-- USUARIO: 1 professor + 24 alunos
INSERT INTO USUARIO (ID, NOME, EMAIL, PERFIL) VALUES
    (1,  'Prof. Anderson',    'professor@uel.br',  'PROFESSOR'),
    (2,  'Breno Navarro',         'Breno@uel.br',        'ALUNO'),
    (3,  'Otavio Dias',        'Otavio@uel.br',      'ALUNO'),
    (4,  'Joao Smoke',      'Smoke@uel.br',      'ALUNO'),
    (5,  'Daniel Trevisam',    'Daniel@uel.br',      'ALUNO'),
    (6,  'Fabricio Felix',     'Fabricio@uel.br',    'ALUNO'),
    (7,  'Felipe Barreto',       'Barreto@uel.br',      'ALUNO'),
    (8,  'Guilherme Tanigawa',    'Tanigawa@uel.br',   'ALUNO'),
    (9,  'Lorenzo',    'Lorenzo@uel.br',   'ALUNO'),
    (10, 'Leo',   'Leo@uel.br',    'ALUNO'),
    (11, 'João Betoni',  'Betoni@uel.br',       'ALUNO'),
    (12, 'Lucas',     'Lucas@uel.br',     'ALUNO'),
    (13, 'Eder',    'Eder@uel.br',      'ALUNO'),
    (14, 'Ruan',     'Ruan@uel.br',    'ALUNO'),
    (15, 'Matheus Yudi',   'Yudi@uel.br',    'ALUNO'),
    (16, 'Gabriel CJ',      'Gabriel@uel.br',     'ALUNO'),
    (17, 'Paulo',    'paulo@uel.br',      'ALUNO'),
    (18, 'Rafaela Gomes',     'rafaela@uel.br',    'ALUNO'),
    (19, 'Samuel Cardoso',    'samuel@uel.br',     'ALUNO'),
    (20, 'Tatiane Pires',     'tatiane@uel.br',    'ALUNO'),
    (21, 'Vinícius Moreira',  'vinicius@uel.br',   'ALUNO'),
    (22, 'Yasmin Freitas',    'yasmin@uel.br',     'ALUNO'),
    (23, 'Thiago Lopes',      'thiago@uel.br',     'ALUNO'),
    (24, 'Beatriz Campos',    'beatriz@uel.br',    'ALUNO'),
    (25, 'Gustavo Ribeiro',   'gustavo@uel.br',    'ALUNO');

-- QUESTAO: 25 de múltipla escolha (1 a 25) + 5 de texto (26 a 30)
INSERT INTO QUESTAO (ID, ENUNCIADO, TIPO) VALUES
    (1,  'Qual comando cria uma tabela?', 'MULTIPLA'),
    (2,  'Qual cláusula filtra grupos depois do GROUP BY?', 'MULTIPLA'),
    (3,  'Qual comando remove uma tabela inteira do banco?', 'MULTIPLA'),
    (4,  'Qual restrição impede valores repetidos numa coluna que não é a chave primária?', 'MULTIPLA'),
    (5,  'Qual restrição garante a integridade referencial?', 'MULTIPLA'),
    (6,  'Qual comando altera dados de linhas já existentes?', 'MULTIPLA'),
    (7,  'Qual junção traz também as linhas da tabela da esquerda sem correspondência?', 'MULTIPLA'),
    (8,  'Qual função conta o número de linhas de um grupo?', 'MULTIPLA'),
    (9,  'Qual cláusula ordena o resultado de uma consulta?', 'MULTIPLA'),
    (10, 'O que é o grau de uma relação?', 'MULTIPLA'),
    (11, 'O que é a cardinalidade de uma relação?', 'MULTIPLA'),
    (12, 'Na entidade fraca, a chave primária é formada por:', 'MULTIPLA'),
    (13, 'Como se mapeia um relacionamento N:M para o modelo relacional?', 'MULTIPLA'),
    (14, 'No mapeamento de um relacionamento 1:N, a chave estrangeira fica:', 'MULTIPLA'),
    (15, 'Uma relação está na 1FN quando:', 'MULTIPLA'),
    (16, 'A 2FN elimina:', 'MULTIPLA'),
    (17, 'A 3FN elimina:', 'MULTIPLA'),
    (18, 'Qual tipo é mais adequado para guardar um código de tamanho fixo?', 'MULTIPLA'),
    (19, 'O que faz ON DELETE CASCADE?', 'MULTIPLA'),
    (20, 'Qual comando acrescenta uma coluna a uma tabela existente?', 'MULTIPLA'),
    (21, 'Qual operador testa se um valor pertence a uma lista?', 'MULTIPLA'),
    (22, 'Qual operador busca textos por um padrão?', 'MULTIPLA'),
    (23, 'Uma subconsulta correlacionada é aquela que:', 'MULTIPLA'),
    (24, 'Qual função de janela coloca valores empatados na mesma posição?', 'MULTIPLA'),
    (25, 'No PostgreSQL, qual tipo gera a chave automaticamente?', 'MULTIPLA'),
    (26, 'Explique com suas palavras o que é a 3FN.', 'TEXTO'),
    (27, 'Explique a diferença entre WHERE e HAVING.', 'TEXTO'),
    (28, 'O que é uma entidade fraca? Dê um exemplo.', 'TEXTO'),
    (29, 'Explique para que serve uma agregação no MER.', 'TEXTO'),
    (30, 'Explique a diferença entre INNER JOIN e LEFT JOIN.', 'TEXTO');

-- ALTERNATIVA (entidade fraca de QUESTAO): 4 por questão de múltipla escolha
INSERT INTO ALTERNATIVA (QUESTAO_ID, LETRA, TEXTO, CORRETA) VALUES
    (1, 'A', 'CREATE TABLE', TRUE),   (1, 'B', 'INSERT INTO', FALSE),  (1, 'C', 'ALTER TABLE', FALSE), (1, 'D', 'NEW TABLE', FALSE),
    (2, 'A', 'WHERE', FALSE),         (2, 'B', 'HAVING', TRUE),        (2, 'C', 'ORDER BY', FALSE),    (2, 'D', 'LIMIT', FALSE),
    (3, 'A', 'DELETE FROM', FALSE),   (3, 'B', 'TRUNCATE', FALSE),     (3, 'C', 'DROP TABLE', TRUE),   (3, 'D', 'REMOVE TABLE', FALSE),
    (4, 'A', 'CHECK', FALSE),         (4, 'B', 'NOT NULL', FALSE),     (4, 'C', 'DEFAULT', FALSE),     (4, 'D', 'UNIQUE', TRUE),
    (5, 'A', 'FOREIGN KEY', TRUE),    (5, 'B', 'PRIMARY KEY', FALSE),  (5, 'C', 'CHECK', FALSE),       (5, 'D', 'UNIQUE', FALSE),
    (6, 'A', 'INSERT', FALSE),        (6, 'B', 'UPDATE', TRUE),        (6, 'C', 'ALTER', FALSE),       (6, 'D', 'MODIFY', FALSE),
    (7, 'A', 'INNER JOIN', FALSE),    (7, 'B', 'CROSS JOIN', FALSE),   (7, 'C', 'LEFT JOIN', TRUE),    (7, 'D', 'NATURAL JOIN', FALSE),
    (8, 'A', 'SUM', FALSE),           (8, 'B', 'AVG', FALSE),          (8, 'C', 'MAX', FALSE),         (8, 'D', 'COUNT', TRUE),
    (9, 'A', 'ORDER BY', TRUE),       (9, 'B', 'GROUP BY', FALSE),     (9, 'C', 'SORT', FALSE),        (9, 'D', 'HAVING', FALSE),
    (10, 'A', 'O número de tuplas', FALSE),     (10, 'B', 'O número de atributos', TRUE),
    (10, 'C', 'O número de chaves', FALSE),     (10, 'D', 'O número de índices', FALSE),
    (11, 'A', 'O número de atributos', FALSE),  (11, 'B', 'O número de chaves estrangeiras', FALSE),
    (11, 'C', 'O número de tuplas', TRUE),      (11, 'D', 'O número de restrições', FALSE),
    (12, 'A', 'Só a chave parcial', FALSE),     (12, 'B', 'A chave do dono mais a chave parcial', TRUE),
    (12, 'C', 'Uma chave artificial', FALSE),   (12, 'D', 'Todos os atributos', FALSE),
    (13, 'A', 'Chave estrangeira no lado N', FALSE),               (13, 'B', 'Atributo multivalorado', FALSE),
    (13, 'C', 'Relação própria com as chaves das duas entidades', TRUE), (13, 'D', 'Juntando as duas entidades numa só', FALSE),
    (14, 'A', 'No lado 1', FALSE),              (14, 'B', 'No lado N', TRUE),
    (14, 'C', 'Numa relação própria', FALSE),   (14, 'D', 'Em qualquer um dos lados', FALSE),
    (15, 'A', 'Não tem dependências transitivas', FALSE), (15, 'B', 'Todos os atributos são atômicos', TRUE),
    (15, 'C', 'Não tem dependências parciais', FALSE),    (15, 'D', 'Tem chave composta', FALSE),
    (16, 'A', 'Dependências parciais da chave', TRUE),   (16, 'B', 'Atributos multivalorados', FALSE),
    (16, 'C', 'Dependências transitivas', FALSE),         (16, 'D', 'Chaves estrangeiras', FALSE),
    (17, 'A', 'Atributos compostos', FALSE),     (17, 'B', 'Dependências parciais', FALSE),
    (17, 'C', 'Dependências transitivas', TRUE), (17, 'D', 'Chaves candidatas', FALSE),
    (18, 'A', 'VARCHAR(n)', FALSE),   (18, 'B', 'CHAR(n)', TRUE),      (18, 'C', 'TEXT', FALSE),       (18, 'D', 'BLOB', FALSE),
    (19, 'A', 'Impede a exclusão', FALSE),       (19, 'B', 'Anula a chave estrangeira', FALSE),
    (19, 'C', 'Remove também as linhas dependentes', TRUE), (19, 'D', 'Volta ao valor-padrão', FALSE),
    (20, 'A', 'ALTER TABLE ... ADD', TRUE),      (20, 'B', 'UPDATE TABLE', FALSE),
    (20, 'C', 'INSERT COLUMN', FALSE),           (20, 'D', 'CREATE COLUMN', FALSE),
    (21, 'A', 'LIKE', FALSE),         (21, 'B', 'BETWEEN', FALSE),     (21, 'C', 'IN', TRUE),          (21, 'D', 'EXISTS', FALSE),
    (22, 'A', 'IN', FALSE),           (22, 'B', 'LIKE', TRUE),         (22, 'C', '=', FALSE),          (22, 'D', 'BETWEEN', FALSE),
    (23, 'A', 'Roda uma única vez', FALSE),      (23, 'B', 'Usa colunas da consulta externa', TRUE),
    (23, 'C', 'Fica sempre no FROM', FALSE),     (23, 'D', 'Não pode usar agregação', FALSE),
    (24, 'A', 'ROW_NUMBER()', FALSE), (24, 'B', 'COUNT()', FALSE),     (24, 'C', 'RANK()', TRUE),      (24, 'D', 'SUM()', FALSE),
    (25, 'A', 'SERIAL', TRUE),        (25, 'B', 'AUTO', FALSE),        (25, 'C', 'IDENTITYKEY', FALSE), (25, 'D', 'COUNTER', FALSE);

-- AVALIACAO: 5 avaliações em datas diferentes
INSERT INTO AVALIACAO (ID, TITULO, DESCRICAO, DATA, HORA_INICIO, HORA_FIM) VALUES
    (1, 'Prova 1 de BD',    'DDL e consultas básicas',             '2026-09-04', '19:15', '21:00'),
    (2, 'Simulado 1',       'Modelo relacional e integridade',     '2026-09-18', '19:15', '20:30'),
    (3, 'Prova 2 de BD',    'Mapeamento MER para relacional',      '2026-10-09', '19:15', '21:00'),
    (4, 'Simulado 2',       'Normalização',                        '2026-10-23', '19:15', '20:30'),
    (5, 'Prova 3 de BD',    'Subconsultas, agrupamento e junções', '2026-11-13', '19:15', '21:00');

-- AVALIACAO_QUESTAO: 6 questões por avaliação (5 de múltipla x 1,5 + 1 de texto x 2,5 = 10)
-- A questão 1 aparece na Prova 1 e na Prova 3; a questão 25 nunca é usada.
INSERT INTO AVALIACAO_QUESTAO (AVALIACAO_ID, QUESTAO_ID, VALOR, ORDEM) VALUES
    (1, 1, 1.5, 1),  (1, 2, 1.5, 2),  (1, 3, 1.5, 3),  (1, 4, 1.5, 4),  (1, 5, 1.5, 5),  (1, 26, 2.5, 6),
    (2, 6, 1.5, 1),  (2, 7, 1.5, 2),  (2, 8, 1.5, 3),  (2, 9, 1.5, 4),  (2, 10, 1.5, 5), (2, 27, 2.5, 6),
    (3, 11, 1.5, 1), (3, 12, 1.5, 2), (3, 13, 1.5, 3), (3, 14, 1.5, 4), (3, 15, 1.5, 5), (3, 28, 2.5, 6),
    (4, 16, 1.5, 1), (4, 17, 1.5, 2), (4, 18, 1.5, 3), (4, 19, 1.5, 4), (4, 20, 1.5, 5), (4, 29, 2.5, 6),
    (5, 21, 1.5, 1), (5, 22, 1.5, 2), (5, 23, 1.5, 3), (5, 24, 1.5, 4), (5, 1, 1.5, 5),  (5, 30, 2.5, 6);

-- AVALIACAO_USUARIO: todos os alunos fazem todas as avaliações
INSERT INTO AVALIACAO_USUARIO (AVALIACAO_ID, USUARIO_ID)
SELECT A.ID, U.ID
FROM AVALIACAO A, USUARIO U
WHERE U.PERFIL = 'ALUNO';

-- Os SERIAL continuam a partir do maior ID inserido acima
SELECT setval('usuario_id_seq',   (SELECT MAX(ID) FROM USUARIO));
SELECT setval('questao_id_seq',   (SELECT MAX(ID) FROM QUESTAO));
SELECT setval('avaliacao_id_seq', (SELECT MAX(ID) FROM AVALIACAO));

-- RESPOSTA: geradas com sorteio (setseed deixa o resultado sempre igual).
-- Cada aluno tem um nível de acerto, que melhora um pouco a cada avaliação;
-- algumas questões são mais difíceis que outras.
-- Na Prova 3, Carla, Henrique, Nicolas e Yasmin ainda não responderam,
-- e as respostas de texto ainda não foram corrigidas (NOTA nula).
SELECT setseed(0.42);

CREATE TEMPORARY TABLE TMP_RESPOSTA AS
SELECT AU.AVALIACAO_ID,
       AQ.QUESTAO_ID,
       AU.USUARIO_ID,
       Q.TIPO,
       AQ.VALOR,
       A.DATA + TIME '19:30' + (AQ.ORDEM * 8) * INTERVAL '1 minute' AS RESPONDIDA_EM,
       LEAST(0.97, GREATEST(0.10,
             0.45 + (AU.USUARIO_ID % 7) * 0.08      -- nível do aluno
           + AU.AVALIACAO_ID * 0.04                 -- melhora ao longo do semestre
           - (AQ.QUESTAO_ID % 5) * 0.07)) AS CHANCE, -- dificuldade da questão
       random() AS SORTEIO
FROM AVALIACAO_USUARIO AU
JOIN AVALIACAO_QUESTAO AQ ON AQ.AVALIACAO_ID = AU.AVALIACAO_ID
JOIN QUESTAO Q ON Q.ID = AQ.QUESTAO_ID
JOIN AVALIACAO A ON A.ID = AU.AVALIACAO_ID
WHERE NOT (AU.AVALIACAO_ID = 5 AND AU.USUARIO_ID IN (4, 9, 15, 22));

-- Múltipla escolha: acerta (nota = valor) ou marca uma alternativa errada (nota 0)
INSERT INTO RESPOSTA (AVALIACAO_ID, QUESTAO_ID, USUARIO_ID, LETRA, NOTA, RESPONDIDA_EM)
SELECT T.AVALIACAO_ID, T.QUESTAO_ID, T.USUARIO_ID,
       CASE WHEN T.SORTEIO < T.CHANCE
            THEN (SELECT C.LETRA FROM ALTERNATIVA C WHERE C.QUESTAO_ID = T.QUESTAO_ID AND C.CORRETA)
            ELSE (SELECT E.LETRA FROM ALTERNATIVA E
                  WHERE E.QUESTAO_ID = T.QUESTAO_ID AND NOT E.CORRETA
                  ORDER BY md5(E.LETRA || T.USUARIO_ID || T.AVALIACAO_ID) LIMIT 1)
       END,
       CASE WHEN T.SORTEIO < T.CHANCE THEN T.VALOR ELSE 0 END,
       T.RESPONDIDA_EM
FROM TMP_RESPOSTA T
WHERE T.TIPO = 'MULTIPLA';

-- Texto livre: nota proporcional ao nível do aluno; na Prova 3 ainda sem correção
INSERT INTO RESPOSTA (AVALIACAO_ID, QUESTAO_ID, USUARIO_ID, TEXTO_RESPOSTA, NOTA, RESPONDIDA_EM)
SELECT T.AVALIACAO_ID, T.QUESTAO_ID, T.USUARIO_ID,
       CASE T.USUARIO_ID % 3
            WHEN 0 THEN 'Resposta curta, com a ideia principal mas sem exemplo.'
            WHEN 1 THEN 'Resposta completa, com definição e exemplo do modelo da disciplina.'
            ELSE 'Resposta parcial: explica o conceito, mas confunde alguns termos.'
       END,
       CASE WHEN T.AVALIACAO_ID = 5 THEN NULL
            ELSE ROUND(CAST(T.VALOR * LEAST(1, GREATEST(0, T.CHANCE + (T.SORTEIO - 0.5) * 0.4)) AS NUMERIC), 1)
       END,
       T.RESPONDIDA_EM
FROM TMP_RESPOSTA T
WHERE T.TIPO = 'TEXTO';

DROP TABLE TMP_RESPOSTA;
