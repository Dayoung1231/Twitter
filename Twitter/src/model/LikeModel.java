package model;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LikeModel {

    /**
     * 게시글 좋아요 기능
     * @param userId 좋아요를 누른 사용자 ID
     * @param postId 좋아요를 누른 게시글 ID
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public boolean likePost(String userId, int postId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // 이미 좋아요를 눌렀는지 확인
            String checkLike = "SELECT l_id FROM post_like WHERE user_id = ? AND post_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkLike);
            checkStmt.setString(1, userId);
            checkStmt.setInt(2, postId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("\nYou have already liked this post.");
                return false; // 중복 좋아요
            }

            // 새로운 좋아요 추가
            String addLike = "INSERT INTO post_like (user_id, post_id) VALUES (?, ?)";
            PreparedStatement likeStmt = con.prepareStatement(addLike);
            likeStmt.setString(1, userId);
            likeStmt.setInt(2, postId);
            likeStmt.executeUpdate();

            // 게시글의 좋아요 수 증가
            String updateLikes = "UPDATE posts SET num_of_likes = num_of_likes + 1 WHERE post_id = ?";
            PreparedStatement updateStmt = con.prepareStatement(updateLikes);
            updateStmt.setInt(1, postId);
            updateStmt.executeUpdate();

            //System.out.println("Post liked successfully!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 댓글 좋아요 기능
     * @param userId 좋아요를 누른 사용자 ID
     * @param commentId 좋아요를 누른 댓글 ID
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public boolean likeComment(String userId, int commentId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // 이미 좋아요를 눌렀는지 확인
            String checkLike = "SELECT l_id FROM comment_like WHERE user_id = ? AND cmt_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkLike);
            checkStmt.setString(1, userId);
            checkStmt.setInt(2, commentId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("\nYou have already liked this comment.");
                return false; // 중복 좋아요
            }

            // 새로운 좋아요 추가
            String addLike = "INSERT INTO comment_like (user_id, cmt_id) VALUES (?, ?)";
            PreparedStatement likeStmt = con.prepareStatement(addLike);
            likeStmt.setString(1, userId);
            likeStmt.setInt(2, commentId);
            likeStmt.executeUpdate();

            // 댓글의 좋아요 수 증가
            String updateLikes = "UPDATE comment SET num_of_likes = num_of_likes + 1 WHERE comment_id = ?";
            PreparedStatement updateStmt = con.prepareStatement(updateLikes);
            updateStmt.setInt(1, commentId);
            updateStmt.executeUpdate();

            //System.out.println("Comment liked successfully!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 게시글 좋아요 취소 기능
     * @param userId 좋아요를 취소하는 사용자 ID
     * @param postId 좋아요를 취소할 게시글 ID
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public boolean unlikePost(String userId, int postId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // 좋아요 삭제
            String removeLike = "DELETE FROM post_like WHERE user_id = ? AND post_id = ?";
            PreparedStatement deleteStmt = con.prepareStatement(removeLike);
            deleteStmt.setString(1, userId);
            deleteStmt.setInt(2, postId);
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected > 0) {
                // 게시글의 좋아요 수 감소
                String updateLikes = "UPDATE posts SET num_of_likes = num_of_likes - 1 WHERE post_id = ?";
                PreparedStatement updateStmt = con.prepareStatement(updateLikes);
                updateStmt.setInt(1, postId);
                updateStmt.executeUpdate();

                System.out.println("Post unliked successfully!");
                return true;
            } else {
                System.out.println("You have not liked this post.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 댓글 좋아요 취소 기능
     * @param userId 좋아요를 취소하는 사용자 ID
     * @param commentId 좋아요를 취소할 댓글 ID
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public boolean unlikeComment(String userId, int commentId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // 좋아요 삭제
            String removeLike = "DELETE FROM comment_like WHERE user_id = ? AND cmt_id = ?";
            PreparedStatement deleteStmt = con.prepareStatement(removeLike);
            deleteStmt.setString(1, userId);
            deleteStmt.setInt(2, commentId);
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected > 0) {
                // 댓글의 좋아요 수 감소
                String updateLikes = "UPDATE comment SET num_of_likes = num_of_likes - 1 WHERE comment_id = ?";
                PreparedStatement updateStmt = con.prepareStatement(updateLikes);
                updateStmt.setInt(1, commentId);
                updateStmt.executeUpdate();

                System.out.println("Comment unliked successfully!");
                return true;
            } else {
                System.out.println("You have not liked this comment.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
