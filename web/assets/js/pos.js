// Interações do PDV: adicionar itens, remover e calcular total
(function () {
  const carrinho = [];
  const carrinhoBody = document.getElementById("carrinhoBody");
  const totalEl = document.getElementById("total");
  const nomeItem = document.getElementById("nomeItem");
  const precoItem = document.getElementById("precoItem");
  const qtdItem = document.getElementById("qtdItem");
  const btnAdd = document.getElementById("btnAdd");
  const btnFinalizar = document.getElementById("btnFinalizar");
  const btnCancelar = document.getElementById("btnCancelar");
  const acaoFeedback = document.getElementById("acaoFeedback");

  function render() {
    carrinhoBody.innerHTML = "";
    carrinho.forEach((item, idx) => {
      const tr = document.createElement("tr");
      const subtotal = item.preco * item.qtd;
      tr.innerHTML = `
        <td>${item.nome}</td>
        <td>R$ ${item.preco.toFixed(2)}</td>
        <td>${item.qtd}</td>
        <td>R$ ${subtotal.toFixed(2)}</td>
        <td>
          <div class="table-actions">
            <button class="btn btn-outline" data-acao="menos" data-idx="${idx}">-</button>
            <button class="btn btn-outline" data-acao="mais" data-idx="${idx}">+</button>
            <button class="btn btn-danger" data-acao="remover" data-idx="${idx}">Remover</button>
          </div>
        </td>
      `;
      carrinhoBody.appendChild(tr);
    });
    atualizarTotal();
  }

  function atualizarTotal() {
    const total = carrinho.reduce((acc, i) => acc + i.preco * i.qtd, 0);
    totalEl.innerHTML = `<strong>R$ ${total.toFixed(2)}</strong>`;
  }

  btnAdd.addEventListener("click", () => {
    const nome = nomeItem.value.trim();
    const preco = parseFloat(precoItem.value);
    const qtd = parseInt(qtdItem.value, 10) || 1;
    if (!nome || isNaN(preco) || preco <= 0 || qtd <= 0) {
      acaoFeedback.textContent = "Preencha produto, preço e quantidade válidos.";
      return;
    }
    carrinho.push({ nome, preco, qtd });
    render();
    nomeItem.value = "";
    precoItem.value = "";
    qtdItem.value = "1";
    acaoFeedback.textContent = "Item adicionado ao carrinho.";
  });

  carrinhoBody.addEventListener("click", (e) => {
    const btn = e.target.closest("button");
    if (!btn) return;
    const acao = btn.getAttribute("data-acao");
    const idx = parseInt(btn.getAttribute("data-idx"), 10);
    const item = carrinho[idx];
    if (!item) return;
    if (acao === "remover") {
      carrinho.splice(idx, 1);
    } else if (acao === "mais") {
      item.qtd += 1;
    } else if (acao === "menos") {
      item.qtd = Math.max(1, item.qtd - 1);
    }
    render();
  });

  btnFinalizar.addEventListener("click", () => {
    if (carrinho.length === 0) {
      acaoFeedback.textContent = "Carrinho vazio. Adicione itens antes de finalizar.";
      return;
    }
    acaoFeedback.textContent = "Venda finalizada (mock). Obrigado!";
    // Limpa carrinho
    carrinho.splice(0, carrinho.length);
    render();
  });

  btnCancelar.addEventListener("click", () => {
    acaoFeedback.textContent = "Venda cancelada.";
    carrinho.splice(0, carrinho.length);
    render();
  });
})();