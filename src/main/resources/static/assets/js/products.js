var API_BASE = '';

var produtosTbody = document.getElementById('produtosBody');
var form = document.getElementById('productForm');
var nomeInput = document.getElementById('productName');
var precoInput = document.getElementById('productPrice');
var estoqueInput = document.getElementById('productStock');
var feedback = document.getElementById('formFeedback');
var buscaInput = document.getElementById('searchInput');

var produtos = [];

function formatCurrencyBRL(v) {
  return Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function renderProdutos(lista) {
  produtosTbody.innerHTML = '';
  lista.forEach(function (p) {
    var tr = document.createElement('tr');
    tr.innerHTML =
      '<td>' + p.nome + '</td>' +
      '<td>' + formatCurrencyBRL(p.preco) + '</td>' +
      '<td>' + p.estoque + '</td>' +
      '<td class="table-actions">' +
      '<button class="btn btn-outline" data-acao="edit" data-id="' + p.id + '">Editar</button>' +
      '<button class="btn btn-danger" data-acao="del" data-id="' + p.id + '">Excluir</button>' +
      '</td>';
    produtosTbody.appendChild(tr);
  });
}

function carregarProdutos() {
  return fetch(API_BASE + '/api/products')
    .then(function (resp) {
      if (!resp.ok) throw new Error('Erro ao carregar produtos: ' + resp.status);
      return resp.json();
    })
    .then(function (data) {
      produtos = data;
      renderProdutos(produtos);
    })
    .catch(function () {
      feedback.textContent = 'Falha ao carregar produtos. Verifique o servidor.';
    });
}

form.addEventListener('submit', function (e) {
  e.preventDefault();
  var nome = nomeInput.value.trim();
  var preco = parseFloat(precoInput.value);
  var estoque = parseInt(estoqueInput.value, 10);
  if (!nome || isNaN(preco) || isNaN(estoque)) {
    feedback.textContent = 'Preencha todos os campos corretamente.';
    return;
  }
  fetch(API_BASE + '/api/products', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nome: nome, preco: preco, estoque: estoque }),
  })
    .then(function (resp) {
      if (!resp.ok) throw new Error('Erro ao criar: ' + resp.status);
      return resp.json();
    })
    .then(function (novo) {
      produtos.push(novo);
      renderProdutos(produtos);
      form.reset();
      feedback.textContent = 'Produto cadastrado com sucesso!';
    })
    .catch(function () {
      feedback.textContent = 'Falha ao cadastrar. Tente novamente.';
    });
});

produtosTbody.addEventListener('click', function (e) {
  var btn = e.target.closest('button');
  if (!btn) return;
  var id = btn.dataset.id;
  var acao = btn.dataset.acao;
  if (!id) return;
  if (acao === 'del') {
    if (!confirm('Excluir este produto?')) return;
    fetch(API_BASE + '/api/products/' + id, { method: 'DELETE' })
      .then(function (resp) {
        if (!resp.ok) throw new Error('Erro ao excluir');
        produtos = produtos.filter(function (p) { return String(p.id) !== String(id); });
        renderProdutos(produtos);
      })
      .catch(function () { alert('Falha ao excluir.'); });
  }
  if (acao === 'edit') {
    var produto = produtos.find(function (p) { return String(p.id) === String(id); });
    if (!produto) return;
    var nome = prompt('Nome do produto:', produto.nome);
    if (nome === null) return;
    var precoStr = prompt('Preço (ex: 10.50):', produto.preco);
    if (precoStr === null) return;
    var estoqueStr = prompt('Estoque:', produto.estoque);
    if (estoqueStr === null) return;
    var preco = parseFloat(precoStr);
    var estoque = parseInt(estoqueStr, 10);
    if (!nome || isNaN(preco) || isNaN(estoque)) {
      alert('Valores inválidos.');
      return;
    }
    fetch(API_BASE + '/api/products/' + id, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id: Number(id), nome: nome, preco: preco, estoque: estoque }),
    })
      .then(function (resp) {
        if (!resp.ok) throw new Error('Erro ao atualizar');
        return resp.json();
      })
      .then(function (atualizado) {
        var idx = produtos.findIndex(function (p) { return String(p.id) === String(id); });
        if (idx >= 0) produtos[idx] = atualizado;
        renderProdutos(produtos);
      })
      .catch(function () { alert('Falha ao atualizar.'); });
  }
});

buscaInput.addEventListener('input', function () {
  var q = buscaInput.value.trim().toLowerCase();
  var filtrados = produtos.filter(function (p) {
    return p.nome.toLowerCase().indexOf(q) >= 0 || String(p.preco).indexOf(q) >= 0;
  });
  renderProdutos(filtrados);
});

carregarProdutos();