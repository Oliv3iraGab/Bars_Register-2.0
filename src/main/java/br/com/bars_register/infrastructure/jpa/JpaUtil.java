package br.com.bars_register.infrastructure.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class JpaUtil {
    private static EntityManagerFactory emf;
    private static final ThreadLocal<EntityManager> TL_EM = new ThreadLocal<>();

    private JpaUtil() {}

    public static synchronized void init() {
        if (emf == null) {
            try {
                // Garante diretório de dados e caminho absoluto para o arquivo H2
                Path dataDir = Paths.get("data").toAbsolutePath();
                Files.createDirectories(dataDir);
                String absFile = dataDir.resolve("barsdb").toString();
                String url = "jdbc:h2:file:" + absFile + ";AUTO_SERVER=TRUE";

                Map<String, Object> props = new HashMap<>();
                props.put("jakarta.persistence.jdbc.url", url);
                props.put("jakarta.persistence.jdbc.user", "sa");
                props.put("jakarta.persistence.jdbc.password", "");

                emf = Persistence.createEntityManagerFactory("barsPU", props);
                System.out.println("[JPA] Inicializado com URL: " + url);
            } catch (Exception e) {
                throw new RuntimeException("Falha ao inicializar JPA", e);
            }
        }
    }

    public static synchronized void shutdown() {
        EntityManager em = TL_EM.get();
        if (em != null && em.isOpen()) {
            em.close();
            TL_EM.remove();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
        }
    }

    public static EntityManager getEntityManager() {
        EntityManager em = TL_EM.get();
        if (em == null || !em.isOpen()) {
            if (emf == null) init();
            em = emf.createEntityManager();
            TL_EM.set(em);
        }
        return em;
    }

    public static void begin() {
        EntityTransaction tx = getEntityManager().getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
    }

    public static void commit() {
        EntityTransaction tx = getEntityManager().getTransaction();
        if (tx.isActive()) {
            tx.commit();
        }
        cleanup();
    }

    public static void rollback() {
        EntityTransaction tx = getEntityManager().getTransaction();
        if (tx.isActive()) {
            tx.rollback();
        }
        cleanup();
    }

    private static void cleanup() {
        EntityManager em = TL_EM.get();
        if (em != null) {
            em.close();
            TL_EM.remove();
        }
    }

    public static <T> T inTransaction(Supplier<T> work) {
        try {
            begin();
            T result = work.get();
            commit();
            return result;
        } catch (RuntimeException e) {
            rollback();
            throw e;
        }
    }

    public static void inTransaction(Runnable work) {
        inTransaction(() -> { work.run(); return null; });
    }
}