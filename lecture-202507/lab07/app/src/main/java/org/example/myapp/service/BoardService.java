package org.example.myapp.service;

import java.util.List;
import org.example.myapp.domain.Board;
import org.example.myapp.dto.*;
import org.example.myapp.repository.BoardRepository;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

  private final BoardRepository boardRepository;

  public BoardService(BoardRepository boardRepository) {
    this.boardRepository = boardRepository;
  }

  public void addBoard(BoardCreateRequest request) {
    Board board = new Board();
    board.setTitle(request.getTitle());
    board.setContent(request.getContent());
    boardRepository.insert(board);
  }

  public List<BoardSummaryDto> getAllBoards() {
    return boardRepository.findAll();
  }

  public BoardDetailDto getBoardByNo(Long no) {
    BoardDetailDto board = boardRepository.findByNo(no);
    if (board == null) {
      throw new IllegalArgumentException("해당 번호의 게시글이 없습니다: " + no);
    }
    return board;
  }

  public BoardDetailDto getBoardByNoWithViewCount(Long no) {
    BoardDetailDto board = boardRepository.findByNo(no);
    if (board == null) {
      throw new IllegalArgumentException("해당 번호의 게시글이 없습니다: " + no);
    }
    boardRepository.updateViewCount(no);
    return board;
  }

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

  public void deleteBoard(Long no) {
    BoardDetailDto boardDetailDto = boardRepository.findByNo(no);
    if (boardDetailDto == null) {
      throw new IllegalArgumentException("해당 번호의 게시글이 없습니다: " + no);
    }
    boardRepository.delete(no);
  }

  public int getBoardCount() {
    return boardRepository.count();
  }
}
