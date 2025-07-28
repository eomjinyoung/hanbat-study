# Lab08 - DBMS 와 JDBC 프로그래밍

## 개요
이번 실습에서는 Spring Boot 애플리케이션에서 데이터베이스와의 연동을 위해 JDBC를 사용하여 게시판 기능을 구현합니다. 이를 통해 데이터베이스 연결, SQL 쿼리 실행, 결과 처리 등을 학습합니다.

## 목표

- SQL 기본 사용법
- JDBC API와 JDBC Driver의 관계 이해
- JDBC를 이용한 데이터베이스 연결 및 쿼리 실행

## 실습

### 1. Docker Desktop 설치

Docker Desktop을 설치하여 MySQL 데이터베이스를 컨테이너로 실행합니다. Docker가 설치되어 있지 않다면 [Docker 공식 사이트](https://www.docker.com/products/docker-desktop/)에서 설치 방법을 참고하세요.

### 2. MySQL 컨테이너 실행

- MySQL 데이터베이스의 설정 파일을 준비합니다.
  ```
  # MySQL 설정 파일 (my.cnf)
  [mysqld]
  character-set-server=utf8mb4
  collation-server=utf8mb4_unicode_ci
  init-connect='SET NAMES utf8mb4'

  [mysql]
  default-character-set=utf8mb4

  [client]
  default-character-set=utf8mb4
  ```
다음 명령어를 사용하여 MySQL 컨테이너를 실행합니다.

- Docker 명령어를 사용하여 MySQL 컨테이너를 실행합니다. 아래 명령어를 터미널에 입력합니다.
  - macOS/Linux:
    ```bash
    docker run --name mysql-container \
      -e MYSQL_ROOT_PASSWORD=root \
      -e MYSQL_DATABASE=studydb \
      -e MYSQL_USER=study \
      -e MYSQL_PASSWORD=study \
      -v ${PWD}/my.cnf:/etc/mysql/conf.d/my.cnf \
      -d -p 3306:3306 mysql:latest 
    ```
  - Windows:
    ```bash
    docker run --name mysql-container `
      -e MYSQL_ROOT_PASSWORD=root `
      -e MYSQL_DATABASE=studydb `
      -e MYSQL_USER=study `
      -e MYSQL_PASSWORD=study `
      -v ${PWD}/my.cnf:/etc/mysql/conf.d/my.cnf `
      -d -p 3306:3306 mysql:latest
    ```
  - 이 명령어는 MySQL 최신 버전을 기반으로 하는 컨테이너를 생성합니다.
  - `MYSQL_ROOT_PASSWORD=root`: 루트 비밀번호를 `root`로 설정합니다. 
  - `MYSQL_USER=study`: `study` 이름으로 사용자를 생성합니다.
  - `MYSQL_PASSWORD=study`: `study` 사용자의 비밀번호를 `study`로 설정합니다.
  - 컨테이너가 실행되면, 호스트의 3306 포트가 컨테이너의 3306 포트와 연결됩니다.

### 3. MySQL 컨테이너에 접속

컨테이너가 실행된 후, 다음 명령어로 MySQL 컨테이너에 설치된 클라이언트를 실행합니다.

```bash
docker exec -it -e LANG=C.UTF-8 -e LC_ALL=C.UTF-8 mysql-container mysql -ustudy -pstudy
```
- `-it` 옵션은 인터랙티브 모드로 컨테이너에 접속할 수 있게 합니다.
- `-e LANG=C.UTF-8 -e LC_ALL=C.UTF-8`는 UTF-8 인코딩을 사용하여 한글이 깨지지 않도록 설정합니다.
- `mysql -ustudy -pstudy`는 `study` 사용자로 MySQL에 접속하는 명령어입니다. 비밀번호는 `study`입니다.


### 4. 데이터베이스 및 테이블 생성

- 사용할 데이터베이스 확인 및 선택
  ```sql
  # 접속한 사용자로 사용할 수 있는 데이터베이스를 확인합니다.
  show databases;

  # 사용할 데이터베이스를 선택합니다.
  use studydb;

  # studydb 데이터베이스에 존재하는 테이블을 확인합니다.
  show tables;
  ```

- 게시글을 저장할 테이블을 생성합니다.
  ```sql
  # boards 테이블 생성 DDL을 작성합니다.
  CREATE TABLE IF NOT EXISTS boards (
      no INT NOT NULL,
      title VARCHAR(255) NOT NULL,
      content TEXT NOT NULL,
      view_count INT DEFAULT 0,
      created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  );

  # 테이블이 생성되었는지 확인합니다.
  show tables;

  # boards 테이블의 구조를 확인합니다.
  desc boards;

  # boards 테이블의 Primary Key 설정합니다.
  ALTER TABLE boards ADD CONSTRAINT pk_boards PRIMARY KEY (no);
  desc boards;

  # boards 테이블의 no 컬럼을 AUTO_INCREMENT로 설정합니다.
  ALTER TABLE boards MODIFY COLUMN no INT NOT NULL AUTO_INCREMENT;
  desc boards;
  ```

### 4. 게시글 데이터 CRUD SQL 테스트

- 게시글 데이터를 삽입합니다.
  ```sql
  INSERT INTO boards (title, content) VALUES ('첫 번째 게시글', '첫 번째 게시글 내용입니다.');
  INSERT INTO boards (title, content) VALUES ('두 번째 게시글', '두 번째 게시글 내용입니다.');
  ```
- 게시글 데이터를 조회합니다.
  ```sql
  SELECT * FROM boards;
  ```
- 게시글 데이터를 수정합니다.
  ```sql
  UPDATE boards SET title = '수정된 게시글', content = '수정된 게시글 내용입니다.' WHERE no = 1;
  ```
- 게시글 데이터를 삭제합니다.
  ```sql
  DELETE FROM boards WHERE no = 2;
  ```

### 5. JDBC Driver 설정

- `build.gradle` 파일에 MySQL JDBC Driver 의존성을 추가합니다.
  ```groovy
  dependencies {
      implementation 'com.mysql:mysql-connector-j:9.3.0'
  }
  ```
  - Gradle 패털에서 `모든 Gradle 프로젝트 동기화` 버튼을 클릭하여 의존성을 업데이트합니다.

### 6. JDBC를 `BoardRepository` 클래스에 적용

- 데이터베이스 연결코드를 작성합니다.
  ```java
  // 다음 필드를 추가합니다.
  private final String url = "jdbc:mysql://localhost:3306/studydb";
  private final String username = "study";
  private final String password = "study";
  private final Connection conn;
  
  // 생성자를 추가합니다.
  public BoardRepository() throws Exception {
    conn = DriverManager.getConnection(url, username, password);
  }
  ```
- 게시글 목록을 조회하는 메서드를 변경합니다.
  ```java
  public List<BoardSummaryDto> findAll() {
    List<BoardSummaryDto> boards = new ArrayList<>();
    try (Statement stmt = conn.createStatement();
        ResultSet rs =
            stmt.executeQuery("SELECT no, title, created_date, view_count FROM boards")) {
      while (rs.next()) {
        BoardSummaryDto boardDto = new BoardSummaryDto();
        boardDto.setNo(rs.getLong("no"));
        boardDto.setTitle(rs.getString("title"));
        boardDto.setViewCount(rs.getInt("view_count"));
        boardDto.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
        boards.add(boardDto);
      }
    } catch (Exception e) {
      throw new RuntimeException("게시글 목록 조회 중 오류 발생", e);
    }
    return boards;
  }
  ```
- 게시글 입력 메서드를 변경합니다.
  ```java
  public void save(BoardDto boardDto) {
    String sql = "INSERT INTO boards (title, content) VALUES (?, ?)";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, boardDto.getTitle());
      pstmt.setString(2, boardDto.getContent());
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw new RuntimeException("게시글 저장 중 오류 발생", e);
    }
  }
  ``` 
- 게시글 상세 조회 메서드를 변경합니다.
  ```java
  public BoardDetailDto findByNo(Long no) {
    String sql = "SELECT no, title, content, view_count, created_date FROM boards WHERE no = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setLong(1, no);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          BoardDetailDto boardDto = new BoardDetailDto();
          boardDto.setNo(rs.getLong("no"));
          boardDto.setTitle(rs.getString("title"));
          boardDto.setContent(rs.getString("content"));
          boardDto.setViewCount(rs.getInt("view_count"));
          boardDto.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
          return boardDto;
        } else {
          throw new RuntimeException("게시글을 찾을 수 없습니다.");
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("게시글 상세 조회 중 오류 발생", e);
    }
  }
  ```
- 게시글 수정 메서드를 변경합니다.
  ```java
  public void update(Board board) {
    String sql = "UPDATE boards SET title = ?, content = ? WHERE no = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, board.getTitle());
      pstmt.setString(2, board.getContent());
      pstmt.setLong(3, board.getNo());
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw new RuntimeException("게시글 수정 중 오류 발생", e);
    }
  }
  ```
- 게시글 조회수를 증사시키는 메서드를 변경합니다.
  ```java
  public void updateViewCount(Long no) {
    String sql = "UPDATE boards SET view_count = view_count + 1 WHERE no = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setLong(1, no);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw new RuntimeException("게시글 조회수 증가 중 오류 발생", e);
    }
  }
  ```
- 게시글 삭제 메서드를 변경합니다.
  ```java
  public void delete(Long no) {
    String sql = "DELETE FROM boards WHERE no = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setLong(1, no);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw new RuntimeException("게시글 삭제 중 오류 발생", e);
    }
  }
  ```
- 게시글 개수를 리턴하는 메서드를 변경합니다.
  ```java
  public int count() {
    String sql = "SELECT COUNT(*) FROM boards";
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      rs.next();
      return rs.getInt(1);
    } catch (Exception e) {
      throw new RuntimeException("게시글 개수 조회 중 오류 발생", e);
    }
  }
  ```
- 기존 코드에서 불필요한 코드를 삭제합니다.
  ```java
  private final List<Board> list = new ArrayList<>(); // 삭제 
  private final Long nextNo = 1L; // 삭제
  ```