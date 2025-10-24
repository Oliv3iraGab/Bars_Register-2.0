package br.com.bars_register.infrastructure.jpa;

import br.com.bars_register.application.repositories.AuditoriaRepository;
import br.com.bars_register.domain.AuditoriaVenda;
import org.springframework.stereotype.Repository;

@Repository
public class AuditoriaRepositoryAdapter implements AuditoriaRepository {

    private final AuditoriaJpaRepository auditoriaJpaRepository;
    private final VendaJpaRepository vendaJpaRepository;

    public AuditoriaRepositoryAdapter(AuditoriaJpaRepository auditoriaJpaRepository, VendaJpaRepository vendaJpaRepository) {
        this.auditoriaJpaRepository = auditoriaJpaRepository;
        this.vendaJpaRepository = vendaJpaRepository;
    }

    @Override
    public AuditoriaVenda save(AuditoriaVenda auditoria) {
        VendaEntity vendaEntity = vendaJpaRepository.findById(auditoria.getVendaId())
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada para auditoria: " + auditoria.getVendaId()));
        AuditoriaVendaEntity saved = auditoriaJpaRepository.save(AuditoriaVendaEntity.fromDomain(auditoria, vendaEntity));
        return saved.toDomain();
    }
}