package br.com.bars_register.infrastructure.repo;

import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.domain.Venda;
import br.com.bars_register.infrastructure.jpa.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class JpaVendaRepository implements VendaRepository {
    @Override
    public Venda save(Venda venda) {
        EntityManager em = JpaUtil.getEntityManager();
        if (em.getTransaction().isActive()) {
            // Persist cascade for items
            em.persist(venda);
            return venda;
        }
        try {
            return JpaUtil.inTransaction(() -> {
                EntityManager em2 = JpaUtil.getEntityManager();
                em2.persist(venda);
                return venda;
            });
        } catch (RuntimeException e) {
            throw new br.com.bars_register.infrastructure.persistence.DataPersistenceException("Falha ao salvar venda", e);
        }
    }

    @Override
    public Optional<Venda> findById(int id) {
        EntityManager em = JpaUtil.getEntityManager();
        Venda v = em.find(Venda.class, id);
        return Optional.ofNullable(v);
    }

    @Override
    public List<Venda> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Venda> q = em.createQuery("SELECT v FROM Venda v ORDER BY v.dataVenda DESC", Venda.class);
        return q.getResultList();
    }

    @Override
    public List<Venda> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Venda> q = em.createQuery(
                "SELECT v FROM Venda v WHERE v.dataVenda BETWEEN :inicio AND :fim ORDER BY v.dataVenda",
                Venda.class);
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        return q.getResultList();
    }
}
