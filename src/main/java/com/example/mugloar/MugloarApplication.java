package com.example.mugloar;

import com.example.mugloar.service.GameLoopService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MugloarApplication {

    public static void main(String[] args) {
        SpringApplication.run(MugloarApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(GameLoopService gameLoopService) {
        return args -> {
            gameLoopService.runUntil1000Points();
        };
    }
}
