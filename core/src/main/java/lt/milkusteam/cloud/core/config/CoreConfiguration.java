package lt.milkusteam.cloud.core.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by gediminas on 3/30/16.
 */
@Configuration
@ComponentScan(basePackages = "lt.milkusteam.cloud.core")
@EnableJpaRepositories(basePackages = "lt.milkusteam.cloud.core.dao.repository")
@EnableTransactionManagement
@PropertySource({"classpath:database.properties", "classpath:dropbox-app.properties"})
public class CoreConfiguration {

    @Value("${db.server}")
    private String databaseServer;

    @Value("${db.user}")
    private String databaseUser;

    @Value("${db.password}")
    private String databasePassword;

    @Value("${db.database}")
    private String databaseName;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    DataSource dataSource() {
        Properties props = new Properties();
        props.setProperty("dataSourceClassName", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        props.setProperty("dataSource.serverName", databaseServer);
        props.setProperty("dataSource.user", databaseUser);
        props.setProperty("dataSource.password", databasePassword);
        props.setProperty("dataSource.databaseName", databaseName);
        props.setProperty("maximumPoolSize", "5");

        return new HikariDataSource(new HikariConfig(props));
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan("lt.milkusteam.cloud.core.model");
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        jpaProperties.put("hibernate.format_sql", true);
        jpaProperties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
        jpaProperties.put("hibernate.show_sql", true);
        jpaProperties.put("hibernate.id.new_generator_mappings", false);

        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }
    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }
}
