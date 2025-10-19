// Lista de produtos em memória (mock)
(function () {
  let produtos = [
    { id: 1, nome: "Produto A", preco: 10.0, estoque: 100 },
    { id: 2, nome: "Produto B", preco: 25.5, estoque: 50 },
    { id: 3, nome: "Produto C", preco: 8.75, estoque: 120 },
  ];
  let nextId = produtos.length + 1;

  const tbody = document.getElementById("produtosBody");
  const busca = document.getElementById("busca");
  const formAdd = document.getElementById("formAdd");

  function render(lista) {
    tbody.innerHTML = "";
    lista.forEach((p) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${p.id}</td>
        <td>${p.nome}</td>
        <td>R$ ${p.preco.toFixed(2)}</td>
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

  function filtrar() {
    const termo = busca.value.toLowerCase();
    const filtrados = produtos.filter((p) =>
      p.nome.toLowerCase().includes(termo) || String(p.preco).includes(termo)
    );
    render(filtrados);
  }

  busca.addEventListener("input", filtrar);

  formAdd.addEventListener("submit", function (e) {
    e.preventDefault();
    const nome = document.getElementById("nome").value.trim();
    const preco = parseFloat(document.getElementById("preco").value);
    const estoque = parseInt(document.getElementById("estoque").value, 10);
    if (!nome || isNaN(preco) || isNaN(estoque)) {
      alert("Preencha os campos corretamente.");
      return;
    }
    produtos.push({ id: nextId++, nome, preco, estoque });
    render(produtos);
    formAdd.reset();
  });

  tbody.addEventListener("click", function (e) {
    const btn = e.target.closest("button");
    if (!btn) return;
    const id = parseInt(btn.getAttribute("data-id"), 10);
    const acao = btn.getAttribute("data-acao");
    const idx = produtos.findIndex((p) => p.id === id);
    if (idx < 0) return;

    if (acao === "excluir") {
      if (confirm("Deseja excluir este produto?")) {
        produtos.splice(idx, 1);
        render(produtos);
      }
    }
    if (acao === "editar") {
      const atual = produtos[idx];
      const novoNome = prompt("Nome do produto:", atual.nome) || atual.nome;
      const novoPreco = parseFloat(prompt("Preço:", atual.preco));
      const novoEstoque = parseInt(prompt("Estoque:", atual.estoque), 10);
      if (!isNaN(novoPreco) && !isNaN(novoEstoque)) {
        produtos[idx] = { ...atual, nome: novoNome, preco: novoPreco, estoque: novoEstoque };
        render(produtos);
      } else {
        alert("Valores inválidos. Edição cancelada.");
      }
    }
  });

  // Render inicial
  render(produtos);
})();