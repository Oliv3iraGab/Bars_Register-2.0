# Arquitetura Técnica

## Visão Geral
Arquitetura em camadas com foco em domínio e serviços, preparada para futura integração com persistência e UI dinâmica.

## Camadas
- `domain`: classes de modelo puras (POJOs) – `Produto`, `Venda`, `ItemVenda`, `Usuario`.
- `application`: regras de negócio e contratos – `ProdutoService`, `VendaService`, `UsuarioService`, `RelatorioService`; interfaces `ProdutoRepository`, `VendaRepository`, `UsuarioRepository`.
- `web`: UI estática (HTML/CSS/JS) consumindo dados mock.

## Serviços
- `ProdutoService`: cadastro, atualização, remoção, busca e ajuste de estoque.
- `VendaService`: registro de venda com validação de estoque e cálculo de total.
- `UsuarioService`: criação e autenticação (contrato).
- `RelatorioService`: agregação de totais por dia.

## Persistência (Futuro)
- Implementar repositórios concretos (em memória / JPA) para `Produto`, `Venda`, `Usuario`.
- Opção JPA/Hibernate: adicionar dependências e mapeamentos; configurar fonte de dados.

## Testes
- Cobertura atual: `domain` (`ItemVendaTest`, `VendaTest`).
- Recomenda-se testes de `application` com repositórios fake ou mocks.

## Padrões e Qualidade
- Princípios SOLID e separação de responsabilidades.
- Facilita testes unitários e evolução do sistema.

## Integração com Backend
- Alternativas: REST (Spring Boot) ou UI Swing existente.
- Contratos de repositório permitem troca de implementação sem afetar serviços.
