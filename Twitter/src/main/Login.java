package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login {

	private JFrame frame;

	// MySQL 연결 정보
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Twitter";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "ekdud0412?";
    
    
    
    
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	// 외부에서 프레임을 표시하기 위해 추가한 메서드
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }


	public Login() {
		initialize();
	}


	private void initialize() {
	    frame = new JFrame();
	    frame.setBounds(100, 100, 400, 600);
	    frame.setLocationRelativeTo(null);
	    frame.setResizable(false);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().setBackground(Color.WHITE);
	    frame.getContentPane().setLayout(null);
	    
	    
	    // 트위터 아이콘 로드
	    try {
	        URL imageUrl = getClass().getResource("/images/blueBird.png");
	        if (imageUrl != null) {
	        	ImageIcon twitterIcon = new ImageIcon(imageUrl);
                Image scaledImage = twitterIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                twitterIcon = new ImageIcon(scaledImage);
	            JLabel twitterLabel = new JLabel(twitterIcon);
	            twitterLabel.setBounds(170, 60, 40, 40);
	            frame.getContentPane().add(twitterLabel);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    
	    JLabel userIdLabel = new JLabel("ID");
	    userIdLabel.setBounds(81, 152, 52, 15);
	    userIdLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    frame.getContentPane().add(userIdLabel);
	    
	    JLabel pwdLabel = new JLabel("Password");
	    pwdLabel.setBounds(81, 210, 72, 15);
	    pwdLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    frame.getContentPane().add(pwdLabel);
	    
	    
	    JTextField idField = new JTextField();
	    idField.setBounds(80, 179, 221, 21);
	    frame.getContentPane().add(idField);
	    idField.setForeground(Color.GRAY);
	    idField.setColumns(10);
	    
	    JTextField pwdField = new JTextField();
	    pwdField.setBounds(80, 233, 221, 21);
	    frame.getContentPane().add(pwdField);
	    pwdField.setForeground(Color.GRAY);
	    pwdField.setColumns(10);
	    

        
	    JButton logInBtn = new JButton("Login");
	    logInBtn.setBounds(85, 309, 95, 23);
	    logInBtn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    logInBtn.setForeground(Color.white); // 글자 색상: 흰색
        logInBtn.setBackground(new Color(106, 181, 249)); // 배경 색상: 파란색
        logInBtn.setFocusPainted(false); // 클릭 시 포커스 효과 제거
	    frame.getContentPane().add(logInBtn);
	    
	    
	    // Login 버튼 클릭 이벤트
	    logInBtn.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            String userIdInput = idField.getText();
	            String passwordInput = pwdField.getText();

	            if (!userIdInput.isEmpty() && !passwordInput.isEmpty()) {
	                try {
	                    // 데이터베이스 연결
	                    Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

	                    // 사용자 ID 존재 여부 확인
	                    String checkUserQuery = "SELECT * FROM USER WHERE user_id = ?";
	                    PreparedStatement stmt = conn.prepareStatement(checkUserQuery);
	                    stmt.setString(1, userIdInput);
	                    ResultSet rs = stmt.executeQuery();

	                    if (rs.next()) {
	                        // 사용자 ID가 존재하면 비밀번호 확인
	                        String storedPassword = rs.getString("pwd");
	                        if (storedPassword.equals(passwordInput)) {
	                            JOptionPane.showMessageDialog(null, "Logged in!");
	                            
	                            // allPost로 화면 전환
	                            EventQueue.invokeLater(() -> {
	                                allPost allPostWindow = new allPost(userIdInput);
	                                allPostWindow.setVisible(true);
	                            });
	                            frame.dispose(); // 현재 화면 닫기
	                        } else {
	                            JOptionPane.showMessageDialog(null, "incorrect password!");
	                        }
	                    } else {
	                        JOptionPane.showMessageDialog(null, "ID does not exit!");
	                    }

	                    conn.close();
	                } catch (Exception ex) {
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(null, "Log in error");
	                }
	            } else {
	                JOptionPane.showMessageDialog(null, "Enter ID/Password");
	            }
	        }
	    });
    
	    
	    JButton signUpBtn = new JButton("Sign Up");
	    signUpBtn.setBounds(200, 309, 95, 23);
	    signUpBtn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    signUpBtn.setForeground(Color.white); // 글자 색상: 흰색
        signUpBtn.setBackground(new Color(106, 181, 249)); // 배경 색상: 파란색
        signUpBtn.setFocusPainted(false); // 클릭 시 포커스 효과 제거
	    frame.getContentPane().add(signUpBtn);

	    
        // SignUp 버튼 클릭 이벤트
        signUpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // SignUP 클래스의 프레임 호출
                SwingUtilities.invokeLater(() -> {
                    try {
                        SignUp SignUpWindow = new SignUp();
                        SignUpWindow.setVisible(true);
                        frame.dispose(); // 현재 프레임 닫기 (필요 시 유지)
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
	}
}
