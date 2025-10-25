(function () {
  let produtos = [];

  const tbody = document.getElementById("produtosBody");
  const busca = document.getElementById("busca");
  const formAdd = document.getElementById("formAdd");

  if (!tbody || !busca || !formAdd) {
    return;
  }

  function render(lista) {
    tbody.innerHTML = "";
    lista.forEach((p) => {
      const tr = document.createElement("tr");
      tr.dataset.id = String(p.id);
      tr.innerHTML = `
        <td>${p.id}</td>
        <td>${p.nome}</td>
        <td>R$ ${Number(p.preco).toFixed(2)}</td>
        <td>${p.estoque}</td>
        <td>
          <div class="table-actions">
            <button class="btn btn-outline" data-acao="editar" data-id="${p.id}">Editar</button>
            <button class="btn btn-danger" data-acao="excluir" data-id="${p.id}">Excluir</button>
          </div>
        </td>
      `;
      tbody.appendChild(tr);
    });
  }

  function getCurrentList() {
    const termo = busca.value.trim().toLowerCase();
    if (!termo) return produtos;
    return produtos.filter((p) =>
      p.nome.toLowerCase().includes(termo) || String(p.preco).includes(termo)
    );
  }

  function rerenderCurrentView() {
    const list = getCurrentList();
    render(list);
  }

  async function carregarProdutos() {
    try {
      const res = await fetch("/api/products", { headers: { "Accept": "application/json" } });
      if (!res.ok) {
        alert(`Erro ao carregar produtos. Status ${res.status}`);
        throw new Error("Falha ao carregar produtos");
      }
      const data = await res.json();
      produtos = data.items || [];
      rerenderCurrentView();
    } catch (err) {
      console.error(err);
      alert("Erro ao carregar produtos.");
    }
  }

  async function criarProduto(nome, preco, estoque) {
    const body = new URLSearchParams({ nome, preco: String(preco), estoque: String(estoque) });
    const res = await fetch("/api/products", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body
    });
    if (!res.ok) throw new Error("Falha ao criar produto");
    const created = await res.json();
    produtos.push(created);
    rerenderCurrentView();
  }

  async function editarProduto(id, nome, preco, estoque) {
    const body = new URLSearchParams({ nome, preco: String(preco), estoque: String(estoque) });
    const res = await fetch(`/api/products/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body
    });
    if (!res.ok) throw new Error("Falha ao editar produto");
    const updated = await res.json();
    const idx = produtos.findIndex((p) => p.id === id);
    if (idx >= 0) produtos[idx] = updated;
    rerenderCurrentView();
  }

  async function excluirProduto(id) {
    const res = await fetch(`/api/products/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error("Falha ao excluir produto");
    produtos = produtos.filter((p) => p.id !== id);
    rerenderCurrentView();
  }

  function enterEditMode(id) {
    const list = getCurrentList();
    render(list);
    const tr = tbody.querySelector(`tr[data-id="${id}"]`);
    const p = produtos.find((x) => x.id === id);
    if (!tr || !p) return;
    tr.innerHTML = `
      <td>${p.id}</td>
      <td><input class="input" type="text" value="${p.nome}" aria-label="Editar nome" /></td>
      <td>
        <div class="grid-2">
          <span>R$</span>
          <input class="input" type="number" step="0.01" value="${Number(p.preco).toFixed(2)}" aria-label="Editar preço" />
        </div>
      </td>
      <td><input class="input" type="number" min="0" value="${p.estoque}" aria-label="Editar estoque" /></td>
      <td>
        <div class="table-actions">
          <button class="btn btn-success" data-acao="salvar" data-id="${p.id}">Salvar</button>
          <button class="btn" data-acao="cancelar-edicao" data-id="${p.id}">Cancelar</button>
        </div>
      </td>
    `;
  }

  function enterDeleteConfirmMode(id) {
    const list = getCurrentList();
    render(list);
    const tr = tbody.querySelector(`tr[data-id="${id}"]`);
    if (!tr) return;
    // Mantém os dados visíveis e troca apenas a célula de ações
    const tds = tr.querySelectorAll("td");
    const actionsTd = tds[tds.length - 1];
    actionsTd.innerHTML = `
      <div class="table-actions">
        <span aria-live="polite">Confirmar exclusão?</span>
        <button class="btn btn-danger" data-acao="confirmar-exclusao" data-id="${id}">Excluir</button>
        <button class="btn" data-acao="cancelar-exclusao" data-id="${id}">Cancelar</button>
      </div>
    `;
  }

  busca.addEventListener("input", rerenderCurrentView);

  formAdd.addEventListener("submit", async function (e) {
    e.preventDefault();
    const nome = document.getElementById("nome").value.trim();
    const preco = parseFloat(document.getElementById("preco").value);
    const estoque = parseInt(document.getElementById("estoque").value, 10);
    if (!nome || isNaN(preco) || isNaN(estoque)) {
      alert("Preencha os campos corretamente.");
      return;
    }
    try {
      await criarProduto(nome, preco, estoque);
      formAdd.reset();
    } catch (err) {
      console.error(err);
      alert("Erro ao adicionar produto.");
    }
  });

  tbody.addEventListener("click", async function (e) {
    const btn = e.target.closest("button");
    if (!btn) return;
    const id = parseInt(btn.getAttribute("data-id"), 10);
    const acao = btn.getAttribute("data-acao");

    if (acao === "excluir") {
      // Exibir confirmação inline, mantendo o item visível
      enterDeleteConfirmMode(id);
      return;
    }
    if (acao === "cancelar-exclusao") {
      rerenderCurrentView();
      return;
    }
    if (acao === "confirmar-exclusao") {
      try {
        await excluirProduto(id);
      } catch (err) {
        console.error(err);
        alert("Erro ao excluir produto.");
        rerenderCurrentView();
      }
      return;
    }

    if (acao === "editar") {
      enterEditMode(id);
      return;
    }
    if (acao === "cancelar-edicao") {
      rerenderCurrentView();
      return;
    }
    if (acao === "salvar") {
      const tr = tbody.querySelector(`tr[data-id="${id}"]`);
      if (!tr) return;
      const inputs = tr.querySelectorAll("input");
      const novoNome = inputs[0].value.trim();
      const novoPreco = parseFloat(inputs[1].value);
      const novoEstoque = parseInt(inputs[2].value, 10);
      if (!novoNome || isNaN(novoPreco) || isNaN(novoEstoque) || novoEstoque < 0) {
        alert("Valores inválidos. Verifique nome, preço e estoque.");
        return;
      }
      try {
        await editarProduto(id, novoNome, novoPreco, novoEstoque);
      } catch (err) {
        console.error(err);
        alert("Erro ao editar produto.");
        rerenderCurrentView();
      }
      return;
    }
  });

  carregarProdutos();
})();