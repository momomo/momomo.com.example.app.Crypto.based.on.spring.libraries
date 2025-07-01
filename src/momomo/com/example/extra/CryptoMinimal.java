package momomo.com.example.extra;

import momomo.com.db.$DatabasePostgres;
import momomo.com.db.$SessionConfig;
import momomo.com.db.$TransactionalSpring;
import momomo.com.db.entitymanager.$EntityManagerRepository;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static momomo.com.example.extra.PUBLIC_STATIC_VOID_MAIN.CONTEXT;

/**
 * @author Joseph S.
 */
@Configuration public class CryptoMinimal {
    
    /////////////////////////////////////////////////////////////////////
    
    public static CryptoTransactionalRepository repository = CONTEXT.getBean(CryptoTransactionalRepository.class);
    
    /////////////////////////////////////////////////////////////////////
    
    public static final class CryptoDatabase implements $DatabasePostgres {
        @Override public String name() {
            return "crypto_database_name_based_on_xxxxx_libraries";  // This database will be created in postgres if it does not exist already
        }
    
        @Override public String password() {
            return "postgres";
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    
    @Configuration public static class CryptoSessionConfig extends $SessionConfig<CryptoDatabase> {
        public CryptoSessionConfig() {
            super(new CryptoDatabase());
        }
    
        @Override protected String[] packages() {
            return new String[]{"momomo/com/example/app/entities"}; // The package to scan for entities
        }
    
        @Bean(name = "Crypto.persistence.unit")
        public SessionFactory create() {
            return super.create();                                 // We delegate to super in order for the @Bean annotation to exist 
        }
    
        @Bean(name = "Crypto.transaction.unit")
        public JpaTransactionManager platformTransactionManager() {
            return new JpaTransactionManager(create());
        }
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Note, both a repository and a transactional instance class in one! 
     */
    @Component public static final class CryptoTransactionalRepository implements $EntityManagerRepository, $TransactionalSpring {
        @PersistenceContext(name = "Crypto.persistence.unit") 
        EntityManager entityManager; @Override public EntityManager entityManager() { 
            return entityManager; 
        }
        
        ///////////
        
        @Resource(name = "Crypto.transaction.unit")
        PlatformTransactionManager platformTransactionManager; @Override public PlatformTransactionManager platformTransactionManager() { 
            return platformTransactionManager; 
        }
    }
}
