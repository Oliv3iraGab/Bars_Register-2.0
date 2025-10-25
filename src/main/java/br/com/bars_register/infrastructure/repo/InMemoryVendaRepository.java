package br.com.bars_register.infrastructure.repo;

import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.domain.Venda;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryVendaRepository implements VendaRepository {
    private final List<Venda> vendas = new ArrayList<>();
    private final AtomicInteger seq = new AtomicInteger(1);

    @Override
    public synchronized Venda save(Venda venda) {
        venda.setId(seq.getAndIncrement());
        vendas.add(venda);
        return venda;
    }

    @Override
    public synchronized Optional<Venda> findById(int id) {
        return vendas.stream().filter(v -> v.getId() == id).findFirst();
    }

    @Override
    public synchronized List<Venda> findAll() {
        return new ArrayList<>(vendas);
    }

    @Override
    public synchronized List<Venda> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return vendas.stream()
                .filter(v -> !v.getDataVenda().isBefore(inicio) && v.getDataVenda().isBefore(fim))
                .collect(Collectors.toList());
    }
}
