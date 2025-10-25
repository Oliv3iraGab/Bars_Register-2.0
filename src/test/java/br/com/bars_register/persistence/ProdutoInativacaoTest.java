package br.com.bars_register.persistence;

import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.application.services.VendaService;
import br.com.bars_register.domain.ItemVenda;
import br.com.bars_register.domain.Produto;
import br.com.bars_register.infrastructure.jpa.JpaUtil;
import br.com.bars_register.infrastructure.repo.JpaProdutoRepository;
import br.com.bars_register.infrastructure.repo.JpaVendaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoInativacaoTest {
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
    void deleteInativaQuandoHaHistorico() {
        Produto p = produtoRepo.save(new Produto(0, "Bebida", 8.0, 10));
        vendaService.registrarVenda(List.of(new ItemVenda(p, 1)), "PIX");
        assertTrue(produtoRepo.hasSalesHistory(p.getId()));
        boolean res = produtoRepo.deleteById(p.getId());
        assertTrue(res);
        Produto after = produtoRepo.findById(p.getId()).orElse(null);
        assertNotNull(after);
        assertEquals("INATIVO", after.getStatus());
        // findAll deve ocultar inativos
        assertTrue(produtoRepo.findAll().stream().noneMatch(pr -> pr.getId() == p.getId()));
        // vendas permanecem
        assertFalse(vendaRepo.findAll().isEmpty());
    }
}
