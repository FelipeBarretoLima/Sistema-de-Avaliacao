-- Dados de teste (rodar depois do schema.sql)

INSERT INTO USUARIO (NOME, EMAIL, PERFIL) VALUES
    ('Prof. Anderson', 'professor@uel.br', 'PROFESSOR'),
    ('Guilherme Tanigawa',      'tanigawa@uel.br',       'ALUNO'),
    ('Fabrício Felix',     'fabricio@uel.br',     'ALUNO'),
    ('Matheus Yudi',   'yudi@uel.br',     'ALUNO');

INSERT INTO QUESTAO (ENUNCIADO, TIPO) VALUES
    ('Qual comando cria uma tabela?', 'MULTIPLA'),
    ('Qual cláusula filtra grupos depois do GROUP BY?', 'MULTIPLA'),
    ('Explique com suas palavras o que é a 3FN.', 'TEXTO');

INSERT INTO ALTERNATIVA (QUESTAO_ID, LETRA, TEXTO, CORRETA) VALUES
    (1, 'A', 'CREATE TABLE', TRUE),
    (1, 'B', 'INSERT INTO',  FALSE),
    (1, 'C', 'ALTER TABLE',  FALSE),
    (1, 'D', 'NEW TABLE',    FALSE),
    (2, 'A', 'WHERE',        FALSE),
    (2, 'B', 'HAVING',       TRUE),
    (2, 'C', 'ORDER BY',     FALSE),
    (2, 'D', 'LIMIT',        FALSE);

INSERT INTO AVALIACAO (TITULO, DESCRICAO, DATA, HORA_INICIO, HORA_FIM) VALUES
    ('Prova 1 de BD', 'DDL e consultas básicas', '2026-12-23', '19:15', '21:00');

INSERT INTO AVALIACAO_QUESTAO (AVALIACAO_ID, QUESTAO_ID, VALOR, ORDEM) VALUES
    (1, 1, 3.00, 1),
    (1, 2, 3.00, 2),
    (1, 3, 4.00, 3);

INSERT INTO AVALIACAO_USUARIO (AVALIACAO_ID, USUARIO_ID) VALUES
    (1, 2), (1, 3), (1, 4);

INSERT INTO RESPOSTA (AVALIACAO_ID, QUESTAO_ID, USUARIO_ID, LETRA, TEXTO_RESPOSTA, NOTA) VALUES
    (1, 1, 2, 'A',  NULL, 3.00),
    (1, 2, 2, 'B',  NULL, 3.00),
    (1, 3, 2, NULL, 'Toda coluna não-chave depende só da chave, sem dependências transitivas.', 3.50),
    (1, 1, 3, 'B',  NULL, 0.00);
