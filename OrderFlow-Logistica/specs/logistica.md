# Logística

## Objetivo

O domínio Logística é responsável pelos eventos de entrega dos pedidos realizados na plataforma OrderFlow.

---
## Escopo

O domínio Logística deve permitir:

- Criar um registro.
- Alterar o status do registro, podendo alterar a ocorrência juntamente.
- Consultar uma lista de registros.
- Consultar um registro pelo seu identificador.
- Um registro pode ser excluído após sua criação.

---
## Entidade da Logística

Um registro deve possuir os seguintes dados:

| Campo                 | Tipo                | Descrição                                          |
|-----------------------|---------------------|----------------------------------------------------|
| `id`                  | `UUID`              | Identificador único da entrega                     |
| `idPedido`            | `UUID`              | Identificador do pedido ao qual o registro pertence |
| `status`              | `Enum`              | Status da entrega do pedido                        |
| `ocorrencia`          | `String(4000)`      | Descrição da ocorrência relacionada à entrega      |
| `dataHoraCriacao`     | `LocalDateTime`     | Data/hora de criação do registro                   |
| `dataHoraAlteracao`   | `LocalDateTime`     | Data/hora da última alteração do registro          |

---
## Regras de negócio

- Todo registro deve estar associado a exatamente um pedido, por meio do campo `idPedido`.
- `idPedido` é obrigatório, único (um pedido possui no máximo um registro de logística) e não pode ser alterado após a criação.
- A existência do pedido não é validada pelo domínio Logística; a criação do registro a partir do pedido será realizada futuramente via mensageria.
- `status` deve ser um Enum com as seguintes opções: `PENDENTE_PAGAMENTO`, `PAGO`, `INICIADA`, `RECUSADA`, `ENTREGUE`.
- O valor do `status` deve ser gravado no banco de dados como `String`.
- `status` terá valor `PENDENTE_PAGAMENTO` por padrão, quando não informado na criação.
- `ocorrencia` é opcional, com no máximo 4000 caracteres.
- Após a criação, somente o `status` e a `ocorrencia` podem ser alterados.
- A `ocorrencia` é alterada juntamente com a mudança do `status`; quando não informada na alteração, a ocorrência passa a ser `null`.
- Campos do tipo `String` devem remover espaços em branco no início e no final.
- `dataHoraCriacao` será preenchida somente na primeira vez que o registro for criado.
- `dataHoraAlteracao` será atualizada toda vez que o registro for alterado (permanece `null` até a primeira alteração).

---
## Contrato da API

Porta padrão da aplicação: `8084`.

| Método   | Endpoint                    | Descrição                                   | Sucesso |
|----------|-----------------------------|---------------------------------------------|---------|
| `POST`   | `/logisticas`               | Cria um registro                            | `201`   |
| `GET`    | `/logisticas`               | Lista registros (paginado com `Pageable`)   | `200`   |
| `GET`    | `/logisticas/{id}`          | Consulta um registro pelo identificador     | `200`   |
| `PATCH`  | `/logisticas/{id}/status`   | Altera o `status` e, opcionalmente, a `ocorrencia` | `200`   |
| `DELETE` | `/logisticas/{id}`          | Exclui um registro                          | `204`   |

Respostas de erro:

| Situação                                   | Status |
|--------------------------------------------|--------|
| Campos inválidos                           | `400`  |
| Registro não encontrado                    | `404`  |
| Pedido já possui registro de logística     | `409`  |
