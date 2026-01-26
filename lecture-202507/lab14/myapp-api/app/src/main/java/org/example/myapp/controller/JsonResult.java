package org.example.myapp.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JsonResult {
  public static final String SUCCESS = "success";
  public static final String FAILURE = "failure";

  private final String status;
  private final Object content;
}
