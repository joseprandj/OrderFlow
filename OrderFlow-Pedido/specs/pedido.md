# Pedido

## Objetivo

O domínio Pedido é responsável pelo cadastro e gerenciamento dos pedidos realizados na plataforma OrderFlow.

---
## Escopo

O domínio Pedido deve permitir:

- Criar pedidos.
- Consultar uma lista de pedidos com seus respectivos itens e financeiros.
- Consultar um pedido pelo seu identificador com seus respectivos itens e financeiros.
- Um pedido pode ser excluído após sua criação.

---
## Entidade do Pedido

Um pedido deve possuir os seguintes dados:

| Campo                 | Tipo               | Descrição                                           |
|-----------------------|--------------------|-----------------------------------------------------|
| `id`                  | `UUID`             | Identificador único do pedido                       |
| `cliente`             | `Cliente`          | Cliente ao qual o pedido pertence                   |
| `itens`               | `List<ItemPedido>` | Itens associados ao pedido                          |
| `valorTotal`          | `BigDecimal`       | Valor total do pedido                               |
| `Financeiro`          | `UUID`             | Identificador do financeiro vinculada ao pedido     |
| `Logistica`           | `UUID`             | Identificador da logistica  vinculada ao pedido     |
| `dataHoraCriacao`     | `LocalDateTime`    | Data/hora de criação do registro                    |
| `dataHoraAlteracao`   | `LocalDateTime`    | Data/hora da última alteração do registro           |

O contrato do `ItemPedido` deve conter:

| Campo                 | Tipo            | Descrição                                                                   |
|-----------------------|-----------------|-----------------------------------------------------------------------------|
| `id`                  | `UUID`          | Identificador único do item                                                 |
| `idProduto`           | `UUID`          | Identificador único do produto                                              |
| `sku`                 | `String`        | SKU do produto                                                              |
| `preco`               | `BigDecimal`    | Preço do produto                                                            |
| `quantidade`          | `int`           | Quantidade negociada do produto                                             |
| `valorTotal`          | `BigDecimal`    | Valor resultante da quantidade negociada multiplicada pelo preço do produto |
| `dataHoraCriacao`     | `LocalDateTime` | Data/hora de criação do registro                                            |
| `dataHoraAlteracao`   | `LocalDateTime` | Data/hora da última alteração do registro                                   |

---
## Regras de negócio

- Todo pedido deve estar associado a exatamente um cliente.
- O cliente informado deve existir na plataforma.
- Um pedido pode possuir vários itens associados.
- Um pedido não pode possuir mais de um item associado ao mesmo `sku`.
- Todo item do pedido deve estar associado a um produto existente.
- A quantidade solicitada para um item não pode ser superior à quantidade disponível em estoque.
- Após a criação do pedido, seus dados principais não poderão ser alterados.
- Após a criação, somente os itens do pedido poderão ser incluídos, alterados ou excluídos.
- `valorTotal` do pedido deve corresponder ao somatório do `valorTotal` dos itens.
- O `preco` do item deve corresponder ao preço do produto no momento da inclusão do item no pedido.
- Ao adicionar, alterar ou remover um item:
  - o valorTotal do pedido deve ser recalculado;
  - o estoque deve ser ajustado conforme a diferença de quantidade;
  - o valor do financeiro deve ser atualizado conforme o novo `valorTotal`.
- Campos do tipo `String` devem remover espaços em branco no início e no final.
- `dataHoraCriacao` será preenchida somente na primeira vez que o registro for criado.
- `dataHoraAlteracao` será atualizada toda vez que o registro for alterado.
