package br.com.bars_register.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemVendaTest {

    @Test
    void subtotalMultiplicaPrecoPorQuantidade() {
        Produto p = new Produto(1, "Cerveja", 10.0, 100);
        ItemVenda item = new ItemVenda(p, 3);
        assertEquals(30.0, item.getSubtotal(), 0.0001);
    }

    @Test
    void subtotalComPrecoZeroResultaZero() {
        Produto p = new Produto(2, "Agua", 0.0, 100);
        ItemVenda item = new ItemVenda(p, 5);
        assertEquals(0.0, item.getSubtotal(), 0.0001);
    }
}