const carrinho = [];

function formatCurrencyBRL(v) {
  return (typeof v === 'number' ? v : Number(v)).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function renderCarrinho() {
  const tbody = document.getElementById('carrinhoBody');
  tbody.innerHTML = '';
  let total = 0;
  carrinho.forEach((item, idx) => {
    const subtotal = item.preco * item.qtd;
    total += subtotal;
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${item.nome}</td>
      <td>${formatCurrencyBRL(item.preco)}</td>
      <td>${item.qtd}</td>
      <td>${formatCurrencyBRL(subtotal)}</td>
      <td class="table-actions">
        <button class="btn btn-outline" data-acao="inc" data-idx="${idx}">+1</button>
        <button class="btn btn-outline" data-acao="dec" data-idx="${idx}">-1</button>
        <button class="btn btn-danger" data-acao="rem" data-idx="${idx}">Remover</button>
      </td>
    `;
    tbody.appendChild(tr);
  });
  document.getElementById('total').innerHTML = `<strong>${formatCurrencyBRL(total)}</strong>`;
}

function addItem(nome, preco, qtd) {
  if (!nome || !preco || !qtd) return;
  carrinho.push({ nome, preco: Number(preco), qtd: Number(qtd) });
  renderCarrinho();
}

function handleAcoes(e) {
  const btn = e.target.closest('button');
  if (!btn) return;
  const idx = Number(btn.dataset.idx);
  const acao = btn.dataset.acao;
  if (!Number.isInteger(idx)) return;
  if (acao === 'inc') carrinho[idx].qtd += 1;
  if (acao === 'dec') {
    carrinho[idx].qtd -= 1;
    if (carrinho[idx].qtd <= 0) carrinho.splice(idx, 1);
  }
  if (acao === 'rem') carrinho.splice(idx, 1);
  renderCarrinho();
}

function finalizarVenda() {
  const feedback = document.getElementById('acaoFeedback');
  if (carrinho.length === 0) {
    feedback.textContent = 'Carrinho vazio. Adicione itens antes de finalizar.';
    return;
  }
  feedback.textContent = 'Venda finalizada (simulação). Obrigado!';
  // Aqui poderíamos integrar com um endpoint /api/sales futuramente.
  carrinho.splice(0, carrinho.length);
  renderCarrinho();
}

function cancelarVenda() {
  carrinho.splice(0, carrinho.length);
  renderCarrinho();
  document.getElementById('acaoFeedback').textContent = 'Venda cancelada.';
}

// Bind UI
const btnAdd = document.getElementById('btnAdd');
btnAdd.addEventListener('click', () => {
  const nome = document.getElementById('nomeItem').value.trim();
  const preco = document.getElementById('precoItem').value;
  const qtd = document.getElementById('qtdItem').value;
  addItem(nome, preco, qtd);
});

document.getElementById('carrinhoBody').addEventListener('click', handleAcoes);

document.getElementById('btnFinalizar').addEventListener('click', finalizarVenda);
document.getElementById('btnCancelar').addEventListener('click', cancelarVenda);

renderCarrinho();