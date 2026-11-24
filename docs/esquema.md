## Esquema relacional

Notação: chave primária entre `_sublinhados_`; FK indicada com →. Os nomes são os mesmos do `database/schema.sql`.

- **USUARIO** (_id_, nome, email, perfil)
  - email UNIQUE
- **QUESTAO** (_id_, enunciado, tipo)
- **ALTERNATIVA** (_questao_id_ → QUESTAO, _letra_, texto, correta)
  - entidade fraca de Questão: PK = chave do dono + chave parcial (letra)
- **AVALIACAO** (_id_, titulo, descricao, data, hora_inicio, hora_fim)
- **AVALIACAO_QUESTAO** (_avaliacao_id_ → AVALIACAO, _questao_id_ → QUESTAO, valor, ordem)
  - agregação "Item da avaliação" (N:M Avaliação–Questão); (avaliacao_id, ordem) UNIQUE
- **AVALIACAO_USUARIO** (_avaliacao_id_ → AVALIACAO, _usuario_id_ → USUARIO)
  - relacionamento N:M "É designado"
- **RESPOSTA** (_id_, (avaliacao_id, questao_id) → AVALIACAO_QUESTAO, (questao_id, letra) → ALTERNATIVA, usuario_id → USUARIO, texto_resposta, nota, respondida_em)
  - FK composta para a agregação (aula 08: a FK aponta para a chave inteira da agregação)
  - FK composta para a entidade fraca: garante que a alternativa marcada é da mesma questão; letra fica nula nas questões de texto
  - (avaliacao_id, questao_id, usuario_id) UNIQUE: cada aluno responde cada questão de uma avaliação uma vez

## Como cada parte do conceitual foi mapeada

| Conceitual | Lógico |
|---|---|
| Entidades Usuário, Questão, Avaliação, Resposta | uma relação para cada |
| Entidade fraca Alternativa, identificada por Questão (Possui) | relação com PK (questao_id, letra); o relacionamento identificador é a própria FK `questao_id` (aula 07) |
| Usuário N:M Avaliação (É designado) | relação própria AVALIACAO_USUARIO, PK = as duas FKs |
| Avaliação N:M Questão (Contém) — agregação | relação AVALIACAO_QUESTAO, PK = as duas FKs, com os atributos valor e ordem |
| Resposta N:1 agregação (Refere-se) | FK composta (avaliacao_id, questao_id) em Resposta |
| Usuário 1:N Resposta (Responde) | FK `usuario_id` em Resposta |
| Resposta N:1 Alternativa (Escolhe) | FK composta (questao_id, letra) em Resposta (pode ser nula: questão de texto) |

## Justificativa da 3FN

Todas as relações têm atributos atômicos (1FN); nas relações de chave composta
(ALTERNATIVA, AVALIACAO_QUESTAO e AVALIACAO_USUARIO) nenhum atributo depende só de parte da
chave — `valor` e `ordem` dependem do par avaliação–questão (2FN); e não há
dependências transitivas, pois dados derivados, como a nota total do aluno, não
são armazenados e sim calculados por consulta (3FN).
