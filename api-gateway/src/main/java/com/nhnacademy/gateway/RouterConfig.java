package com.nhnacademy.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfig {

    @Bean
    RouteLocator routerLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("task-api",
                        p -> p.path("/api/tasks/**")
                                .uri("lb://task-api"))
                .route("account-api",
                        p -> p.path("/api/account/**", "/api/accounts/**")
                                .uri("lb://account-api"))
                .build();
    }
}
