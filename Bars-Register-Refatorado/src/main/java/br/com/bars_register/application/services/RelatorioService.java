package br.com.bars_register.application.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.domain.Venda;

public class RelatorioService {
    private final VendaRepository vendaRepository;

    public RelatorioService(VendaRepository vendaRepository) {
        this.vendaRepository = vendaRepository;
    }

    public Map<LocalDate, Double> totalVendasPorDia(LocalDate inicio, LocalDate fim) {
        List<Venda> vendas = vendaRepository.findByPeriodo(inicio.atStartOfDay(), fim.plusDays(1).atStartOfDay());
        return vendas.stream()
            .collect(Collectors.groupingBy(v -> v.getDataVenda().toLocalDate(),
                    Collectors.summingDouble(Venda::getTotal)));
    }
}