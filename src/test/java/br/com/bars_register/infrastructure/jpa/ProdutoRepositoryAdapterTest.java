package br.com.bars_register.infrastructure.jpa;

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
class ProdutoRepositoryAdapterTest {

    @Mock
    private ProdutoJpaRepository jpaRepository;

    private ProdutoRepositoryAdapter repositoryAdapter;

    @BeforeEach
    void setUp() {
        repositoryAdapter = new ProdutoRepositoryAdapter(jpaRepository);
    }

    @Test
    void save_DeveRetornarProdutoSalvo() {
        // Arrange
        Produto produto = new Produto(0, "Cerveja", 10.0, 100);
        ProdutoEntity entity = new ProdutoEntity("Cerveja", 10.0, 100);
        entity.setId(1);
        
        when(jpaRepository.save(any(ProdutoEntity.class))).thenReturn(entity);

        // Act
        Produto resultado = repositoryAdapter.save(produto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Cerveja", resultado.getNome());
        assertEquals(10.0, resultado.getPreco());
        assertEquals(100, resultado.getEstoque());
        verify(jpaRepository).save(any(ProdutoEntity.class));
    }

    @Test
    void update_DeveRetornarProdutoAtualizado() {
        // Arrange
        Produto produto = new Produto(1, "Cerveja Premium", 15.0, 80);
        ProdutoEntity entity = new ProdutoEntity("Cerveja Premium", 15.0, 80);
        entity.setId(1);
        
        when(jpaRepository.save(any(ProdutoEntity.class))).thenReturn(entity);

        // Act
        Produto resultado = repositoryAdapter.update(produto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Cerveja Premium", resultado.getNome());
        assertEquals(15.0, resultado.getPreco());
        assertEquals(80, resultado.getEstoque());
        verify(jpaRepository).save(any(ProdutoEntity.class));
    }

    @Test
    void deleteById_ProdutoExistente_DeveRetornarTrue() {
        // Arrange
        when(jpaRepository.existsById(1)).thenReturn(true);

        // Act
        boolean resultado = repositoryAdapter.deleteById(1);

        // Assert
        assertTrue(resultado);
        verify(jpaRepository).existsById(1);
        verify(jpaRepository).deleteById(1);
    }

    @Test
    void deleteById_ProdutoNaoExistente_DeveRetornarFalse() {
        // Arrange
        when(jpaRepository.existsById(999)).thenReturn(false);

        // Act
        boolean resultado = repositoryAdapter.deleteById(999);

        // Assert
        assertFalse(resultado);
        verify(jpaRepository).existsById(999);
        verify(jpaRepository, never()).deleteById(anyInt());
    }

    @Test
    void findById_ProdutoExistente_DeveRetornarProduto() {
        // Arrange
        ProdutoEntity entity = new ProdutoEntity("Cerveja", 10.0, 100);
        entity.setId(1);
        
        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        Optional<Produto> resultado = repositoryAdapter.findById(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getId());
        assertEquals("Cerveja", resultado.get().getNome());
        assertEquals(10.0, resultado.get().getPreco());
        assertEquals(100, resultado.get().getEstoque());
        verify(jpaRepository).findById(1);
    }

    @Test
    void findById_ProdutoNaoExistente_DeveRetornarVazio() {
        // Arrange
        when(jpaRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Produto> resultado = repositoryAdapter.findById(999);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(jpaRepository).findById(999);
    }

    @Test
    void findAll_DeveRetornarListaDeProdutos() {
        // Arrange
        ProdutoEntity entity1 = new ProdutoEntity("Cerveja", 10.0, 100);
        entity1.setId(1);
        
        ProdutoEntity entity2 = new ProdutoEntity("Refrigerante", 7.5, 50);
        entity2.setId(2);
        
        List<ProdutoEntity> entities = Arrays.asList(entity1, entity2);
        when(jpaRepository.findAll()).thenReturn(entities);

        // Act
        List<Produto> resultado = repositoryAdapter.findAll();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(1, resultado.get(0).getId());
        assertEquals("Cerveja", resultado.get(0).getNome());
        assertEquals(2, resultado.get(1).getId());
        assertEquals("Refrigerante", resultado.get(1).getNome());
        verify(jpaRepository).findAll();
    }
}
