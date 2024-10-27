package com.geolite.drilling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.geolite.drilling.model")
@EnableJpaRepositories("com.geolite.drilling.repository")
public class DrillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrillingServiceApplication.class, args);
    }

}
