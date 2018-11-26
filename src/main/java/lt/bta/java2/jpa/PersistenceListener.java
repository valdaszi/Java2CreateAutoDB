package lt.bta.java2.jpa;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

@WebListener
public class PersistenceListener implements ServletContextListener {

    private static Logger logger = Logger.getLogger(PersistenceListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        PersistenceExecutor.getEntityManagerFactory();
        logger.info("EntityManagerFactory initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        PersistenceExecutor.getEntityManagerFactory().close();
        logger.info("EntityManagerFactory closed");
    }
}
