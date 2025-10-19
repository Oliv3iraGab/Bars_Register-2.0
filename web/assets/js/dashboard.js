// Dashboard com dados mock: soma de vendas, itens e gráfico simples
(function () {
  // Dados mensais de vendas (mock)
  const vendasMensais = [1200, 1540, 980, 2100, 1750, 2300, 1950, 2450, 1800, 2200, 2600, 2400];
  const itensVendidosMensal = [120, 154, 98, 210, 175, 230, 195, 245, 180, 220, 260, 240];

  const totalVendas = vendasMensais.reduce((a, b) => a + b, 0);
  const totalItens = itensVendidosMensal.reduce((a, b) => a + b, 0);

  const vendasTotaisEl = document.getElementById("vendasTotais");
  const itensVendidosEl = document.getElementById("itensVendidos");
  const graficoEl = document.getElementById("grafico");

  vendasTotaisEl.innerHTML = `<strong>R$ ${totalVendas.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}</strong>`;
  itensVendidosEl.innerHTML = `<strong>${totalItens.toLocaleString("pt-BR")}</strong>`;

  // Renderiza barras do gráfico
  const max = Math.max(...vendasMensais);
  vendasMensais.forEach((v) => {
    const bar = document.createElement("div");
    bar.className = "bar";
    bar.style.height = `${Math.round((v / max) * 200)}px`;
    graficoEl.appendChild(bar);
  });
})();