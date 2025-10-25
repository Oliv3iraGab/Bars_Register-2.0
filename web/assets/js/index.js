(function () {
  const recentSalesEl = document.getElementById("recentSales");
  const recentFeedbackEl = document.getElementById("recentSalesFeedback");
  const filtroPeriodo = document.getElementById("filtroPeriodo");
  const ordenarPor = document.getElementById("ordenarPor");
  const ordem = document.getElementById("ordem");

  const chartEl = document.getElementById("weeklyChart");
  const metricTotal = document.getElementById("metricTotal");
  const metricTicket = document.getElementById("metricTicket");
  const metricComparacao = document.getElementById("metricComparacao");
  const trendArrow = document.getElementById("trendArrow");
  const trendValue = document.getElementById("trendValue");
  const busyDayEl = document.getElementById("busyDay");

  function fmtBRL(n) { try { return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL'}).format(n); } catch { return `R$ ${Number(n||0).toFixed(2)}`; } }
  function isoToDDMM(iso) {
    try { const d = new Date(iso); return d.toLocaleDateString('pt-BR', { day:'2-digit', month:'2-digit'}); } catch { return iso; }
  }

  async function carregarVendas() {
    if (!recentSalesEl) return;
    recentSalesEl.setAttribute('aria-busy', 'true');
    if (recentFeedbackEl) recentFeedbackEl.textContent = '';
    recentSalesEl.innerHTML = '';
    const params = new URLSearchParams({
      period: filtroPeriodo?.value || 'week',
      sort: ordenarPor?.value || 'date',
      order: ordem?.value || 'desc',
      limit: '10'
    });
    try {
      const res = await fetch(`/api/vendas?${params.toString()}`, { headers: { 'Accept': 'application/json' } });
      if (!res.ok) throw new Error(`Status ${res.status}`);
      const data = await res.json();
      const itens = Array.isArray(data?.items) ? data.items : [];
      if (itens.length === 0) {
        if (recentFeedbackEl) recentFeedbackEl.textContent = 'Sem vendas no período selecionado.';
        return;
      }
      const frag = document.createDocumentFragment();
      itens.forEach(v => {
        const card = document.createElement('div');
        card.className = 'card';
        card.style.boxShadow = 'var(--shadow)';
        const produtosResumo = (Array.isArray(v.itens) ? v.itens : []).map(i => `${i.produto} x${i.qtd}`).join(', ');
        card.innerHTML = `
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
            <div><strong>${isoToDDMM(v.data)}</strong></div>
            <div style="font-weight:700;">${fmtBRL(v.total)}</div>
          </div>
          <div style="color:var(--muted); font-size:0.9rem;">${produtosResumo || '—'}</div>
          <div style="margin-top:8px; display:flex; gap:8px; align-items:center;">
            <span class="btn btn-outline" style="pointer-events:none;">${v.tipoPagamento || '—'}</span>
          </div>
        `;
        frag.appendChild(card);
      });
      recentSalesEl.appendChild(frag);
    } catch (err) {
      console.error('Falha carregar vendas', err);
      if (recentFeedbackEl) recentFeedbackEl.textContent = 'Não foi possível carregar vendas recentes.';
    } finally {
      recentSalesEl.setAttribute('aria-busy', 'false');
    }
  }

  async function carregarDashboard() {
    if (!chartEl) return;
    chartEl.innerHTML = '';
    try {
      const period = filtroPeriodo?.value || 'week';
      const res = await fetch(`/api/dashboard?period=${period}`, { headers: { 'Accept': 'application/json' } });
      if (!res.ok) throw new Error(`Status ${res.status}`);
      const data = await res.json();
      const dias = Array.isArray(data?.dias) ? data.dias : [];
      const maxTotal = dias.reduce((m, d) => Math.max(m, Number(d.total||0)), 0) || 1;
      dias.forEach(d => {
        const bar = document.createElement('div');
        bar.className = 'bar';
        const h = Math.max(4, Math.round((Number(d.total||0) / maxTotal) * 200));
        bar.style.height = `${h}px`;
        bar.title = `${d.data} — ${fmtBRL(d.total)} (${d.count} vendas)`;
        chartEl.appendChild(bar);
      });
      if (metricTotal) { const sp = metricTotal.querySelector('span'); if (sp) sp.textContent = fmtBRL(Number(data?.totalPeriodo||0)); }
      if (metricTicket) { const sp = metricTicket.querySelector('span'); if (sp) sp.textContent = fmtBRL(Number(data?.ticketMedio||0)); }
      const pct = Number(data?.comparacaoPercent || 0);
      if (trendValue) trendValue.textContent = `${pct.toFixed(1)}%`;
      if (pct > 0) { if (trendArrow) { trendArrow.textContent = '↑'; trendArrow.style.color = 'var(--success)'; } if (trendValue) trendValue.style.color = 'var(--success)'; }
      else if (pct < 0) { if (trendArrow) { trendArrow.textContent = '↓'; trendArrow.style.color = 'var(--danger)'; } if (trendValue) trendValue.style.color = 'var(--danger)'; }
      else { if (trendArrow) { trendArrow.textContent = '→'; trendArrow.style.color = 'var(--muted)'; } if (trendValue) trendValue.style.color = 'var(--muted)'; }
      if (busyDayEl) {
        const dm = data?.diaMaisMovimento;
        busyDayEl.textContent = dm ? `${dm.data} — ${fmtBRL(dm.total)} (${dm.count} vendas)` : '—';
      }
    } catch (err) {
      console.error('Falha carregar dashboard', err);
      chartEl.innerHTML = '<div class="form-error">Erro ao carregar gráfico.</div>';
    }
  }

  // Eventos e auto-refresh
  filtroPeriodo?.addEventListener('change', () => { carregarVendas(); carregarDashboard(); });
  ordenarPor?.addEventListener('change', carregarVendas);
  ordem?.addEventListener('change', carregarVendas);

  carregarVendas();
  carregarDashboard();
  setInterval(() => { carregarVendas(); carregarDashboard(); }, 30000);
})();