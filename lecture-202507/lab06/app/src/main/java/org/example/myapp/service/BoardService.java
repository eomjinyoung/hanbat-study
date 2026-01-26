package org.example.myapp.service;

import java.util.List;
import org.example.myapp.domain.Board;
import org.example.myapp.repository.BoardRepository;
import org.springframework.stereotype.Service;

@Service
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
