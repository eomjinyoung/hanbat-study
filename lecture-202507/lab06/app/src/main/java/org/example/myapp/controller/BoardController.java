package org.example.myapp.controller;

import java.util.List;
import org.example.myapp.domain.Board;
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
  public String add(@ModelAttribute Board board) {
    boardService.addBoard(board);
    return "redirect:/board/list"; // 저장 후 게시글 목록 페이지로 리다이렉트
  }

  @GetMapping("/list")
  public String list(Model model) {
    List<Board> boards = boardService.getAllBoards();
    model.addAttribute("boards", boards); // 모델에 게시글 목록 추가
    return "board/list"; // 게시글 목록 페이지로 이동
  }

  @GetMapping("/view")
  public String view(@RequestParam("no") Long no, Model model) {
    Board board = boardService.getBoardByNo(no);
    if (board == null) {
      return "redirect:/board/list"; // 게시글이 없으면 목록 페이지로 리다이렉트
    }
    board.setViewCount(board.getViewCount() + 1); // 조회수 증가

    // List 에 저장된 객체의 값을 그대로 변경했기 때문에 추가로 updateBoard()를 호출할 필요는 없다.
    // boardService.updateBoard(board); // 조회수 업데이트

    model.addAttribute("board", board); // 모델에 게시글 추가
    return "board/view"; // 게시글 조회 페이지로 이동
  }

  @PostMapping("/update")
  public String update(@ModelAttribute Board board) {
    Board old = boardService.getBoardByNo(board.getNo());
    if (old == null) {
      return "redirect:/board/list"; // 게시글이 없으면 목록 페이지로 리다이렉트
    }

    old.setTitle(board.getTitle());
    old.setContent(board.getContent());

    // List 에 저장된 객체의 값을 변경했기 때문에 추가로 updateBoard()를 호출할 필요는 없다.
    // boardService.updateBoard(board);

    return "redirect:/board/view?no=" + board.getNo(); // 변경 후 게시글 조회 페이지로 리다이렉트
  }

  @PostMapping("/delete")
  public String delete(@RequestParam("no") Long no) {
    boardService.deleteBoard(no);
    return "redirect:/board/list"; // 삭제 후 게시글 목록 페이지로 리다이렉트
  }
}
