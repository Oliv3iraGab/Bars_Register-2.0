package br.com.bars_register.infrastructure.memory;

import br.com.bars_register.application.repositories.AuditoriaRepository;
import br.com.bars_register.domain.AuditoriaVenda;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryAuditoriaRepository implements AuditoriaRepository {
    private final List<AuditoriaVenda> storage = new ArrayList<>();
    private final AtomicInteger seq = new AtomicInteger(1);

    @Override
    public AuditoriaVenda save(AuditoriaVenda auditoria) {
        auditoria.setId(seq.getAndIncrement());
        storage.add(auditoria);
        return auditoria;
    }
}