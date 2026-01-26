package org.example.myapp;

import com.google.common.base.Joiner;

public class App6 {
  public static void main(String[] args) {
    String message = Joiner.on(", ").join("hello", "world");
    System.out.println(message);
  }
}
