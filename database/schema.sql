-- Sistema de Avaliações — Banco de Dados I (UEL)

DROP TABLE IF EXISTS RESPOSTA CASCADE;
DROP TABLE IF EXISTS AVALIACAO_USUARIO CASCADE;
DROP TABLE IF EXISTS AVALIACAO_QUESTAO CASCADE;
DROP TABLE IF EXISTS AVALIACAO CASCADE;
DROP TABLE IF EXISTS ALTERNATIVA CASCADE;
DROP TABLE IF EXISTS QUESTAO CASCADE;
DROP TABLE IF EXISTS USUARIO CASCADE;

-- USUARIO
CREATE TABLE USUARIO (
    ID      SERIAL,                                                              -- PK
    NOME    VARCHAR(100) NOT NULL,
    EMAIL   VARCHAR(120) NOT NULL,                                               -- UNIQUE
    PERFIL  VARCHAR(10)  NOT NULL CHECK (PERFIL IN ('PROFESSOR', 'ALUNO')),
    PRIMARY KEY (ID),
    UNIQUE (EMAIL) );

-- QUESTAO
CREATE TABLE QUESTAO (
    ID         SERIAL,                                                           -- PK
    ENUNCIADO  VARCHAR(1000) NOT NULL,
    TIPO       VARCHAR(8)    NOT NULL CHECK (TIPO IN ('MULTIPLA', 'TEXTO')),
    PRIMARY KEY (ID) );

-- ALTERNATIVA (entidade fraca de QUESTAO)
CREATE TABLE ALTERNATIVA (
    QUESTAO_ID  INT          NOT NULL,                                           -- PK, FK -> QUESTAO
    LETRA       CHAR(1)      NOT NULL CHECK (LETRA IN ('A', 'B', 'C', 'D', 'E')), -- PK
    TEXTO       VARCHAR(500) NOT NULL,
    CORRETA     BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (QUESTAO_ID, LETRA),
    FOREIGN KEY (QUESTAO_ID)
        REFERENCES QUESTAO(ID) );

-- AVALIACAO
CREATE TABLE AVALIACAO (
    ID           SERIAL,                                                         -- PK
    TITULO       VARCHAR(150) NOT NULL,
    DESCRICAO    VARCHAR(500),
    DATA         DATE         NOT NULL,
    HORA_INICIO  TIME         NOT NULL,
    HORA_FIM     TIME         NOT NULL,
    PRIMARY KEY (ID),
    CHECK (HORA_FIM > HORA_INICIO) );

-- AVALIACAO_QUESTAO (agregação Item da avaliação)
CREATE TABLE AVALIACAO_QUESTAO (
    AVALIACAO_ID  INT          NOT NULL,                                         -- PK, FK -> AVALIACAO
    QUESTAO_ID    INT          NOT NULL,                                         -- PK, FK -> QUESTAO
    VALOR         DECIMAL(4,2) NOT NULL CHECK (VALOR > 0),
    ORDEM         INT          NOT NULL CHECK (ORDEM > 0),
    PRIMARY KEY (AVALIACAO_ID, QUESTAO_ID),
    UNIQUE (AVALIACAO_ID, ORDEM),
    FOREIGN KEY (AVALIACAO_ID)
        REFERENCES AVALIACAO(ID),
    FOREIGN KEY (QUESTAO_ID)
        REFERENCES QUESTAO(ID) );

-- AVALIACAO_USUARIO (É designado)
CREATE TABLE AVALIACAO_USUARIO (
    AVALIACAO_ID  INT NOT NULL,                                                  -- PK, FK -> AVALIACAO
    USUARIO_ID    INT NOT NULL,                                                  -- PK, FK -> USUARIO
    PRIMARY KEY (AVALIACAO_ID, USUARIO_ID),
    FOREIGN KEY (AVALIACAO_ID)
        REFERENCES AVALIACAO(ID),
    FOREIGN KEY (USUARIO_ID)
        REFERENCES USUARIO(ID) );

-- RESPOSTA
CREATE TABLE RESPOSTA (
    ID              SERIAL,                                                      -- PK
    AVALIACAO_ID    INT          NOT NULL,                                       -- FK -> AVALIACAO_QUESTAO
    QUESTAO_ID      INT          NOT NULL,                                       -- FK -> AVALIACAO_QUESTAO, ALTERNATIVA
    LETRA           CHAR(1),                                                     -- FK -> ALTERNATIVA
    USUARIO_ID      INT          NOT NULL,                                       -- FK -> USUARIO
    TEXTO_RESPOSTA  VARCHAR(2000),
    NOTA            DECIMAL(4,2) CHECK (NOTA >= 0),
    RESPONDIDA_EM   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (ID),
    UNIQUE (AVALIACAO_ID, QUESTAO_ID, USUARIO_ID),
    FOREIGN KEY (AVALIACAO_ID, QUESTAO_ID)
        REFERENCES AVALIACAO_QUESTAO(AVALIACAO_ID, QUESTAO_ID),
    FOREIGN KEY (QUESTAO_ID, LETRA)
        REFERENCES ALTERNATIVA(QUESTAO_ID, LETRA),
    FOREIGN KEY (USUARIO_ID)
        REFERENCES USUARIO(ID),
    CHECK (LETRA IS NOT NULL OR TEXTO_RESPOSTA IS NOT NULL) );
