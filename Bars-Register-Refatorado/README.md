# Bars Register Refatorado

Separação de camadas e aplicação de SOLID sobre o projeto base "Bar's Register".

## Arquitetura
- `domain`: entidades de negócio (`Usuario`, `Produto`, `Venda`, `ItemVenda`).
- `application`: interfaces de repositório e serviços de negócio.
- `infrastructure`: implementações de infraestrutura (ex.: repositórios em memória para testes).
- `ui`: reservado para futura integração com Swing; aqui a UI deve apenas orquestrar serviços.

## Como executar os testes no `main()`
1. Instale Java 17+ e Maven.
2. No diretório `Bars-Register-Refatorado`, execute:
   - `mvn -q package`
   - `mvn -q exec:java -Dexec.mainClass=br.com.bars_register.AppMain`

Os testes no `main` cobrem:
- Autenticação de usuário.
- Cadastro, listagem e remoção de produtos.
- Registro de venda e atualização de estoque.
- Relatório de total por dia.

## Próxima etapa
- Integrar a UI Swing existente para usar os serviços (`ProdutoService`, `UsuarioService`, `VendaService`).
- Adicionar implementação JPA dos repositórios e configurar `persistence.xml`.

## Referência
- Projeto base: https://github.com/Oliv3iraGab/Bars-Register