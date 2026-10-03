# Produto

## Objetivo

O domínio Produto é responsável pelo cadastro e gerenciamento dos produtos na plataforma OrderFlow.

---
## Escopo

O domínio Produto deve permitir:

- Realizar operações completas de CRUD: criar, consultar, atualizar e excluir produtos.
- Consultar um produto pelo seu identificador.
- Consultar produtos pelo campo `ativo`.
- Realizar atualização total de um produto, permitindo a alteração de todos os campos, exceto o `id` e `sku`.
- Realizar atualização parcial de um produto, alterando somente os campos informados, exceto o `id` e `sku`.

---
## Entidade do Produto

Um produto deve possuir os seguintes dados:

| Campo                 | Tipo            | Descrição                          |
|-----------------------|-----------------|------------------------------------|
| `id`                  | `UUID`          | Identificador único do produto     |
| `sku`                 | `String`        | SKU do produto                     |
| `nome`                | `String`        | Nome do produto                    |
| `descricao`           | `String`        | Descrição do produto               |
| `preco`               | `BigDecimal`    | Preço do produto                   |
| `ativo`               | `boolean`       | Produto ativado ou desativado      |
| `dataHoraCriacao`     | `LocalDateTime` | Data/hora de criação do registro   |
| `dataHoraAlteracao`   | `LocalDateTime` | Data/hora da alteração do registro |

---
## Regras de negócio

- `sku` deve ser único.
- `sku` e `nome` são obrigatórios e não podem ser nulos ou vazios.
- `descricao` terá valor `null` por padrão.
- `preco` é obrigatório, deve ser maior que zero e possuir precisão de 2 casas decimais (valores com mais casas decimais são rejeitados).
- `ativo` terá valor `false` por padrão.
- Na atualização total, `nome`, `preco` e `ativo` são obrigatórios; `descricao` não informada passa a ser `null`.
- Na atualização parcial, campos não informados (`null`) não são alterados; `nome` informado não pode ser vazio.
- `sku` informado nas atualizações é ignorado.
- Campos do tipo `String` devem remover espaços em branco no início e no final.
- `dataHoraCriacao` será preenchida somente na primeira vez que o registro for criado.
- `dataHoraAlteracao` será atualizada toda vez que o registro for alterado (permanece `null` até a primeira alteração).

### Relacionamentos entre domínios

#### Produto x Estoque
- Ao criar um produto, deve ser criado automaticamente um registro de estoque associado ao seu `sku`, com quantidade inicial igual a `0`.
- A integração é realizada via HTTP com o domínio Estoque (`POST /estoques`).
- Caso a criação do estoque falhe, a criação do produto é desfeita e a API responde com `502`.
- Caso o domínio Estoque já possua registro para o `sku` (ex.: produto excluído e recriado), o registro existente é mantido, preservando sua quantidade.
- A exclusão de um produto não exclui o seu registro de estoque.

---
## Contrato da API

Porta padrão da aplicação: `8086`.

| Método   | Endpoint            | Descrição                                                    | Sucesso |
|----------|---------------------|--------------------------------------------------------------|---------|
| `POST`   | `/produtos`         | Cria um produto e seu estoque inicial                        | `201`   |
| `GET`    | `/produtos`         | Lista produtos (paginado); filtro opcional `?ativo=true/false` | `200`   |
| `GET`    | `/produtos/{id}`    | Consulta um produto pelo identificador                       | `200`   |
| `PUT`    | `/produtos/{id}`    | Atualização total                                            | `200`   |
| `PATCH`  | `/produtos/{id}`    | Atualização parcial                                          | `200`   |
| `DELETE` | `/produtos/{id}`    | Exclui um produto                                            | `204`   |

Respostas de erro:

| Situação                                   | Status |
|--------------------------------------------|--------|
| Campos inválidos                           | `400`  |
| Produto não encontrado                     | `404`  |
| `sku` já cadastrado                        | `409`  |
| Falha ao criar o estoque do produto        | `502`  |
