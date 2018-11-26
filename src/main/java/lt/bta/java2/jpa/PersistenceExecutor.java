package lt.bta.java2.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Function;

public class PersistenceExecutor {

    private static final String PERSISTENCE_UNIT_NAME = "my-persistence-unit";

    private static final EntityManagerFactory emf;

    static {
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial EntityManagerFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }



    public static <R> R executeInsideTransaction(Function<EntityManager, R> action) {
        EntityManager entityManager = createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            R res = action.apply(entityManager);
            transaction.commit();
            return res;
        }
        catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
        finally {
            entityManager.close();
        }
    }
}
