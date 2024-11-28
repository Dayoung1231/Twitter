package main;

import java.sql.Connection;
import java.sql.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.*;

import model.PostModel;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URI;

public class createPost extends JFrame {

	// MySQL 연결 정보
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Twitter";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "ekdud0412?";
    
    
    private final JPanel postPanel = new JPanel();
    private JTextField nameField;
    private JTextField idField;
    
    private String userID = "user7"; // 일단 설정
    private JTextArea messageArea;    // 메시지 입력 필드
    private JTextField photoURLField; // 포토 URL 필드


	public createPost() {
		initialize();
	}


	private void initialize() {
        
        // JFrame 설정
        setTitle("Create Post");
        setBounds(100, 100, 400, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);
        postPanel.setBackground(new Color(255, 255, 255));
        
        // 포스트 패널
        postPanel.setBounds(1, 1, 384, 562);
        createPost.this.getContentPane().add(postPanel);
        postPanel.setLayout(null);
        
        // 스크롤 패널
        JScrollPane scrollPane = new JScrollPane(postPanel);
        scrollPane.setBounds(0, 0, 386, 563); // 스크롤 패널 크기 설정
        createPost.this.getContentPane().add(scrollPane);
        
        // 유저 사진 -> 클릭 시 프로필로 이동
        JButton userImageBtn = new JButton();
        userImageBtn.setBackground(new Color(255, 255, 255));
        userImageBtn.setBounds(12, 10, 55, 55);
        userImageBtn.setContentAreaFilled(false); // 버튼 배경 투명
        userImageBtn.setBorderPainted(false);    // 버튼 테두리 제거
        userImageBtn.setFocusPainted(false);     // 버튼 포커스 표시 제거
        userImageBtn.setOpaque(false);           // 불투명 설정 해제
        postPanel.add(userImageBtn);
        // 유저 사진 로드 함수 호출
        loadUserImage(userImageBtn, userID);
        
        
        // 유저 이름
        nameField = new JTextField();
        nameField.setBounds(85, 22, 180, 23);
        nameField.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        nameField.setBorder(null);
        postPanel.add(nameField);
        nameField.setColumns(10);
        // 데이터베이스에서 user_name 가져오기
        String userName = loadUserName(userID); // 일단 설정
        if (userName != null) {
            nameField.setText(userName);
        } else {
            nameField.setText("Unknown User");
        }
        
        
        // 유저 아이디
        idField = new JTextField();
        idField.setText("@" + userID);
        idField.setBounds(85, 45, 180, 15);
        idField.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        idField.setBorder(null);
        postPanel.add(idField);
        idField.setColumns(10);
        
        
        // 포스트 메시지 입력
        messageArea = new JTextArea();
        messageArea.setBackground(new Color(239, 239, 239));
        messageArea.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        messageArea.setBounds(12, 80, 362, 180);
        postPanel.add(messageArea);
        
        
        // 포스트 URL 입력
        JLabel photoURLLabel = new JLabel(" Photo URL: ");
        photoURLLabel.setBounds(12, 263, 70, 21);
        photoURLLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        photoURLLabel.setBackground(new Color(239, 239, 239));
        photoURLLabel.setOpaque(true); // 배경색을 보이게 설정
        photoURLLabel.setBorder(null);
        photoURLLabel.setVisible(true);
        postPanel.add(photoURLLabel);
        
        photoURLField = new JTextField();
        photoURLField.setBounds(82, 263, 292, 21);
        photoURLField.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        photoURLField.setBackground(new Color(239, 239, 239));
        photoURLField.setBorder(null);
        postPanel.add(photoURLField);
        photoURLField.setColumns(10);
        
        
        // 저장 버튼
        JButton saveButton = new JButton("Create");
        saveButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        saveButton.setBounds(283, 294, 84, 26);
        saveButton.setForeground(Color.WHITE); // 글자 색상: 흰색
        saveButton.setBackground(new Color(106, 181, 249)); // 배경 색상: 파란색
        saveButton.setFocusPainted(false); // 클릭 시 포커스 효과 제거
        saveButton.setBorderPainted(false); // 기본 테두리 제거
        saveButton.setOpaque(false); // 불투명 효과 제거
        saveButton.setContentAreaFilled(false); // 버튼 배경 투명 처리
        postPanel.add(saveButton);
        
        // save 버튼 클릭 이벤트
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            	savePost();
            	
                // allPost 클래스의 프레임 호출
                SwingUtilities.invokeLater(() -> {
                    try {
                        allPost allPostWindow = new allPost();
                        allPostWindow.setVisible(true);
                        createPost.this.dispose(); // 현재 프레임 닫기 (필요 시 유지)
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
        
        // 둥근 버튼 모양 만들기
        saveButton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 버튼 배경 채우기
                g2.setColor(saveButton.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 30, 30); // 둥근 사각형: 30px radius

                super.paint(g, c);
            }
        });
        
        
        // Back 버튼
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        backButton.setBounds(283, 35, 84, 26);
        backButton.setForeground(Color.WHITE); // 글자 색상: 흰색
        backButton.setBackground(new Color(106, 181, 249)); // 배경 색상: 파란색
        backButton.setFocusPainted(false); // 클릭 시 포커스 효과 제거
        backButton.setBorderPainted(false); // 기본 테두리 제거
        backButton.setOpaque(false); // 불투명 효과 제거
        backButton.setContentAreaFilled(false); // 버튼 배경 투명 처리
        postPanel.add(backButton);
        
        // Back 버튼 클릭 이벤트
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                // allPost 클래스의 프레임 호출
                SwingUtilities.invokeLater(() -> {
                    try {
                        allPost allPostWindow = new allPost();
                        allPostWindow.setVisible(true);
                        createPost.this.dispose(); // 현재 프레임 닫기 (필요 시 유지)
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
        
        // 둥근 버튼 모양 만들기
        backButton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 버튼 배경 채우기
                g2.setColor(backButton.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 30, 30); // 둥근 사각형: 30px radius

                super.paint(g, c);
            }
        });
        
	}

	
	private void savePost() {
	    String message = messageArea.getText().trim();
	    String photoUrl = photoURLField.getText().trim();

	    if (message.isEmpty()) {
	        JOptionPane.showMessageDialog(createPost.this, "Message cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    PostModel postModel = new PostModel();
	    boolean success = postModel.savePost(userID, message, photoUrl);
	    if (success) {
	        JOptionPane.showMessageDialog(createPost.this, "Post saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
	    } else {
	        JOptionPane.showMessageDialog(createPost.this, "Failed to save post.", "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}

	
	private String loadUserName(String userId) {
	    String userName = null;

	    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
	        // SQL 쿼리: user_id로 user_name 가져오기
	        String query = "SELECT user_name FROM User WHERE user_id = ?";
	        PreparedStatement pstmt = conn.prepareStatement(query);
	        pstmt.setString(1, userId);

	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            userName = rs.getString("user_name");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return userName;
	}
	
	
	private void loadUserImage(JButton button, String userId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        	
            String query = "SELECT image_url FROM User WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId); // user_id 조건 설정

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String imageUrl = rs.getString("image_url");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // URL 대신 URI 사용
                    URI uri = new URI(imageUrl);
                    URL url = uri.toURL();
                    ImageIcon circularIcon = createCircularImageIcon(url, 55, 55);
                    button.setIcon(circularIcon); // 동그란 이미지 설정
                } else {
                    // 기본 이미지 설정
                    setDefaultUserImage(button);
                }
            } else {
                // 기본 이미지 설정 (유저 ID가 없을 때)
                setDefaultUserImage(button);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외가 발생한 경우에도 기본 이미지 설정
            setDefaultUserImage(button);
        }
    }
	
	
	private ImageIcon createCircularImageIcon(URL url, int width, int height) throws Exception {
		// URL에서 BufferedImage 로드
	    BufferedImage originalImage = ImageIO.read(url);

	    // 원본 이미지 크기 확인
	    int originalWidth = originalImage.getWidth();
	    int originalHeight = originalImage.getHeight();

	    // 정사각형으로 변환
	    int size = Math.min(originalWidth, originalHeight); // 최소 크기로 맞춤
	    BufferedImage squareImage = originalImage.getSubimage(
	        (originalWidth - size) / 2, 
	        (originalHeight - size) / 2, 
	        size, 
	        size
	    );

	    // 고품질로 크기 조정
	    BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = scaledImage.createGraphics();
	    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2d.drawImage(squareImage, 0, 0, width, height, null);
	    g2d.dispose();

	    // 원형으로 처리
	    BufferedImage circularImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = circularImage.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, width, height));
	    g2.drawImage(scaledImage, 0, 0, width, height, null);
	    g2.dispose();
	    
        return new ImageIcon(circularImage);
    }

    private void setDefaultUserImage(JButton button) {
        try {
        	
            // 기본 이미지 로드 (로컬 리소스)
            URL defaultImageUrl = getClass().getResource("/images/defaultUserImage.jpeg");
            
            if (defaultImageUrl != null) {
                ImageIcon defaultIcon = createCircularImageIcon(defaultImageUrl, 55, 55);
                button.setIcon(defaultIcon);
            } else {
                System.err.println("Default image not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
