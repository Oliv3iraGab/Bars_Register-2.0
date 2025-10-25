# Documentação Técnica (Resumo) — Funções do Bar's Register

Este documento resume as principais funções descritas no arquivo "Documentação Bar's Register.md", com foco em assinaturas, parâmetros, retornos, comportamento e exemplos.

## Organização por Módulo
- Produtos
- Vendas (POS)
- Estoque
- Relatórios
- Usuários
- Armazenamento de Registros

## Funções Principais

### Produtos
- **CadastrarProduto**  
  Assinatura: `CadastrarProduto(nome:String, preco:Decimal, categoria:String, codigoBarras?:String, estoqueInicial:Int): Int`  
  Comportamento: valida campos, evita duplicidade (código de barras), persiste produto e cria estoque inicial.  
  Exemplo: `CadastrarProduto("Cerveja IPA", 15.90, "Bebidas", "789...", 48) → 1027`

- **AtualizarProduto**  
  Assinatura: `AtualizarProduto(id:Int, nome?:String, preco?:Decimal, categoria?:String, codigoBarras?:String): Boolean`  
  Comportamento: atualiza parcialmente, valida duplicidades e registra auditoria.  
  Exemplo: `AtualizarProduto(1027, preco=16.90) → true`

- **RemoverProduto**  
  Assinatura: `RemoverProduto(id:Int): Boolean`  
  Comportamento: valida vínculos com vendas; inativa ou remove com ajuste de estoque.  
  Exemplo: `RemoverProduto(1027) → true`

### Vendas (POS)
- **RegistrarVenda**  
  Assinatura: `RegistrarVenda(itens:Lista<ItemVenda>, formaPagamento:String, clienteId?:Int, aplicarPromocoes:Boolean=true): Int`  
  Comportamento: valida estoque, aplica combos, calcula totais, persiste venda+itens (transação) e baixa estoque.  
  Exemplo: `RegistrarVenda([{produtoId:1027, quantidade:2, precoUnitario:15.90}], "pix", null, true) → 88534`

- **CancelarVenda**  
  Assinatura: `CancelarVenda(vendaId:Int, motivo:String): Boolean`  
  Comportamento: valida janela de cancelamento, reverte estoque e registra auditoria.  
  Exemplo: `CancelarVenda(88534, "Pedido incorreto") → true`

- **AplicarCombo**  
  Assinatura: `AplicarCombo(itens:Lista<ItemVenda>, regras:Lista<RegraCombo>): Lista<ItemVenda>`  
  Comportamento: aplica regras (ex.: leve 3 pague 2) sem duplicidade e recalcula totais.  
  Exemplo: `AplicarCombo(itens, regras) → itensAtualizados`

- **CalcularTotal**  
  Assinatura: `CalcularTotal(itens:Lista<ItemVenda>, taxas:Lista<Taxa>, descontos:Lista<Desconto>): Decimal`  
  Comportamento: soma subtotais, aplica descontos e taxas na ordem definida.  
  Exemplo: `CalcularTotal(itens, taxas, descontos) → 78.50`

### Estoque
- **AjustarEstoque**  
  Assinatura: `AjustarEstoque(produtoId:Int, quantidade:Int, motivo:String): Boolean`  
  Comportamento: aplica delta (+/-) com validação de saldo e auditoria.  
  Exemplo: `AjustarEstoque(1027, -5, "Quebra") → true`

- **ReservarEstoque**  
  Assinatura: `ReservarEstoque(itens:Lista<ItemVenda>): Boolean`  
  Comportamento: reserva para evitar sobre-venda; falha se insuficiente.  
  Exemplo: `ReservarEstoque([{produtoId:1027, quantidade:2}]) → true`

- **LiberarEstoque**  
  Assinatura: `LiberarEstoque(itens:Lista<ItemVenda>): Boolean`  
  Comportamento: desfaz reservas quando a venda é cancelada/expira.  
  Exemplo: `LiberarEstoque([{produtoId:1027, quantidade:2}]) → true`

- **ConsultarEstoque**  
  Assinatura: `ConsultarEstoque(produtoId:Int): Int`  
  Comportamento: retorna quantidade disponível considerando reservas ativas.  
  Exemplo: `ConsultarEstoque(1027) → 41`

### Relatórios
- **GerarRelatorioVendas**  
  Assinatura: `GerarRelatorioVendas(periodo:IntervaloData, agrupamento:String='dia'): RelatorioVendas`  
  Comportamento: agrega métricas (total, ticket médio, itens) e suporta exportação.  
  Exemplo: `GerarRelatorioVendas({inicio:"2025-10-01", fim:"2025-10-31"}, "dia") → RelatorioVendas`

- **ListarProdutosMaisVendidos**  
  Assinatura: `ListarProdutosMaisVendidos(periodo:IntervaloData, limite:Int=10): Lista<ItemResumo>`  
  Comportamento: ranqueia por quantidade/receita.  
  Exemplo: `ListarProdutosMaisVendidos({inicio:"2025-10-01", fim:"2025-10-31"}, 5) → [ItemResumo...]`

### Usuários
- **CriarUsuario**  
  Assinatura: `CriarUsuario(nome:String, email:String, senhaHash:String, perfil:String): Int`  
  Comportamento: valida email único, persiste e aplica política de perfis.  
  Exemplo: `CriarUsuario("Ana", "ana@example.com", "<hash>", "ATENDENTE") → 501`

- **AutenticarUsuario**  
  Assinatura: `AutenticarUsuario(email:String, senha:String): String`  
  Comportamento: verifica hash de senha, emite token/sessão e registra auditoria.  
  Exemplo: `AutenticarUsuario("ana@example.com", "senha123") → "eyJhbGciOi..."`

### Armazenamento de Registros
- **SalvarRegistro**  
  Assinatura: `SalvarRegistro(tipo:String, payload:JSON, timestamp:DateTime): Int`  
  Comportamento: persiste com schema de auditoria e acesso fácil às informações.  
  Exemplo: `SalvarRegistro("VENDA", {...}, "2025-10-25T10:34:12Z") → 900123`

- **ExportarRegistros**  
  Assinatura: `ExportarRegistros(formato:String='csv', periodo:IntervaloData, tipo?:String): Arquivo`  
  Comportamento: gera arquivo para auditoria/compliance com metadados.  
  Exemplo: `ExportarRegistros("csv", {inicio:"2025-10-01", fim:"2025-10-31"}, "VENDA") → "/exports/vendas-out-2025.csv"`

- **BackupBaseDados**  
  Assinatura: `BackupBaseDados(destino:Path): Boolean`  
  Comportamento: snapshot consistente com verificação de espaço e hash de integridade.  
  Exemplo: `BackupBaseDados("C:\\Backups\\barsdb-2025-10-31.zip") → true`

- **RestaurarBaseDados**  
  Assinatura: `RestaurarBaseDados(origem:Path): Boolean`  
  Comportamento: restaura atomicamente, valida versão/schema e reindexa.  
  Exemplo: `RestaurarBaseDados("C:\\Backups\\barsdb-2025-10-31.zip") → true`

## Fluxos (Resumo)
- **RegistrarVenda**: validar itens/estoque → aplicar combos → calcular totais → persistir → baixar estoque.
- **CadastrarProduto**: validar obrigatórios → checar duplicidade → persistir → inicializar estoque.

## Requisitos de Sistema (Resumo)
- Java 17; Maven 3.9+; H2 (arquivo `./data/barsdb`); navegador moderno para `web/`.
- Windows/Linux/macOS com permissões de escrita no diretório de dados.

## Dependências Externas (Resumo)
- Lombok (provided), Jakarta Persistence, Hibernate Core, H2, JUnit Jupiter.
