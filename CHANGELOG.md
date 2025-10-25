# Changelog

## 2025-10-25 – Migração de conteúdo Dashboard ↔ Início

- Início (`index.html`): passou a exibir o conteúdo da antiga Dashboard, com blocos de resumo (Vendas Totais, Produtos Vendidos) e gráfico mensal.
- Dashboard (`dashboard.html`): recebeu o conteúdo anteriormente em Início, incluindo filtros, vendas recentes, métricas semanais e gráfico.
- Scripts:
  - `assets/js/index.js`: mantém carregamento de vendas recentes e métricas, removido o botão “Detalhar”. Proteções adicionadas para evitar erros quando elementos DOM não estão presentes.
  - `assets/js/dashboard.js`: continua operando com o novo layout do Dashboard (resumo e gráfico), consumindo `/api/dashboard`.
- Rotas/links: navegação principal preservada (`index.html`, `products.html`, `pos.html`, `dashboard.html`).
- Remoções: botão “Detalhar” retirado de todas as renderizações.
- Responsividade: mantida pelo CSS existente (`assets/css/styles.css`).
- Versionamento: commit criado com mensagem descritiva.

### Validações recomendadas
- Funcionalidades para usuários autenticados/guest/admin (quando aplicável): verificar carregamentos e interações básicas.
- Navegadores: testar em Chrome, Firefox, Safari e Edge (últimas 3 versões).
- Resoluções: 320px, 768px e 1024px.
- Performance: medir tempo de carregamento antes/depois (Lighthouse/DevTools).
- Confirmar ausência do botão “Detalhar” em todas as páginas.
