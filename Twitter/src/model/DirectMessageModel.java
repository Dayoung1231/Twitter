package model;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class DirectMessageModel {

    /**
     * DM 전송 기능
     * @param senderId   발신자 ID
     * @param receiverId 수신자 ID
     * @param message    메시지 내용
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public boolean sendMessage(String senderId, String receiverId, String message) {
        try (Connection con = DatabaseConnection.getConnection()) {
        	// 수신자 존재 여부 확인
            String checkReceiverExistence = "SELECT user_id FROM user WHERE user_id = ?";
            PreparedStatement checkReceiverStmt = con.prepareStatement(checkReceiverExistence);
            checkReceiverStmt.setString(1, receiverId);
            ResultSet receiverCheckResult = checkReceiverStmt.executeQuery();

            if (!receiverCheckResult.next()) {
                System.out.print("\nThe receiver does not exist.");
                return false; // 수신자가 존재하지 않음
            }

            // DM 추가
            String insertDM = "INSERT INTO dm (sender_id, receiver_id, message) VALUES (?, ?, ?)";
            PreparedStatement dmStmt = con.prepareStatement(insertDM);
            dmStmt.setString(1, senderId);
            dmStmt.setString(2, receiverId);
            dmStmt.setString(3, message);
            dmStmt.executeUpdate();

            //System.out.println("DM sent successfully!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 대화 기록 조회 기능
     * @param userId1 사용자 1 ID
     * @param userId2 사용자 2 ID
     * @return 두 사용자 간의 대화 기록 (문자열) 또는 null (대화 기록이 없는 경우)
     */
    public String getConversation(String userId1, String userId2) {
        try (Connection con = DatabaseConnection.getConnection()) {
        	// 수신자 존재 여부 확인
            String checkReceiverExistence = "SELECT user_id FROM user WHERE user_id = ?";
            PreparedStatement checkReceiverStmt = con.prepareStatement(checkReceiverExistence);
            checkReceiverStmt.setString(1, userId2);
            ResultSet receiverCheckResult = checkReceiverStmt.executeQuery();

            if (!receiverCheckResult.next()) {
                System.out.print("\nThe user you are trying to fetch conversation with does not exist.");
                return null; // 수신자가 존재하지 않음
            }
            
            // 두 사용자 간의 대화 기록 조회
            String getDMHistory = "SELECT * FROM dm WHERE " +
                    "(sender_id = ? AND receiver_id = ?) OR " +
                    "(sender_id = ? AND receiver_id = ?) " +
                    "ORDER BY created_at";
            PreparedStatement stmt = con.prepareStatement(getDMHistory);
            stmt.setString(1, userId1);
            stmt.setString(2, userId2);
            stmt.setString(3, userId2);
            stmt.setString(4, userId1);
            ResultSet rs = stmt.executeQuery();

            StringBuilder conversation = new StringBuilder();
            while (rs.next()) {
                String senderId = rs.getString("sender_id");
                String receiverId = rs.getString("receiver_id");
                String message = rs.getString("message");
                Timestamp createdAt = rs.getTimestamp("created_at");

                conversation.append("[From: ").append(senderId)
                        .append(" To: ").append(receiverId).append("]\n")
                        .append("Message: ").append(message).append("\n")
                        .append("Sent At: ").append(createdAt).append("\n")
                        .append("-------------------\n");
            }

            if (conversation.length() > 0) {
                //System.out.println("Conversation fetched successfully!");
                return conversation.toString();
            } else {
                //System.out.println("No conversation found between these users.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 특정 사용자와 주고받은 DM 삭제
     * @param userId1 사용자 1 ID
     * @param userId2 사용자 2 ID
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public boolean deleteConversation(String userId1, String userId2) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // 두 사용자 간의 모든 대화 삭제
            String deleteDMs = "DELETE FROM dm WHERE " +
                    "(sender_id = ? AND receiver_id = ?) OR " +
                    "(sender_id = ? AND receiver_id = ?)";
            PreparedStatement stmt = con.prepareStatement(deleteDMs);
            stmt.setString(1, userId1);
            stmt.setString(2, userId2);
            stmt.setString(3, userId2);
            stmt.setString(4, userId1);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Conversation deleted successfully!");
                return true;
            } else {
                System.out.println("No conversation to delete.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
