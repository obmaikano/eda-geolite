package com.geolite.scenarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.geolite.scenarios.model")
@EnableJpaRepositories("com.geolite.scenarios.repository")
public class ScenarioServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScenarioServiceApplication.class, args);
    }

}
