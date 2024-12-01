package main;

import java.sql.Connection;
import java.sql.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import javax.swing.*;

import model.UserModel;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URI;

public class editProfile extends JFrame {

	// MySQL 연결 정보
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Twitter";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "ekdud0412?";
    
    private JTextField nameField;
    private JTextField phoneNumField;
    private JTextField introField;
    private JTextField imageField;
    
    private String currentUser;
	
	public editProfile(String userId) {
		this.currentUser = userId;
		initialize(currentUser);
	}


	private void initialize(String currentUser) {
        // JFrame 설정
        setTitle("Post Details");
        setBounds(100, 100, 400, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);
        
        // 상단 패널 생성
	    JPanel topPanel = new JPanel();
	    topPanel.setBackground(new Color(106, 181, 249)); // 트위터와 비슷한 파란색
	    topPanel.setBounds(0, 0, 390, 60);
	    topPanel.setLayout(null);
	    editProfile.this.getContentPane().add(topPanel);


	    // 트위터 아이콘 로드
	    try {
	        URL imageUrl = getClass().getResource("/images/twitterBird.jpeg");
	        if (imageUrl != null) {
	        	ImageIcon twitterIcon = new ImageIcon(imageUrl);
                Image scaledImage = twitterIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                twitterIcon = new ImageIcon(scaledImage);
	            JLabel twitterLabel = new JLabel(twitterIcon);
	            twitterLabel.setBounds(10, 10, 40, 40);
	            topPanel.add(twitterLabel);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // 텍스트 라벨 생성
	    JLabel textLabel = new JLabel("Edit Profile");
	    textLabel.setFont(new Font("Arial", Font.BOLD, 20));
	    textLabel.setForeground(Color.WHITE);
	    textLabel.setBounds(60, 15, 150, 30);
	    topPanel.add(textLabel);
	    
	    
	    // back 버튼
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        backBtn.setBounds(290, 20, 80, 26);
        backBtn.setForeground(new Color(106, 181, 249)); // 글자 색상: 흰색
        backBtn.setBackground(Color.WHITE); // 배경 색상: 파란색
        backBtn.setFocusPainted(false); // 클릭 시 포커스 효과 제거
        backBtn.setBorderPainted(false); // 기본 테두리 제거
        backBtn.setOpaque(false); // 불투명 효과 제거
        backBtn.setContentAreaFilled(false); // 버튼 배경 투명 처리
        topPanel.add(backBtn);
        

        // back 버튼 클릭 이벤트
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Profile 클래스의 프레임 호출
                SwingUtilities.invokeLater(() -> {
                    try {
                        Profile profileWindow = new Profile(currentUser);
                        profileWindow.setVisible(true);
                        editProfile.this.dispose(); // 현재 프레임 닫기 (필요 시 유지)
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
        
        
        // 둥근 버튼 모양 만들기
        backBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 버튼 배경 채우기
                g2.setColor(backBtn.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 30, 30); // 둥근 사각형: 30px radius

                super.paint(g, c);
            }
        });
        
        
        // 수정 패널 생성
	    JPanel editPanel = new JPanel();
	    editPanel.setBackground(Color.white);
	    editPanel.setBounds(0, 60, 390, 503);
	    editPanel.setLayout(null);
	    editProfile.this.getContentPane().add(editPanel);
	    
	    // Label
	    JLabel nameLabel = new JLabel("Name");
	    nameLabel.setBounds(55, 76, 52, 21);
	    nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    editPanel.add(nameLabel);
	    
	    JLabel phoneNumLabel = new JLabel("Phone Num");
	    phoneNumLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    phoneNumLabel.setBounds(55, 112, 80, 21);
	    editPanel.add(phoneNumLabel);
	    
	    JLabel introLabel = new JLabel("Bio");
	    introLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    introLabel.setBounds(55, 148, 60, 21);
	    editPanel.add(introLabel);
	    
	    JLabel imageLabel = new JLabel("Image");
	    imageLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    imageLabel.setBounds(55, 184, 60, 21);
	    editPanel.add(imageLabel);
	    
	    
	    // Button
	    JButton editBtn = new JButton("Edit");
	    editBtn.setBounds(150, 250, 95, 23);
	    editBtn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	    editBtn.setForeground(Color.white); // 글자 색상: 흰색
        editBtn.setBackground(new Color(106, 181, 249)); // 배경 색상: 파란색
        editBtn.setFocusPainted(false); // 클릭 시 포커스 효과 제거
	    editPanel.add(editBtn);

        // edit Button Action
        editBtn.addActionListener(e -> {

        	String name = nameField.getText().trim();
            String phoneNum = phoneNumField.getText().trim();
            String intro = introField.getText().trim();
            String image = imageField.getText().trim();

            UserModel userModel = new UserModel();
            boolean isEdited = userModel.edit(currentUser, name, phoneNum, intro, image);

            if (isEdited) {
                JOptionPane.showMessageDialog(null, "Edit successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> {
                    Profile profileWindow = new Profile(currentUser);
                    profileWindow.setVisible(true);
                    dispose();
                });
            } else {
                JOptionPane.showMessageDialog(null, "Edit failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
	 
        
        
        // textField
        nameField = new JTextField();
	    nameField.setBounds(145, 76, 180, 21);
	    nameField.setForeground(Color.GRAY);
	    nameField.setColumns(10);
	    editPanel.add(nameField);
	    
	    phoneNumField = new JTextField();
	    phoneNumField.setForeground(Color.GRAY);
	    phoneNumField.setColumns(10);
	    phoneNumField.setBounds(145, 112, 180, 21);
	    editPanel.add(phoneNumField);
	    
	    introField = new JTextField();
	    introField.setForeground(Color.GRAY);
	    introField.setColumns(10);
	    introField.setBounds(145, 148, 180, 21);
	    editPanel.add(introField);
	    
	    imageField = new JTextField();
	    imageField.setForeground(Color.GRAY);
	    imageField.setColumns(10);
	    imageField.setBounds(145, 184, 180, 21);
	    editPanel.add(imageField);

	}
	


}
