var API_BASE = '';
var MAX_RESULTS = 10; // limite máximo de itens na combobox
var DEBOUNCE_MS = 150; // tempo de espera para filtrar, mantém UI responsiva

var carrinho = [];
var productsCache = [];
var debounceTimer = null;

function formatCurrencyBRL(v) {
  return (typeof v === 'number' ? v : Number(v)).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function loadProductsCache() {
  return fetch(API_BASE + '/api/products')
    .then(function (resp) {
      if (!resp.ok) throw new Error('Falha ao carregar produtos');
      return resp.json();
    })
    .then(function (data) {
      productsCache = data || [];
    })
    .catch(function (err) {
      console.error(err);
      productsCache = [];
    });
}

function renderCarrinho() {
  var tbody = document.getElementById('carrinhoBody');
  tbody.innerHTML = '';
  var total = 0;
  carrinho.forEach(function (item, idx) {
    var subtotal = item.preco * item.qtd;
    total += subtotal;
    var tr = document.createElement('tr');
    tr.innerHTML =
      '<td>' + item.nome + '</td>' +
      '<td>' + formatCurrencyBRL(item.preco) + '</td>' +
      '<td>' + item.qtd + '</td>' +
      '<td>' + formatCurrencyBRL(subtotal) + '</td>' +
      '<td class="table-actions">' +
      '<button class="btn btn-outline" data-acao="inc" data-idx="' + idx + '">+1</button>' +
      '<button class="btn btn-outline" data-acao="dec" data-idx="' + idx + '">-1</button>' +
      '<button class="btn btn-danger" data-acao="rem" data-idx="' + idx + '">Remover</button>' +
      '</td>';
    tbody.appendChild(tr);
  });
  document.getElementById('total').innerHTML = '<strong>' + formatCurrencyBRL(total) + '</strong>';
}

function handleAcoes(e) {
  var btn = e.target.closest('button');
  if (!btn) return;
  var idx = Number(btn.dataset.idx);
  var acao = btn.dataset.acao;
  if (!Number.isInteger(idx)) return;
  if (acao === 'inc') carrinho[idx].qtd += 1;
  if (acao === 'dec') {
    carrinho[idx].qtd -= 1;
    if (carrinho[idx].qtd <= 0) carrinho.splice(idx, 1);
  }
  if (acao === 'rem') carrinho.splice(idx, 1);
  renderCarrinho();
}

function addItemById(produtoId, qtd) {
  var feedback = document.getElementById('acaoFeedback');
  fetch(API_BASE + '/api/products/' + produtoId)
    .then(function (resp) {
      if (!resp.ok) throw new Error('Produto não encontrado');
      return resp.json();
    })
    .then(function (produto) {
      carrinho.push({ produtoId: Number(produtoId), nome: produto.nome, preco: Number(produto.preco), qtd: Number(qtd) });
      renderCarrinho();
      feedback.textContent = '';
      // Limpeza automática do campo de busca (Nome do Produto)
      var produtoSearch = document.getElementById('produtoSearch');
      var produtoIdHidden = document.getElementById('produtoId');
      var dropdown = document.getElementById('produtoDropdown');
      produtoSearch.value = '';
      produtoIdHidden.value = '';
      if (dropdown) dropdown.style.display = 'none';
      // Manter foco no campo para próxima entrada
      produtoSearch.focus();
    })
    .catch(function () {
      feedback.textContent = 'Falha ao adicionar item. Selecione um produto válido.';
    });
}

function finalizarVenda() {
  var feedback = document.getElementById('acaoFeedback');
  if (carrinho.length === 0) {
    feedback.textContent = 'Carrinho vazio. Adicione itens antes de finalizar.';
    return;
  }
  var tipoPagamento = document.getElementById('tipoPagamento').value || 'DINHEIRO';
  var payload = {
    itens: carrinho.map(function (i) { return { produtoId: i.produtoId, quantidade: i.qtd }; }),
    tipoPagamento: tipoPagamento,
  };
  fetch(API_BASE + '/api/sales', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
    .then(function (resp) {
      if (!resp.ok) {
        var msg = resp.status === 422 ? 'Dados inválidos.' : 'Falha ao registrar venda.';
        throw new Error(msg);
      }
      return resp.json();
    })
    .then(function (venda) {
      feedback.textContent = 'Venda #' + venda.id + ' registrada com sucesso! Total: ' + formatCurrencyBRL(Number(venda.total));
      carrinho.splice(0, carrinho.length);
      renderCarrinho();
    })
    .catch(function (err) {
      feedback.textContent = err.message || 'Erro ao finalizar venda.';
    });
}

function cancelarVenda() {
  carrinho.splice(0, carrinho.length);
  renderCarrinho();
  document.getElementById('acaoFeedback').textContent = 'Venda cancelada.';
}

function rankAndFilterProducts(query) {
  var q = String(query || '').trim().toLowerCase();
  if (!q) return [];

  var matches = [];
  for (var i = 0; i < productsCache.length; i++) {
    var p = productsCache[i];
    var nome = String(p.nome || '').toLowerCase();
    if (!nome) continue;
    var bucket = 3; // 0: exato, 1: prefixo, 2: contém, 3: nenhum
    if (nome === q) bucket = 0;
    else if (nome.indexOf(q) === 0) bucket = 1;
    else if (nome.indexOf(q) >= 0) bucket = 2;
    if (bucket < 3) {
      matches.push({ bucket: bucket, nome: p.nome, preco: p.preco, id: p.id });
    }
  }

  matches.sort(function (a, b) {
    if (a.bucket !== b.bucket) return a.bucket - b.bucket;
    if (a.nome.length !== b.nome.length) return a.nome.length - b.nome.length;
    return a.nome.localeCompare(b.nome, 'pt-BR', { sensitivity: 'accent' });
  });

  return matches.slice(0, MAX_RESULTS);
}

function renderDropdown(options) {
  var dropdown = document.getElementById('produtoDropdown');
  dropdown.innerHTML = '';
  if (!options.length) {
    dropdown.style.display = 'none';
    return;
  }
  for (var i = 0; i < options.length; i++) {
    var opt = options[i];
    var o = document.createElement('option');
    o.value = String(opt.id);
    o.textContent = opt.nome + ' (' + formatCurrencyBRL(opt.preco) + ')';
    dropdown.appendChild(o);
  }
  dropdown.style.display = 'block';
}

function onSearchInput() {
  var input = document.getElementById('produtoSearch');
  var produtoIdHidden = document.getElementById('produtoId');
  produtoIdHidden.value = '';
  var q = input.value;
  var results = rankAndFilterProducts(q);
  renderDropdown(results);
}

function onDropdownChange() {
  var dropdown = document.getElementById('produtoDropdown');
  var produtoIdHidden = document.getElementById('produtoId');
  var produtoSearch = document.getElementById('produtoSearch');
  var selectedId = dropdown.value;
  if (!selectedId) return;
  var selected = productsCache.find(function (p) { return String(p.id) === String(selectedId); });
  if (selected) {
    produtoIdHidden.value = String(selected.id);
    produtoSearch.value = selected.nome;
  }
  dropdown.style.display = 'none';
}

function bindUI() {
  var searchInput = document.getElementById('produtoSearch');
  var dropdown = document.getElementById('produtoDropdown');
  var btnAdd = document.getElementById('btnAdd');

  searchInput.addEventListener('input', function () {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(onSearchInput, DEBOUNCE_MS);
  });

  dropdown.addEventListener('change', onDropdownChange);

  btnAdd.addEventListener('click', function () {
    var produtoId = Number(document.getElementById('produtoId').value);
    var qtd = Number(document.getElementById('qtdItem').value);
    if (!produtoId || produtoId < 1 || !qtd || qtd < 1) {
      document.getElementById('acaoFeedback').textContent = 'Selecione um produto válido e informe a quantidade.';
      return;
    }
    addItemById(produtoId, qtd);
  });

  document.getElementById('carrinhoBody').addEventListener('click', handleAcoes);
  document.getElementById('btnFinalizar').addEventListener('click', finalizarVenda);
  document.getElementById('btnCancelar').addEventListener('click', cancelarVenda);
}

(function init() {
  loadProductsCache().then(function () {
    bindUI();
    renderCarrinho();
  });
})();