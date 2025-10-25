package br.com.bars_register.infrastructure.repo;

import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.domain.Produto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryProdutoRepository implements ProdutoRepository {

    private final List<Produto> produtos = new ArrayList<>();
    private final AtomicInteger seq = new AtomicInteger(1);

    @Override
    public synchronized Produto save(Produto produto) {
        produto.setId(seq.getAndIncrement());
        produtos.add(produto);
        return produto;
    }

    @Override
    public synchronized Produto update(Produto produto) {
        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i).getId() == produto.getId()) {
                produtos.set(i, produto);
                return produto;
            }
        }
        throw new IllegalArgumentException("Produto não encontrado: " + produto.getId());
    }

    @Override
    public synchronized boolean deleteById(int id) {
        Optional<Produto> opt = findById(id);
        if (opt.isEmpty()) return false;
        Produto p = opt.get();
        // Repositório em memória não rastreia vendas; se necessário, pode ser ajustado
        // Para consistência com JPA, quando houver histórico deve inativar. Aqui assumimos falso.
        boolean hasHistory = hasSalesHistory(id);
        if (hasHistory) {
            p.setStatus("INATIVO");
            update(p);
            return true;
        }
        return produtos.removeIf(pr -> pr.getId() == id);
    }

    @Override
    public synchronized Optional<Produto> findById(int id) {
        return produtos.stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
    public synchronized List<Produto> findAll() {
        List<Produto> ativos = new ArrayList<>();
        for (Produto p : produtos) {
            String st = p.getStatus();
            if (st == null || !"INATIVO".equalsIgnoreCase(st)) {
                ativos.add(p);
            }
        }
        return ativos;
    }

    @Override
    public synchronized boolean hasSalesHistory(int id) {
        // Em memória padrão não possui referência de vendas; retornar falso.
        return false;
    }
}