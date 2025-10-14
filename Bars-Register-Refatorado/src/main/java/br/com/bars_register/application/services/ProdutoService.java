package br.com.bars_register.application.services;

import java.util.List;
import java.util.Optional;

import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.domain.Produto;

public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto cadastrar(Produto produto) {
        if (produto.getPreco() < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        if (produto.getEstoque() < 0) {
            throw new IllegalArgumentException("Estoque não pode ser negativo");
        }
        return produtoRepository.save(produto);
    }

    public Produto atualizar(Produto produto) {
        return produtoRepository.update(produto);
    }

    public boolean remover(int id) {
        return produtoRepository.deleteById(id);
    }

    public Optional<Produto> buscarPorId(int id) {
        return produtoRepository.findById(id);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public void atualizarEstoque(int produtoId, int delta) {
        Produto p = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));
        int novoEstoque = p.getEstoque() + delta;
        if (novoEstoque < 0) {
            throw new IllegalArgumentException("Estoque insuficiente para o produto: " + p.getNome());
        }
        p.setEstoque(novoEstoque);
        produtoRepository.update(p);
    }
}