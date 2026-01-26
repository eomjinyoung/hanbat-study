package org.example.myapp.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.example.myapp.dto.*;
import org.example.myapp.service.BoardService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards")
public class BoardController {

  private final BoardService boardService;

  public BoardController(BoardService boardService) {
    this.boardService = boardService;
  }

  @PostMapping
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

  @GetMapping
  public JsonResult list() {
    List<BoardSummaryDto> boards = boardService.getAllBoards();
    return JsonResult.builder()
        .status(JsonResult.SUCCESS)
        .content(boards.stream().map(BoardResponse::from).toList())
        .build();
  }

  @GetMapping("/{no}")
  public JsonResult view(@PathVariable("no") Long no) {
    BoardDetailDto boardDetailDto = boardService.getBoardByNoWithViewCount(no);
    return JsonResult.builder()
        .status(JsonResult.SUCCESS)
        .content(BoardResponse.from(boardDetailDto))
        .build();
  }

  @PatchMapping("/{no}")
  public JsonResult update(
      @PathVariable("no") Long no,
      @Valid @RequestBody BoardUpdateRequest request,
      BindingResult bindingResult) {

    // 검증 실패 시 에러 응답
    if (bindingResult.hasErrors()) {
      return JsonResult.builder()
          .status(JsonResult.FAILURE)
          .content(bindingResult.getFieldErrors().getFirst().getDefaultMessage())
          .build();
    }

    request.setNo(no);
    boardService.updateBoard(request);
    return JsonResult.builder().status(JsonResult.SUCCESS).build();
  }

  @DeleteMapping("/{no}")
  public JsonResult delete(@PathVariable("no") Long no) {
    boardService.deleteBoard(no);
    return JsonResult.builder().status(JsonResult.SUCCESS).build();
  }
}
