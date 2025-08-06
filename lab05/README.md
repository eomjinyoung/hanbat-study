# Lab05 - 게시판 CRUD 구현하기

## 개요
이번 실습에서는 Spring WebMVC를 사용하여 간단한 게시판 애플리케이션을 구현합니다. 
이 애플리케이션은 게시글을 작성하고, 조회하고, 수정하고, 삭제하는 기능을 포함합니다. 
Thymeleaf를 사용하여 HTML 템플릿을 렌더링 하는 방법도 배웁니다.

## 목표

- Spring WebMVC와 Thymeleaf를 사용하여 게시판 애플리케이션 개발
- MVC 패턴 이해

## 실습

### 1. 정적 리소스 설정

- `src/main/resources/static` 디렉터리에 정적 리소스(이미지, CSS, JavaScript 등)를 저장합니다.
- 폴더 구조:
  ```
  src
  └── main
      └── resources
          └── static
              └── index.html
  ```
- `index.html` 파일을 생성하고, 기본 HTML 구조를 작성합니다.
  ```html
  <!DOCTYPE html>
  <html lang="ko">
  <head>
      <meta charset="UTF-8">
      <title>MyApp</title>
  </head>
  <body>
      <h1>MyApp에 오신 것을 환영합니다!</h1>
  </body>
  </html>
  ```
- 정적 웹 페이지 확인
  - `bootRun` 명령어로 애플리케이션을 실행합니다.
  - 브라우저에서 `http://localhost:9999/index.html`로 접속하여 페이지가 정상적으로 표시되는지 확인합니다.
- `index.html` 파일을 수정하여 내용을 변경하고, 다시 브라우저에서 확인합니다.
  - 예를 들어 다음 태그를 `h1` 태그 아래에 추가합니다.
    ```html
    <p>웹 애플리케이션 실습 예제입니다.</p>
    ``` 
  - 변경 사항이 적용되지 않는 이유는?
    - Spring Boot의 웹 서버는 정적 리소스를 classpath의 `static` 디렉터리에서 찾습니다.
    - 예를 들어, `app/build/resources/main/static` 폴더에서 찾습니다.
    - `src/main/resources/static` 디렉터리에 있는 파일들은 애플리케이션이 실행될 때 자동으로 classpath로 복사됩니다.
    - 따라서, `index.html` 파일을 수정하더라도, 애플리케이션을 다시 시작하지 않으면 변경 사항이 반영되지 않습니다.
    - 변경 사항을 반영하려면 애플리케이션을 다시 시작해야 합니다.
    - `bootRun` 명령어로 애플리케이션을 다시 실행한 후 테스트 해 보세요.
- 개발하는 동안 정적 리소스가 변경될 때마다 즉시 반영되도록 하는 방법은?
  - application.yml 파일에 다음 설정을 추가합니다.
    정적 리소스 디렉토리를 **classpath** 가 아닌 **소스 디렉토리**로 변경합니다.
    ```yaml
    spring:
      ... # 기존 설정 유지
      web:
        resources:
          static-locations: file:src/main/resources/static/
    ```
  - 개발 완료 후 배포할 때는 이 설정을 제거해야 합니다.
  - `index.html` 파일을 수정한 후, 브라우저에서 새로고침을 하면 변경된 내용이 즉시 반영되는지 확인해 보세요.


### 2. Thymeleaf 뷰 템플릿 엔진 설정

- Thymeleaf 의존 라이브러리 추가
  - `build.gradle` 파일에 다음 의존성을 추가합니다.
  ```groovy
  dependencies {
      ... // 기존 의존성 유지
      implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
  }
  ```
  - Gradle `모든 Gradle 프로젝트 동기화` 버튼을 클릭하여 빌드 스크립트를 동기화합니다.
    - 외부 라이브러리 노드에 추가된 `Thymeleaf` 라이브러리를 확인할 수 있습니다.
- Thymeleaf 템플릿 파일을 둘 디렉터리 생성
  - `src/main/resources/templates` 디렉터리를 생성합니다.
  - 이 디렉터리에 Thymeleaf 템플릿 파일을 저장합니다.
- 도움말 템플릿 페이지를 작성합니다.
  - `src/main/resources/templates/help.html` 파일을 생성합니다.
  - 다음 내용을 작성합니다.
  ```html
  <!DOCTYPE html>
  <html lang="ko">
  <head>
      <meta charset="UTF-8">
      <title>도움말</title>
  </head>
  <body>
      <h1>도움말</h1>
      <p>이 페이지는 애플리케이션 사용에 대한 도움말을 제공합니다.</p>
  </body>
  </html>
  ```
- 도움말 템플릿 페이지를 처리할 request handler를 `App` 클래스에 추가합니다.
  ```java
  @Controller // RestController --> Controller
  @SpringBootApplication
  public class App {
    public static void main(String[] args) {
      ... //
    }

    @GetMapping("/help") // "/help" URL 요청을 help.html 템플릿으로 매핑  
    public String help() {
        return "help"; // templates/help.html 파일을 렌더링
    }
  }
  ```
- 애플리케이션을 (재)실행하고, 브라우저에서 `http://localhost:9999/help` URL로 접속하여 도움말 페이지가 정상적으로 표시되는지 확인합니다.
  - `help.html` 파일을 수정하여 내용을 변경하고, 다시 브라우저에서 확인합니다.
  - 변경된 내용이 반영되지 않는 이유는 static 리소스의 경우와 마찬가지입니다.
- 개발하는 동안 Thymeleaf 템플릿 파일이 변경될 때마다 애플리케이션을 다시 시작하지 않고도 변경 사항을 즉시 반영할 수 있도록 설정합니다.
  - `application.yml` 파일에 다음 설정을 추가합니다.
  ```yaml
  spring:
    thymeleaf:
      prefix: file:src/main/resources/templates/ # 템플릿 파일 경로
      suffix: .html # 템플릿 파일 확장자
  ```
  - 개발 완료 후 배포할 때는 이 설정을 제거해야 합니다.

### 3. 게시글 CRUD 기능 구현을 위한 패키지 구조 설정

- `org.example.myapp` 패키지에 다음 하위 패키지를 생성합니다.
  - `controller`: 페이지 요청을 처리하는 컨트롤러 클래스
  - `service`: 비즈니스 로직을 처리하는 서비스 클래스
  - `repository`: 데이터베이스와 상호작용하는 리포지토리 클래스
  - `domain`: 도메인 모델 클래스
  - `dto`: 데이터 전송 객체(DTO) 클래스. 복잡한 JOIN 쿼리 결과 및 성능 최적화가 필요한 조회에 사용.
- 패키지 구조 예시:
  ```
  src
  └── main
      └── java
          └── org
              └── example
                  └── myapp
                      ├── controller
                      ├── service
                      ├── repository
                      ├── domain
                      └── dto
  ```

### 4. 게시글 Entity 클래스 정의

- 게시글 정보를 담는 엔티티 클래스를 `domain` 패키지에 정의합니다.
- `src/main/java/org/example/myapp/domain/Board.java` 파일을 생성합니다.
  ```java
  public class Board {
    private Long no;
    private String title;
    private String content;
    private int viewCount;
    private LocalDateTime createdDate;

    // toString() 자동 생성: 디버깅할 때 Board 객체의 내용을 쉽게 확인할 수 있다.
    // Getter/Setter 메서드 자동 생성
  }
  ```

### 5. 게시글 Repository 클래스 정의

- 게시글 데이터를 저장하고 조회하는 Repository 클래스를 `repository` 패키지에 정의합니다.
- `src/main/java/org/example/myapp/repository/BoardRepository.java` 파일을 생성합니다.
  ```java
  @Repository // Spring IoC 컨테이너가 관리하는 객체로 설정
  public class BoardRepository {
    private List<Board> list = new ArrayList<>();
    private Long nextNo = 1L; // 게시글 번호를 자동 증가시키기 위한 변수

    public void insert(Board board) {
        board.setNo(nextNo++);
        board.setCreatedDate(LocalDateTime.now());
        list.add(board);
    }
    public List<Board> findAll() {
        return list;
    }
    public Board findByNo(Long no) {
        for (Board board : list) {
            if (board.getNo().equals(no)) {
                return board;
            }
        }
        return null;
    }
    public void update(Board board) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getNo().equals(board.getNo())) {
                list.set(i, board);
                return;
            }
        }
    }
    public void delete(Long no) {
        list.removeIf(board -> board.getNo().equals(no));
    }
    public int count() {
        return list.size();
    }
  }
  ``` 

### 6. 게시글 Service 클래스 정의

- 게시글 관련 비즈니스 로직을 처리하는 서비스 클래스를 `service` 패키지에 정의합니다.
- `src/main/java/org/example/myapp/service/BoardService.java` 파일을 생성합니다.
  ```java
  @Service // Spring IoC 컨테이너가 관리하는 객체로 설정
  public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
      this.boardRepository = boardRepository;
    }

    public void addBoard(Board board) {
      boardRepository.insert(board);
    }
    public List<Board> getAllBoards() {
      return boardRepository.findAll();
    }
    public Board getBoardByNo(Long no) {
      return boardRepository.findByNo(no);
    }
    public void updateBoard(Board board) {
      boardRepository.update(board);
    }
    public void deleteBoard(Long no) {
      boardRepository.delete(no);
    }
    public int getBoardCount() {
      return boardRepository.count();
    }
  }
  ```

### 7. 게시글 입력 폼 기능 구현

- `src/main/resources/templates/board/` 디렉터리에 `form.html` 파일을 생성합니다.
- `form.html` 파일에 게시글 입력 폼을 작성합니다.
  ```html
  <!DOCTYPE html>
  <html lang="ko">
  <head>
      <meta charset="UTF-8">
      <title>게시글 작성</title>
  </head>
  <body>
      <h1>게시글 작성</h1>
      <form action="#" data-th-action="@{/board/add}" method="post">
          <label for="title">제목:</label>
          <input type="text" id="title" name="title" required><br>
          <label for="content">내용:</label><br>
          <textarea id="content" name="content" rows="4" cols="50" required></textarea><br>
          <button type="submit">저장</button>
      </form>
  </body>
  </html>
  ```
- `/board/form` URL 요청을 처리할 페이지 컨트롤러 정의
  - `src/main/java/org/example/myapp/controller/BoardController.java` 파일을 생성합니다.
  - 다음 코드를 작성합니다.
  ```java
  @Controller
  @RequestMapping("/board")
  public class BoardController {

    @GetMapping("/form")
    public void form() {
      // 메서드의 리턴 값이 없으면 요청 URL이 템플릿 파일의 경로로 사용된다.
      // 즉 '/board/form' URL의 템플릿 파일 경로는 'templates/board/form.html' 이다.
    }
  }
  ```
- 애플리케이션을 실행하고, 브라우저에서 `http://localhost:9999/board/form` URL로 접속하여 게시글 작성 폼이 정상적으로 표시되는지 확인합니다.

### 8. 게시글 저장 기능 구현
- 게시글 저장을 처리할 메서드를 `BoardController` 클래스에 추가합니다.
  ```java
  // 게시글 비즈니스 로직을 처리하는 서비스 클래스 주입
  private final BoardService boardService;

  public BoardController(BoardService boardService) {
    this.boardService = boardService;
  }

  @PostMapping("/add")
  public String add(@ModelAttribute Board board) {
      boardService.addBoard(board);
      return "redirect:/board/list"; // 저장 후 게시글 목록 페이지로 리다이렉트
  }
  ```
- 애플리케이션을 실행하고, 브라우저에서 `http://localhost:9999/board/form` URL로 접속하여 게시글을 작성하고 저장합니다. 아직 게시글 목록 페이지가 없으므로 저장 후에는 페이지 오류가 발생합니다.

### 9. 게시글 목록 기능 구현

- 게시글 목록을 처리할 request handler를 `BoardController` 클래스에 추가합니다.
  ```java
  @GetMapping("/list")
  public String list(Model model) {
      List<Board> boards = boardService.getAllBoards();
      model.addAttribute("boards", boards); // 모델에 게시글 목록 추가
      return "board/list"; // 게시글 목록 페이지로 이동
  }
  ```  
- 게시글 목록을 표시할 템플릿 파일을 `src/main/resources/templates/board/list.html` 파일로 생성합니다.
  ```html
  <!DOCTYPE html>
  <html lang="ko">
  <head>
      <meta charset="UTF-8">
      <title>게시글 목록</title>
  </head>
  <body>
  <h1>게시글 목록</h1>
  <table border="1">
      <thead>
      <tr>
          <th>번호</th>
          <th>제목</th>
          <th>작성일</th>
          <th>조회수</th>
      </tr>
      </thead>
      <tbody>
      <tr data-th-if="${#lists.isEmpty(boards)}">
          <td colspan="4">게시글이 없습니다.</td>
      </tr>
      <tr data-th-each="board : ${boards}">
          <td data-th-text="${board.no}"></td>
          <td><a data-th-href="@{/board/view(no=${board.no})}" data-th-text="${board.title}"></a></td>
          <td data-th-text="${#temporals.format(board.createdDate, 'yyyy-MM-dd')}"></td>
          <td data-th-text="${board.viewCount}"></td>
      </tr>
      </tbody>
  </table>
  <a href="/board/form">새 게시글</a>
  </body>
  </html>
  ```
- 브라우저에서 `http://localhost:9999/board/list` URL로 접속하여 게시글 목록을 조회합니다.

### 10. 게시글 조회 기능 구현
- 게시글 조회를 처리할 request handler를 `BoardController` 클래스에 추가합니다.
  ```java
  @GetMapping("/view")
  public String view(@RequestParam("no") Long no, Model model) {
      Board board = boardService.getBoardByNo(no);
      if (board == null) {
          return "redirect:/board/list"; // 게시글이 없으면 목록 페이지로 리다이렉트
      }
      board.setViewCount(board.getViewCount() + 1); // 조회수 증가
      // List 에 저장된 객체의 값을 그대로 변경했기 때문에 추가로 updateBoard()를 호출할 필요는 없다.
      // boardService.updateBoard(board); // 조회수 업데이트
      model.addAttribute("board", board); // 모델에 게시글 추가
      return "board/view"; // 게시글 조회 페이지로 이동
  }
  ```
- 게시글 조회 페이지를 `src/main/resources/templates/board/view.html` 파일로 생성합니다.
  ```html
  <!DOCTYPE html>
  <html lang="ko">
  <head>
      <meta charset="UTF-8">
      <title>게시글 조회</title>
  </head>
  <body>
  <h1>게시글 조회</h1>
  <form action="#" data-th-action="@{/board/update}" method="post">
      <label for="no">번호:</label>
      <input type="text" id="no" name="no" readonly data-th-value="${board.no}"><br>
      <label for="title">제목:</label>
      <input type="text" id="title" name="title" required data-th-value="${board.title}"><br>
      <label for="content">내용:</label><br>
      <textarea id="content" name="content" rows="4" cols="50" required data-th-text="${board.content}"></textarea><br>
      <label for="created-date">작성일:</label>
      <input type="text" id="created-date" name="createdDate" readonly
            data-th-value="${#temporals.format(board.createdDate, 'yyyy-MM-dd')}"><br>
      <label for="view-count">조회수:</label>
      <input type="text" id="view-count" name="viewCount" readonly data-th-value="${board.viewCount}"><br>
      <button type="submit">변경</button>
  </form>
  <a href="/board/list">목록으로</a>
  </body>
  </html>
  ```
- 브라우저에서 게시글 목록 페이지로 이동하여 게시글 제목을 클릭하여 게시글 조회 페이지로 이동합니다.
- 게시글 조회 페이지에서 게시글 내용을 확인합니다.  

### 11. 게시글 변경 기능 구현

- 게시글 변경을 처리할 request handler를 `BoardController` 클래스에 추가합니다.
  ```java
  @PostMapping("/update")
  public String update(@ModelAttribute Board board) {
    Board old = boardService.getBoardByNo(board.getNo());
    if (board == null) {
      return "redirect:/board/list"; // 게시글이 없으면 목록 페이지로 리다이렉트
    }

    old.setTitle(board.getTitle());
    old.setContent(board.getContent());

    // List 에 저장된 객체의 값을 변경했기 때문에 추가로 updateBoard()를 호출할 필요는 없다.
    // boardService.updateBoard(board);

    return "redirect:/board/view?no=" + board.getNo(); // 변경 후 게시글 조회 페이지로 리다이렉트
  }
  ```
- 게시글 조회 페이지에서 제목이나 내용을 변경하고, "변경" 버튼을 클릭하여 변경된 내용을 저장합니다.
- 변경 후 게시글 조회 페이지로 리다이렉트되어 변경된 내용을 확인합니다.

### 12. 게시글 삭제 기능 구현
- 게시글 삭제를 처리할 request handler를 `BoardController` 클래스에 추가합니다.
  ```java
  @PostMapping("/delete")
  public String delete(@RequestParam("no") Long no) {
      boardService.deleteBoard(no);
      return "redirect:/board/list"; // 삭제 후 게시글 목록 페이지로 리다이렉트
  }
  ```
- 게시글 조회 페이지에 삭제 버튼을 추가합니다.
  ```html
  <form action="#" data-th-action="@{/board/update}" method="post">
      ... 
  </form>
  <form action="#" data-th-action="@{/board/delete}" method="post">
      <input type="hidden" name="no" data-th-value="${board.no}">
      <button type="submit">삭제</button>
  </form>
  <a href="/board/list">목록으로</a>
  ```
- 게시글 조회 페이지에서 "삭제" 버튼을 클릭하여 게시글을 삭제합니다.
- 게시글 삭제 후 게시글 목록 페이지로 리다이렉트되어 삭제된 게시글이 목록에서 사라진 것을 확인합니다.
