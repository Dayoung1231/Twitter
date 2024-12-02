package main;

import model.UserModel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;

import javax.swing.*;


public class SignUp extends JFrame {
	
	
    private JTextField idField;
    private JTextField pwdField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneNumField;
    
    private boolean isIdChecked = false; // ID 확인 여부
    private boolean isEmailChecked = false; // 이메일 확인 여부


	public SignUp() {
		initialize();
	}


	private void initialize() {
		setTitle("Sign Up");
	    setBounds(100, 100, 400, 600);
	    setLocationRelativeTo(null);
	    setResizable(false);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    getContentPane().setBackground(Color.WHITE);
	    getContentPane().setLayout(null);
	    
	    
	    // 트위터 아이콘 로드
	    try {
	        URL imageUrl = getClass().getResource("/images/blueBird.png");
	        if (imageUrl != null) {
	        	ImageIcon twitterIcon = new ImageIcon(imageUrl);
                Image scaledImage = twitterIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                twitterIcon = new ImageIcon(scaledImage);
	            JLabel twitterLabel = new JLabel(twitterIcon);
	            twitterLabel.setBounds(170, 60, 40, 40);
	            getContentPane().add(twitterLabel);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    // Label
	    JLabel userIdLabel = new JLabel("ID");
	    userIdLabel.setBounds(55, 152, 52, 21);
	    userIdLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    getContentPane().add(userIdLabel);
	    
	    JLabel pwdLabel = new JLabel("Password");
	    pwdLabel.setBounds(55, 188, 60, 21);
	    pwdLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    getContentPane().add(pwdLabel);
	    
	    JLabel emailLabel = new JLabel("Email");
	    emailLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    emailLabel.setBounds(55, 260, 60, 21);
	    getContentPane().add(emailLabel);
	    
	    JLabel nameLabel = new JLabel("Name");
	    nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    nameLabel.setBounds(55, 224, 60, 21);
	    getContentPane().add(nameLabel);
	    
	    JLabel phoneNumLabel = new JLabel("Phone Num");
	    phoneNumLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    phoneNumLabel.setBounds(55, 296, 87, 21);
	    getContentPane().add(phoneNumLabel);
	    
	    
	    // Button
	    JButton idCheckBtn = new JButton("check");
	    idCheckBtn.setFont(new Font("맑은 고딕", Font.BOLD, 10));
	    idCheckBtn.setBackground(Color.white);
	    idCheckBtn.setBounds(265, 152, 60, 20);
	    idCheckBtn.setFocusPainted(false); // 클릭 시 포커스 효과 제거
	    getContentPane().add(idCheckBtn);
	    
	    JButton emailCheckBtn = new JButton("check");
	    emailCheckBtn.setFont(new Font("맑은 고딕", Font.BOLD, 10));
	    emailCheckBtn.setBackground(Color.white);
	    emailCheckBtn.setBounds(265, 260, 60, 20);
	    emailCheckBtn.setFocusPainted(false); // 클릭 시 포커스 효과 제거
	    getContentPane().add(emailCheckBtn);
	    
	    JButton signUpBtn = new JButton("Sign Up");
	    signUpBtn.setBounds(230, 362, 95, 23);
	    signUpBtn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    signUpBtn.setForeground(Color.white); // 글자 색상: 흰색
        signUpBtn.setBackground(new Color(106, 181, 249)); // 배경 색상: 파란색
        signUpBtn.setFocusPainted(false); // 클릭 시 포커스 효과 제거
	    getContentPane().add(signUpBtn);
	    
	    JButton backBtn = new JButton("Back");
	    backBtn.setBounds(120, 362, 95, 23);
	    backBtn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    backBtn.setForeground(Color.white); // 글자 색상: 흰색
        backBtn.setBackground(new Color(106, 181, 249)); // 배경 색상: 파란색
        backBtn.setFocusPainted(false); // 클릭 시 포커스 효과 제거
	    getContentPane().add(backBtn);
	    
	    // Back Button Action
        backBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                Login loginWindow = new Login();
                loginWindow.setVisible(true);
                dispose();
            });
        });
        
        // ID Check Button Action
        idCheckBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "ID field cannot be empty.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            UserModel userModel = new UserModel();
            boolean isAvailable = userModel.idCheck(id);
            if (isAvailable) {
                JOptionPane.showMessageDialog(null, "ID is available.", "Success", JOptionPane.INFORMATION_MESSAGE);
                isIdChecked = true;
                
                // ID 필드 수정 불가능하게 설정
                idField.setEditable(false);
            } else {
                JOptionPane.showMessageDialog(null, "ID is already in use.", "Error", JOptionPane.WARNING_MESSAGE);
                isIdChecked = false;
            }
        });
        
        // Email Check Button Action
        emailCheckBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Email field cannot be empty.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            UserModel userModel = new UserModel();
            boolean isAvailable = userModel.emailCheck(email);
            if (isAvailable) {
                JOptionPane.showMessageDialog(null, "Email is available.", "Success", JOptionPane.INFORMATION_MESSAGE);
                isEmailChecked = true;
                
                // email 필드 수정 불가능하게 설정
                emailField.setEditable(false);
            } else {
                JOptionPane.showMessageDialog(null, "Email is already in use.", "Error", JOptionPane.WARNING_MESSAGE);
                isEmailChecked = false;
            }
        });

        // Sign Up Button Action
        signUpBtn.addActionListener(e -> {
            if (!isIdChecked || !isEmailChecked) {
                JOptionPane.showMessageDialog(null, "Please verify ID and Email before signing up.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = idField.getText().trim();
            String pwd = pwdField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phoneNum = phoneNumField.getText().trim();

            if (id.isEmpty() || pwd.isEmpty() || name.isEmpty() || email.isEmpty() || phoneNum.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Fill in all the fields.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            UserModel userModel = new UserModel();
            boolean isSignedUp = userModel.signup(id, pwd, name, email, phoneNum);

            if (isSignedUp) {
                JOptionPane.showMessageDialog(null, "Sign-up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> {
                    Login loginWindow = new Login();
                    loginWindow.setVisible(true);
                    dispose();
                });
            } else {
                JOptionPane.showMessageDialog(null, "Sign-up failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
	    
	    
	    // textField
	    idField = new JTextField();
	    idField.setBounds(145, 152, 120, 21);
	    getContentPane().add(idField);
	    idField.setForeground(Color.GRAY);
	    idField.setColumns(10);
	    
	    pwdField = new JTextField();
	    pwdField.setBounds(145, 188, 180, 21);
	    getContentPane().add(pwdField);
	    pwdField.setForeground(Color.GRAY);
	    pwdField.setColumns(10);
	    
	    nameField = new JTextField();
	    nameField.setForeground(Color.GRAY);
	    nameField.setColumns(10);
	    nameField.setBounds(145, 224, 180, 21);
	    getContentPane().add(nameField);
	    
	    emailField = new JTextField();
	    emailField.setForeground(Color.GRAY);
	    emailField.setColumns(10);
	    emailField.setBounds(145, 260, 120, 21);
	    getContentPane().add(emailField);
	    
	    phoneNumField = new JTextField();
	    phoneNumField.setForeground(Color.GRAY);
	    phoneNumField.setColumns(10);
	    phoneNumField.setBounds(145, 296, 180, 21);
	    getContentPane().add(phoneNumField);

	}

}
