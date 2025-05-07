package com.zura.gymCRM;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient

public class GymCrmApplication {

  public static void main(String[] args) {
    SpringApplication.run(GymCrmApplication.class, args);
  }

  @Bean
  CommandLineRunner init() {
    return args -> {
      System.out.println("Application started with a CommandLineRunner bean.");
    };
  }


}