package org.example.myapp.service;

import java.util.List;
import org.example.myapp.dto.*;

public interface BoardService {

  void addBoard(BoardCreateRequest request);

  List<BoardResponse> getAllBoards();

  BoardDetailDto getBoardByNo(Long no);

  BoardDetailDto getBoardByNoWithViewCount(Long no);

  void updateBoard(BoardUpdateRequest request);

  void deleteBoard(Long no);

  int getBoardCount() ;
}
