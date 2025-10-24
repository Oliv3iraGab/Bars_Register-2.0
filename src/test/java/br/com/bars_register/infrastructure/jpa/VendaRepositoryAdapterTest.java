package br.com.bars_register.infrastructure.jpa;

import br.com.bars_register.domain.Venda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaRepositoryAdapterTest {

    @Mock
    private VendaJpaRepository jpaRepository;

    private VendaRepositoryAdapter repositoryAdapter;

    @BeforeEach
    void setUp() {
        repositoryAdapter = new VendaRepositoryAdapter(jpaRepository);
    }

    @Test
    void save_DeveRetornarVendaSalva() {
        // Arrange
        Venda venda = new Venda();
        venda.setDataVenda(LocalDateTime.now());
        venda.setTipoPagamento("DINHEIRO");
        venda.setTotal(50.0);
        
        VendaEntity entity = new VendaEntity(LocalDateTime.now(), "DINHEIRO", 50.0);
        entity.setId(1);
        
        when(jpaRepository.save(any(VendaEntity.class))).thenReturn(entity);

        // Act
        Venda resultado = repositoryAdapter.save(venda);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("DINHEIRO", resultado.getTipoPagamento());
        assertEquals(50.0, resultado.getTotal());
        verify(jpaRepository).save(any(VendaEntity.class));
    }

    @Test
    void findById_VendaExistente_DeveRetornarVenda() {
        // Arrange
        VendaEntity entity = new VendaEntity(LocalDateTime.now(), "CARTÃO", 75.0);
        entity.setId(1);
        
        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        Optional<Venda> resultado = repositoryAdapter.findById(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getId());
        assertEquals("CARTÃO", resultado.get().getTipoPagamento());
        assertEquals(75.0, resultado.get().getTotal());
        verify(jpaRepository).findById(1);
    }

    @Test
    void findById_VendaNaoExistente_DeveRetornarVazio() {
        // Arrange
        when(jpaRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Venda> resultado = repositoryAdapter.findById(999);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(jpaRepository).findById(999);
    }

    @Test
    void findAll_DeveRetornarListaDeVendas() {
        // Arrange
        VendaEntity entity1 = new VendaEntity(LocalDateTime.now(), "DINHEIRO", 50.0);
        entity1.setId(1);
        
        VendaEntity entity2 = new VendaEntity(LocalDateTime.now(), "CARTÃO", 75.0);
        entity2.setId(2);
        
        List<VendaEntity> entities = Arrays.asList(entity1, entity2);
        when(jpaRepository.findAll()).thenReturn(entities);

        // Act
        List<Venda> resultado = repositoryAdapter.findAll();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(1, resultado.get(0).getId());
        assertEquals("DINHEIRO", resultado.get(0).getTipoPagamento());
        assertEquals(2, resultado.get(1).getId());
        assertEquals("CARTÃO", resultado.get(1).getTipoPagamento());
        verify(jpaRepository).findAll();
    }

    @Test
    void findByPeriodo_ComVendasNoPeriodo_DeveRetornarListaDeVendas() {
        // Arrange
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(1);
        
        VendaEntity entity = new VendaEntity(LocalDateTime.now(), "PIX", 100.0);
        entity.setId(1);
        
        List<VendaEntity> entities = Arrays.asList(entity);
        when(jpaRepository.findByDataVendaBetween(inicio, fim)).thenReturn(entities);

        // Act
        List<Venda> resultado = repositoryAdapter.findByPeriodo(inicio, fim);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getId());
        assertEquals("PIX", resultado.get(0).getTipoPagamento());
        assertEquals(100.0, resultado.get(0).getTotal());
        verify(jpaRepository).findByDataVendaBetween(inicio, fim);
    }

    @Test
    void findByPeriodo_SemVendasNoPeriodo_DeveRetornarListaVazia() {
        // Arrange
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(1);
        
        when(jpaRepository.findByDataVendaBetween(inicio, fim)).thenReturn(Arrays.asList());

        // Act
        List<Venda> resultado = repositoryAdapter.findByPeriodo(inicio, fim);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(jpaRepository).findByDataVendaBetween(inicio, fim);
    }
}
