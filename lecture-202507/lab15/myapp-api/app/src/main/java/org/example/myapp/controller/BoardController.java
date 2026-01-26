package org.example.myapp.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.example.myapp.dto.*;
import org.example.myapp.service.BoardService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
public class BoardController {

  private final BoardService boardService;

  public BoardController(BoardService boardService) {
    this.boardService = boardService;
  }

  @PostMapping("/add")
  public JsonResult add(
      @Valid @RequestBody BoardCreateRequest request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return JsonResult.builder()
          .status(JsonResult.FAILURE)
          .content(bindingResult.getFieldErrors().getFirst().getDefaultMessage())
          .build();
    }
    boardService.addBoard(request);
    return JsonResult.builder().status(JsonResult.SUCCESS).build();
  }

  @GetMapping("/list")
  public JsonResult list(Model model) {
    List<BoardSummaryDto> boards = boardService.getAllBoards();
    return JsonResult.builder()
        .status(JsonResult.SUCCESS)
        .content(boards.stream().map(BoardResponse::from).toList())
        .build();
  }

  @GetMapping("/view")
  public JsonResult view(@RequestParam("no") Long no, Model model) {
    BoardDetailDto boardDetailDto = boardService.getBoardByNoWithViewCount(no);
    return JsonResult.builder()
        .status(JsonResult.SUCCESS)
        .content(BoardResponse.from(boardDetailDto))
        .build();
  }

  @PatchMapping("/update")
  public JsonResult update(
      @Valid @RequestBody BoardUpdateRequest request, BindingResult bindingResult, Model model) {

    // 검증 실패 시 에러 응답
    if (bindingResult.hasErrors()) {
      return JsonResult.builder()
          .status(JsonResult.FAILURE)
          .content(bindingResult.getFieldErrors().getFirst().getDefaultMessage())
          .build();
    }

    boardService.updateBoard(request);
    return JsonResult.builder().status(JsonResult.SUCCESS).build();
  }

  @DeleteMapping("/delete")
  public JsonResult delete(@RequestParam("no") Long no) {
    boardService.deleteBoard(no);
    return JsonResult.builder().status(JsonResult.SUCCESS).build();
  }
}
