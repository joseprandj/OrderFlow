# Cliente

## Objetivo

O domínio Cliente é responsável pelo cadastro e gerenciamento dos clientes da plataforma OrderFlow.

---
## Escopo

O domínio Cliente deve permitir:

- Realizar operações completas de CRUD: criar, consultar, atualizar e deletar clientes.
- Consultar um cliente pelo seu identificador.
- Consultar um cliente pelo `cpfCnpj`.
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
- `tipo` deve possuir exatamente uma posição e ser preenchido automaticamente conforme a quantidade de dígitos do `cpfCnpj`: `F` quando possuir 11 dígitos e `J` quando possuir 14 dígitos.
- `cpfCnpj` deve ser armazenado normalizado, contendo somente dígitos.
- `email` deve possuir formato válido de endereço de e-mail.
- Campos do tipo `String` devem remover espaços em branco no início e no final.
- `dataHoraCriacao` será preenchida somente na primeira vez que o registro for criado.
- `dataHoraAlteracao` será atualizada toda vez que o registro for alterado.
