// Interações do PDV: adicionar itens, remover e calcular total (integrado à API)
(function () {
  const carrinho = [];
  const carrinhoBody = document.getElementById("carrinhoBody");
  const totalEl = document.getElementById("total");
  const nomeItem = document.getElementById("nomeItem");
  const precoItem = document.getElementById("precoItem");
  const qtdItem = document.getElementById("qtdItem");
  const listaSugestoes = document.getElementById("listaSugestoes");
  let produtoSelecionado = null;
  const btnAdd = document.getElementById("btnAdd");
  const btnFinalizar = document.getElementById("btnFinalizar");
  const btnCancelar = document.getElementById("btnCancelar");
  const acaoFeedback = document.getElementById("acaoFeedback");
  let finalizando = false;

  let produtos = [];

  // Proteção: se elementos necessários não existirem, não inicia handlers
  if (
    !carrinhoBody || !totalEl || !nomeItem || !precoItem || !qtdItem ||
    !btnAdd || !btnFinalizar || !btnCancelar || !acaoFeedback
  ) {
    return;
  }

  async function carregarProdutos() {
    try {
      const res = await fetch("/api/products", { headers: { "Accept": "application/json" } });
      if (!res.ok) {
        alert(`Erro ao carregar produtos no PDV. Status ${res.status}`);
        throw new Error("Falha ao carregar produtos");
      }
      const contentType = res.headers.get("content-type") || "";
      console.log("/api/products status:", res.status, "content-type:", contentType);
      const data = await res.json();
      produtos = Array.isArray(data) ? data : (data.items || []);

      function renderSugestoes(filtro) {
        listaSugestoes.innerHTML = "";
        const termo = (filtro || "").toLowerCase();
        const filtrados = produtos.filter(p => (p.nome || "").toLowerCase().includes(termo));
        filtrados.forEach((p, index) => {
          const li = document.createElement("li");
          li.textContent = `${p.nome} - R$ ${Number(p.preco).toFixed(2)}`;
          li.tabIndex = 0;
          li.setAttribute("role", "option");
          li.dataset.index = String(index);
          const selecionar = () => {
            produtoSelecionado = p;
            nomeItem.value = p.nome;
            precoItem.value = Number(p.preco).toFixed(2);
            listaSugestoes.innerHTML = "";
            listaSugestoes.setAttribute("hidden", "");
            nomeItem.focus();
          };
          li.addEventListener("click", selecionar);
          li.addEventListener("keydown", (ev) => {
            if (ev.key === "Enter") {
              selecionar();
            } else if (ev.key === "ArrowDown") {
              const next = li.nextElementSibling;
              if (next) next.focus();
            } else if (ev.key === "ArrowUp") {
              const prev = li.previousElementSibling;
              if (prev) prev.focus();
              else nomeItem.focus();
            }
          });
          listaSugestoes.appendChild(li);
        });
        // Controle de visibilidade do dropdown com base em resultados
        if (filtrados.length > 0) {
          listaSugestoes.removeAttribute("hidden");
        } else {
          listaSugestoes.setAttribute("hidden", "");
        }
      }

      // Primeira carga
      renderSugestoes("");

      nomeItem.addEventListener("input", (ev) => {
        produtoSelecionado = null;
        precoItem.value = "";
        renderSugestoes(ev.target.value);
      });
      // Navegação por teclado: setas para baixo a partir do input
      nomeItem.addEventListener("keydown", (ev) => {
        if (ev.key === "ArrowDown") {
          const first = listaSugestoes.querySelector("li");
          if (first) first.focus();
        }
      });
    } catch (err) {
      console.error(err);
      acaoFeedback.textContent = "Erro ao carregar produtos. Verifique a conexão.";
    }
  }

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
            <button class="btn btn-outline" data-acao="mais" data-idx="${idx}">+</button>
            <button class="btn btn-danger" data-acao="remover" data-idx="${idx}" aria-label="Remover ${item.nome}">Remover</button>
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
    const qtd = parseInt(qtdItem.value, 10) || 1;
    const precoStr = (precoItem.value || "").trim();
    if (!nome || !precoStr || qtd <= 0) {
      acaoFeedback.textContent = "Preencha produto, valor e quantidade válidos.";
      return;
    }
    if (!produtoSelecionado || produtoSelecionado.nome.toLowerCase() !== nome.toLowerCase()) {
      acaoFeedback.textContent = "Selecione um produto na lista de sugestões.";
      return;
    }
    const preco = Number(produtoSelecionado.preco);
    if (Number.isNaN(preco)) {
      acaoFeedback.textContent = "Valor do produto inválido.";
      return;
    }
    // Validação de estoque: considera itens já no carrinho
    const jaNoCarrinho = carrinho
      .filter((i) => i.produtoId === produtoSelecionado.id)
      .reduce((acc, i) => acc + i.qtd, 0);
    const disponivel = Number(produtoSelecionado.estoque) - jaNoCarrinho;
    if (qtd > disponivel) {
      acaoFeedback.textContent = `Estoque insuficiente para ${produtoSelecionado.nome}. Disponível: ${disponivel}`;
      return;
    }

    carrinho.push({ nome: produtoSelecionado.nome, preco, qtd, produtoId: produtoSelecionado.id });
    render();
    nomeItem.value = "";
    precoItem.value = "";
    qtdItem.value = "1";
    produtoSelecionado = null;
    listaSugestoes.innerHTML = "";
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
    }
    render();
  });

  btnFinalizar.addEventListener("click", async () => {
    if (finalizando) return;
    finalizando = true;
    btnFinalizar.disabled = true;

    try {
      if (carrinho.length === 0) {
        acaoFeedback.textContent = "Carrinho vazio. Adicione itens antes de finalizar.";
        return;
      }
      if (carrinho.some((i) => !i.produtoId)) {
        acaoFeedback.textContent = "Há itens não cadastrados. Utilize produtos cadastrados.";
        return;
      }
      // Segunda validação de estoque antes de enviar
      for (const item of carrinho) {
        const p = produtos.find((x) => x.id === item.produtoId);
        const jaNoCarrinho = carrinho.filter((i) => i.produtoId === item.produtoId).reduce((acc, i) => acc + i.qtd, 0);
        const disponivel = Number(p?.estoque ?? 0) - (jaNoCarrinho - item.qtd);
        if (!p || item.qtd <= 0 || disponivel < item.qtd) {
          acaoFeedback.textContent = `Validação falhou para ${item.nome}. Estoque disponível: ${Number(p?.estoque ?? 0)}`;
          return;
        }
      }

      const itemsStr = carrinho.map((i) => `${i.produtoId}:${i.qtd}`).join(",");
      const body = new URLSearchParams({ items: itemsStr, tipoPagamento: "DINHEIRO" });
      const res = await fetch("/api/vendas", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body,
      });
      if (!res.ok) {
        const errText = await res.text().catch(() => "");
        acaoFeedback.textContent = errText ? `Erro: ${errText}` : "Erro ao finalizar venda. Verifique estoque e dados.";
        return;
      }
      const data = await res.json();
      acaoFeedback.textContent = `Venda #${data.id} finalizada. Total R$ ${Number(data.total).toFixed(2)}`;

      // Atualizar estoque local em tempo real
      for (const item of carrinho) {
        const p = produtos.find((x) => x.id === item.produtoId);
        if (p) p.estoque = Math.max(0, Number(p.estoque) - item.qtd);
      }

      // Limpa carrinho
      carrinho.splice(0, carrinho.length);
      render();
    } catch (err) {
      console.error(err);
      acaoFeedback.textContent = "Erro ao finalizar venda. Verifique estoque e dados.";
    } finally {
      finalizando = false;
      btnFinalizar.disabled = false;
    }
  });

  btnCancelar.addEventListener("click", () => {
    acaoFeedback.textContent = "Venda cancelada.";
    carrinho.splice(0, carrinho.length);
    render();
  });

  carregarProdutos();
})();