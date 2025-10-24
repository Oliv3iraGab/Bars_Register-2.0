package br.com.bars_register.infrastructure.jpa;

import br.com.bars_register.domain.Produto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProdutoEntityTest {

    @Test
    void toDomain_DeveConverterEntityParaDomain() {
        // Arrange
        ProdutoEntity entity = new ProdutoEntity("Cerveja", 10.0, 100);
        entity.setId(1);

        // Act
        Produto produto = entity.toDomain();

        // Assert
        assertNotNull(produto);
        assertEquals(1, produto.getId());
        assertEquals("Cerveja", produto.getNome());
        assertEquals(10.0, produto.getPreco());
        assertEquals(100, produto.getEstoque());
    }

    @Test
    void fromDomain_DeveConverterDomainParaEntity() {
        // Arrange
        Produto produto = new Produto(1, "Refrigerante", 7.5, 50);

        // Act
        ProdutoEntity entity = ProdutoEntity.fromDomain(produto);

        // Assert
        assertNotNull(entity);
        assertEquals(1, entity.getId());
        assertEquals("Refrigerante", entity.getNome());
        assertEquals(7.5, entity.getPreco());
        assertEquals(50, entity.getEstoque());
    }

    @Test
    void gettersAndSetters_DeveFuncionarCorretamente() {
        // Arrange
        ProdutoEntity entity = new ProdutoEntity();

        // Act
        entity.setId(1);
        entity.setNome("Água");
        entity.setPreco(3.0);
        entity.setEstoque(200);

        // Assert
        assertEquals(1, entity.getId());
        assertEquals("Água", entity.getNome());
        assertEquals(3.0, entity.getPreco());
        assertEquals(200, entity.getEstoque());
    }

    @Test
    void constructor_ComParametros_DeveInicializarCorretamente() {
        // Act
        ProdutoEntity entity = new ProdutoEntity("Suco", 5.0, 75);

        // Assert
        assertEquals("Suco", entity.getNome());
        assertEquals(5.0, entity.getPreco());
        assertEquals(75, entity.getEstoque());
    }

    @Test
    void constructor_SemParametros_DeveCriarInstanciaVazia() {
        // Act
        ProdutoEntity entity = new ProdutoEntity();

        // Assert
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getNome());
        assertNull(entity.getPreco());
        assertNull(entity.getEstoque());
    }
}
