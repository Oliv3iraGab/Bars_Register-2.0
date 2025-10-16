package br.com.bars_register.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VendaTest {

    @Test
    void calcularTotalSomaSubtotaisDosItens() {
        Produto p1 = new Produto(1, "Cerveja", 10.0, 100);
        Produto p2 = new Produto(2, "Refrigerante", 7.5, 50);
        Venda venda = new Venda();
        venda.adicionarItem(new ItemVenda(p1, 2)); // subtotal 20
        venda.adicionarItem(new ItemVenda(p2, 4)); // subtotal 30
        assertEquals(50.0, venda.calcularTotal(), 0.0001);
        assertEquals(50.0, venda.getTotal(), 0.0001);
    }

    @Test
    void calcularTotalSemItensRetornaZero() {
        Venda venda = new Venda();
        assertEquals(0.0, venda.calcularTotal(), 0.0001);
        assertTrue(venda.getItens().isEmpty());
    }
}