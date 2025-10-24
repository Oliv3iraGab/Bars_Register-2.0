package br.com.bars_register.infrastructure.jpa;

import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.domain.Venda;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class VendaRepositoryAdapter implements VendaRepository {
    
    private final VendaJpaRepository jpaRepository;
    
    public VendaRepositoryAdapter(VendaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Venda save(Venda venda) {
        VendaEntity entity = VendaEntity.fromDomain(venda);
        VendaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public Optional<Venda> findById(int id) {
        return jpaRepository.findById(id)
                .map(VendaEntity::toDomain);
    }
    
    @Override
    public List<Venda> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(VendaEntity::toDomain)
                .toList();
    }
    
    @Override
    public List<Venda> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.findByDataVendaBetween(inicio, fim)
                .stream()
                .map(VendaEntity::toDomain)
                .toList();
    }
}
