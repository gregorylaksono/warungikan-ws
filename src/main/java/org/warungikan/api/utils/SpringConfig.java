package org.warungikan.api.utils;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"org.warungikan.db.repository","org.warungikan.db.model", "id.travel.api"})
@ComponentScan(basePackages = { "org.warungikan.api.*" })
@EntityScan("org.warungikan.db.model")
public  class SpringConfig {

}
