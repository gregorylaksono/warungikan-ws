package org.warungikan.api;

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
@EnableJpaRepositories(basePackages = ("org.warungikan.db.repository"))
@EntityScan(basePackages = "org.warungikan.db.model")
@ComponentScan(basePackages="org.warungikan.api")
public class Launcher {
	private static final Logger log = LoggerFactory.getLogger(Launcher.class);

	public static void main(String[] args) {
		log.info("Test application");
        SpringApplication.run(Launcher.class, args);
        log.info("Finish Test application");
	}

}
