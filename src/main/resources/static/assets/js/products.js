const API_BASE = ''; // usa mesma origem do backend

const produtosTbody = document.getElementById('produtosBody');
const form = document.getElementById('productForm');
const nomeInput = document.getElementById('productName');
const precoInput = document.getElementById('productPrice');
const estoqueInput = document.getElementById('productStock');
const feedback = document.getElementById('formFeedback');
const buscaInput = document.getElementById('searchInput');

let produtos = [];

function formatCurrencyBRL(v) {
  return Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function renderProdutos(lista) {
  produtosTbody.innerHTML = '';
  lista.forEach((p) => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${p.nome}</td>
      <td>${formatCurrencyBRL(p.preco)}</td>
      <td>${p.estoque}</td>
      <td class="table-actions">
        <button class="btn btn-outline" data-acao="edit" data-id="${p.id}">Editar</button>
        <button class="btn btn-danger" data-acao="del" data-id="${p.id}">Excluir</button>
      </td>
    `;
    produtosTbody.appendChild(tr);
  });
}

async function carregarProdutos() {
  try {
    const resp = await fetch(`${API_BASE}/api/products`);
    if (!resp.ok) throw new Error(`Erro ao carregar produtos: ${resp.status}`);
    const data = await resp.json();
    produtos = data;
    renderProdutos(produtos);
  } catch (err) {
    feedback.textContent = 'Falha ao carregar produtos. Verifique o servidor.';
  }
}

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  const nome = nomeInput.value.trim();
  const preco = parseFloat(precoInput.value);
  const estoque = parseInt(estoqueInput.value, 10);
  if (!nome || isNaN(preco) || isNaN(estoque)) {
    feedback.textContent = 'Preencha todos os campos corretamente.';
    return;
  }
  try {
    const resp = await fetch(`${API_BASE}/api/products`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nome, preco, estoque }),
    });
    if (!resp.ok) throw new Error(`Erro ao criar: ${resp.status}`);
    const novo = await resp.json();
    produtos.push(novo);
    renderProdutos(produtos);
    form.reset();
    feedback.textContent = 'Produto cadastrado com sucesso!';
  } catch (err) {
    feedback.textContent = 'Falha ao cadastrar. Tente novamente.';
  }
});

produtosTbody.addEventListener('click', async (e) => {
  const btn = e.target.closest('button');
  if (!btn) return;
  const id = btn.dataset.id;
  const acao = btn.dataset.acao;
  if (!id) return;
  if (acao === 'del') {
    if (!confirm('Excluir este produto?')) return;
    try {
      const resp = await fetch(`${API_BASE}/api/products/${id}`, { method: 'DELETE' });
      if (!resp.ok) throw new Error('Erro ao excluir');
      produtos = produtos.filter((p) => String(p.id) !== String(id));
      renderProdutos(produtos);
    } catch (err) {
      alert('Falha ao excluir.');
    }
  }
  if (acao === 'edit') {
    const produto = produtos.find((p) => String(p.id) === String(id));
    if (!produto) return;
    const nome = prompt('Nome do produto:', produto.nome);
    if (nome === null) return;
    const precoStr = prompt('Preço (ex: 10.50):', produto.preco);
    if (precoStr === null) return;
    const estoqueStr = prompt('Estoque:', produto.estoque);
    if (estoqueStr === null) return;
    const preco = parseFloat(precoStr);
    const estoque = parseInt(estoqueStr, 10);
    if (!nome || isNaN(preco) || isNaN(estoque)) {
      alert('Valores inválidos.');
      return;
    }
    try {
      const resp = await fetch(`${API_BASE}/api/products/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id: Number(id), nome, preco, estoque }),
      });
      if (!resp.ok) throw new Error('Erro ao atualizar');
      const atualizado = await resp.json();
      const idx = produtos.findIndex((p) => String(p.id) === String(id));
      if (idx >= 0) produtos[idx] = atualizado;
      renderProdutos(produtos);
    } catch (err) {
      alert('Falha ao atualizar.');
    }
  }
});

buscaInput.addEventListener('input', () => {
  const q = buscaInput.value.trim().toLowerCase();
  const filtrados = produtos.filter(
    (p) => p.nome.toLowerCase().includes(q) || String(p.preco).includes(q)
  );
  renderProdutos(filtrados);
});

carregarProdutos();