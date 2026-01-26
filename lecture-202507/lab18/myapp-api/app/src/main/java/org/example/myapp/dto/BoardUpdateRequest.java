package org.example.myapp.dto;

import jakarta.validation.constraints.*;

public class BoardUpdateRequest {

  private Long no;

  @NotBlank(message = "제목은 필수 입력 항목입니다")
  @Size(min = 2, max = 100, message = "제목은 2자 이상 100자 이하로 입력해주세요")
  @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s!?.,'-]+$", message = "제목에는 특수문자를 사용할 수 없습니다 (일부 문장부호 제외)")
  private String title;

  @NotBlank(message = "내용은 필수 입력 항목입니다")
  @Size(min = 10, max = 5000, message = "내용은 10자 이상 5000자 이하로 입력해주세요")
  private String content;

  public Long getNo() {
    return no;
  }

  public void setNo(Long no) {
    this.no = no;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
