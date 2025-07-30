# Lab10 - 인터페이스를 활용하여 구현체와의 종속성을 완화하기

## 개요
이번 실습에서는 인터페이스를 활용하여 구현체와의 종속성을 완화하는 방법을 학습합니다. 이를 통해 코드의 유연성과 테스트 용이성을 높일 수 있습니다.

## 목표

- 인터페이스를 활용하여 Low Coupling으로 설계하기


## 실습

### 1. 서비스 클래스에 인터페이스를 적용한다.

- `BoardService` 클래스를 인터페이스와 구현체로 분리한다.
  ```java
    // org.example.myapp.service.BoardService 인터페이스
    public interface BoardService {

        void addBoard(BoardCreateRequest request);

        List<BoardResponse> getAllBoards();

        BoardDetailDto getBoardByNo(Long no);

        BoardDetailDto getBoardByNoWithViewCount(Long no);

        void updateBoard(BoardUpdateRequest request);

        void deleteBoard(Long no);

        int getBoardCount() ;
    }

    // org.example.myapp.service.BoardServiceImpl 클래스
    @Service
    @Transactional
    public class BoardServiceImpl implements BoardService {
        // 기존의 BoardService 클래스에 있던 코드 그대로 유지
    }
  ```