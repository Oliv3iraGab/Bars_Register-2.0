package br.com.bars_register.persistence;

import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.domain.Produto;
import br.com.bars_register.infrastructure.jpa.JpaUtil;
import br.com.bars_register.infrastructure.repo.JpaProdutoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoRepositoryJpaTest {
    private ProdutoRepository repo;

    @BeforeEach
    void setup() {
        JpaUtil.init();
        // Clear tables
        EntityManager em = JpaUtil.getEntityManager();
        JpaUtil.inTransaction(() -> {
            em.createQuery("DELETE FROM ItemVenda").executeUpdate();
            em.createQuery("DELETE FROM Venda").executeUpdate();
            em.createQuery("DELETE FROM Produto").executeUpdate();
        });
        repo = new JpaProdutoRepository();
    }

    @Test
    void saveAndFindById() {
        Produto p = new Produto(0, "Água", 3.5, 50);
        Produto saved = repo.save(p);
        assertTrue(saved.getId() > 0);
        Optional<Produto> found = repo.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Água", found.get().getNome());
    }

    @Test
    void updateAndFindAllAndDelete() {
        Produto p = repo.save(new Produto(0, "Suco", 6.0, 20));
        p.setPreco(7.0);
        repo.update(p);
        List<Produto> all = repo.findAll();
        assertFalse(all.isEmpty());
        assertTrue(all.stream().anyMatch(pr -> pr.getId() == p.getId() && pr.getPreco() == 7.0));
        boolean removed = repo.deleteById(p.getId());
        assertTrue(removed);
        assertTrue(repo.findById(p.getId()).isEmpty());
    }
}
