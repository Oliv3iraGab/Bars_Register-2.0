var API_BASE = '';

function formatCurrencyBRL(v) {
  return Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function toISODate(d) {
  var year = d.getFullYear();
  var month = String(d.getMonth() + 1).padStart(2, '0');
  var day = String(d.getDate()).padStart(2, '0');
  return year + '-' + month + '-' + day;
}

function fetchDailyTotals(inicioISO, fimISO) {
  var url = API_BASE + '/api/reports/daily?inicio=' + inicioISO + '&fim=' + fimISO;
  return fetch(url)
    .then(function (resp) {
      if (!resp.ok) throw new Error('Falha ao obter relatório diário');
      return resp.json();
    })
    .then(function (data) {
      var entries = Object.entries(data).sort(function (a, b) { return new Date(a[0]) - new Date(b[0]); });
      return entries.map(function (pair) { return { date: pair[0], total: Number(pair[1]) }; });
    });
}

function fetchSales() {
  return fetch(API_BASE + '/api/sales')
    .then(function (resp) {
      if (!resp.ok) throw new Error('Falha ao obter vendas');
      return resp.json();
    });
}

function renderTotais(totalVendas, totalItens) {
  document.getElementById('vendasTotais').innerHTML = '<strong>' + formatCurrencyBRL(totalVendas) + '</strong>';
  document.getElementById('itensVendidos').innerHTML = '<strong>' + totalItens + '</strong>';
}

function createTooltip() {
  var el = document.getElementById('dash-tooltip');
  if (!el) {
    el = document.createElement('div');
    el.id = 'dash-tooltip';
    el.className = 'tooltip';
    el.setAttribute('role', 'tooltip');
    el.setAttribute('aria-hidden', 'true');
    document.body.appendChild(el);
  }
  return el;
}

function showTooltip(el, text) {
  var tt = createTooltip();
  tt.textContent = text;
  tt.setAttribute('aria-hidden', 'false');
  tt.classList.add('show');
  var rect = el.getBoundingClientRect();
  var padding = 8;
  var x = rect.left + (rect.width / 2);
  var y = rect.top - padding;
  var ttRect = tt.getBoundingClientRect();
  var left = Math.max(8, Math.min(x - ttRect.width / 2, window.innerWidth - ttRect.width - 8));
  var top = Math.max(8, y - ttRect.height);
  tt.style.left = left + 'px';
  tt.style.top = top + 'px';
  el.setAttribute('aria-describedby', 'dash-tooltip');
}

function hideTooltip(el) {
  var tt = document.getElementById('dash-tooltip');
  if (!tt) return;
  tt.classList.remove('show');
  tt.setAttribute('aria-hidden', 'true');
  el.removeAttribute('aria-describedby');
}

// Substitui a criação das barras para incluir tooltip e acessibilidade
function renderGrafico(dadosDiarios) {
  var grafico = document.getElementById('grafico');
  grafico.innerHTML = '';
  var max = Math.max.apply(null, dadosDiarios.map(function (d) { return d.total; }).concat([1]));
  dadosDiarios.forEach(function (obj) {
    var total = obj.total;
    var bar = document.createElement('div');
    bar.className = 'bar';
    bar.setAttribute('tabindex', '0');
    bar.setAttribute('role', 'img');
    bar.setAttribute('aria-label', 'Vendas: ' + formatCurrencyBRL(total));
    var altura = Math.max(10, Math.round((total / max) * 120));
    bar.style.height = altura + 'px';
    // Remover title padrão e usar tooltip custom
    // bar.title = formatCurrencyBRL(total);
    bar.dataset.tooltip = formatCurrencyBRL(total);

    bar.addEventListener('mouseenter', function () { showTooltip(bar, bar.dataset.tooltip); });
    bar.addEventListener('mouseleave', function () { hideTooltip(bar); });
    bar.addEventListener('focus', function () { showTooltip(bar, bar.dataset.tooltip); });
    bar.addEventListener('blur', function () { hideTooltip(bar); });

    grafico.appendChild(bar);
  });
}

function loadDashboard() {
  var fim = new Date();
  var inicio = new Date(fim);
  inicio.setDate(fim.getDate() - 29);
  var inicioISO = toISODate(inicio);
  var fimISO = toISODate(fim);

  Promise.all([
    fetchDailyTotals(inicioISO, fimISO),
    fetchSales(),
  ])
    .then(function (arr) {
      var dadosDiarios = arr[0];
      var vendas = arr[1];
      var totalVendas = dadosDiarios.reduce(function (acc, cur) { return acc + cur.total; }, 0);
      var totalItens = Array.isArray(vendas)
        ? vendas.reduce(function (acc, v) { return acc + (Array.isArray(v.itens) ? v.itens.reduce(function (a, i) { return a + (i.quantidade || 0); }, 0) : 0); }, 0)
        : 0;
      renderTotais(totalVendas, totalItens);
      renderGrafico(dadosDiarios);
    })
    .catch(function (err) {
      document.getElementById('vendasTotais').innerHTML = '<strong>Falha ao carregar</strong>';
      document.getElementById('itensVendidos').innerHTML = '<strong>Falha ao carregar</strong>';
      console.error(err);
    });
}

loadDashboard();