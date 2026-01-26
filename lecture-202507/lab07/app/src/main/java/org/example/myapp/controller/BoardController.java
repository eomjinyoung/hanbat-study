package org.example.myapp.controller;

import java.util.List;
import org.example.myapp.dto.BoardCreateRequest;
import org.example.myapp.dto.BoardDetailDto;
import org.example.myapp.dto.BoardResponse;
import org.example.myapp.dto.BoardSummaryDto;
import org.example.myapp.dto.BoardUpdateRequest;
import org.example.myapp.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/board")
public class BoardController {

  private final BoardService boardService;

  public BoardController(BoardService boardService) {
    this.boardService = boardService;
  }

  @GetMapping("/form")
  public void form() {
    // 메서드의 리턴 값이 없으면 요청 URL이 템플릿 파일의 경로로 사용된다.
    // 즉 '/board/form' URL의 템플릿 파일 경로는 'templates/board/form.html' 이다.
  }

  @PostMapping("/add")
  public String add(@ModelAttribute BoardCreateRequest request) {
    boardService.addBoard(request);
    return "redirect:/board/list"; // 저장 후 게시글 목록 페이지로 리다이렉트
  }

  @GetMapping("/list")
  public String list(Model model) {
    List<BoardSummaryDto> list = boardService.getAllBoards();
    List<BoardResponse> boards = list.stream().map(BoardResponse::from).toList();
    model.addAttribute("boards", boards); // 모델에 게시글 목록 추가
    return "board/list"; // 게시글 목록 페이지로 이동
  }

  @GetMapping("/view")
  public String view(@RequestParam("no") Long no, Model model) {
    BoardDetailDto boardDetailDto = boardService.getBoardByNoWithViewCount(no);
    model.addAttribute("board", BoardResponse.from(boardDetailDto)); // 모델에 게시글 추가
    return "board/view"; // 게시글 조회 페이지로 이동
  }

  @PostMapping("/update")
  public String update(@ModelAttribute BoardUpdateRequest request) {
    boardService.updateBoard(request);
    return "redirect:/board/list"; // 변경 후 게시글 목록 페이지로 리다이렉트
  }

  @PostMapping("/delete")
  public String delete(@RequestParam("no") Long no) {
    boardService.deleteBoard(no);
    return "redirect:/board/list"; // 삭제 후 게시글 목록 페이지로 리다이렉트
  }
}
