package main;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class detailPost extends JFrame {

    // MySQL 연결 정보
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Twitter";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "ekdud0412?";

    private final JPanel postPanel = new JPanel();

    public detailPost(int postId) {
        initialize(postId);
    }

    private void initialize(int postId) {
        // JFrame 설정
        setTitle("Post Details");
        setBounds(100, 100, 400, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);

        // 포스트 패널
        postPanel.setBackground(new Color(255, 255, 255));
        postPanel.setLayout(null);

        // 스크롤 패널
        JScrollPane scrollPane = new JScrollPane(postPanel);
        scrollPane.setBounds(0, 0, 384, 562);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 스크롤 속도 조정
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤 비활성화
        add(scrollPane);

        // 데이터베이스에서 포스트 정보 가져오기
        String[] postDetails = loadPostDetails(postId);

        if (postDetails != null) {

            // 유저 사진
            JButton userImageButton = new JButton();
            userImageButton.setBounds(10, 10, 60, 60);
            userImageButton.setContentAreaFilled(false);
            userImageButton.setBorderPainted(false);
            userImageButton.setFocusPainted(false);
            userImageButton.setOpaque(false);
            postPanel.add(userImageButton);

            // 유저 사진 로드
            loadUserImage(userImageButton, postDetails[3]); // postDetails[3]에 유저 이미지 URL

            
            // 유저 이름
            JLabel userNameLabel = new JLabel(postDetails[0]);
            userNameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            userNameLabel.setBounds(85, 23, 300, 23);
            postPanel.add(userNameLabel);

            // 유저 아이디
            JLabel userIdLabel = new JLabel("@" + postDetails[4]);
            userIdLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            userIdLabel.setForeground(Color.GRAY);
            userIdLabel.setBounds(85, 45, 300, 15);
            postPanel.add(userIdLabel);
			
            

            // 메시지
            JTextArea messageArea = new JTextArea(postDetails[1]);
            messageArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            messageArea.setBackground(new Color(245, 245, 245));
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            messageArea.setEditable(false);
            messageArea.setBounds(10, 80, 350, 200);
            postPanel.add(messageArea);


            
            // 이미지 URL 확인 및 표시
            if (postDetails.length > 5 && postDetails[5] != null && !postDetails[5].isEmpty()) {
            	JLabel imageLabel = new JLabel();
                loadPostImage(imageLabel, postDetails[5]); // 이미지 로드

                // 이미지 위치 및 크기 동적 설정
                imageLabel.setBounds(10, 290, 350, 200); // 세로 크기는 200, 가로 크기는 조정된 값에 따라 자동
                postPanel.add(imageLabel);
                
                // 생성일
                JLabel createdAtLabel = new JLabel("Created at: " + postDetails[2]);
                createdAtLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
                createdAtLabel.setBackground(new Color(245, 245, 245));
                createdAtLabel.setBounds(10, 500, 350, 20);
                createdAtLabel.setBorder(null);
                createdAtLabel.setOpaque(true);
                postPanel.add(createdAtLabel);
                
                // 패널 크기 조정
                //postPanel.setPreferredSize(new Dimension(380, 700)); // 이미지 크기에 맞게 패널 크기 증가
            } else {
            	// 생성일
                JLabel createdAtLabel = new JLabel("Created at: " + postDetails[2]);
                createdAtLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
                createdAtLabel.setBackground(new Color(245, 245, 245));
                createdAtLabel.setBounds(10, 300, 350, 20);
                createdAtLabel.setBorder(null);
                createdAtLabel.setOpaque(true);
                postPanel.add(createdAtLabel);
                //postPanel.setPreferredSize(new Dimension(380, 430)); // 이미지가 없을 경우 기본 크기 유지
            }

            
            
            postPanel.setPreferredSize(new Dimension(380, 430)); // 패널 크기 설정
        } else {
            JLabel errorLabel = new JLabel("Failed to load post details.");
            errorLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            errorLabel.setForeground(Color.RED);
            errorLabel.setBounds(10, 10, 350, 30);
            postPanel.add(errorLabel);
        }
    }

    private String[] loadPostDetails(int postId) {
        String[] postDetails = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT u.user_name, p.message, p.created_at, u.image_url, u.user_id, p.photo_url FROM POSTS p " +
                    "JOIN USER u ON p.writer_id = u.user_id WHERE p.post_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, postId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                postDetails = new String[6];
                postDetails[0] = rs.getString("user_name");  // 사용자 이름
                postDetails[1] = rs.getString("message");    // 메시지
                postDetails[2] = rs.getTimestamp("created_at").toString(); // 생성일
                postDetails[3] = rs.getString("image_url");  // 사용자 이미지 URL
                postDetails[4] = rs.getString("user_id");    // 사용자 ID
                postDetails[5] = rs.getString("photo_url");  // 게시물 이미지 URL
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postDetails;
    }

    
    private void loadPostImage(JLabel label, String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // URI를 통해 URL 생성
                URI uri = new URI(imageUrl);
                URL url = uri.toURL(); // 안전한 URL 변환

                // 이미지 로드 및 크기 조정
                BufferedImage originalImage = ImageIO.read(url);
                int newHeight = 380; // 세로 크기 고정
                int newWidth = originalImage.getWidth() * newHeight / originalImage.getHeight(); // 가로 크기 비율 계산

                Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImage));
            } else {
                label.setText("No Image Available"); // 이미지 URL이 없을 경우
            }
        } catch (Exception e) {
            e.printStackTrace();
            label.setText("Failed to load image"); // 오류 발생 시 메시지
        }
    }

    
    
    private void loadUserImage(JButton button, String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                URI uri = new URI(imageUrl);
                URL url = uri.toURL();
                ImageIcon circularIcon = createCircularImageIcon(url, 60, 60);
                button.setIcon(circularIcon);
            } else {
                setDefaultUserImage(button);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setDefaultUserImage(button);
        }
    }

    private ImageIcon createCircularImageIcon(URL url, int width, int height) throws Exception {
        BufferedImage originalImage = ImageIO.read(url);
        int size = Math.min(originalImage.getWidth(), originalImage.getHeight());
        BufferedImage squareImage = originalImage.getSubimage(
                (originalImage.getWidth() - size) / 2,
                (originalImage.getHeight() - size) / 2,
                size, size);

        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(squareImage, 0, 0, width, height, null);
        g2d.dispose();

        BufferedImage circularImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circularImage.createGraphics();
        g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, width, height));
        g2.drawImage(scaledImage, 0, 0, width, height, null);
        g2.dispose();

        return new ImageIcon(circularImage);
    }

    private void setDefaultUserImage(JButton button) {
        try {
            URL defaultImageUrl = getClass().getResource("/images/defaultUserImage.jpeg");
            if (defaultImageUrl != null) {
                ImageIcon defaultIcon = createCircularImageIcon(defaultImageUrl, 60, 60);
                button.setIcon(defaultIcon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
