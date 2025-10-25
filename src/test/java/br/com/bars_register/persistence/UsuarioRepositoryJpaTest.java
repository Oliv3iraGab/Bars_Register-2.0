package br.com.bars_register.persistence;

import br.com.bars_register.application.repositories.UsuarioRepository;
import br.com.bars_register.domain.Usuario;
import br.com.bars_register.infrastructure.jpa.JpaUtil;
import br.com.bars_register.infrastructure.repo.JpaUsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioRepositoryJpaTest {
    private UsuarioRepository repo;

    @BeforeEach
    void setup() {
        JpaUtil.init();
        // Clear table
        EntityManager em = JpaUtil.getEntityManager();
        JpaUtil.inTransaction(() -> {
            em.createQuery("DELETE FROM Usuario").executeUpdate();
        });
        repo = new JpaUsuarioRepository();
    }

    @Test
    void saveAndFindByLogin() {
        Usuario u = new Usuario(0, "Maria", "ADMIN", "maria@example.com", true, "maria", "1234");
        repo.save(u);
        assertTrue(u.getId() > 0);
        assertTrue(repo.findByLoginAndSenha("maria", "1234").isPresent());
        assertFalse(repo.findByLoginAndSenha("maria", "wrong").isPresent());
    }
}
