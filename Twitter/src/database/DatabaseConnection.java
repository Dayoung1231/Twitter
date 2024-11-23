package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    // 데이터베이스 연결 정보
    private static final String URL = "jdbc:mysql://localhost:3306/twitter"; // your_database_name을 실제 데이터베이스 이름으로 변경하세요.
    private static final String USER = "root"; // 데이터베이스 사용자 이름
    private static final String PASSWORD = "ekdud0412?"; // 데이터베이스 비밀번호

    // 데이터베이스 연결 메서드
    public static Connection getConnection() {
        try {
            // JDBC 드라이버 등록
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 데이터베이스 연결 생성
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


