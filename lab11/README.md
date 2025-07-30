# Lab11 - SQL Mapper를 OR Mapper로 교체하기

## 개요
이번 실습에서는 SQL Mapper를 OR Mapper로 변경하는 방법을 배웁니다. SQL Mapper는 SQL 쿼리를 직접 작성하여 데이터베이스와 상호작용하는 반면, OR Mapper는 객체 지향 프로그래밍의 개념을 사용하여 데이터베이스와의 상호작용을 추상화합니다.

## 목표

- OR Mapper의 구동 원리 이해
- JPA (Java Persistence API) 사용법 익히기 


## 실습

### 1. JPA 설정

- `build.gradle` 파일에 JPA와 H2 데이터베이스 의존성을 추가합니다.
  ```groovy
  dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
  }
  ```
- `application.yml` 파일에 JSP 설정을 추가합니다.
  ```yaml
  spring:
    jpa:
      hibernate:
        ddl-auto: update
      show-sql: true
      database-platform: org.hibernate.dialect.MySQLDialect
  ```   

### 2. 엔티티 클래스 변경

- `Board` 엔티티 클래스를 JPA 엔티티로 변경합니다.
  ```java
  @Entity
  @Table(name = "boards")
  public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(name = "created_date", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdDate;

    // 기존의 getter와 setter 메서드 유지
  }
  ```

### 3. Repository 인터페이스 변경

- `BoardRepository` 인터페이스를 JPA Repository로 변경합니다.
  ```java
  import org.springframework.data.jpa.repository.JpaRepository;

  public interface BoardRepository extends JpaRepository<Board, Long> {
    // 추가적인 쿼리 메서드 정의 가능
  }
  ```

### 4. 서비스 클래스 변경
- `BoardServiceImpl` 클래스를 JPA 사용으로 변경합니다.
  ```java
  @Service
  @Transactional
  public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    public BoardServiceImpl(BoardRepository boardRepository) {
      this.boardRepository = boardRepository;
    }

    public void addBoard(BoardCreateRequest request) {
      Board board = new Board();
      board.setTitle(request.getTitle());
      board.setContent(request.getContent());
      boardRepository.save(board);
    }

    public List<BoardSummaryDto> getAllBoards() {
      return boardRepository.findAll().stream()
          .map(
              board -> {
                BoardSummaryDto boardDto = new BoardSummaryDto();
                boardDto.setNo(board.getNo());
                boardDto.setTitle(board.getTitle());
                boardDto.setViewCount(board.getViewCount());
                boardDto.setCreatedDate(board.getCreatedDate());
                return boardDto;
              })
          .collect(Collectors.toList());
    }

    public BoardDetailDto getBoardByNo(Long no) {
      Board board =
          boardRepository
              .findById(no)
              .orElseThrow(() -> new IllegalArgumentException("해당 번호의 게시글이 없습니다: " + no));
      BoardDetailDto dto = new BoardDetailDto();
      dto.setNo(board.getNo());
      dto.setTitle(board.getTitle());
      dto.setContent(board.getContent());
      dto.setViewCount(board.getViewCount());
      dto.setCreatedDate(board.getCreatedDate());
      return dto;
    }

    public BoardDetailDto getBoardByNoWithViewCount(Long no) {
      Board board =
          boardRepository
              .findById(no)
              .orElseThrow(() -> new IllegalArgumentException("해당 번호의 게시글이 없습니다: " + no));
      board.setViewCount(board.getViewCount() + 1);
      boardRepository.save(board);
      BoardDetailDto dto = new BoardDetailDto();
      dto.setNo(board.getNo());
      dto.setTitle(board.getTitle());
      dto.setContent(board.getContent());
      dto.setViewCount(board.getViewCount());
      dto.setCreatedDate(board.getCreatedDate());
      return dto;
    }

    public void updateBoard(BoardUpdateRequest request) {
      Board board =
          boardRepository
              .findById(request.getNo())
              .orElseThrow(
                  () -> new IllegalArgumentException("해당 번호의 게시글이 없습니다: " + request.getNo()));
      board.setTitle(request.getTitle());
      board.setContent(request.getContent());
      boardRepository.save(board);
    }

    public void deleteBoard(Long no) {
      if (!boardRepository.existsById(no)) {
        throw new IllegalArgumentException("해당 번호의 게시글이 없습니다: " + no);
      }
      boardRepository.deleteById(no);
    }

    public int getBoardCount() {
      return (int) boardRepository.count();
    }
  }

  ```