package model;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class UserModel {


    public boolean signup(String id, String pwd, String userName, String email, String phoneNum) {
        try (Connection con = DatabaseConnection.getConnection()) {

            // 사용자 등록
            String registerUser = "INSERT INTO user (user_id, pwd, user_name, email, phone_num) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement registerStmt = con.prepareStatement(registerUser);
            registerStmt.setString(1, id);
            registerStmt.setString(2, pwd);
            registerStmt.setString(3, userName);
            registerStmt.setString(4, email);
            registerStmt.setString(5, phoneNum);
            registerStmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ID 중복 확인
    public boolean idCheck(String id) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // ID 중복 확인
            String checkUserId = "SELECT user_id FROM user WHERE user_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkUserId);
            checkStmt.setString(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // ID 중복
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Email 중복 확인
    public boolean emailCheck(String email) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // 이메일 중복 확인
            String checkEmail = "SELECT user_id FROM user WHERE email = ?";
            PreparedStatement emailStmt = con.prepareStatement(checkEmail);
            emailStmt.setString(1, email);
            ResultSet rs = emailStmt.executeQuery();
            if (rs.next()) {
                return false; // 이메일 중복
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /* 사용하지 않으므로 주석 처리
    public String login(String id, String pwd) {
        try (Connection con = DatabaseConnection.getConnection()) {
            String loginQuery = "SELECT user_id FROM user WHERE user_id = ? AND pwd = ?";
            PreparedStatement loginStmt = con.prepareStatement(loginQuery);
            loginStmt.setString(1, id);
            loginStmt.setString(2, pwd);
            ResultSet rs = loginStmt.executeQuery();
            if (rs.next()) {
                System.out.println("\nLogin successful!");
                return id; // 로그인 성공
            } else {
                System.out.println("\nInvalid ID or password.");
                return null; // 로그인 실패
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean changePassword(String id, String currentPwd, String newPwd) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // 사용자 확인
            String checkUser = "SELECT user_id FROM user WHERE user_id = ? AND pwd = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkUser);
            checkStmt.setString(1, id);
            checkStmt.setString(2, currentPwd);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) { // 현재 ID 또는 비밀번호가 틀린 경우
                System.out.println("Incorrect ID or current password.");
                return false;
            }

            // 비밀번호 업데이트
            String updatePassword = "UPDATE user SET pwd = ? WHERE user_id = ?";
            PreparedStatement updateStmt = con.prepareStatement(updatePassword);
            updateStmt.setString(1, newPwd);
            updateStmt.setString(2, id);
            updateStmt.executeUpdate();
            //System.out.println("Password changed successfully!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    */
}
