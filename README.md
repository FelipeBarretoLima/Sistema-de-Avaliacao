# Sistema de Avaliações

## Integrantes
- Felipe Barreto
- Breno Navarro

## Natureza
Avaliação de aprendizagem (provas e simulados).

Cada questão tem um valor dentro da prova. A nota de cada questão é registrada
(automática nas de múltipla escolha, dada pelo professor nas dissertativas) e a
nota final do aluno é a soma dessas notas.

## Como rodar
1. Criar o banco `avaliacoes` no PostgreSQL.
2. Rodar `database/schema.sql` e depois `database/carga.sql`.
3. Colocar usuário e senha do PostgreSQL em `src/main/resources/application.properties`.
4. Rodar a classe `SistemaAvaliacoesApplication` e abrir http://localhost:8080.