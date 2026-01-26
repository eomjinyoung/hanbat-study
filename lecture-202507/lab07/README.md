# Lab07 - Validation 활용

## 개요
이번 실습에서는 Validation(유효성 검사)을 활용하여 게시판 애플리케이션을 개선합니다. 유효성 검사를 통해 클라이언트로부터 입력받은 데이터의 무결성을 확보하고, 잘못된 데이터가 서버로 전달되는 것을 방지합니다.

## 목표

- 유효성 검사를 통한 데이터 무결성 확보

## 실습

### 1. Validation 라이브러리 추가

- `build.gradle` 파일에 Validation 라이브러리를 추가합니다.
  ```groovy
  dependencies {
      ... // 기존 의존성
      implementation 'org.springframework.boot:spring-boot-starter-validation'
  }
  ```
  - Gradle 의존성 추가 후, 프로젝트를 동기화합니다.

### 2. 게시글 입력: DTO 클래스에 Validation 어노테이션 추가

- 게시글 입력 폼 DTO 클래스에 유효성 검사 어노테이션을 추가합니다.
- `src/main/java/org/example/myapp/dto/BoardCreateRequest.java` 파일을 수정합니다.
  ```java
  public class BoardCreateRequest {

    @NotBlank(message = "제목은 필수 입력 항목입니다")
    @Size(min = 2, max = 100, message = "제목은 2자 이상 100자 이하로 입력해주세요")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s!?.,'-]+$",
            message = "제목에는 특수문자를 사용할 수 없습니다 (일부 문장부호 제외)")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다")
    @Size(min = 10, max = 5000, message = "내용은 10자 이상 5000자 이하로 입력해주세요")
    private String content;

    // Getter/Setter 메서드 자동 생성
  }
  ```

### 3. 게시글 입력: Controller에서 검증 활성화

- 게시글 입력 처리 메서드에서 DTO 검증을 활성화합니다.
- `src/main/java/org/example/myapp/controller/BoardController.java` 파일을 수정합니다.
  ```java
  // 게시글 입력 폼을 출력할 때 사용할 객체를 담을 Model 객체를 파라미터로 받는다.
  // 기본 입력 값을 설정하기 위해 BoardCreateRequest 객체를 생성하여 Model에 추가한다.
  @GetMapping("/form")
  public void form(Model model) {
    model.addAttribute("boardCreateRequest", new BoardCreateRequest());
  }

  // @Value: 게시글 입력 폼에서 전송된 데이터를 검증하도록 설정
  // BindingResult: 검증 결과를 담는 객체
  @PostMapping("/add")
  public String add(
      @Valid @ModelAttribute BoardCreateRequest request, BindingResult bindingResult) {

    // 검증 실패 시 에러 응답
    if (bindingResult.hasErrors()) {
      // BindingResult가 "boardCreateRequest" 이름으로 Model에 자동 저장됨
      // @ModelAttribute("aaa")로 이름이 명시된 경우 "aaa" 이름으로 저장됨
      return "board/form"; // 에러가 발생하면 다시 폼 페이지로 이동
    }

    boardService.addBoard(request);
    return "redirect:/board/list"; // 저장 후 게시글 목록 페이지로 리다이렉트
  }
  ```

### 4. 게시글 입력: 템플릿 에러 메시지 처리
- 에러 메시지를 사용자에게 보여주기 위해 Thymeleaf 템플릿을 수정합니다.
- `src/main/resources/templates/board/form.html` 파일을 수정합니다.
  ```html
  <!DOCTYPE html>
  <html lang="ko">
  <head>
      <meta charset="UTF-8">
      <title>게시글 작성</title>
      <style> <!-- 오류 메시지 스타일 지정-->
          .error { color: red; font-size: 12px; }
      </style>
  </head>
  <body>
  <h1>게시글 작성</h1>
  <form action="#" data-th-action="@{/board/add}" data-th-object="${boardCreateRequest}" method="post">
      <label for="title">제목:</label>
      <input type="text" id="title" data-th-field="*{title}" required><br>
      <div class="error" data-th-if="${#fields.hasErrors('title')}" data-th-errors="*{title}"></div>

      <label for="content">내용:</label><br>
      <textarea data-th-field="*{content}" rows="4" cols="50" required></textarea><br>
      <div class="error" id="content" data-th-if="${#fields.hasErrors('content')}" data-th-errors="*{content}"></div>
      <button type="submit">저장</button>
  </form>
  </body>
  </html>
  ```

### 5. 게시글 변경: DTO 클래스에 Validation 어노테이션 추가
- 게시글 변경 폼 DTO 클래스에 유효성 검사 어노테이션을 추가합니다.
- `src/main/java/org/example/myapp/dto/BoardUpdateRequest.java` 파일을 수정합니다.
  ```java
  public class BoardUpdateRequest {

    @NotNull(message = "게시글 번호는 필수입니다")
    @Positive(message = "게시글 번호는 양수여야 합니다")
    private Long no;

    @NotBlank(message = "제목은 필수 입력 항목입니다")
    @Size(min = 2, max = 100, message = "제목은 2자 이상 100자 이하로 입력해주세요")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s!?.,'-]+$", message = "제목에는 특수문자를 사용할 수 없습니다 (일부 문장부호 제외)")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다")
    @Size(min = 10, max = 5000, message = "내용은 10자 이상 5000자 이하로 입력해주세요")
    private String content;

    // Getter/Setter 메서드 자동 생성
  }
  ```

### 6. 게시글 변경: Controller에서 검증 활성화
- 게시글 입력 처리 메서드에서 DTO 검증을 활성화합니다.
- `src/main/java/org/example/myapp/controller/BoardController.java` 파일을 수정합니다.
  ```java
  @GetMapping("/view")
  public String view(@RequestParam("no") Long no, Model model) {
    BoardDetailDto boardDetailDto = boardService.getBoardByNoWithViewCount(no);

    // 읽기 전용 정보
    model.addAttribute("board", BoardResponse.from(boardDetailDto));

    // 폼 입력 항목에 출력될 정보: 수정 폼 입력 값 오류 출력을 위해 템플릿에 추가된 코드에서 사용할 객체 
    BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest();
    boardUpdateRequest.setNo(boardDetailDto.getNo());
    boardUpdateRequest.setTitle(boardDetailDto.getTitle());
    boardUpdateRequest.setContent(boardDetailDto.getContent());
    model.addAttribute("boardUpdateRequest", boardUpdateRequest);

    return "board/view"; // 게시글 조회 페이지로 이동
  }

  @PostMapping("/update")
  public String update(
      @Valid @ModelAttribute BoardUpdateRequest request, BindingResult bindingResult, Model model) {

    // 검증 실패 시 에러 응답
    if (bindingResult.hasErrors()) {
      BoardDetailDto boardDetailDto = boardService.getBoardByNo(request.getNo());
      model.addAttribute("board", BoardResponse.from(boardDetailDto));
      return "board/view"; // 에러가 발생하면 다시 게시글 조회 페이지로 이동
    }

    boardService.updateBoard(request);
    return "redirect:/board/list"; // 변경 후 게시글 목록 페이지로 리다이렉트
  }
  ```
### 7. 게시글 변경: 에러 메시지 처리
- 에러 메시지를 사용자에게 보여주기 위해 Thymeleaf 템플릿을 수정합니다.
- `src/main/resources/templates/board/view.html` 파일을 수정합니다.
  ```html
  <!DOCTYPE html>
  <html lang="ko">
  <head>
      <meta charset="UTF-8">
      <title>게시글 조회</title>
      <style>
          .error { color: red; font-size: 12px; }
      </style>
  </head>
  <body>
  <h1>게시글 조회</h1>
  <form action="#" data-th-action="@{/board/update}" data-th-object="${boardUpdateRequest}" method="post">
      <label for="no">번호:</label>
      <input type="text" id="no" name="no" readonly data-th-value="${board.no}"><br>

      <label for="title">제목:</label>
      <input type="text" id="title" data-th-field="*{title}" required><br>
      <div class="error" data-th-if="${#fields.hasErrors('title')}" data-th-errors="*{title}"></div>

      <label for="content">내용:</label><br>
      <textarea id="content" data-th-field="*{content}" name="content" rows="4" cols="50" required></textarea><br>
      <div class="error" data-th-if="${#fields.hasErrors('content')}" data-th-errors="*{content}"></div>

      <label for="created-date">작성일:</label>
      <input type="text" id="created-date" readonly
            data-th-value="${#temporals.format(board.createdDate, 'yyyy-MM-dd')}"><br>
      <label for="view-count">조회수:</label>
      <input type="text" id="view-count" readonly data-th-value="${board.viewCount}"><br>
      <button type="submit">변경</button>
  </form>
  <form action="#" data-th-action="@{/board/delete}" method="post">
      <input type="hidden" name="no" data-th-value="${board.no}">
      <button type="submit">삭제</button>
  </form>
  <a href="/board/list">목록으로</a>
  </body>
  </html>