package br.com.bars_register.infrastructure.repo;

import br.com.bars_register.application.repositories.UsuarioRepository;
import br.com.bars_register.domain.Usuario;
import br.com.bars_register.infrastructure.jpa.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class JpaUsuarioRepository implements UsuarioRepository {
    @Override
    public Usuario save(Usuario usuario) {
        EntityManager em = JpaUtil.getEntityManager();
        if (em.getTransaction().isActive()) {
            em.persist(usuario);
            return usuario;
        }
        try {
            return JpaUtil.inTransaction(() -> {
                EntityManager em2 = JpaUtil.getEntityManager();
                em2.persist(usuario);
                return usuario;
            });
        } catch (RuntimeException e) {
            throw new br.com.bars_register.infrastructure.persistence.DataPersistenceException("Falha ao salvar usuário", e);
        }
    }

    @Override
    public Usuario update(Usuario usuario) {
        EntityManager em = JpaUtil.getEntityManager();
        if (em.getTransaction().isActive()) {
            em.merge(usuario);
            return usuario;
        }
        try {
            return JpaUtil.inTransaction(() -> {
                EntityManager em2 = JpaUtil.getEntityManager();
                em2.merge(usuario);
                return usuario;
            });
        } catch (RuntimeException e) {
            throw new br.com.bars_register.infrastructure.persistence.DataPersistenceException("Falha ao atualizar usuário", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        EntityManager em = JpaUtil.getEntityManager();
        if (em.getTransaction().isActive()) {
            Usuario u = em.find(Usuario.class, id);
            if (u != null) { em.remove(u); return true; }
            return false;
        }
        try {
            return JpaUtil.inTransaction(() -> {
                EntityManager em2 = JpaUtil.getEntityManager();
                Usuario u = em2.find(Usuario.class, id);
                if (u != null) { em2.remove(u); return true; }
                return false;
            });
        } catch (RuntimeException e) {
            throw new br.com.bars_register.infrastructure.persistence.DataPersistenceException("Falha ao remover usuário", e);
        }
    }

    @Override
    public Optional<Usuario> findById(int id) {
        EntityManager em = JpaUtil.getEntityManager();
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    @Override
    public Optional<Usuario> findByLoginAndSenha(String login, String senha) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Usuario> q = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.login = :login AND u.senha = :senha",
                Usuario.class);
        q.setParameter("login", login);
        q.setParameter("senha", senha);
        List<Usuario> results = q.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Usuario> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Usuario> q = em.createQuery("SELECT u FROM Usuario u ORDER BY u.id", Usuario.class);
        return q.getResultList();
    }
}
