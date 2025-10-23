// Dados mock para demonstrar gráfico e totais
const dadosMensais = [1200, 980, 1500, 1750, 2200, 1990, 2400, 1800, 2100, 2600, 3000, 2800];
const itensVendidos = [30, 25, 40, 42, 55, 48, 60, 39, 51, 66, 80, 74];

function formatCurrencyBRL(v) {
  return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function renderTotais() {
  const total = dadosMensais.reduce((a, b) => a + b, 0);
  document.getElementById('vendasTotais').innerHTML = `<strong>${formatCurrencyBRL(total)}</strong>`;
  const totalItens = itensVendidos.reduce((a, b) => a + b, 0);
  document.getElementById('itensVendidos').innerHTML = `<strong>${totalItens}</strong>`;
}

function renderGrafico() {
  const grafico = document.getElementById('grafico');
  grafico.innerHTML = '';
  dadosMensais.forEach((valor) => {
    const bar = document.createElement('div');
    bar.className = 'bar';
    const altura = Math.max(10, Math.round(valor / 50));
    bar.style.height = `${altura}px`;
    bar.title = `${formatCurrencyBRL(valor)}`;
    grafico.appendChild(bar);
  });
}

renderTotais();
renderGrafico();