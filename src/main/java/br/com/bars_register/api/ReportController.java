package br.com.bars_register.api;

import br.com.bars_register.application.services.RelatorioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final RelatorioService relatorioService;

    public ReportController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/daily")
    public Map<LocalDate, Double> totalPorDia(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        return relatorioService.totalVendasPorDia(inicio, fim);
    }
}