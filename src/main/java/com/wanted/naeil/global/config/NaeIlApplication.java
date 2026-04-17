package com.wanted.naeil.global.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.wanted.naeil")
@EnableJpaAuditing
@EnableScheduling
public class NaeIlApplication {

    public static void main(String[] args) {
        SpringApplication.run(NaeIlApplication.class, args);
    }

}
