package br.com.bars_register.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoJpaRepository extends JpaRepository<ProdutoEntity, Integer> {
    List<ProdutoEntity> findAllByAtivoTrue();
}
