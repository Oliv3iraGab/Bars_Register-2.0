package br.com.bars_register.application.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import br.com.bars_register.domain.Venda;

public interface VendaRepository {
    Venda save(Venda venda);
    Optional<Venda> findById(int id);
    List<Venda> findAll();
    List<Venda> findByPeriodo(LocalDateTime inicio, LocalDateTime fim);
}