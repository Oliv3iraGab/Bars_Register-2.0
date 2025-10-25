package br.com.bars_register.infrastructure.repo;

import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.domain.Produto;
import br.com.bars_register.infrastructure.jpa.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class JpaProdutoRepository implements ProdutoRepository {
    @Override
    public Produto save(Produto produto) {
        EntityManager em = JpaUtil.getEntityManager();
        if (em.getTransaction().isActive()) {
            em.persist(produto);
            return produto;
        }
        try {
            return JpaUtil.inTransaction(() -> {
                EntityManager em2 = JpaUtil.getEntityManager();
                em2.persist(produto);
                return produto;
            });
        } catch (RuntimeException e) {
            throw new br.com.bars_register.infrastructure.persistence.DataPersistenceException("Falha ao salvar produto", e);
        }
    }

    @Override
    public Produto update(Produto produto) {
        EntityManager em = JpaUtil.getEntityManager();
        if (em.getTransaction().isActive()) {
            em.merge(produto);
            return produto;
        }
        try {
            return JpaUtil.inTransaction(() -> {
                EntityManager em2 = JpaUtil.getEntityManager();
                em2.merge(produto);
                return produto;
            });
        } catch (RuntimeException e) {
            throw new br.com.bars_register.infrastructure.persistence.DataPersistenceException("Falha ao atualizar produto", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        EntityManager em = JpaUtil.getEntityManager();
        if (em.getTransaction().isActive()) {
            Produto p = em.find(Produto.class, id);
            if (p != null) {
                // Se houver histórico de vendas, apenas inativa
                Long count = em.createQuery("SELECT COUNT(iv) FROM ItemVenda iv WHERE iv.produto.id = :id", Long.class)
                        .setParameter("id", id)
                        .getSingleResult();
                if (count != null && count > 0) {
                    p.setStatus("INATIVO");
                    em.merge(p);
                    return true;
                } else {
                    em.remove(p);
                    return true;
                }
            }
            return false;
        }
        try {
            return JpaUtil.inTransaction(() -> {
                EntityManager em2 = JpaUtil.getEntityManager();
                Produto p = em2.find(Produto.class, id);
                if (p != null) {
                    Long count = em2.createQuery("SELECT COUNT(iv) FROM ItemVenda iv WHERE iv.produto.id = :id", Long.class)
                            .setParameter("id", id)
                            .getSingleResult();
                    if (count != null && count > 0) {
                        p.setStatus("INATIVO");
                        em2.merge(p);
                        return true;
                    } else {
                        em2.remove(p);
                        return true;
                    }
                }
                return false;
            });
        } catch (RuntimeException e) {
            throw new br.com.bars_register.infrastructure.persistence.DataPersistenceException("Falha ao remover produto", e);
        }
    }

    @Override
    public Optional<Produto> findById(int id) {
        EntityManager em = JpaUtil.getEntityManager();
        Produto p = em.find(Produto.class, id);
        return Optional.ofNullable(p);
    }

    @Override
    public List<Produto> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Produto> q = em.createQuery("SELECT p FROM Produto p WHERE p.status IS NULL OR p.status <> 'INATIVO' ORDER BY p.id", Produto.class);
        return q.getResultList();
    }

    @Override
    public boolean hasSalesHistory(int id) {
        EntityManager em = JpaUtil.getEntityManager();
        Long count = em.createQuery("SELECT COUNT(iv) FROM ItemVenda iv WHERE iv.produto.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count != null && count > 0;
    }
}
