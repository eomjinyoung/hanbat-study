# Lab06 - DTO 객체 활용

## 개요
이번 실습에서는 DTO(Data Transfer Object) 객체를 활용하여 게시판 애플리케이션을 개선합니다.

## 목표

- DTO 객체를 활용한 데이터 전송 최적화

## 실습

### 0. 실습 완료 후 프로젝트 구조
```
└── org
    └── example
        └── myapp
            ├── App.java
            ├── controller
            │   └── BoardController.java
            ├── domain
            │   └── Board.java
            ├── dto
            │   ├── BoardCreateRequest.java
            │   ├── BoardDetailDto.java
            │   ├── BoardResponse.java
            │   ├── BoardSummaryDto.java
            │   └── BoardUpdateRequest.java
            ├── repository
            │   └── BoardRepository.java
            └── service
                └── BoardService.java
```

### 1. 조회용 DTO 정의

- 게시글 목록 조회 결과를 반환할 DTO 클래스를 정의합니다.
- `src/main/java/org/example/myapp/dto/BoardSummaryResponse.java` 파일을 생성합니다.
  ```java
  public class BoardSummaryDto {
    private Long no;
    private String title;
    private int viewCount;
    private LocalDateTime createdDate;

    // Getter/Setter 메서드 자동 생성
  }
  ```
- 게시글 상세 정보 조회 결과를 반환할 DTO 클래스를 정의합니다.
- `src/main/java/org/example/myapp/dto/BoardDetailDto.java` 파일을 생성합니다.
  ```java
  public class BoardDetailDto {
    private Long no;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private int viewCount;

    // Getter/Setter 메서드 자동 생성
  }
  ```

### 2. API(HTTP 요청/응답) 전용 DTO 정의

- 새 게시글 입력 폼 값을 받을 DTO 클래스를 정의합니다.
- `src/main/java/org/example/myapp/dto/BoardCreateRequest.java` 파일을 생성합니다.
  ```java
  public class BoardCreateRequest {
    private String title;
    private String content;

    // Getter/Setter 메서드 자동 생성
  }
  ```
- 게시글 변경 폼 값을 받을 DTO 클래스를 정의합니다.
- `src/main/java/org/example/myapp/dto/BoardUpdateRequest.java` 파일을 생성합니다.
  ```java
  public class BoardUpdateRequest {
    private Long no;
    private String title;
    private String content;

    // Getter/Setter 메서드 자동 생성
  }
  ```
  - 게시글 조회 결과를 반환할 DTO 클래스를 정의합니다.
- `src/main/java/org/example/myapp/dto/BoardResponse.java` 파일을 생성합니다.
  ```java
  public class BoardResponse {
    private Long no;
    private String title;
    private String content;
    private LocalDateTime createdDate; // 출력할 때 자유롭게 포맷을 제어할 수 있도록 하기 위함
    private int viewCount;

    public static BoardResponse from(BoardDetailDto dto) {
      BoardResponse response = new BoardResponse();
      response.setNo(dto.getNo());
      response.setTitle(dto.getTitle());
      response.setContent(dto.getContent());
      response.setViewCount(dto.getViewCount());
      response.setCreatedDate(dto.getCreatedDate());
      return response;
    }

    public static BoardResponse from(BoardSummaryDto dto) {
      BoardResponse response = new BoardResponse();
      response.setNo(dto.getNo());
      response.setTitle(dto.getTitle());
      response.setViewCount(dto.getViewCount());
      response.setCreatedDate(dto.getCreatedDate());
      return response;
    }
    // Getter/Setter 메서드 자동 생성
  }
  ```


### 3. 게시글 목록 조회 처리에 DTO 적용
- 게시글 목록 조회 처리 메서드에서 DTO를 사용하도록 수정합니다.
- `src/main/java/org/example/myapp/repository/BoardRepository.java` 파일을 수정합니다.
  ```java
  public List<BoardSummaryDto> findAll() {
    return list.stream()
        .map(
            board -> {
              BoardSummaryDto response = new BoardSummaryDto();
              response.setNo(board.getNo());
              response.setTitle(board.getTitle());
              response.setViewCount(board.getViewCount());
              response.setCreatedDate(board.getCreatedDate());
              return response;
            })
        .toList();
  }
  ```
- `src/main/java/org/example/myapp/service/BoardService.java` 파일을 수정합니다.
  ```java
  public List<BoardSummaryDto> getAllBoards() {
    return boardRepository.findAll();
  }
  ```
- `src/main/java/org/example/myapp/controller/BoardController.java` 파일을 수정합니다.
  ```java
  @GetMapping("/list")
  public String list(Model model) {
    List<BoardSummaryDto> list = boardService.getAllBoards();
    List<BoardResponse> boards = list.stream().map(BoardResponse::from).toList();
    model.addAttribute("boards", boards); // 모델에 게시글 목록 추가
    return "board/list"; // 게시글 목록 페이지로 이동
  }
  ```
- 게시글 목록 페이지에서 DTO를 사용하여 게시글 정보를 출력합니다.

### 4. 게시글 상세 조회 처리에 DTO 적용
- 게시글 상세 조회 처리 메서드에서 DTO를 사용하도록 수정합니다.
- `src/main/java/org/example/myapp/repository/BoardRepository.java` 파일을 수정합니다.
  ```java
  public BoardDetailDto findByNo(Long no) {
    for (Board board : list) {
      if (board.getNo().equals(no)) {
        BoardDetailDto response = new BoardDetailDto();
        response.setNo(board.getNo());
        response.setTitle(board.getTitle());
        response.setContent(board.getContent());
        response.setCreatedDate(board.getCreatedDate());
        response.setViewCount(board.getViewCount());
        return response;
      }
    }
    return null; // 해당 번호의 게시글이 없을 경우 null 반환
  }

  // 게시글 조회 시 조회수를 증가시키는 메서드 추가
  public void updateViewCount(Long no) {
    for (Board board : list) {
      if (board.getNo().equals(no)) {
        board.setViewCount(board.getViewCount() + 1);
        return;
      }
    }
  }
  ```
- `src/main/java/org/example/myapp/service/BoardService.java` 파일을 수정합니다.
  ```java
  public BoardDetailDto getBoardByNo(Long no) {           
    BoardDetailDto board = boardRepository.findByNo(no);
    if (board == null) {
      throw new IllegalArgumentException("해당 번호의 게시글이 없습니다: " + no);
    }
    return board;
  }

  // 게시글 조회 시 조회수를 증가시키는 메서드 추가
  public BoardDetailDto getBoardByNoWithViewCount(Long no) {           
    BoardDetailDto board = boardRepository.findByNo(no);
    if (board == null) {
      throw new IllegalArgumentException("해당 번호의 게시글이 없습니다: " + no);
    }
    boardRepository.updateViewCount(no);
    return board;
  }
  ```
- `src/main/java/org/example/myapp/controller/BoardController.java` 파일을 수정합니다.
  ```java
  @GetMapping("/view")
  public String view(@RequestParam("no") Long no, Model model) {
    BoardDetailDto boardDetailDto = boardService.getBoardByNo(no);
    model.addAttribute("board", BoardResponse.from(boardDetailDto)); // 모델에 게시글 추가
    return "board/view"; // 게시글 조회 페이지로 이동
  }
  ```

### 5. 게시글 입력 처리에 DTO 적용
- 게시글 입력 처리 메서드에서 DTO를 사용하도록 수정합니다.
- `src/main/java/org/example/myapp/controller/BoardController.java` 파일을 수정합니다.
  ```java
  @PostMapping("/add")
  public String add(@ModelAttribute BoardCreateRequest request) {
    boardService.addBoard(request);
    return "redirect:/board/list"; // 저장 후 게시글 목록 페이지로 리다이렉트
  }
  ```
- `src/main/java/org/example/myapp/service/BoardService.java` 파일을 수정합니다.
  ```java
  public void addBoard(BoardCreateRequest request) {
    Board board = new Board();
    board.setTitle(request.getTitle());
    board.setContent(request.getContent());
    boardRepository.insert(board);
  }
  ```
### 6. 게시글 변경 처리에 DTO 적용
- 게시글 변경 처리 메서드에서 DTO를 사용하도록 수정합니다.
- `src/main/java/org/example/myapp/controller/BoardController.java` 파일을 수정합니다.
  ```java
  @PostMapping("/update")
  public String update(@ModelAttribute BoardUpdateRequest request) {
    boardService.updateBoard(request);
    return "redirect:/board/list"; // 변경 후 게시글 목록 페이지로 리다이렉트
  }
  ```
- `src/main/java/org/example/myapp/service/BoardService.java` 파일을 수정합니다.
  ```java
  public void updateBoard(BoardUpdateRequest request) {
    BoardDetailDto boardDetailDto = boardRepository.findByNo(request.getNo());
    if (boardDetailDto == null) {
      throw new IllegalArgumentException("해당 번호의 게시글이 없습니다: " + request.getNo());
    }
    Board board = new Board();
    board.setNo(boardDetailDto.getNo());
    board.setTitle(request.getTitle());
    board.setContent(request.getContent());
    board.setCreatedDate(boardDetailDto.getCreatedDate());
    board.setViewCount(boardDetailDto.getViewCount());

    boardRepository.update(board);
  }
  ```

### 7. 게시글 삭제 처리에 DTO 적용
- 게시글 삭제 처리 메서드에서 DTO를 사용하도록 수정합니다.
- `src/main/java/org/example/myapp/controller/BoardController.java` 파일을 수정합니다.
  ```java
  @PostMapping("/delete")
  public String delete(@RequestParam("no") Long no) {
    boardService.deleteBoard(no);
    return "redirect:/board/list"; // 삭제 후 게시글 목록 페이지로 리다이렉트
  }
  ```
- `src/main/java/org/example/myapp/service/BoardService.java` 파일을 수정합니다.
  ```java
  public void deleteBoard(Long no) {
    BoardDetailDto boardDetailDto = boardRepository.findByNo(no);
    if (boardDetailDto == null) {
      throw new IllegalArgumentException("해당 번호의 게시글이 없습니다: " + no);
    }
    boardRepository.delete(no);
  }
  ```



