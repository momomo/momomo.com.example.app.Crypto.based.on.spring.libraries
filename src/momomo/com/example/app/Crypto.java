package momomo.com.example.app;

import momomo.com.db.$DatabasePostgres;
import momomo.com.db.$Service;
import momomo.com.db.$SessionConfig;
import momomo.com.db.$TransactionalSpring;
import momomo.com.db.entities.$EntityId;
import momomo.com.db.entitymanager.$EntityManagerRepository;
import org.hibernate.SessionFactory;
import org.hibernate.tool.schema.TargetType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static momomo.com.example.extra.PUBLIC_STATIC_VOID_MAIN.CONTEXT;
import static org.hibernate.tool.hbm2ddl.SchemaExport.Action;

/**
 * @author Joseph S.
 */
@Configuration public class Crypto {
    
    private static final CryptoDatabase DATABASE = new CryptoDatabase();
    
    /********************************************************************
    We can not declare or inject these as static fields due to Spring CONTEXT not being ready at this point.
    Things won't work for this example. We've tried. We also can not set these ourselves either but we have to use Spring to do it for us.
    To make the static fields work here would have required us to move out the inner classes in order to scan them prior to triggering any logic here. Spring at work telling us how to organize our code, even for an example.  
    ********************************************************************/
    private static CryptoTransactional TRANSACTIONAL() { return CONTEXT.getBean(CryptoTransactional.class); }
    private static CryptoRepository REPOSITORY()    { return CONTEXT.getBean(CryptoRepository.class); }
    public  static CryptoTransactionalRepository repository()    { return CONTEXT.getBean(CryptoTransactionalRepository.class); }
    
    /////////////////////////////////////////////////////////////////////
    // All classes used above can be found declared in this file below 
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Our database setup also gives us access to execute JDBC queries anytime should we require that.
     * 
     * See {@link momomo.com.example.extra.CryptoLargest.CryptoDatabase} for more configuration options with plenty of comments. Also check the superclass.
     */
    public static final class CryptoDatabase implements $DatabasePostgres {
        @Override public String name() {
            return "crypto_database_name_based_on_spring_libraries";  // This database will be created in postgres if it does not exist already
        }
        
        @Override public String password() {
            return "postgres";
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    /**
     * See {@link momomo.com.example.extra.CryptoLargest.CryptoSessionConfig} for more configuration options with plenty of comments. Also check the superclass.
     */
    @Configuration public static class CryptoSessionConfig extends $SessionConfig<CryptoDatabase> {
    
        public CryptoSessionConfig() {
            super(DATABASE);
        }
    
        @Override protected String[] packages() {
            return new String[]{"momomo/com/example/app/entities"}; // The package to scan for entities
        }
    
        @Bean(name = "shop.persistence.unit")
        public SessionFactory create() {
            return super.create();                                 // We delegate to super in order for the @Bean annotation to exist 
        }
    
        @Bean(name = "shop.transaction.unit")
        public JpaTransactionManager platformTransactionManager() {
            return new JpaTransactionManager(create());
        }
    
        @Override protected Export export(Export export) {
            return export.target(TargetType.DATABASE).action(Action.BOTH);
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    @Component public static final class CryptoTransactional implements $TransactionalSpring {
        @Resource(name = "shop.transaction.unit")
        PlatformTransactionManager platformTransactionManager; @Override public PlatformTransactionManager platformTransactionManager() {
            return platformTransactionManager;
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    /**
     * We set up the "repository" which gives you access to save, find, findByProperty, delete ...
     */
    @Component public static final class CryptoRepository implements $EntityManagerRepository {
        @PersistenceContext(name = "shop.persistence.unit")
        EntityManager entityManager; @Override public EntityManager entityManager() {
            return entityManager;
        }
    }
    
    /**
     * We set up a combo of repository and transactional 
     */
    @Component public static final class CryptoTransactionalRepository implements $EntityManagerRepository, $TransactionalSpring {
        @Override public EntityManager entityManager() {
            return Crypto.REPOSITORY().entityManager();
        }
        
        @Override public PlatformTransactionManager platformTransactionManager() {
            return Crypto.TRANSACTIONAL().platformTransactionManager();
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    public static abstract class CryptoService<T extends $EntityId> extends $Service<T> implements $TransactionalSpring {
        @Override public CryptoRepository repository() {
            return Crypto.REPOSITORY();
        }
        @Override public PlatformTransactionManager platformTransactionManager() {
            return Crypto.TRANSACTIONAL().platformTransactionManager();
        }
    }
    
}
