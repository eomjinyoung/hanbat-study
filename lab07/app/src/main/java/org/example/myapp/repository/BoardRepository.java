package org.example.myapp.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.example.myapp.domain.Board;
import org.example.myapp.dto.BoardDetailDto;
import org.example.myapp.dto.BoardSummaryDto;
import org.springframework.stereotype.Repository;

@Repository
public class BoardRepository {

  private final List<Board> list = new ArrayList<>();
  private Long nextNo = 1L; // 게시글 번호를 자동 증가시키기 위한 변수

  public void insert(Board board) {
    board.setNo(nextNo++);
    board.setCreatedDate(LocalDateTime.now());
    list.add(board);
  }

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

  public void updateViewCount(Long no) {
    for (Board board : list) {
      if (board.getNo().equals(no)) {
        board.setViewCount(board.getViewCount() + 1);
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
