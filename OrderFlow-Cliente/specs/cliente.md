# Cliente

## Objetivo

O domínio Cliente é responsável pelo cadastro e gerenciamento dos clientes da plataforma OrderFlow.

---
## Escopo

O domínio Cliente deve permitir:

- Realizar operações completas de CRUD: criar, consultar, atualizar e deletar clientes.
- Consultar a lista de todos os clientes.
- Consultar um cliente pelo `cpfCnpj` (única forma de consulta individual).
- Atualizar e excluir um cliente pelo seu identificador (`id`).
- Realizar atualização total de um cliente, permitindo a alteração de todos os campos, exceto o `id`.
- Realizar atualização parcial de um cliente, alterando somente os campos informados, exceto o `id`.

---
## Entidade do Cliente

Um cliente deve possuir os seguintes dados:

| Campo                 | Tipo            | Descrição                                       |
|-----------------------|-----------------|-------------------------------------------------|
| `id`                  | `UUID`          | Identificador único do cliente                  |
| `cpfCnpj`             | `String`        | CPF ou CNPJ do cliente                          |
| `nome`                | `String`        | Nome do cliente                                 |
| `tipo`                | `char(1)`       | Tipo de pessoa: `F` (física) ou `J` (jurídica)  |
| `telefone`            | `String`        | Telefone do cliente                             |
| `email`               | `String`        | Endereço de e-mail do cliente                   |
| `endereco`            | `String`        | Endereço residencial do cliente                 |
| `dataHoraCriacao`     | `LocalDateTime` | Data/hora de criação do registro                |
| `dataHoraAlteracao`   | `LocalDateTime` | Data/hora da última alteração do registro       |

---
## Regras de negócio
- `cpfCnpj` deve ser único.
- `nome`, `cpfCnpj`, `telefone` e `email` devem ser obrigatórios e não podem ser vazios ou nulos.
- `endereco` é opcional.
- `tipo` deve possuir exatamente uma posição e ser preenchido automaticamente conforme a quantidade de dígitos do `cpfCnpj`: `F` quando possuir 11 dígitos e `J` quando possuir 14 dígitos.
- `tipo` não é informado na requisição; é recalculado sempre que o `cpfCnpj` for alterado.
- `cpfCnpj` deve ser informado somente com dígitos, sem máscara (`.`, `/`, `-` ou qualquer outro caractere tornam o valor inválido).
- `cpfCnpj` deve possuir exatamente 11 (CPF) ou 14 (CNPJ) dígitos.
- `cpfCnpj` é armazenado exatamente como informado, contendo somente dígitos.
- A consulta por `cpfCnpj` deve ser realizada informando somente os dígitos.
- `email` deve possuir formato válido de endereço de e-mail.
- Campos do tipo `String` devem remover espaços em branco no início e no final.
- Na atualização parcial, campos não informados (`null`) não são alterados; campos obrigatórios informados não podem ser vazios.
- `dataHoraCriacao` será preenchida somente na primeira vez que o registro for criado.
- `dataHoraAlteracao` será atualizada toda vez que o registro for alterado (permanece `null` até a primeira alteração).

---
## Contrato da API

Porta padrão da aplicação: `8081`.

| Método   | Endpoint                         | Descrição                                   | Sucesso |
|----------|----------------------------------|---------------------------------------------|---------|
| `POST`   | `/clientes`                      | Cria um cliente                             | `201`   |
| `GET`    | `/clientes`                      | Lista todos os clientes (paginado com `Pageable`) | `200`   |
| `GET`    | `/clientes/{cpfCnpj}`            | Consulta um cliente pelo `cpfCnpj`          | `200`   |
| `PUT`    | `/clientes/{id}`                 | Atualização total                           | `200`   |
| `PATCH`  | `/clientes/{id}`                 | Atualização parcial                         | `200`   |
| `DELETE` | `/clientes/{id}`                 | Exclui um cliente                           | `204`   |

Respostas de erro:

| Situação                                   | Status |
|--------------------------------------------|--------|
| Campos inválidos (incluindo `cpfCnpj` com máscara ou quantidade de dígitos inválida) | `400`  |
| Cliente não encontrado                     | `404`  |
| `cpfCnpj` já cadastrado                    | `409`  |
