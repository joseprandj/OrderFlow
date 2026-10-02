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
| `dataHoraAlteracao` | `LocalDateTime` | Data/hora da alteração do registro |

---
## Regras de negócio

- `sku` deve ser único.
- `sku` e `nome` são obrigatórios e não podem ser nulos ou vazios.
- `descricao` terá valor `null` por padrão.
- `preco` deve ser maior que zero e possuir precisão de 2 casas decimais.
- `ativo` terá valor `false` por padrão.
- Campos do tipo `String` devem remover espaços em branco no início e no final.
- `dataHoraCriacao` será preenchida somente na primeira vez que o registro for criado.
- `dataHoraAlteracao` será atualizada toda vez que o registro for alterado.

### Relacionamentos entre domínios

#### Produto x Estoque
- Ao criar um produto, deve ser criado automaticamente um registro de estoque associado ao seu `sku`, com quantidade inicial igual a `0`.