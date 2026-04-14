package com.wanted.naeil.global.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.wanted.naeil")
@EntityScan(basePackages = "com.wanted.naeil")
public class JpaConfig {

}
