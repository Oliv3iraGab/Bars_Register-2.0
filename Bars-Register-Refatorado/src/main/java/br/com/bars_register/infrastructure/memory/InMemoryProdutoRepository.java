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
        return storage.remove(id) != null;
    }

    @Override
    public Optional<Produto> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Produto> findAll() {
        return new ArrayList<>(storage.values());
    }
}