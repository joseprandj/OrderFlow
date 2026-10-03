# Estoque

## Objetivo

O domínio Estoque é responsável pelo controle da quantidade disponível de cada produto na plataforma OrderFlow.

---
## Escopo

O domínio Estoque deve permitir:

- Criar o estoque de um produto (utilizado pelo domínio Produto na criação do produto).
- Consultar a lista de estoques.
- Consultar estoques por quantidade disponível.
- Consultar o estoque de um produto pelo seu identificador (`sku`).
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
- `sku` é obrigatório e não pode ser alterado após a criação.
- O estoque é identificado externamente pelo `sku` do produto (o domínio Estoque não conhece o `id` do produto).
- Na criação, o estoque é iniciado com `quantidade` igual a `0`.
- `quantidade` deve ser um inteiro maior ou igual a zero.
- A atualização de quantidade define o novo valor absoluto da `quantidade`.
- A consulta por quantidade disponível retorna os estoques com `quantidade` maior ou igual ao valor informado (`quantidadeMinima`).
- Campos do tipo `String` devem remover espaços em branco no início e no final.
- `dataHoraCriacao` será preenchida somente na primeira vez que o registro for criado.
- `dataHoraAlteracao` será atualizada toda vez que o registro for alterado (permanece `null` até a primeira alteração).

---
## Contrato da API

Porta padrão da aplicação: `8082`.

| Método   | Endpoint            | Descrição                                                                 | Sucesso |
|----------|---------------------|---------------------------------------------------------------------------|---------|
| `POST`   | `/estoques`         | Cria o estoque de um `sku` com quantidade `0`                             | `201`   |
| `GET`    | `/estoques`         | Lista estoques (paginado); filtro opcional `?quantidadeMinima=N`          | `200`   |
| `GET`    | `/estoques/{sku}`   | Consulta o estoque de um produto pelo `sku`                               | `200`   |
| `PATCH`  | `/estoques/{sku}`   | Atualiza somente a `quantidade`                                           | `200`   |

Respostas de erro:

| Situação                                   | Status |
|--------------------------------------------|--------|
| Campos inválidos                           | `400`  |
| Estoque não encontrado para o `sku`        | `404`  |
| Já existe estoque para o `sku`             | `409`  |

---
## Pontos em aberto

- A atualização de quantidade é absoluta. Quando o domínio Pedido ajusta o estoque, ele consulta a quantidade atual e grava o novo valor; requisições simultâneas para o mesmo `sku` podem sobrescrever uma à outra. Avaliar a especificação de operações de baixa/devolução (incremento/decremento) ou o tratamento via mensageria.
