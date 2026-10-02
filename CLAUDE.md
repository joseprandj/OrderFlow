# OrderFlow

## Contexto
O OrderFlow é uma plataforma de gestão de pedidos desenvolvida para estudo e aplicação prática de conceitos de desenvolvimento backend, arquitetura e engenharia de software com utilização de IA.

---
## Arquitetura
- Monólito modular organizado por domínios de negócio.
- Cada domínio possui seu próprio módulo Maven.
- Os módulos devem manter responsabilidades e fronteiras bem definidas.
- Utilização do padrão arquitetural MVC.
- Separação de responsabilidades entre Controller, Service e Repository.
- PostgreSQL como banco de dados.
- Cada domínio possui seu próprio tratamento de exceções.
- A comunicação entre módulos deve respeitar suas respectivas responsabilidades.

### Domínios:
- Cliente
- Estoque
- Financeiro
- Logística
- Pedido
- Produto

Exemplo da estrutura esperada:
```text
OrderFlow/
├── OrderFlow-Cliente/
├── OrderFlow-Estoque/
├── OrderFlow-Financeiro/
├── OrderFlow-Logistica/
├── OrderFlow-Pedido/
└── OrderFlow-Produto/
```
---
## Especificações

- Cada domínio deve possuir sua própria especificação funcional.
- A especificação deve estar localizada dentro do respectivo módulo.
- Antes de implementar ou alterar uma funcionalidade, consultar a especificação correspondente ao domínio.
- As regras definidas na especificação devem ser respeitadas durante a implementação.
- Não implementar funcionalidades que não estejam especificadas.
- Caso uma alteração necessária não esteja especificada, sinalizar a necessidade de atualização da especificação antes da implementação.

Exemplo:
```text
OrderFlow-Produto/
├── specs/
│   └── produto.md
```

---
## Princípios de desenvolvimento
- Seguir princípios de *Clean Code*.
- Manter responsabilidades bem definidas.
- A aplicação disponibiliza APIs RESTful.
  - Endpoints de consulta que retornem listas devem utilizar paginação com `Pageable`.
- *Controllers* devem lidar com responsabilidades relacionadas à camada HTTP (Request/Response).
  - Não devem conter regras de negócio.
- *Services* devem ser responsáveis pela implementação das regras de negócio e pela orquestração das operações necessárias para executá-las.
  - Utilizar `@Transactional` em operações de serviço que realizam escrita ou alteração de dados.  
  - Utilizar `@Transactional(readOnly = true)` em operações de serviço que realizam somente leitura.
- *Repositories* devem ser responsáveis pelo acesso e persistência dos dados.
  - Não devem conter regras de negócio.
- Utilizar DTOs para entrada e saída das APIs.
- Não expor entidades JPA diretamente como contratos da API.
- Escrever testes automatizados para as regras de negócio.
- **Não implemente funcionalidades que não tenham sido especificadas.**

---
### Tratamento de exceções
- O tratamento de exceções deve ser realizado dentro de cada domínio.
- Cada domínio deve possuir seu próprio `GlobalExceptionHandler`.
- O `GlobalExceptionHandler` deve tratar somente exceções relacionadas ao seu próprio domínio.
- Não criar um `GlobalExceptionHandler` compartilhado entre os domínios.
- Exceções não devem ser tratadas diretamente nos Controllers.
- Exceções de negócio devem ser representadas por exceções específicas.
- O tratamento das exceções deve converter as exceções em respostas HTTP apropriadas.
- Todas as respostas de erro da API devem seguir um contrato padronizado.
- O contrato de erro deve possuir, no mínimo:
  - Data e hora da ocorrência.
  - Status HTTP.
  - Mensagem descritiva do erro.
  - Lista de erros relacionados a campos, quando aplicável.
- Erros relacionados à validação de campos devem identificar o campo que originou o erro e apresentar uma mensagem correspondente.
- Exceções inesperadas devem possuir um tratamento genérico.
- Não expor stack trace, detalhes internos da aplicação ou informações sensíveis nas respostas da API.
- Quando já existir um contrato padronizado para respostas de erro, novas exceções devem utilizá-lo em vez de criar novos formatos de resposta.

---
## Testes
- Utilizar *JUnit* para testes automatizados.
- Utilizar *Mockito* quando necessário.
- Testar principalmente regras de negócio.
- Novas funcionalidades devem possuir testes.
- Alterações em funcionalidades existentes devem atualizar os testes afetados.

---
## Postman
- Criar e manter uma collection do Postman denominada `OrderFlow Collection Postman` para simulação dos endpoints disponibilizados pela aplicação.
- A collection deve ser organizada em folders, sendo um folder para cada domínio da aplicação.
- Cada endpoint deve ser incluído no folder correspondente ao seu domínio.
- Os folders devem utilizar o mesmo nome dos respectivos domínios da aplicação.
- A collection deve ser armazenada na pasta `docs/` na raiz do projeto.

---
## Uso de IA

- A IA deve analisar a arquitetura existente antes de propor alterações.
- Consultar a especificação correspondente antes de implementar ou alterar uma funcionalidade.
- A IA não deve criar funcionalidades não especificadas.
- A IA não deve alterar decisões arquiteturais sem justificar a necessidade.
- Alterações que afetem múltiplos módulos devem considerar os impactos entre eles.
- Em caso de ambiguidade ou conflito entre requisitos, não assumir uma decisão silenciosamente.
- Preferir alterações pequenas e alinhadas à arquitetura existente.