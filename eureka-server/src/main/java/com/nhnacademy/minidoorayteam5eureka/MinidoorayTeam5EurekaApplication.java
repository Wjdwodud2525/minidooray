package com.nhnacademy.minidoorayteam5eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class MinidoorayTeam5EurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinidoorayTeam5EurekaApplication.class, args);
    }

}
