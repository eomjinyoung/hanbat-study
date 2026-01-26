package org.example.myapp.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.example.myapp.dto.*;
import org.example.myapp.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/board")
public class BoardController {

  private final BoardService boardService;

  public BoardController(BoardService boardService) {
    this.boardService = boardService;
  }

  @GetMapping("/form")
  public void form(Model model) {
    model.addAttribute("boardCreateRequest", new BoardCreateRequest());
  }

  @PostMapping("/add")
  public String add(
      @Valid @ModelAttribute BoardCreateRequest request, BindingResult bindingResult) {

    // 검증 실패 시 에러 응답
    if (bindingResult.hasErrors()) {
      // BindingResult가 "boardCreateRequest" 이름으로 Model에 자동 저장됨
      // @ModelAttribute("aaa")로 이름이 명시된 경우 "aaa" 이름으로 저장됨
      return "board/form"; // 에러가 발생하면 다시 폼 페이지로 이동
    }

    boardService.addBoard(request);
    return "redirect:/board/list"; // 저장 후 게시글 목록 페이지로 리다이렉트
  }

  @GetMapping("/list")
  public String list(Model model) {
    List<BoardSummaryDto> boards = boardService.getAllBoards();
    List<BoardResponse> responses = boards.stream().map(BoardResponse::from).toList();
    model.addAttribute("boards", responses);
    return "board/list"; // 게시글 목록 페이지로 이동
  }

  @GetMapping("/view")
  public String view(@RequestParam("no") Long no, Model model) {
    BoardDetailDto boardDetailDto = boardService.getBoardByNoWithViewCount(no);

    // 읽기 전용 정보
    model.addAttribute("board", BoardResponse.from(boardDetailDto));

    // 폼 입력 항목에 출력될 정보
    BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest();
    boardUpdateRequest.setNo(boardDetailDto.getNo());
    boardUpdateRequest.setTitle(boardDetailDto.getTitle());
    boardUpdateRequest.setContent(boardDetailDto.getContent());
    model.addAttribute("boardUpdateRequest", boardUpdateRequest);

    return "board/view"; // 게시글 조회 페이지로 이동
  }

  @PostMapping("/update")
  public String update(
      @Valid @ModelAttribute BoardUpdateRequest request, BindingResult bindingResult, Model model) {

    // 검증 실패 시 에러 응답
    if (bindingResult.hasErrors()) {
      BoardDetailDto boardDetailDto = boardService.getBoardByNo(request.getNo());
      model.addAttribute("board", BoardResponse.from(boardDetailDto));
      return "board/view"; // 에러가 발생하면 다시 게시글 조회 페이지로 이동
    }

    boardService.updateBoard(request);
    return "redirect:/board/list"; // 변경 후 게시글 목록 페이지로 리다이렉트
  }

  @PostMapping("/delete")
  public String delete(@RequestParam("no") Long no) {
    boardService.deleteBoard(no);
    return "redirect:/board/list"; // 삭제 후 게시글 목록 페이지로 리다이렉트
  }
}
