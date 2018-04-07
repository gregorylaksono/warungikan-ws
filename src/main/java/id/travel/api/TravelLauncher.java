package id.travel.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.UserRepository;

@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = ("id.travel.db.repository"), entityManagerFactoryRef="entityManagerFactory")
@EntityScan(basePackages = "id.travel.db.model")
@ComponentScan(basePackages="id.travel.api")
public class TravelLauncher {
	private static final Logger log = LoggerFactory.getLogger(TravelLauncher.class);

	public static void main(String[] args) {
        SpringApplication.run(TravelLauncher.class, args);
	}
	
	@Bean
	public CommandLineRunner demo(UserRepository repository) {
		return (args) -> {
			User u = new User();
			u.setEmail("greg.laksono@gmail.com").setName("Gregory Laksono").setPassword("test");
			repository.save(u);
			
			u = repository.findOne(1L);
			log.info("----------RETRIEVE---------");
			log.info("Email:"+u.getEmail());
			log.info("Name:"+u.getName());
		};
	}

	@Bean
	@Autowired
	public EntityManagerFactory entityManagerFactory(DataSource dataSource) {
	    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
	    vendorAdapter.setGenerateDdl(true);

	    Properties jpaProperties = new Properties();
	    jpaProperties.setProperty("hibernate.show_sql", "true");
	    jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

	    LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
	    localContainerEntityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
	    localContainerEntityManagerFactoryBean.setPackagesToScan("id.travel.db.model");
	    localContainerEntityManagerFactoryBean.setDataSource(dataSource);
	    localContainerEntityManagerFactoryBean.setJpaProperties(jpaProperties);
	    localContainerEntityManagerFactoryBean.afterPropertiesSet();

	    return localContainerEntityManagerFactoryBean.getObject();
	}

	@Bean
	@Autowired
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
	    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
	    jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
	    return jpaTransactionManager;
	}
}
