# Lab09 - SQL Mapper 도입하기

## 개요
이번 실습에서는 SQL Mapper를 도입하여 데이터베이스와의 연동을 보다 효율적으로 처리하는 방법을 학습합니다. JDBC를 직접 사용하던 방식에서 Mybatis와 같은 SQL Mapper 프레임워크를 활용하여 SQL 쿼리와 자바 객체 간의 매핑을 자동화합니다.

## 목표

- SQL Mapper의 개념과 장점을 이해한다.
- Mybatis를 설정하고 사용하는 방법을 익힌다.
- Mybatis를 이용하여 데이터베이스 CRUD(Create, Read, Update, Delete) 작업을 구현한다.

## 실습

### 1. Mybatis 라이브러리 설치

- `build.gradle` 파일에 Mybatis 의존성을 추가합니다.
    ```groovy
    dependencies {
        implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.4'
    }
    ```
    - Gradle 패널에서 `모든 Gradle 프로젝트 동기화` 버튼을 클릭하여 의존성을 다운로드하여 프로젝트에 적용합니다.

### 2. Mybatis 설정 파일 작성

- `application.yml` 파일에 데이터베이스 연결 정보와 Mybatis 설정을 추가합니다.
    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/studydb # DB URL
        username: study # DB 사용자 이름
        password: study # DB 비밀번호
        driver-class-name: com.mysql.cj.jdbc.Driver # MySQL 드라이버 클래스

    mybatis:
      type-aliases-package: org.example.myapp.domain,org.example.myapp.dto
    ```
    - 도메인 클래스와 DTO 클래스의 패키지를 지정하여 Mybatis가 해당 클래스를 자동으로 매핑할 수 있도록 합니다.

### 3. SQL Mapper XML 파일 작성

- `src/main/resources/org/example/myapp/repository/BoardRepository.xml` 파일을 생성하고, `BoardRepository` 클래스에 있던 SQL 쿼리를 이 파일로 옮깁니다.
    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper
            PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

    <mapper namespace="org.example.myapp.repository.BoardRepository">

        <!-- 게시글 추가 -->
        <insert id="insert" parameterType="Board">
            INSERT INTO boards (title, content)
            VALUES (#{title}, #{content})
        </insert>

        <!-- 게시글 목록 조회 -->
        <select id="findAll" resultType="org.example.myapp.dto.BoardSummaryDto">
            SELECT no, title, created_date AS createdDate, view_count AS viewCount
            FROM boards
        </select>

        <!-- 게시글 상세 조회 -->
        <select id="findByNo" parameterType="long" resultType="BoardDetailDto">
            SELECT no, title, content, view_count AS viewCount, created_date AS createdDate
            FROM boards
            WHERE no = #{no}
        </select>

        <!-- 게시글 수정 -->
        <update id="update" parameterType="Board">
            UPDATE boards
            SET title = #{title}, content = #{content}
            WHERE no = #{no}
        </update>

        <!-- 조회수 증가 -->
        <update id="updateViewCount" parameterType="long">
            UPDATE boards
            SET view_count = view_count + 1
            WHERE no = #{no}
        </update>

        <!-- 게시글 삭제 -->
        <delete id="delete" parameterType="long">
            DELETE FROM boards
            WHERE no = #{no}
        </delete>

        <!-- 게시글 개수 조회 -->
        <select id="count" resultType="int">
            SELECT COUNT(*)
            FROM boards
        </select>

    </mapper>
    ```

### 4. Repository 인터페이스 작성
- `BoardRepository` 클래스를 인터페이스를 전환합니다.
  ```java
  @Mapper
  public interface BoardRepository {

    void insert(Board board);

    List<BoardSummaryDto> findAll();

    BoardDetailDto findByNo(Long no);

    void update(Board board);

    void updateViewCount(Long no);

    void delete(Long no);

    int count();
  }
  ```
  - @Mapper 어노테이션을 사용하여 Mybatis가 이 인터페이스를 매핑할 수 있도록 합니다.
  - 인터페이스의 이름 및 패키지와 SQL Mapper 파일의 namespace 값이 일치해야 자동으로 매핑됩니다.
  - 메서드 시그니처는 SQL Mapper 파일에서 정의한 SQL 쿼리 ID와 일치해야 합니다.

### 5. Transaction 설정

- `BoardService` 클래스에 @Transactional 어노테이션을 추가하여 트랜잭션을 관리합니다.
  ```java
  @Service
  @Transactional
  public class BoardService {
      private final BoardRepository boardRepository;

      public BoardService(BoardRepository boardRepository) {
          this.boardRepository = boardRepository;
      }

      // CRUD 메서드 구현...
  }
  ```

- `App` 클래스에 @EnableTransactionManagement 어노테이션을 추가하여 트랜잭션 관리 기능을 활성화합니다.
  ```java
  @SpringBootApplication
  @EnableTransactionManagement
  public class App {
      public static void main(String[] args) {
          SpringApplication.run(App.class, args);
      }
  }
  ``` 