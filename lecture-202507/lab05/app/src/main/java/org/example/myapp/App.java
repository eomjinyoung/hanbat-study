package org.example.myapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
    System.out.println("Spring Boot 웹 애플리케이션 서버 시작!");
  }

  @GetMapping("/hello")
  public String hello() {
    return "hello, world";
  }

  @GetMapping("/hello2")
  public String helloWithName(String name) {
    return "안녕하세요, " + name + "님!";
  }

  @GetMapping("/help")
  public String help() {
    return "이 애플리케이션은 Spring Boot로 개발되었습니다. (자동 컴파일 테스트)";
  }
}
