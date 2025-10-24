var API_BASE = '';

function formatCurrencyBRL(v) {
  return Number(v || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function toISODate(d) {
  var year = d.getFullYear();
  var month = String(d.getMonth() + 1).padStart(2, '0');
  var day = String(d.getDate()).padStart(2, '0');
  return year + '-' + month + '-' + day;
}

function parseDate(value) {
  // Backend usa LocalDateTime em ISO; aceita string Date.
  try { return new Date(value); } catch (e) { return new Date(); }
}

function fetchSales() {
  return fetch(API_BASE + '/api/sales')
    .then(function (resp) { if (!resp.ok) throw new Error('Falha ao obter vendas'); return resp.json(); });
}

function groupDailyTotals(vendas, daysCount) {
  var totals = {};
  var now = new Date();
  var lastDays = [];
  for (var i = daysCount - 1; i >= 0; i--) {
    var d = new Date(now.getFullYear(), now.getMonth(), now.getDate() - i);
    lastDays.push(toISODate(d));
  }
  for (var i2 = 0; i2 < vendas.length; i2++) {
    var v = vendas[i2];
    var dt = parseDate(v.dataVenda);
    var key = toISODate(dt);
    totals[key] = (totals[key] || 0) + (Number(v.total) || 0);
  }
  return { days: lastDays, totals: totals };
}

function createTooltip() {
  var el = document.getElementById('home-tooltip');
  if (!el) {
    el = document.createElement('div');
    el.id = 'home-tooltip';
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
  el.setAttribute('aria-describedby', 'home-tooltip');
}

function hideTooltip() {
  var tt = document.getElementById('home-tooltip');
  if (tt) { tt.setAttribute('aria-hidden', 'true'); tt.classList.remove('show'); }
}

function renderChart(days, totals) {
  var el = document.getElementById('homeChart');
  if (!el) return;
  el.innerHTML = '';
  var max = 0;
  for (var i = 0; i < days.length; i++) {
    var v = Number(totals[days[i]] || 0);
    if (v > max) max = v;
  }
  var safeMax = max > 0 ? max : 1;
  for (var j = 0; j < days.length; j++) {
    var day = days[j];
    var value = Number(totals[day] || 0);
    var hPct = Math.round((value / safeMax) * 100);
    var bar = document.createElement('div');
    bar.className = 'bar';
    bar.style.height = hPct + '%';
    bar.setAttribute('tabindex', '0');
    bar.setAttribute('aria-label', 'Vendas de ' + day + ': ' + formatCurrencyBRL(value));
    bar.addEventListener('mouseenter', (function (b, d, val) { return function () { showTooltip(b, d + ' • ' + formatCurrencyBRL(val)); }; })(bar, day, value));
    bar.addEventListener('mouseleave', hideTooltip);
    bar.addEventListener('focus', (function (b, d, val) { return function () { showTooltip(b, d + ' • ' + formatCurrencyBRL(val)); }; })(bar, day, value));
    bar.addEventListener('blur', hideTooltip);
    el.appendChild(bar);
  }
}

function topFeaturedProducts(vendas, limit) {
  var map = {};
  for (var i = 0; i < vendas.length; i++) {
    var v = vendas[i];
    var itens = v.itens || [];
    for (var j = 0; j < itens.length; j++) {
      var it = itens[j];
      var nome = it.produto && it.produto.nome ? it.produto.nome : 'Produto';
      var qtd = Number(it.quantidade || 0);
      map[nome] = (map[nome] || 0) + qtd;
    }
  }
  var arr = [];
  for (var k in map) { if (Object.prototype.hasOwnProperty.call(map, k)) arr.push({ nome: k, qtd: map[k] }); }
  arr.sort(function (a, b) { return b.qtd - a.qtd; });
  if (arr.length > limit) arr = arr.slice(0, limit);
  return arr;
}

function renderFeatured(list) {
  var ul = document.getElementById('destaquesLista');
  if (!ul) return;
  ul.innerHTML = '';
  for (var i = 0; i < list.length; i++) {
    var li = document.createElement('li');
    li.textContent = list[i].nome + ' • ' + list[i].qtd + ' un.';
    ul.appendChild(li);
  }
  if (!list.length) {
    var li2 = document.createElement('li');
    li2.textContent = 'Sem dados de vendas.';
    ul.appendChild(li2);
  }
}

function recentActivity(vendas, limit) {
  var arr = vendas.slice().sort(function (a, b) {
    var ad = parseDate(a.dataVenda).getTime();
    var bd = parseDate(b.dataVenda).getTime();
    return bd - ad;
  });
  if (arr.length > limit) arr = arr.slice(0, limit);
  return arr;
}

function renderActivity(vendas) {
  var ul = document.getElementById('atividadeLista');
  if (!ul) return;
  ul.innerHTML = '';
  for (var i = 0; i < vendas.length; i++) {
    var v = vendas[i];
    var dt = parseDate(v.dataVenda);
    var hora = dt.toLocaleString('pt-BR').replace(',', '');
    var itens = v.itens || [];
    var nomes = [];
    for (var j = 0; j < itens.length; j++) {
      var it = itens[j];
      nomes.push((it.produto && it.produto.nome) ? it.produto.nome : 'Produto');
    }
    var li = document.createElement('li');
    li.textContent = hora + ' • ' + nomes.join(', ') + ' • Total ' + formatCurrencyBRL(v.total);
    ul.appendChild(li);
  }
  if (!vendas.length) {
    var li2 = document.createElement('li');
    li2.textContent = 'Nenhuma atividade recente.';
    ul.appendChild(li2);
  }
}

function refreshHome() {
  fetchSales()
    .then(function (vendas) {
      var daily = groupDailyTotals(vendas, 5);
      renderChart(daily.days, daily.totals);
      renderFeatured(topFeaturedProducts(vendas, 5));
      renderActivity(recentActivity(vendas, 6));
    })
    .catch(function () {
      // Render placeholders sem alterar o layout
      renderChart(['Dia 1','Dia 2','Dia 3','Dia 4','Dia 5'], {});
      renderFeatured([]);
      renderActivity([]);
    });
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', refreshHome);
} else {
  refreshHome();
}

// Atualizações leves a cada 15s
setInterval(refreshHome, 15000);