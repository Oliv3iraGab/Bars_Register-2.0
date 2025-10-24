(function () {
  var API_BASE = '';
  var POLL_MS = 15000; // atualização em "tempo real" por polling
  var BARS_COUNT = 5; // manter 5 barras como no modelo
  var chartEl = document.getElementById('homeChart');
  var destaquesEl = document.getElementById('destaquesLista');
  var atividadeEl = document.getElementById('atividadeLista');

  function formatCurrencyBRL(v) {
    var n = typeof v === 'number' ? v : Number(v || 0);
    return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  function ymd(d) {
    var yyyy = d.getFullYear();
    var mm = String(d.getMonth() + 1).padStart(2, '0');
    var dd = String(d.getDate()).padStart(2, '0');
    return yyyy + '-' + mm + '-' + dd;
  }

  function getLastNDays(n) {
    var days = [];
    var today = new Date();
    for (var i = n - 1; i >= 0; i--) {
      var d = new Date(today);
      d.setDate(today.getDate() - i);
      days.push(d);
    }
    return days;
  }

  function fetchDailyTotals(inicio, fim) {
    var url = API_BASE + '/api/reports/daily?inicio=' + inicio + '&fim=' + fim;
    return fetch(url).then(function (resp) {
      if (!resp.ok) throw new Error('Falha ao carregar totais diários');
      return resp.json();
    });
  }

  function fetchSales() {
    return fetch(API_BASE + '/api/sales').then(function (resp) {
      if (!resp.ok) throw new Error('Falha ao carregar vendas');
      return resp.json();
    });
  }

  function renderChart(totalsByDate) {
    if (!chartEl) return;
    // Construir array de 5 dias na ordem correta
    var days = getLastNDays(BARS_COUNT);
    var valores = [];
    for (var i = 0; i < days.length; i++) {
      var key = ymd(days[i]);
      var v = totalsByDate[key];
      valores.push(typeof v === 'number' ? v : Number(v || 0));
    }
    var max = 0;
    for (var j = 0; j < valores.length; j++) {
      if (valores[j] > max) max = valores[j];
    }
    if (max <= 0) max = 1;

    // Evitar reflow excessivo: usar fragment
    var frag = document.createDocumentFragment();
    for (var k = 0; k < valores.length; k++) {
      var bar = document.createElement('div');
      bar.className = 'bar';
      // chart.css define altura 220px, barras usam 200px como base no dashboard; aqui replicamos 200px proporção
      var h = Math.round((valores[k] / max) * 200);
      bar.style.height = h + 'px';
      frag.appendChild(bar);
    }
    // Atualizar apenas se conteúdo mudou significativamente
    chartEl.innerHTML = '';
    chartEl.appendChild(frag);
  }

  function renderDestaques(vendas) {
    if (!destaquesEl) return;
    // Tally por produto
    var mapa = {}; // id -> {nome, qtd}
    for (var i = 0; i < vendas.length; i++) {
      var itens = vendas[i].itens || [];
      for (var j = 0; j < itens.length; j++) {
        var item = itens[j];
        var p = item.produto || {};
        var id = p.id;
        if (!id && id !== 0) continue;
        if (!mapa[id]) mapa[id] = { nome: p.nome || ('Produto #' + id), qtd: 0 };
        mapa[id].qtd += Number(item.quantidade || 0);
      }
    }
    var arr = [];
    for (var pid in mapa) {
      if (Object.prototype.hasOwnProperty.call(mapa, pid)) {
        arr.push({ id: pid, nome: mapa[pid].nome, qtd: mapa[pid].qtd });
      }
    }
    arr.sort(function (a, b) { return b.qtd - a.qtd; });
    arr = arr.slice(0, 3);

    var frag = document.createDocumentFragment();
    for (var k = 0; k < arr.length; k++) {
      var li = document.createElement('li');
      li.textContent = arr[k].nome + ' — ' + arr[k].qtd + ' vendidos';
      frag.appendChild(li);
    }
    destaquesEl.innerHTML = '';
    destaquesEl.appendChild(frag);
  }

  function renderAtividade(vendas) {
    if (!atividadeEl) return;
    // Ordenar por dataVenda desc
    var copia = vendas.slice();
    copia.sort(function (a, b) {
      var da = a.dataVenda ? new Date(a.dataVenda).getTime() : 0;
      var db = b.dataVenda ? new Date(b.dataVenda).getTime() : 0;
      return db - da;
    });
    var take = copia.slice(0, 5);
    var frag = document.createDocumentFragment();
    for (var i = 0; i < take.length; i++) {
      var v = take[i];
      var li = document.createElement('li');
      li.textContent = 'Venda #' + v.id + ' • Total ' + formatCurrencyBRL(v.total) + ' • ' + (v.tipoPagamento || '');
      frag.appendChild(li);
    }
    atividadeEl.innerHTML = '';
    atividadeEl.appendChild(frag);
  }

  function atualizar() {
    // Intervalo: últimos 5 dias
    var dias = getLastNDays(BARS_COUNT);
    var inicio = ymd(dias[0]);
    var fim = ymd(dias[dias.length - 1]);
    fetchDailyTotals(inicio, fim)
      .then(function (totais) {
        renderChart(totais || {});
      })
      .catch(function (err) {
        // falha: mantém chart como está
        console.error(err);
      });

    fetchSales()
      .then(function (vendas) {
        vendas = vendas || [];
        renderDestaques(vendas);
        renderAtividade(vendas);
      })
      .catch(function (err) { console.error(err); });
  }

  // Primeira carga rápida após idle para não bloquear pintura
  if (typeof window.requestIdleCallback === 'function') {
    window.requestIdleCallback(function () { atualizar(); });
  } else {
    setTimeout(atualizar, 0);
  }
  // Atualizações em tempo real por polling leve
  setInterval(atualizar, POLL_MS);
})();