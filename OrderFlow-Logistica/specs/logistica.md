# Logística

## Objetivo

O domínio Logística é responsável pelos eventos de entrega dos pedidos realizados na plataforma OrderFlow.

---
## Escopo

O domínio Logística deve permitir:

- Criar um registro.
- Alterar o status do registro.
- Consultar uma lista de registros.
- Consultar um registro pelo seu identificador.
- Um registro pode ser excluído após sua criação.

---
## Entidade da Logística

Um registro deve possuir os seguintes dados:

| Campo                 | Tipo                | Descrição                                          |
|-----------------------|---------------------|----------------------------------------------------|
| `id`                  | `UUID`              | Identificador único da entrega                     |
| `status`              | `Enum`              | Status da entrega do pedido                        |
| `ocorrencia`          | `String(4000)`      | Descrição da ocorrência relacionada à entrega      |
| `dataHoraCriacao`     | `LocalDateTime`     | Data/hora de criação do registro                   |
| `dataHoraAlteracao` | `LocalDateTime`     | Data/hora da última alteração do registro          |

---
## Regras de negócio

- Todo registro deve estar associado a exatamente um pedido.
- `status` deve ser um Enum com as seguintes opções: `PENDENTE_PAGAMENTO`, `PAGO`, `INICIADA`, `RECUSADA`, `ENTREGUE`.
- O valor do `status` deve ser gravado no banco de dados como `String`.
- `status` terá valor `PENDENTE_PAGAMENTO` por padrão.
- Campos do tipo `String` devem remover espaços em branco no início e no final.
- `dataHoraCriacao` será preenchida somente na primeira vez que o registro for criado.
- `dataHoraAlteracao` será atualizada toda vez que o registro for alterado.
