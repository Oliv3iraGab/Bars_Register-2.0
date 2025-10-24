package br.com.bars_register.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriaJpaRepository extends JpaRepository<AuditoriaVendaEntity, Integer> {
}