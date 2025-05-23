package model;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostModel {

    /**
     * 게시글 작성 기능
     * @param userId  작성자 ID
     * @param message 게시글 내용
     * @param photoUrl 게시글에 첨부된 사진 URL (없을 경우 null)
     * @return 성공 여부 (true: 성공, false: 실패)
     */
	public boolean savePost(String userId, String message, String photoUrl) {
        try (Connection con = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO posts (writer_id, message, photo_url) VALUES (?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, userId);
            pstmt.setString(2, message);
            pstmt.setString(3, photoUrl.isEmpty() ? null : photoUrl);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	

    /**
     * 게시글 조회 기능
     * @param postId 게시글 ID
     * @return 게시글 내용 (문자열) 또는 null (게시글이 없는 경우)
     */
    public String getPost(int postId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            String getPost = "SELECT * FROM posts WHERE post_id = ?";
            PreparedStatement stmt = con.prepareStatement(getPost);
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                StringBuffer postDetails = new StringBuffer();
                postDetails.append("Post ID: ").append(rs.getInt("post_id")).append("\n");
                postDetails.append("Message: ").append(rs.getString("message")).append("\n");
                postDetails.append("Photo URL: ").append(rs.getString("photo_url")).append("\n");
                postDetails.append("Likes: ").append(rs.getInt("num_of_likes")).append("\n");
                postDetails.append("Created At: ").append(rs.getTimestamp("created_at")).append("\n");
                //System.out.println(postDetails);
                return postDetails.toString();
            } else {
                //System.out.println("Post not found.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 게시글 삭제 기능
     * @param postId 게시글 ID
     * @param userId 삭제 요청을 한 사용자 ID (작성자인지 확인)
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public boolean deletePost(int postId, String userId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // 게시글 작성자인지 확인
            String checkOwnership = "SELECT writer_id FROM posts WHERE post_id = ? AND writer_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkOwnership);
            checkStmt.setInt(1, postId);
            checkStmt.setString(2, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("You are not the owner of this post.");
                return false; // 작성자가 아님
            }

            // 게시글 삭제
            String deletePost = "DELETE FROM posts WHERE post_id = ?";
            PreparedStatement deleteStmt = con.prepareStatement(deletePost);
            deleteStmt.setInt(1, postId);
            deleteStmt.executeUpdate();
            System.out.println("Post deleted successfully!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
