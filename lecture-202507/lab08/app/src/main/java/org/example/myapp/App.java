package org.example.myapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SpringBootApplication
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
    System.out.println("Spring Boot 웹 애플리케이션 서버 시작!");
  }

  @GetMapping("/help") // "/help" URL 요청을 help.html 템플릿으로 매핑
  public String help() {
    return "help"; // templates/help.html 파일을 렌더링
  }
}
