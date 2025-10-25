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

## Persistência
- Tecnologia: JPA/Hibernate 6 com H2 (arquivo `./data/barsdb`) em desenvolvimento.
- Configuração: `META-INF/persistence.xml` com `transaction-type=RESOURCE_LOCAL` e `hibernate.hbm2ddl.auto=update`.
- Mapeamento: `Produto`, `Venda`, `ItemVenda`, `Usuario` anotados com `jakarta.persistence`.
- Padrão de acesso: interfaces `Repository` no módulo `application` com implementações JPA em `infrastructure.repo`.
- Transações: utilitário `JpaUtil` coordena `EntityManager` thread-local e commits/rollbacks. `VendaService.registrarVenda` executa em transação única para garantir atomicidade (atualização de estoque + persistência da venda e itens).
- Exceções: falhas de persistência são encapsuladas em `DataPersistenceException` para diagnóstico e tratamento específico.
- Compatibilidade: `AppMain` instância repositórios JPA sem alterar contratos; funcionalidades e endpoints seguem inalterados.

## Testes
- Cobertura atual: `domain` (`ItemVendaTest`, `VendaTest`).
- Recomenda-se testes de `application` com repositórios fake ou mocks.

## Padrões e Qualidade
- Princípios SOLID e separação de responsabilidades.
- Facilita testes unitários e evolução do sistema.

## Integração com Backend
- Alternativas: REST (Spring Boot) ou UI Swing existente.
- Contratos de repositório permitem troca de implementação sem afetar serviços.
