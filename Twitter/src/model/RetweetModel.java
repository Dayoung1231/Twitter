package model;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RetweetModel {

    /**
     * 리트윗 기능
     * @param userId 리트윗한 사용자 ID
     * @param postId 리트윗된 게시글 ID
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public boolean addRetweet(String userId, int postId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // 이미 리트윗했는지 확인
            String checkQuery = "SELECT * FROM retweet WHERE user_id = ? AND post_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setString(1, userId);
            checkStmt.setInt(2, postId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // 이미 리트윗한 경우
                return false; // 실패로 처리
            }

            // 리트윗 추가
            String query = "INSERT INTO retweet (user_id, post_id) VALUES (?, ?)";
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
