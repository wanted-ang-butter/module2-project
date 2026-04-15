package com.wanted.naeil.global.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.wanted.naeil")
@EnableJpaAuditing
public class NaeIlApplication {

    public static void main(String[] args) {
        SpringApplication.run(NaeIlApplication.class, args);
    }

}
