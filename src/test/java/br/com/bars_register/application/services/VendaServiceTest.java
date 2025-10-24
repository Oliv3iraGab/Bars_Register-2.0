package br.com.bars_register.application.services;

import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.application.repositories.AuditoriaRepository;
import br.com.bars_register.domain.ItemVenda;
import br.com.bars_register.domain.Produto;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private VendaService vendaService;

    @BeforeEach
    void setUp() {
        vendaService = new VendaService(vendaRepository, produtoRepository, auditoriaRepository);
    }

    @Test
    void registrarVenda_ComItensValidos_DeveRetornarVenda() {
        // Arrange
        Produto cerveja = new Produto(1, "Cerveja", 10.0, 100);
        Produto refrigerante = new Produto(2, "Refrigerante", 7.5, 50);
        
        ItemVenda item1 = new ItemVenda(cerveja, 5);
        ItemVenda item2 = new ItemVenda(refrigerante, 3);
        List<ItemVenda> itens = Arrays.asList(item1, item2);

        when(produtoRepository.findById(1)).thenReturn(Optional.of(cerveja));
        when(produtoRepository.findById(2)).thenReturn(Optional.of(refrigerante));
        when(produtoRepository.update(any(Produto.class))).thenReturn(cerveja);
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocation -> {
            Venda venda = invocation.getArgument(0);
            venda.setId(1);
            return venda;
        });

        // Act
        Venda resultado = vendaService.registrarVenda(itens, "DINHEIRO");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("DINHEIRO", resultado.getTipoPagamento());
        assertEquals(72.5, resultado.getTotal(), 0.01);
        assertEquals(2, resultado.getItens().size());
        
        // Verificar se o estoque foi atualizado
        assertEquals(95, cerveja.getEstoque());
        assertEquals(47, refrigerante.getEstoque());
        
        verify(produtoRepository).findById(1);
        verify(produtoRepository).findById(2);
        verify(produtoRepository, times(2)).update(any(Produto.class));
        verify(vendaRepository).save(any(Venda.class));
        verify(auditoriaRepository).save(any());
    }

    @Test
    void registrarVenda_ComListaVazia_DeveLancarExcecao() {
        // Arrange
        List<ItemVenda> itens = Arrays.asList();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            vendaService.registrarVenda(itens, "DINHEIRO");
        });
    }

    @Test
    void registrarVenda_ComListaNula_DeveLancarExcecao() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            vendaService.registrarVenda(null, "DINHEIRO");
        });
    }

    @Test
    void registrarVenda_ComProdutoNaoExistente_DeveLancarExcecao() {
        // Arrange
        Produto produto = new Produto(999, "Produto Inexistente", 10.0, 100);
        ItemVenda item = new ItemVenda(produto, 1);
        List<ItemVenda> itens = Arrays.asList(item);

        when(produtoRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            vendaService.registrarVenda(itens, "DINHEIRO");
        });
    }

    @Test
    void registrarVenda_ComEstoqueInsuficiente_DeveLancarExcecao() {
        // Arrange
        Produto produto = new Produto(1, "Cerveja", 10.0, 5);
        ItemVenda item = new ItemVenda(produto, 10);
        List<ItemVenda> itens = Arrays.asList(item);

        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            vendaService.registrarVenda(itens, "DINHEIRO");
        });
    }

    @Test
    void registrarVenda_ComTipoPagamentoVazio_DeveLancarExcecao() {
        // Arrange
        Produto produto = new Produto(1, "Cerveja", 10.0, 100);
        ItemVenda item = new ItemVenda(produto, 5);
        List<ItemVenda> itens = Arrays.asList(item);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            vendaService.registrarVenda(itens, "");
        });
    }

    @Test
    void registrarVenda_ComTipoPagamentoNulo_DeveLancarExcecao() {
        // Arrange
        Produto produto = new Produto(1, "Cerveja", 10.0, 100);
        ItemVenda item = new ItemVenda(produto, 5);
        List<ItemVenda> itens = Arrays.asList(item);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            vendaService.registrarVenda(itens, null);
        });
    }

}
