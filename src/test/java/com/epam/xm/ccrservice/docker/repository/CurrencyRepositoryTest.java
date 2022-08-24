package com.epam.xm.ccrservice.docker.repository;

import com.epam.xm.ccrservice.model.projection.CurrencyRange;
import com.epam.xm.ccrservice.repository.CurrencyRepository;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CurrencyRepositoryTest.TestConfig.class)
@Sql(scripts = {"classpath:db/migration/V18_08_22_1122__CREATE-TABLE.sql",
        "classpath:sql/insert-currencies.sql"})
public class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    public void testGetRanges() {
        List<CurrencyRange> currencyRanges = currencyRepository.getCurrencyRanges();

        assertThat(currencyRanges, contains(allOf(hasProperty("currencyCode", equalTo("DOGE")),
                        hasProperty("normalizedRange", closeTo(BigDecimal.valueOf(1.000000), BigDecimal.valueOf(0)))),
                allOf(hasProperty("currencyCode", equalTo("BTC")),
                        hasProperty("normalizedRange", closeTo(BigDecimal.valueOf(0.250000), BigDecimal.valueOf(0))))));
    }

    @Configuration
    @EnableJpaRepositories(basePackageClasses = CurrencyRepository.class)
    static class TestConfig {

        @Bean
        public MySQLContainer<?> mySQLContainer() {

            DockerImageName MYSQL_IMAGE = DockerImageName.parse("mysql:8.0.28");

            MySQLContainer<?> mySqlContainer = new MySQLContainer<>(MYSQL_IMAGE)
                    .withEnv(Map.of("MYSQL_DATABASE", "cryptodb",
                            "MYSQL_USER","crypto",
                            "MYSQL_PASSWORD", "crypto",
                            "MYSQL_ROOT_PASSWORD", "test"))
                    .withDatabaseName("cryptodb")
                    .withUsername("crypto")
                    .withPassword("crypto");

            mySqlContainer.start();

            return mySqlContainer;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory(MySQLContainer<?> mySQLContainer) {

            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setUser(mySQLContainer.getUsername());
            mysqlDataSource.setPassword(mySQLContainer.getPassword());
            mysqlDataSource.setUrl(mySQLContainer.getJdbcUrl());

            LocalContainerEntityManagerFactoryBean localContainer =
                    new LocalContainerEntityManagerFactoryBean();
            localContainer.setDataSource(mysqlDataSource);

            localContainer.setPackagesToScan("com.epam.xm.ccrservice.model");

            HibernateJpaVendorAdapter va = new HibernateJpaVendorAdapter();

            localContainer.setJpaVendorAdapter(va);

            Properties properties = new Properties();

      /*
        hibernate.hbm2ddl.auto none by default which is what is needed
        See AvailableSetting.HBM2DDL_AUTO for details
      */
            properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLDialect");
            properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY,
                    CamelCaseToUnderscoresNamingStrategy.class.getName());
            properties.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY,
                    SpringImplicitNamingStrategy.class.getName());

            localContainer.setJpaProperties(properties);
            localContainer.afterPropertiesSet();

            return localContainer;
        }

        @Bean
        public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
        }
    }
}
