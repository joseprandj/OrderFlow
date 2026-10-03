# Pedido

## Objetivo

O domínio Pedido é responsável pelo cadastro e gerenciamento dos pedidos realizados na plataforma OrderFlow.

---
## Escopo

O domínio Pedido deve permitir:

- Criar pedidos.
- Consultar uma lista de pedidos com seus respectivos itens e financeiros.
- Consultar um pedido pelo seu identificador com seus respectivos itens e financeiros.
- Incluir, alterar a quantidade e remover itens de um pedido existente.
- Um pedido pode ser excluído após sua criação.

---
## Entidade do Pedido

Um pedido deve possuir os seguintes dados:

| Campo                 | Tipo               | Descrição                                           |
|-----------------------|--------------------|-----------------------------------------------------|
| `id`                  | `UUID`             | Identificador único do pedido                       |
| `idCliente`           | `UUID`             | Identificador do cliente ao qual o pedido pertence  |
| `itens`               | `List<ItemPedido>` | Itens associados ao pedido                          |
| `valorTotal`          | `BigDecimal`       | Valor total do pedido                               |
| `idFinanceiro`        | `UUID`             | Identificador do financeiro vinculado ao pedido     |
| `idLogistica`         | `UUID`             | Identificador da logística vinculada ao pedido      |
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

- Todo pedido deve estar associado a exatamente um cliente, referenciado por `idCliente`.
- Na criação, o cliente é informado pelo `cpfCnpj` (somente dígitos); o pedido consulta o domínio Cliente (`GET /clientes/{cpfCnpj}`) e armazena o `idCliente` retornado.
- O cliente informado deve existir na plataforma.
- Um pedido pode possuir vários itens associados.
- Na criação, o pedido deve possuir ao menos um item.
- Um pedido não pode possuir mais de um item associado ao mesmo `sku`.
- Todo item do pedido deve estar associado a um produto existente (validado no domínio Produto).
- O item é informado com `idProduto` e `quantidade`; `sku` e `preco` são obtidos do produto.
- A `quantidade` de um item deve ser maior que zero.
- A quantidade solicitada para um item não pode ser superior à quantidade disponível em estoque.
- Produtos inativos (`ativo = false`) não podem ser utilizados no pedido, seja na criação ou na inclusão de itens.
- Após a criação do pedido, seus dados principais não poderão ser alterados.
- Após a criação, somente os itens do pedido poderão ser incluídos, alterados ou excluídos.
- A alteração de um item permite modificar somente a sua `quantidade`.
- A remoção do último item do pedido é permitida; o pedido permanece com `valorTotal` igual a zero.
- `valorTotal` do pedido deve corresponder ao somatório do `valorTotal` dos itens.
- O `preco` do item deve corresponder ao preço do produto no momento da inclusão do item no pedido e não é alterado posteriormente.
- Ao criar o pedido, o estoque de cada item deve ser debitado.
- Ao adicionar, alterar ou remover um item:
  - o valorTotal do pedido deve ser recalculado;
  - o estoque deve ser ajustado conforme a diferença de quantidade;
  - o valor do financeiro deve ser atualizado conforme o novo `valorTotal` (será realizado futuramente via mensageria).
- Ao excluir o pedido, a quantidade de todos os seus itens deve ser devolvida ao estoque.
- Campos do tipo `String` devem remover espaços em branco no início e no final.
- `dataHoraCriacao` será preenchida somente na primeira vez que o registro for criado.
- `dataHoraAlteracao` será atualizada toda vez que o registro for alterado (permanece `null` até a primeira alteração).

### Relacionamentos entre domínios

- A integração com os domínios Cliente, Produto e Estoque é realizada via HTTP.
- O ajuste de estoque é feito consultando a quantidade disponível e gravando a nova quantidade no domínio Estoque.
- Quando um ajuste de estoque falha durante uma operação com vários itens, os ajustes já aplicados são revertidos e a operação do pedido é desfeita.
- A criação e a atualização do Financeiro e da Logística a partir do pedido **não** são realizadas pelo domínio Pedido neste momento; serão implementadas via mensageria. Até lá, `idFinanceiro` e `idLogistica` permanecem `null`.
- As consultas de pedido retornam o identificador do financeiro (`idFinanceiro`), não os dados do financeiro.

---
## Contrato da API

Porta padrão da aplicação: `8085`.

| Método   | Endpoint                          | Descrição                                                  | Sucesso |
|----------|-----------------------------------|------------------------------------------------------------|---------|
| `POST`   | `/pedidos`                        | Cria um pedido e debita o estoque dos itens                | `201`   |
| `GET`    | `/pedidos`                        | Lista pedidos com seus itens (paginado com `Pageable`)     | `200`   |
| `GET`    | `/pedidos/{id}`                   | Consulta um pedido com seus itens                          | `200`   |
| `DELETE` | `/pedidos/{id}`                   | Exclui o pedido e devolve o estoque dos itens              | `204`   |
| `POST`   | `/pedidos/{id}/itens`             | Inclui um item e debita o estoque                          | `201`   |
| `PATCH`  | `/pedidos/{id}/itens/{idItem}`    | Altera a quantidade do item e ajusta o estoque             | `200`   |
| `DELETE` | `/pedidos/{id}/itens/{idItem}`    | Remove o item e devolve o estoque                          | `200`   |

As operações sobre itens retornam o pedido atualizado.

Respostas de erro:

| Situação                                                         | Status |
|------------------------------------------------------------------|--------|
| Campos inválidos                                                 | `400`  |
| Pedido ou item não encontrado                                    | `404`  |
| Item com `sku` já existente no pedido                            | `409`  |
| Cliente, produto ou estoque inexistente; produto inativo; estoque insuficiente | `422`  |
| Falha de comunicação com outro domínio                           | `502`  |

---
## Pontos em aberto

- O ajuste de estoque (consulta + gravação da nova quantidade) não é atômico: pedidos simultâneos para o mesmo `sku` podem sobrescrever um ao outro, e uma falha ao confirmar a transação do pedido após o ajuste pode deixar estoque e pedido inconsistentes. Avaliar operações de baixa/devolução no Estoque ou o tratamento via mensageria.
