package br.com.bars_register.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaJpaRepository extends JpaRepository<VendaEntity, Integer> {
    @Query("SELECT v FROM VendaEntity v WHERE v.dataVenda >= :inicio AND v.dataVenda < :fim")
    List<VendaEntity> findByDataVendaBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
