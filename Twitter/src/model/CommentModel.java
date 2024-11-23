package model;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CommentModel {

    /**
     * 댓글 작성 기능
     * @param userId   작성자 ID
     * @param postId   댓글을 작성할 게시글 ID
     * @param message  댓글 내용
     * @param photoUrl 댓글에 첨부된 사진 URL (없을 경우 null)
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public boolean addComment(String userId, int postId, String message, String photoUrl) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // 댓글 추가
            String insertComment = "INSERT INTO comment (user_id, post_id, message, photo_url) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(insertComment);
            stmt.setString(1, userId);
            stmt.setInt(2, postId);
            stmt.setString(3, message);
            stmt.setString(4, photoUrl);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 댓글 조회 기능
     * @param postId 댓글이 달린 게시글 ID
     * @return 댓글 목록 (문자열) 또는 null (댓글이 없는 경우)
     */
    public String getCommentsByPost(int postId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            String getComments = "SELECT * FROM comment WHERE post_id = ?";
            PreparedStatement stmt = con.prepareStatement(getComments);
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder comments = new StringBuilder();
            while (rs.next()) {
                comments.append("Comment ID: ").append(rs.getInt("comment_id")).append("\n");
                comments.append("Message: ").append(rs.getString("message")).append("\n");
                comments.append("Photo URL: ").append(rs.getString("photo_url")).append("\n");
                comments.append("Likes: ").append(rs.getInt("num_of_likes")).append("\n");
                comments.append("Created At: ").append(rs.getTimestamp("created_at")).append("\n");
                comments.append("-------------------\n");
            }

            if (comments.length() > 0) {
                System.out.println("Comments fetched successfully!");
                return comments.toString();
            } else {
                System.out.println("No comments found for this post.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 댓글 삭제 기능
     * @param commentId 삭제할 댓글 ID
     * @param userId    삭제 요청을 한 사용자 ID (작성자인지 확인)
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public boolean deleteComment(int commentId, String userId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // 댓글 작성자인지 확인
            String checkOwnership = "SELECT user_id FROM comment WHERE comment_id = ? AND user_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkOwnership);
            checkStmt.setInt(1, commentId);
            checkStmt.setString(2, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("\nYou are not the owner of this comment.");
                return false; // 작성자가 아님
            }

            // 댓글 삭제
            String deleteComment = "DELETE FROM comment WHERE comment_id = ?";
            PreparedStatement deleteStmt = con.prepareStatement(deleteComment);
            deleteStmt.setInt(1, commentId);
            deleteStmt.executeUpdate();
            System.out.println("\nComment deleted successfully!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
