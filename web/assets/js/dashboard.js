// Dashboard consumindo API real de resumo de vendas
(function () {
  const vendasTotaisEl = document.getElementById("vendasTotais");
  const itensVendidosEl = document.getElementById("itensVendidos");
  const graficoEl = document.getElementById("grafico");

  if (!vendasTotaisEl || !itensVendidosEl) return;

  async function carregarResumo() {
    try {
      const res = await fetch("/api/dashboard", { headers: { "Accept": "application/json" } });
      if (!res.ok) throw new Error("Falha ao carregar resumo");
      const data = await res.json();
      const totalVendas = Number(data.totalPeriodo || 0);
      const totalItens = (data.dias || []).reduce((acc, d) => acc + (d.count || 0), 0);
      vendasTotaisEl.innerHTML = `<strong>R$ ${totalVendas.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}</strong>`;
      itensVendidosEl.innerHTML = `<strong>${totalItens.toLocaleString("pt-BR")}</strong>`;

      // Remove gráfico ilustrativo; poderá ser reconstruído com dados reais mensais futuramente
      if (graficoEl) {
        graficoEl.innerHTML = "";
      }
    } catch (err) {
      console.error(err);
      vendasTotaisEl.textContent = "Erro ao carregar vendas totais";
      itensVendidosEl.textContent = "Erro ao carregar itens vendidos";
      if (graficoEl) graficoEl.innerHTML = "";
    }
  }

  carregarResumo();
})();
