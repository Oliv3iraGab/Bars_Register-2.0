package br.com.bars_register.infrastructure.memory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.domain.Venda;

public class InMemoryVendaRepository implements VendaRepository {
    private final List<Venda> storage = new ArrayList<>();
    private final AtomicInteger seq = new AtomicInteger(1);

    @Override
    public Venda save(Venda venda) {
        venda.setId(seq.getAndIncrement());
        storage.add(venda);
        return venda;
    }

    @Override
    public Optional<Venda> findById(int id) {
        return storage.stream().filter(v -> v.getId() == id).findFirst();
    }

    @Override
    public List<Venda> findAll() {
        return new ArrayList<>(storage);
    }

    @Override
    public List<Venda> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return storage.stream()
            .filter(v -> !v.getDataVenda().isBefore(inicio) && v.getDataVenda().isBefore(fim))
            .toList();
    }
}