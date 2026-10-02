# Estoque

## Objetivo

O domínio Estoque é responsável pelo controle da quantidade disponível de cada produto na plataforma OrderFlow.

---
## Escopo

O domínio Estoque deve permitir:

- Consultar a lista de estoques.
- Consultar estoques por quantidade disponível.
- Consultar o estoque de um produto pelo seu identificador.
- Atualizar somente quantidade de estoque de um produto.

---
## Entidade do Estoque

Um estoque deve possuir os seguintes dados:

| Campo                 | Tipo            | Descrição                                    |
|-----------------------|-----------------|----------------------------------------------|
| `id`                  | `UUID`          | Identificador único do estoque               |
| `sku`                 | `String`        | SKU do produto                               |
| `quantidade`          | `int`           | Quantidade disponível no estoque             |
| `dataHoraCriacao`     | `LocalDateTime` | Data/hora de criação do registro             |
| `dataHoraAlteracao`   | `LocalDateTime` | Data/hora da última alteração do registro    |

---
## Regras de negócio

- `sku` deve ser único.
- `quantidade` deve ser um inteiro maior ou igual a zero.
- Campos do tipo `String` devem remover espaços em branco no início e no final.
- `dataHoraCriacao` será preenchida somente na primeira vez que o registro for criado.
- `dataHoraAlteracao` será atualizada toda vez que o registro for alterado.