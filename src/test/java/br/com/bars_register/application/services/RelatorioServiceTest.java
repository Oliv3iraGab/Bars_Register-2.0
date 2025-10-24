package br.com.bars_register.application.services;

import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.domain.Venda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    private RelatorioService relatorioService;

    @BeforeEach
    void setUp() {
        relatorioService = new RelatorioService(vendaRepository);
    }

    @Test
    void totalVendasPorDia_ComVendas_DeveRetornarMapaComTotais() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        LocalDate ontem = hoje.minusDays(1);
        LocalDate inicio = ontem;
        LocalDate fim = hoje.plusDays(1);
        
        Venda venda1 = new Venda();
        venda1.setDataVenda(hoje.atStartOfDay());
        venda1.setTotal(100.0);
        
        Venda venda2 = new Venda();
        venda2.setDataVenda(hoje.atStartOfDay());
        venda2.setTotal(50.0);
        
        Venda venda3 = new Venda();
        venda3.setDataVenda(ontem.atStartOfDay());
        venda3.setTotal(75.0);
        
        List<Venda> vendas = Arrays.asList(venda1, venda2, venda3);
        when(vendaRepository.findByPeriodo(any(), any())).thenReturn(vendas);

        // Act
        Map<LocalDate, Double> resultado = relatorioService.totalVendasPorDia(inicio, fim);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(150.0, resultado.get(hoje));
        assertEquals(75.0, resultado.get(ontem));
        verify(vendaRepository).findByPeriodo(any(), any());
    }

    @Test
    void totalVendasPorDia_SemVendas_DeveRetornarMapaVazio() {
        // Arrange
        LocalDate inicio = LocalDate.now().minusDays(1);
        LocalDate fim = LocalDate.now().plusDays(1);
        when(vendaRepository.findByPeriodo(any(), any())).thenReturn(Arrays.asList());

        // Act
        Map<LocalDate, Double> resultado = relatorioService.totalVendasPorDia(inicio, fim);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(vendaRepository).findByPeriodo(any(), any());
    }
}
