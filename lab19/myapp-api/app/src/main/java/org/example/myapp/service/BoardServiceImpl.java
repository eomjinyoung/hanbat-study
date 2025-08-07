package org.example.myapp.service;

import java.util.List;
import java.util.stream.Collectors;
import org.example.myapp.domain.Board;
import org.example.myapp.dto.*;
import org.example.myapp.repository.BoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
