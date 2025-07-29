package org.example.myapp.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.example.myapp.domain.Board;
import org.example.myapp.dto.BoardDetailDto;
import org.example.myapp.dto.BoardSummaryDto;

@Mapper
public interface BoardRepository {

  void insert(Board board);

  List<BoardSummaryDto> findAll();

  BoardDetailDto findByNo(Long no);

  void update(Board board);

  void updateViewCount(Long no);

  void delete(Long no);

  int count();
}
