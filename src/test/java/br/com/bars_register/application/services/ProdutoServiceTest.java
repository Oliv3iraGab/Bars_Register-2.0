package br.com.bars_register.application.services;

import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.domain.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    private ProdutoService produtoService;

    @BeforeEach
    void setUp() {
        produtoService = new ProdutoService(produtoRepository);
    }

    @Test
    void cadastrar_DeveRetornarProdutoSalvo() {
        // Arrange
        Produto produto = new Produto(0, "Cerveja", 10.0, 100);
        Produto produtoSalvo = new Produto(1, "Cerveja", 10.0, 100);
        
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoSalvo);

        // Act
        Produto resultado = produtoService.cadastrar(produto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Cerveja", resultado.getNome());
        assertEquals(10.0, resultado.getPreco());
        assertEquals(100, resultado.getEstoque());
        verify(produtoRepository).save(produto);
    }

    @Test
    void cadastrar_ComDadosInvalidos_DeveLancarExcecao() {
        // Arrange
        Produto produto = new Produto(0, "", -10.0, -5);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            produtoService.cadastrar(produto);
        });
    }

    @Test
    void buscarPorId_ProdutoExistente_DeveRetornarProduto() {
        // Arrange
        Produto produto = new Produto(1, "Cerveja", 10.0, 100);
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));

        // Act
        Optional<Produto> resultado = produtoService.buscarPorId(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(produto, resultado.get());
        verify(produtoRepository).findById(1);
    }

    @Test
    void buscarPorId_ProdutoNaoExistente_DeveRetornarVazio() {
        // Arrange
        when(produtoRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Produto> resultado = produtoService.buscarPorId(999);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(produtoRepository).findById(999);
    }

    @Test
    void listarTodos_DeveRetornarListaDeProdutos() {
        // Arrange
        List<Produto> produtos = Arrays.asList(
            new Produto(1, "Cerveja", 10.0, 100),
            new Produto(2, "Refrigerante", 7.5, 50)
        );
        when(produtoRepository.findAll()).thenReturn(produtos);

        // Act
        List<Produto> resultado = produtoService.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(produtos, resultado);
        verify(produtoRepository).findAll();
    }

    @Test
    void atualizar_ProdutoExistente_DeveRetornarProdutoAtualizado() {
        // Arrange
        Produto produto = new Produto(1, "Cerveja Premium", 15.0, 80);
        when(produtoRepository.update(any(Produto.class))).thenReturn(produto);

        // Act
        Produto resultado = produtoService.atualizar(produto);

        // Assert
        assertNotNull(resultado);
        assertEquals(produto, resultado);
        verify(produtoRepository).update(produto);
    }

    @Test
    void remover_ProdutoExistente_DeveRetornarTrue() {
        // Arrange
        when(produtoRepository.deleteById(1)).thenReturn(true);

        // Act
        boolean resultado = produtoService.remover(1);

        // Assert
        assertTrue(resultado);
        verify(produtoRepository).deleteById(1);
    }

    @Test
    void remover_ProdutoNaoExistente_DeveRetornarFalse() {
        // Arrange
        when(produtoRepository.deleteById(999)).thenReturn(false);

        // Act
        boolean resultado = produtoService.remover(999);

        // Assert
        assertFalse(resultado);
        verify(produtoRepository).deleteById(999);
    }

    @Test
    void atualizarEstoque_ComQuantidadePositiva_DeveAumentarEstoque() {
        // Arrange
        Produto produto = new Produto(1, "Cerveja", 10.0, 100);
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        when(produtoRepository.update(any(Produto.class))).thenReturn(produto);

        // Act
        produtoService.atualizarEstoque(1, 50);

        // Assert
        assertEquals(150, produto.getEstoque());
        verify(produtoRepository).findById(1);
        verify(produtoRepository).update(produto);
    }

    @Test
    void atualizarEstoque_ComQuantidadeNegativa_DeveDiminuirEstoque() {
        // Arrange
        Produto produto = new Produto(1, "Cerveja", 10.0, 100);
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        when(produtoRepository.update(any(Produto.class))).thenReturn(produto);

        // Act
        produtoService.atualizarEstoque(1, -30);

        // Assert
        assertEquals(70, produto.getEstoque());
        verify(produtoRepository).findById(1);
        verify(produtoRepository).update(produto);
    }

    @Test
    void atualizarEstoque_ProdutoNaoExistente_DeveLancarExcecao() {
        // Arrange
        when(produtoRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            produtoService.atualizarEstoque(999, 10);
        });
    }
}
