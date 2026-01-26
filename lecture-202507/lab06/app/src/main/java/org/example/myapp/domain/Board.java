package org.example.myapp.domain;

import java.time.LocalDateTime;

public class Board {
  private Long no;
  private String title;
  private String content;
  private int viewCount;
  private LocalDateTime createdDate;

  @Override
  public String toString() {
    return "Board{"
        + "no="
        + no
        + ", title='"
        + title
        + '\''
        + ", content='"
        + content
        + '\''
        + ", viewCount="
        + viewCount
        + ", createdDate="
        + createdDate
        + '}';
  }

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

  public int getViewCount() {
    return viewCount;
  }

  public void setViewCount(int viewCount) {
    this.viewCount = viewCount;
  }

  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }
}
