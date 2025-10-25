package br.com.bars_register.infrastructure.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.domain.Produto;

public class InMemoryProdutoRepository implements ProdutoRepository {
    private final Map<Integer, Produto> storage = new HashMap<>();
    private final AtomicInteger seq = new AtomicInteger(1);

    @Override
    public Produto save(Produto produto) {
        int id = seq.getAndIncrement();
        produto.setId(id);
        storage.put(id, produto);
        return produto;
    }

    @Override
    public Produto update(Produto produto) {
        storage.put(produto.getId(), produto);
        return produto;
    }

    @Override
    public boolean deleteById(int id) {
        Produto p = storage.get(id);
        if (p == null) return false;
        boolean hasHistory = hasSalesHistory(id);
        if (hasHistory) {
            p.setStatus("INATIVO");
            storage.put(id, p);
            return true;
        }
        return storage.remove(id) != null;
    }

    @Override
    public Optional<Produto> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Produto> findAll() {
        List<Produto> ativos = new ArrayList<>();
        for (Produto p : storage.values()) {
            String st = p.getStatus();
            if (st == null || !"INATIVO".equalsIgnoreCase(st)) {
                ativos.add(p);
            }
        }
        return ativos;
    }

    @Override
    public boolean hasSalesHistory(int id) {
        // Repositório de memória não possui vinculação a vendas; retornar falso.
        return false;
    }
}