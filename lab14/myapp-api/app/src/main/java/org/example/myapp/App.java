package org.example.myapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication // MyBatis 매퍼 인터페이스가 위치한 패키지 지정
@EnableTransactionManagement
public class App implements WebMvcConfigurer {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
    System.out.println("Spring Boot 웹 애플리케이션 서버 시작!");
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins("http://localhost:3000")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
        .allowCredentials(true);
  }
}
