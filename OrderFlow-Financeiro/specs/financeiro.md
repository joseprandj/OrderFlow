# Financeiro

## Objetivo

O domínio Financeiro é responsável pelo cadastro e gerenciamento dos financeiros dos pedidos realizados na plataforma OrderFlow.

---
## Escopo

O domínio Financeiro deve permitir:

- Criar um financeiro.
- Alterar o valor e o status do financeiro.
- Consultar uma lista de financeiros.
- Consultar um financeiro pelo seu identificador.
- Um financeiro pode ser excluído após sua criação.

---
## Entidade do Financeiro

Um financeiro deve possuir os seguintes dados:

| Campo                 | Tipo                | Descrição                                          |
|-----------------------|---------------------|----------------------------------------------------|
| `id`                  | `UUID`              | Identificador único do financeiro                  |
| `valor`               | `BigDecimal`        | Valor do pedido                                    |
| `status`              | `Enum`              | Status de pagamento do financeiro                  |
| `dataHoraCriacao`     | `LocalDateTime`     | Data/hora de criação do registro                   |
| `dataHoraAlteracao` | `LocalDateTime`     | Data/hora da última alteração do registro          |

---
## Regras de negócio

- Todo financeiro deve estar associado a exatamente um pedido.
- `status` deve ser um Enum com as seguintes opções: `PENDENTE`, `CANCELADO`, `NEGADO`, `APROVADO`.
- O valor do `status` deve ser gravado no banco de dados como `String`.
- `dataHoraCriacao` será preenchida somente na primeira vez que o registro for criado.
- `dataHoraAlteracao` será atualizada toda vez que o registro for alterado.