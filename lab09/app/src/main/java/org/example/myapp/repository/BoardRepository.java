package org.example.myapp.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.example.myapp.domain.Board;
import org.example.myapp.dto.BoardDetailDto;
import org.example.myapp.dto.BoardSummaryDto;
import org.springframework.stereotype.Repository;

@Repository
public class BoardRepository {

  private final String url = "jdbc:mysql://localhost:3306/studydb";
  private final String username = "study";
  private final String password = "study";
  private final Connection conn;

  public BoardRepository() throws Exception {
    conn = DriverManager.getConnection(url, username, password);
  }

  public void insert(Board board) {
    String sql = "INSERT INTO boards (title, content) VALUES (?, ?)";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, board.getTitle());
      pstmt.setString(2, board.getContent());
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw new RuntimeException("게시글 저장 중 오류 발생", e);
    }
  }

  public List<BoardSummaryDto> findAll() {
    List<BoardSummaryDto> boards = new ArrayList<>();
    try (Statement stmt = conn.createStatement();
        ResultSet rs =
            stmt.executeQuery("SELECT no, title, created_date, view_count FROM boards")) {
      while (rs.next()) {
        BoardSummaryDto boardDto = new BoardSummaryDto();
        boardDto.setNo(rs.getLong("no"));
        boardDto.setTitle(rs.getString("title"));
        boardDto.setViewCount(rs.getInt("view_count"));
        boardDto.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
        boards.add(boardDto);
      }
    } catch (Exception e) {
      throw new RuntimeException("게시글 목록 조회 중 오류 발생", e);
    }
    return boards;
  }

  public BoardDetailDto findByNo(Long no) {
    String sql = "SELECT no, title, content, view_count, created_date FROM boards WHERE no = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setLong(1, no);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          BoardDetailDto boardDto = new BoardDetailDto();
          boardDto.setNo(rs.getLong("no"));
          boardDto.setTitle(rs.getString("title"));
          boardDto.setContent(rs.getString("content"));
          boardDto.setViewCount(rs.getInt("view_count"));
          boardDto.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
          return boardDto;
        } else {
          throw new RuntimeException("게시글을 찾을 수 없습니다.");
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("게시글 상세 조회 중 오류 발생", e);
    }
  }

  public void update(Board board) {
    String sql = "UPDATE boards SET title = ?, content = ? WHERE no = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, board.getTitle());
      pstmt.setString(2, board.getContent());
      pstmt.setLong(3, board.getNo());
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw new RuntimeException("게시글 수정 중 오류 발생", e);
    }
  }

  public void updateViewCount(Long no) {
    String sql = "UPDATE boards SET view_count = view_count + 1 WHERE no = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setLong(1, no);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw new RuntimeException("게시글 조회수 증가 중 오류 발생", e);
    }
  }

  public void delete(Long no) {
    String sql = "DELETE FROM boards WHERE no = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setLong(1, no);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw new RuntimeException("게시글 삭제 중 오류 발생", e);
    }
  }

  public int count() {
    String sql = "SELECT COUNT(*) FROM boards";
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      rs.next();
      return rs.getInt(1);
    } catch (Exception e) {
      throw new RuntimeException("게시글 개수 조회 중 오류 발생", e);
    }
  }
}
