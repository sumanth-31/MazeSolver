package com.uga.mazesolvingagent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.uga.mazesolvingagent.service.MazeSolverService;

@Configuration
public class AppConfig {
    @Bean
    MazeSolverService getMazeSolverService() {
        return new MazeSolverService();
    }
}
