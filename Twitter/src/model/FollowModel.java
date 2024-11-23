package model;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FollowModel {

    /**
     * 팔로우 기능
     * @param userId 팔로우를 요청한 사용자 ID
     * @param followUserId 팔로우할 사용자 ID
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public boolean followUser(String userId, String followUserId) {
        try (Connection con = DatabaseConnection.getConnection()) {
        	// 유저 존재 여부 확인
            String checkUserExistence = "SELECT user_id FROM user WHERE user_id = ?";
            PreparedStatement checkUserStmt = con.prepareStatement(checkUserExistence);
            checkUserStmt.setString(1, followUserId);
            ResultSet userCheckResult = checkUserStmt.executeQuery();

            if (!userCheckResult.next()) {
                System.out.print("\nThe user you are trying to follow does not exist.");
                return false; // 유저가 존재하지 않음
            }
            
            // 팔로우 중복 확인
            String checkFollow = "SELECT f_id FROM following WHERE user_id = ? AND following_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkFollow);
            checkStmt.setString(1, userId);
            checkStmt.setString(2, followUserId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.print("\nYou are already following this user.");
                return false; // 이미 팔로우 중
            }

            // following 테이블에 추가
            String addFollowing = "INSERT INTO following (user_id, following_id) VALUES (?, ?)";
            PreparedStatement followStmt = con.prepareStatement(addFollowing);
            followStmt.setString(1, userId);
            followStmt.setString(2, followUserId);
            followStmt.executeUpdate();

            // follower 테이블에 추가
            String addFollower = "INSERT INTO follower (user_id, follower_id) VALUES (?, ?)";
            PreparedStatement followerStmt = con.prepareStatement(addFollower);
            followerStmt.setString(1, followUserId);
            followerStmt.setString(2, userId);
            followerStmt.executeUpdate();

            //System.out.println("\nFollowed user successfully!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 언팔로우 기능
     * @param userId 언팔로우를 요청한 사용자 ID
     * @param unfollowUserId 언팔로우할 사용자 ID
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public boolean unfollowUser(String userId, String unfollowUserId) {
        try (Connection con = DatabaseConnection.getConnection()) {
        	// 유저 존재 여부 확인
            String checkUserExistence = "SELECT user_id FROM user WHERE user_id = ?";
            PreparedStatement checkUserStmt = con.prepareStatement(checkUserExistence);
            checkUserStmt.setString(1, unfollowUserId);
            ResultSet userCheckResult = checkUserStmt.executeQuery();

            if (!userCheckResult.next()) {
                System.out.print("\nThe user you are trying to unfollow does not exist.");
                return false; // 유저가 존재하지 않음
            }
            
            // following 테이블에서 제거
            String removeFollowing = "DELETE FROM following WHERE user_id = ? AND following_id = ?";
            PreparedStatement unfollowStmt = con.prepareStatement(removeFollowing);
            unfollowStmt.setString(1, userId);
            unfollowStmt.setString(2, unfollowUserId);
            int rowsAffected = unfollowStmt.executeUpdate();

            if (rowsAffected > 0) {
                // follower 테이블에서 제거
                String removeFollower = "DELETE FROM follower WHERE user_id = ? AND follower_id = ?";
                PreparedStatement removeFollowerStmt = con.prepareStatement(removeFollower);
                removeFollowerStmt.setString(1, unfollowUserId);
                removeFollowerStmt.setString(2, userId);
                removeFollowerStmt.executeUpdate();

                System.out.println("Unfollowed user successfully!");
                return true;
            } else {
                System.out.println("You are not following this user.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 팔로잉 목록 조회
     * @param userId 조회할 사용자 ID
     * @return 팔로잉 사용자 목록 (문자열) 또는 null (팔로잉 목록이 없는 경우)
     */
    public String getFollowingList(String userId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            String getFollowing = "SELECT following_id FROM following WHERE user_id = ?";
            PreparedStatement stmt = con.prepareStatement(getFollowing);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder followingList = new StringBuilder();
            while (rs.next()) {
                followingList.append(rs.getString("following_id")).append("\n");
            }

            if (followingList.length() > 0) {
                //System.out.println("Following list fetched successfully!");
                return followingList.toString();
            } else {
                //System.out.println("You are not following anyone.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 팔로워 목록 조회
     * @param userId 조회할 사용자 ID
     * @return 팔로워 사용자 목록 (문자열) 또는 null (팔로워 목록이 없는 경우)
     */
    public String getFollowerList(String userId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            String getFollowers = "SELECT follower_id FROM follower WHERE user_id = ?";
            PreparedStatement stmt = con.prepareStatement(getFollowers);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder followerList = new StringBuilder();
            while (rs.next()) {
                followerList.append(rs.getString("follower_id")).append("\n");
            }

            if (followerList.length() > 0) {
                //System.out.println("Follower list fetched successfully!");
                return followerList.toString();
            } else {
                //System.out.println("You have no followers.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
