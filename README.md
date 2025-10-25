# Bars Register Refatorado

Projeto com separação de camadas e princípios SOLID aplicado ao domínio de vendas para bares.

## Arquitetura
- `domain`: entidades de negócio (`Usuario`, `Produto`, `Venda`, `ItemVenda`).
- `application`: serviços e interfaces de repositório (`ProdutoService`, `UsuarioService`, `VendaService`, `RelatorioService`).
- `web`: UI estática (HTML/CSS/JS) para navegação, PDV, dashboard e gestão de produtos.
- `docs`: documentação de usuário, técnica e instruções de instalação.

## Requisitos
- Java 17+
- Maven 3.9+
- Navegador moderno (Chrome, Edge, Firefox ou Safari)

## Como construir e testar
```sh
mvn -q clean package
mvn -q test
```
- Testes cobrem lógica de cálculo de `ItemVenda` e `Venda`.
- Serviços estão prontos para integração com repositórios (infraestrutura futura).

## Como executar a UI (frontend)
A UI é estática e pode ser aberta diretamente:
- Abra `web/index.html` no navegador.
- Navegação disponível: `Início`, `Produtos`, `Vendas (POS)`, `Dashboard`, `Entrar`.

Opcional (servidor estático):
- Com Node instalado: `npx serve web` e acesse `http://localhost:3000`.

## Funcionalidades
- Produtos: busca, adição (mock), tabela com ações.
- PDV: adicionar/remover itens, ajustar quantidade, finalizar/cancelar venda (mock), cálculo de total.
- Dashboard: totais e gráfico de vendas (mock).
- Acessibilidade: skip-link, foco visível, ARIA em navegação e seções.
- Responsividade: grids e layout ajustável (breakpoint 900px).

## Próximas etapas
- Implementar repositórios concretos (ex.: em memória/JPA) e conexão com serviços.
- Integrar backend (REST ou Swing) para persistência real.
- Expandir cobertura de testes para serviços (`ProdutoService`, `VendaService`, `RelatorioService`, `UsuarioService`).

## Documentação
Consulte `docs/` para:
- Manual do Usuário
- Instalação e Configuração
- Arquitetura Técnica
