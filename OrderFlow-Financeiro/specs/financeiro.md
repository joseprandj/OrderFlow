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
| `idPedido`            | `UUID`              | Identificador do pedido ao qual o financeiro pertence |
| `valor`               | `BigDecimal`        | Valor do pedido                                    |
| `status`              | `Enum`              | Status de pagamento do financeiro                  |
| `dataHoraCriacao`     | `LocalDateTime`     | Data/hora de criação do registro                   |
| `dataHoraAlteracao`   | `LocalDateTime`     | Data/hora da última alteração do registro          |

---
## Regras de negócio

- Todo financeiro deve estar associado a exatamente um pedido, por meio do campo `idPedido`.
- `idPedido` é obrigatório, único (um pedido possui no máximo um financeiro) e não pode ser alterado após a criação.
- A existência do pedido não é validada pelo domínio Financeiro; a criação do financeiro a partir do pedido será realizada futuramente via mensageria.
- `valor` é obrigatório, deve ser maior ou igual a zero e possuir no máximo 2 casas decimais.
- `status` deve ser um Enum com as seguintes opções: `PENDENTE`, `CANCELADO`, `NEGADO`, `APROVADO`.
- `status` é obrigatório na criação (não possui valor padrão).
- O valor do `status` deve ser gravado no banco de dados como `String`.
- A alteração do financeiro substitui `valor` e `status`, ambos obrigatórios.
- `dataHoraCriacao` será preenchida somente na primeira vez que o registro for criado.
- `dataHoraAlteracao` será atualizada toda vez que o registro for alterado (permanece `null` até a primeira alteração).

---
## Contrato da API

Porta padrão da aplicação: `8083`.

| Método   | Endpoint              | Descrição                                     | Sucesso |
|----------|-----------------------|-----------------------------------------------|---------|
| `POST`   | `/financeiros`        | Cria um financeiro                            | `201`   |
| `GET`    | `/financeiros`        | Lista financeiros (paginado com `Pageable`)   | `200`   |
| `GET`    | `/financeiros/{id}`   | Consulta um financeiro pelo identificador     | `200`   |
| `PUT`    | `/financeiros/{id}`   | Altera `valor` e `status`                     | `200`   |
| `DELETE` | `/financeiros/{id}`   | Exclui um financeiro                          | `204`   |

Respostas de erro:

| Situação                                   | Status |
|--------------------------------------------|--------|
| Campos inválidos                           | `400`  |
| Financeiro não encontrado                  | `404`  |
| Pedido já possui financeiro                | `409`  |
