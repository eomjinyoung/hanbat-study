# Lab04 - Spring Boot 기반 웹 애플리케이션 프로젝트 만들기

## 개요
이번 실습에서는 Spring Boot 기반의 웹 애플리케이션 프로젝트를 생성하고 실행하는 방법을 배웁니다.

## 목표

- Spring Boot 프로젝트를 생성하는 방법
- 기본적인 웹 애플리케이션 구조 이해
- 간단한 REST API 구현

## 실습

### 1. Spring Boot 웹 프로젝트 설정

- Spring Boot 플러그인 추가
  - Gradle 홈페이지 > Community > Community Plugins 페이지로 이동
  - `spring` 키워드 검색 > `org.springframework.boot` 플러그인 선택
  - `Other Versions`에서 `3.5.4` 선택
  - plugins DSL 을 build.gradle 파일에 추가
    ```groovy
    plugins {
        id 'java' // application 플러그인을 java 플러그인으로 변경
        id 'org.springframework.boot' version '3.5.4' // 추가
    }
    ```
  - `application` 플러그인 관련 설정 삭제
    ```groovy
    // 삭제
    application {
        mainClass = 'org.example.myapp.App'
    }
    ```
  - `run` 태스크를 `bootRun`으로 변경
    ```groovy
    // 변경
    tasks.named('bootRun') { // run --> bootRun
        standardInput = System.in
    }
    ```
  - 애플리케이션 실행
    - `myapp` > `Tasks` > `application` > `bootRun` 태스크 실행
      - 전체 패키지에서 main() 메서드를 가진 클래스를 찾아 실행합니다.
- `io.spring.dependency-management` 플러그인 추가
  ```groovy
  plugins {
      ... // 기존 플러그인 설정
      id 'io.spring.dependency-management' version '1.1.7' // 추가
  }
  ```
  - 이 플러그인이 추가되면 Spring Boot 플러그인은 `spring-boot-dependencies`라는 이름의 BOM을 자동으로 가져옵니다.
    - BOM(Bill of Materials)은 의존성 관리에 사용되는 파일로, 특정 버전과 호환되는 의존 라이브러리를 그룹화하여 관리합니다.
  - 즉 의존 라이브러리를 가져올 때 버전을 명시하지 않아도 됩니다.
  - Spring Boot 버전에 맞는 의존 라이브러리를 자동으로 가져오기 때문에, 버전 충돌 문제를 줄일 수 있습니다.
- Spring Boot 플러그인 적용 후, 따로 `io.spring.dependency-management` 플러그인을 적용하기
  ```groovy
  plugins {
      id 'java'
      id 'org.springframework.boot' version '3.5.4'
  }
  apply plugin: 'io.spring.dependency-management'
  // 플러그인 로딩 순서:
  // 1. Spring Boot 플러그인 먼저 로드
  // 2. apply plugin으로 dependency-management 나중에 적용
  // 3. Spring Boot가 이미 로드된 상태에서 dependency-management 설정
  ```
  - 이 방식의 특징
    - Spring Boot 팀이 테스트한 호환 버전 사용
    - 버전 충돌 위험 최소화
    - Spring Boot 업그레이드 시 dependency-management도 함께 업데이트
- 웹 애플리케이션 개발에 필요한 의존 라이브러리 추가
  ```groovy
  dependencies {
      implementation 'org.springframework.boot:spring-boot-starter-web'
      testImplementation 'org.springframework.boot:spring-boot-starter-test'
      ... // 기존 의존 라이브러리 설정
  }
  ```
  - `spring-boot-starter-web`: Spring WebMVC와 Tomcat을 포함한 웹 애플리케이션 개발에 필요한 라이브러리
  - `spring-boot-starter-test`: Spring Boot 테스트를 위한 라이브러리
- 의존 라이브러리를 프로젝트에 적용
  - Gradle 도구 창에서 `모든 Gradle 프로젝트 동기화` 아이콘 클릭
  - 왼쪽 `프로젝트` 창 > `외부 라이브러리` 노드에서 의존 라이브러리 목록  확인 

### 2. Spring Boot 애플리케이션 서버 실행 
- App.java 
  ```java
  @SpringBootApplication
  public class App {
      public static void main(String[] args) {
          SpringApplication.run(App.class, args);
      }
  }
  ```
- 애플리케이션 실행
  - `myapp` > `Tasks` > `application` > `bootRun` 태스크 실행
  - 터미널에서 다음 명령어로 애플리케이션 실행
    ```bash
    ./gradlew bootRun
    ```
- 애플리케이션 실행 확인
  - 브라우저에서 `http://localhost:8080` 주소로 접속
  - "Whitelabel Error Page"가 표시되면 정상적으로 실행된 것입니다.

### 3. 실행 파일 만들기

- 실행 파일 만들기
  - `myapp` > `Tasks` > `build` > `bootJar` 태스크 실행
  - 터미널에서 다음 명령어로 실행 파일 만들기
    ```bash
    ./gradlew bootJar
    ```
  - `build/libs` 디렉터리에 `app.jar` 파일이 생성됩니다.
- 실행 파일 실행
  - 터미널에서 다음 명령어로 실행 파일 실행
    ```bash
    java -jar app/build/libs/app.jar
    ```
  - 브라우저에서 `http://localhost:8080` 주소로 접속하여 정상적으로 실행되는지 확인합니다.
- JAR 파일명 변경하기
  - `build.gradle` 파일에 다음 설정 추가
    ```groovy
    bootJar {
        archiveFileName = 'myapp.jar'
    }
    ```
  - 다시 `bootJar` 태스크 실행
  - `build/libs` 디렉터리에 `myapp.jar` 파일이 생성됩니다.
  - 실행 명령어도 변경
    ```bash
    java -jar app/build/libs/myapp.jar
    ``` 
- JAR 파일명 및 버전 변경하기
  - `build.gradle` 파일에 다음 설정 추가
    ```groovy
    bootJar {
        archiveFileName = 'myapp-1.0.0.jar'
    }
    ```
  - 다시 `bootJar` 태스크 실행
  - `build/libs` 디렉터리에 `myapp-1.0.0.jar` 파일이 생성됩니다.
  - 실행 명령어도 변경
    ```bash
    java -jar app/build/libs/myapp-1.0.0.jar
    ```

### 4. `hello, world` 구현하기

- App 클래스에 hello() 메서드 추가
  ```java
  @RestController // REST API 컨트롤러로 지정
  @SpringBootApplication
  public class App {
    ... // 기존 코드
    
    @GetMapping("/hello") // HTTP GET 요청을 처리하는 메서드 지정
    public String hello() {
        return "hello, world"; // 클라이언트에게 리턴할 콘텐트
    }
  }
  ```
- 애플리케이션 실행
  - `myapp` > `Tasks` > `application` > `bootRun` 태스크 실행 또는 재실행
  - 웹브라우저에서 `http://localhost:8080/hello` 주소로 접속
  - "hello, world" 메시지가 표시되면 정상적으로 동작하는 것입니다.

### 5. 값 입력 받기

- App 클래스에 helloWithName() 메서드 추가
  ```java
  @RestController
  @SpringBootApplication
  public class App {
      ... // 기존 코드
      
      @GetMapping("/hello2") // URL 경로에서 name 값을 받음
      public String helloWithName(String name) {
          return "안녕하세요, " + name + "님!"; // 입력받은 이름을 포함한 메시지 리턴
      }
  }
  ```
- 애플리케이션 실행
  - `myapp` > `Tasks` > `application` > `bootRun` 태스크 실행 또는 재실행
  - 웹브라우저에서 `http://localhost:8080/hello2?name=홍길동` 주소로 접속
  - "안녕하세요, 홍길동님!" 메시지가 표시되면 정상적으로 동작하는 것입니다.

### 6. 기존 컨트롤러를 변경했을 때 자동으로 해당 컨트롤러를 다시 로딩하는 법

- `build.gradle` 파일에 다음 설정 추가
  ```groovy
  dependencies {
      ... // 기존 의존 라이브러리 설정
      developmentOnly 'org.springframework.boot:spring-boot-devtools'
  }
  ```
- 의존 라이브러리를 프로젝트에 적용
  - Gradle 도구 창에서 `모든 Gradle 프로젝트 동기화` 아이콘 클릭
  - 왼쪽 `프로젝트` 창 > `외부 라이브러리` 노드에서 의존 라이브러리 목록  확인 
- 애플리케이션 재실행
  - `myapp` > `Tasks` > `application` > `bootRun` 태스크 실행 또는 재실행
- 애플리케이션 도움말 기능 추가
  - `App.java` 파일에 다음 메서드 추가
    ```java
    @GetMapping("/help")
    public String help() {
        return "이 애플리케이션은 Spring Boot로 개발되었습니다.";
    }
    ```
  - `myapp` > `Tasks` > `build` > `classes` 태스크 실행
  - 애플리케이션 재로딩 확인 
  - 웹브라우저에서 `http://localhost:8080/help` 주소로 접속 및 실행 확인
- 소스 파일을 변경한 후 저장하면 자동으로 컴파일 하기
  - IntelliJ IDEA에서 `Preferences` > `Build, Execution, Deployment` > `Compiler`로 이동
  - `Build project automatically` 옵션을 체크
  - help() 메서드의 리턴 값을 변경
    ```java
    @GetMapping("/help")
    public String help() {
        return "이 애플리케이션은 Spring Boot로 개발되었습니다. (자동 컴파일 테스트)";
    }
    ```
  - 저장 후 애플리케이션이 자동으로 다시 로딩되는지 확인
  - 웹브라우저에서 `http://localhost:8080/help` 주소로 접속 및 실행 확인

### 7. application.properties 파일 또는 application.yml 설정

- `src/main/resources` 디렉터리에 `application.properties` 파일 생성
- `application.properties` 파일에 다음 설정 추가
  ```properties
  server.port=8888
  spring.application.name=myapp
  ```
  - 애플리케이션 재실행 및 포트 변경 확인
    - 웹브라우저에서 `http://localhost:8888/hello` 주소로 접속 및 실행 확인
- `application.yml` 파일로 변경
  ```yaml
  server:
    port: 9999 # 서버 포트 번호
  spring:
    application:
      name: myapp # 애플리케이션 이름
  ```
  - 애플리케이션 재실행 및 포트 변경 확인
    - 웹브라우저에서 `http://localhost:9999/hello` 주소로 접속 및 실행 확인 