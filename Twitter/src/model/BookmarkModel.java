package model;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookmarkModel {

    /**
     * 북마크 추가 기능
     * @param userId 북마크한 사용자 ID
     * @param postId 북마크된 게시글 ID
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public boolean addBookmark(String userId, int postId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // 이미 북마크했는지 확인
            String checkQuery = "SELECT * FROM bookmark WHERE user_id = ? AND post_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setString(1, userId);
            checkStmt.setInt(2, postId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // 이미 북마크한 경우
                return false; // 실패로 처리
            }

            // 북마크 추가
            String query = "INSERT INTO bookmark (user_id, post_id) VALUES (?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, userId);
            stmt.setInt(2, postId);
            stmt.executeUpdate();
            return true; // 성공
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
