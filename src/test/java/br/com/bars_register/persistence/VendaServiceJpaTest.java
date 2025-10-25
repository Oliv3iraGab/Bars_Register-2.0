package br.com.bars_register.persistence;

import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.application.services.VendaService;
import br.com.bars_register.domain.ItemVenda;
import br.com.bars_register.domain.Produto;
import br.com.bars_register.domain.Venda;
import br.com.bars_register.infrastructure.jpa.JpaUtil;
import br.com.bars_register.infrastructure.repo.JpaProdutoRepository;
import br.com.bars_register.infrastructure.repo.JpaVendaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VendaServiceJpaTest {
    private ProdutoRepository produtoRepo;
    private VendaRepository vendaRepo;
    private VendaService vendaService;

    @BeforeEach
    void setup() {
        JpaUtil.init();
        EntityManager em = JpaUtil.getEntityManager();
        JpaUtil.inTransaction(() -> {
            em.createQuery("DELETE FROM ItemVenda").executeUpdate();
            em.createQuery("DELETE FROM Venda").executeUpdate();
            em.createQuery("DELETE FROM Produto").executeUpdate();
        });
        produtoRepo = new JpaProdutoRepository();
        vendaRepo = new JpaVendaRepository();
        vendaService = new VendaService(vendaRepo, produtoRepo);
    }

    @Test
    void registrarVendaAtualizaEstoqueEPersisteItens() {
        Produto p = produtoRepo.save(new Produto(0, "Café", 5.0, 100));
        Venda venda = vendaService.registrarVenda(List.of(new ItemVenda(p, 2)), "DINHEIRO");
        assertTrue(venda.getId() > 0);
        assertEquals(1, venda.getItens().size());
        // estoque atualizado
        Produto atualizado = produtoRepo.findById(p.getId()).orElseThrow();
        assertEquals(98, atualizado.getEstoque());
    }

    @Test
    void vendaComEstoqueInsuficienteFazRollback() {
        Produto p = produtoRepo.save(new Produto(0, "Bolo", 10.0, 1));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> vendaService.registrarVenda(List.of(new ItemVenda(p, 2)), "PIX"));
        assertTrue(ex.getMessage().contains("Estoque insuficiente"));
        // Verifica que estoque não foi alterado
        Produto pos = produtoRepo.findById(p.getId()).orElseThrow();
        assertEquals(1, pos.getEstoque());
        // Não deve haver vendas registradas
        assertTrue(vendaRepo.findAll().isEmpty());
    }
}
