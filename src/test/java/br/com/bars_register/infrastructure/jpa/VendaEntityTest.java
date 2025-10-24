package br.com.bars_register.infrastructure.jpa;

import br.com.bars_register.domain.Venda;
import br.com.bars_register.domain.Produto;
import br.com.bars_register.domain.ItemVenda;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

class VendaEntityTest {

    @Test
    void toDomain_DeveConverterEntityParaDomain() {
        // Arrange
        VendaEntity entity = new VendaEntity(LocalDateTime.now(), "DINHEIRO", 50.0);
        entity.setId(1);

        // Act
        Venda venda = entity.toDomain();

        // Assert
        assertNotNull(venda);
        assertEquals(1, venda.getId());
        assertEquals("DINHEIRO", venda.getTipoPagamento());
        assertEquals(50.0, venda.getTotal());
        assertNotNull(venda.getDataVenda());
    }

    @Test
    void fromDomain_DeveConverterDomainParaEntity() {
        // Arrange
        Venda venda = new Venda();
        venda.setId(1);
        venda.setDataVenda(LocalDateTime.now());
        venda.setTipoPagamento("CARTÃO");
        venda.setTotal(75.0);

        // Act
        VendaEntity entity = VendaEntity.fromDomain(venda);

        // Assert
        assertNotNull(entity);
        assertEquals(1, entity.getId());
        assertEquals("CARTÃO", entity.getTipoPagamento());
        assertEquals(75.0, entity.getTotal());
        assertNotNull(entity.getDataVenda());
    }

    @Test
    void gettersAndSetters_DeveFuncionarCorretamente() {
        // Arrange
        VendaEntity entity = new VendaEntity();

        // Act
        entity.setId(1);
        entity.setDataVenda(LocalDateTime.now());
        entity.setTipoPagamento("PIX");
        entity.setTotal(100.0);

        // Assert
        assertEquals(1, entity.getId());
        assertNotNull(entity.getDataVenda());
        assertEquals("PIX", entity.getTipoPagamento());
        assertEquals(100.0, entity.getTotal());
    }

    @Test
    void constructor_ComParametros_DeveInicializarCorretamente() {
        // Arrange
        LocalDateTime data = LocalDateTime.now();

        // Act
        VendaEntity entity = new VendaEntity(data, "DINHEIRO", 25.0);

        // Assert
        assertEquals(data, entity.getDataVenda());
        assertEquals("DINHEIRO", entity.getTipoPagamento());
        assertEquals(25.0, entity.getTotal());
    }

    @Test
    void constructor_SemParametros_DeveCriarInstanciaVazia() {
        // Act
        VendaEntity entity = new VendaEntity();

        // Assert
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getDataVenda());
        assertNull(entity.getTipoPagamento());
        assertNull(entity.getTotal());
        assertNotNull(entity.getItens());
        assertTrue(entity.getItens().isEmpty());
    }

    @Test
    void itens_DevePermitirAdicionarERemoverItens() {
        // Arrange
        VendaEntity entity = new VendaEntity();
        ItemVendaEntity item1 = new ItemVendaEntity();
        ItemVendaEntity item2 = new ItemVendaEntity();

        // Act
        entity.getItens().add(item1);
        entity.getItens().add(item2);

        // Assert
        assertEquals(2, entity.getItens().size());
        assertTrue(entity.getItens().contains(item1));
        assertTrue(entity.getItens().contains(item2));

        // Act - Remover item
        entity.getItens().remove(item1);

        // Assert
        assertEquals(1, entity.getItens().size());
        assertFalse(entity.getItens().contains(item1));
        assertTrue(entity.getItens().contains(item2));
    }
}
