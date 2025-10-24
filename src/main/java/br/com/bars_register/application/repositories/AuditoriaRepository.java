package br.com.bars_register.application.repositories;

import br.com.bars_register.domain.AuditoriaVenda;

public interface AuditoriaRepository {
    AuditoriaVenda save(AuditoriaVenda auditoria);
}