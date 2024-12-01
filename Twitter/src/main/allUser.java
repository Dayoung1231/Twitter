package main;

import java.sql.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import javax.swing.*;

import model.BookmarkModel;
import model.CommentModel;
import model.FollowModel;
import model.LikeModel;
import model.RetweetModel;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URI;

public class allUser extends JFrame {

	// MySQL 연결 정보
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Twitter";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "ekdud0412?";
    
    private String currentUser;
	
	public allUser(String userId) {
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
	    allUser.this.getContentPane().add(topPanel);


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
	    JLabel textLabel = new JLabel("User List");
	    textLabel.setFont(new Font("Arial", Font.BOLD, 20));
	    textLabel.setForeground(Color.WHITE);
	    textLabel.setBounds(60, 15, 100, 30);
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

                // Following 클래스의 프레임 호출
                SwingUtilities.invokeLater(() -> {
                    try {
                        Following followingWindow = new Following(currentUser);
                        followingWindow.setVisible(true);
                        allUser.this.dispose(); // 현재 프레임 닫기 (필요 시 유지)
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
        
        
	    // 모든 유저를 담을 컨테이너 패널
	    JPanel containerPanel = new JPanel();
	    containerPanel.setBackground(new Color(255, 255, 255));
	    containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS)); // BoxLayout으로 설정하여 자동 크기 조정containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS)); // BoxLayout으로 설정하여 자동 크기 조정
	    containerPanel.setPreferredSize(new Dimension(380, 250)); // 초기 크기 설정

	    // 스크롤 패널
	    JScrollPane scrollPane = new JScrollPane(containerPanel);
	    scrollPane.setBounds(0, 60, 390, 503);
	    scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 스크롤 속도 조정
	    // 세로 스크롤바 스타일 적용
	    scrollPane.getVerticalScrollBar().setUI(new ScrollBarUI());
	    scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); // 너비를 8로 설정 (이미지처럼 얇게)
	    // 가로 스크롤바 숨기기 (필요 시 추가)
	    scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
	    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤 비활성화
	    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); // 세로 스크롤 활성화
	    allUser.this.getContentPane().add(scrollPane);

	    // 모든 유저 가져오기
	    java.util.List<String[]> allUser = loadAllUsers(currentUser);

	    // 각 following 정보를 표시할 패널 생성 및 추가
	    int yOffset = 10; // 첫 following 간격
	    int panelHeight = 70; // 각 패널의 높이
	    for (String[] user : allUser) {
	        JPanel userPanel = new JPanel();
	        userPanel.setLayout(null);
	        userPanel.setBounds(10, yOffset, 360, panelHeight); // 패널 위치와 크기 설정
	        userPanel.setBackground(new Color(255, 255, 255)); // 패널 배경 색상
	        userPanel.setBorder(BorderFactory.createLineBorder(new Color(235, 235, 235), 2)); // 회색 테두리, 두께 2


	        // 유저 사진 표시
	        JButton userImageButton = new JButton();
	        userImageButton.setBounds(10, 10, 50, 50);
	        userImageButton.setContentAreaFilled(false);
	        userImageButton.setBorderPainted(false);
	        userImageButton.setFocusPainted(false);
	        userImageButton.setOpaque(false);
	        userPanel.add(userImageButton);

	        // 유저 사진 로드
	        if (user[2] != null && !user[2].isEmpty()) {
	            try {
	                URI uri = new URI(user[2]);
	                URL url = uri.toURL();
	                ImageIcon circularIcon = createCircularImageIcon(url, 50); // 지름 50px로 생성
	                userImageButton.setIcon(circularIcon);
	            } catch (Exception e) {
	                e.printStackTrace();
	                userImageButton.setIcon(getDefaultUserImageIcon(50)); // 기본 이미지
	            }
	        } else {
	            userImageButton.setIcon(getDefaultUserImageIcon(50)); // 기본 이미지
	        }

	        // userImage 버튼 클릭 이벤트
	        userImageButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {

	                // otherUserProfile 클래스의 프레임 호출
	                SwingUtilities.invokeLater(() -> {
	                    try {
	                    	otherUserProfile profileWindow = new otherUserProfile(currentUser, user[1]);
		                    profileWindow.setVisible(true);
		                    allUser.this.dispose();
	                    } catch (Exception ex) {
	                        ex.printStackTrace();
	                    }
	                });
	            }
	        });
	        
	        // 유저 이름과 아이디 표시 (HTML로 스타일 적용)
	        JLabel nameLabel = new JLabel();
	        nameLabel.setText("<html><span style='font-size:12px;'>" + user[0] + "</span> " +
	                          "<span style='font-size:10px; color:gray;'>@" + user[1] + "</span></html>");
	        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12)); // 기본 폰트 설정 (HTML 내부 스타일로 크기 조정 가능)
	        nameLabel.setBounds(70, 13, 280, 23); // 크기와 위치 설정
	        userPanel.add(nameLabel);

	        
	        // profile
	        JButton profileBtn = new JButton("Profile");
	        profileBtn.setBounds(295, 13, 55, 23); // 버튼 위치 및 크기 설정
	        profileBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
	        profileBtn.setContentAreaFilled(false); // 배경 제거
	        profileBtn.setBorder(BorderFactory.createLineBorder((new Color(245, 245, 245)), 2)); // 버튼 테두리
	        profileBtn.setOpaque(false); // 불투명도 제거
	        profileBtn.setFocusPainted(false); 
	        userPanel.add(profileBtn);


	        // profile 버튼 클릭 이벤트
	        profileBtn.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {

	                // otherUserProfile 클래스의 프레임 호출
	                SwingUtilities.invokeLater(() -> {
	                    try {
	                        otherUserProfile profileWindow = new otherUserProfile(currentUser, user[1]);
	                        profileWindow.setVisible(true);
	                        allUser.this.dispose(); // 현재 프레임 닫기 (필요 시 유지)
	                    } catch (Exception ex) {
	                        ex.printStackTrace();
	                    }
	                });
	            }
	        });
	        
	        
	        // follow 버튼 생성 및 초기화
	        JButton followBtn = new JButton("Follow");
	        followBtn.setBounds(230, 13, 55, 23); // 버튼 위치 및 크기 설정
	        followBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
	        followBtn.setContentAreaFilled(true); // 배경 활성화
	        followBtn.setOpaque(true); // 불투명 활성화
	        followBtn.setBorder(BorderFactory.createLineBorder(new Color(245, 245, 245), 2)); // 버튼 테두리
	        followBtn.setFocusPainted(false); 
	        userPanel.add(followBtn);

	        // FollowModel 인스턴스 생성
	        FollowModel followModel = new FollowModel();

	        // boolean 배열로 선언 (final 변수처럼 사용 가능)
	        final boolean[] isFollowing = { followModel.isFollowing(currentUser, user[1]) };

	        // 팔로우 여부에 따른 버튼 설정
	        if (isFollowing[0]) {
	            followBtn.setText("Following");
	            followBtn.setBackground(Color.WHITE);
	            followBtn.setForeground(new Color(50, 50, 50));
	        } else {
	            followBtn.setText("Follow");
	            followBtn.setBackground(new Color(106, 181, 249)); // 파란 글자
	            followBtn.setForeground(Color.WHITE);
	        }

	        // follow 버튼 클릭 이벤트
	        followBtn.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                if (isFollowing[0]) {
	                    // 언팔로우 처리
	                    boolean success = followModel.unfollowUser(currentUser, user[1]);
	                    if (success) {
	                        followBtn.setText("Follow");
	                        followBtn.setBackground(new Color(106, 181, 249)); // 파란색
	                        followBtn.setForeground(Color.WHITE);
	                        isFollowing[0] = false; // 상태 업데이트
	                        JOptionPane.showMessageDialog(null, "Unfollowed " + user[0] + " successfully!");
	                    } else {
	                        JOptionPane.showMessageDialog(null, "Failed to unfollow " + user[0] + ".");
	                    }
	                } else {
	                    // 팔로우 처리
	                    boolean success = followModel.followUser(currentUser, user[1]);
	                    if (success) {
	                        followBtn.setText("Following");
	                        followBtn.setBackground(Color.WHITE);
	                        followBtn.setForeground(new Color(50, 50, 50));
	                        isFollowing[0] = true; // 상태 업데이트
	                        JOptionPane.showMessageDialog(null, "Successfully followed " + user[0] + "!");
	                    } else {
	                        JOptionPane.showMessageDialog(null, "You are already following this user.");
	                    }
	                }
	            }
	        });




	        // followingPanel을 containerPanel에 추가
	        containerPanel.add(userPanel);

	        // 다음 패널의 Y축 위치 계산
	        yOffset += panelHeight + 10;
	    }

	    // containerPanel의 크기를 모든 포스트 수에 맞게 조정
	    containerPanel.setPreferredSize(new Dimension(380, yOffset));
	    
	    // 스크롤바 위치 초기화 (맨 위로 설정)
	    SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
	}

	
	private java.util.List<String[]> loadAllUsers(String currentUser) {
	    java.util.List<String[]> allUsers = new ArrayList<>();

	    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
	        // 현재 유저를 제외한 모든 유저를 가져오는 쿼리
	        String query = "SELECT u.user_name, u.user_id, u.image_url " +
	                       "FROM USER u " +
	                       "WHERE u.user_id != ? " +
	                       "ORDER BY u.user_id ASC";

	        PreparedStatement pstmt = conn.prepareStatement(query);
	        pstmt.setString(1, currentUser);

	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            String[] userDetails = new String[3]; // 배열 크기를 3으로 설정
	            userDetails[0] = rs.getString("user_name"); // 유저 이름
	            userDetails[1] = rs.getString("user_id");   // 유저 아이디
	            userDetails[2] = rs.getString("image_url"); // 프로필 이미지 URL
	            allUsers.add(userDetails);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return allUsers;
	}






	private ImageIcon getDefaultUserImageIcon(int diameter) {
	    try {
	        URL defaultImageUrl = getClass().getResource("/images/defaultUserImage.jpeg");
	        if (defaultImageUrl != null) {
	            return createCircularImageIcon(defaultImageUrl, diameter);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}


	private ImageIcon createCircularImageIcon(URL url, int diameter) throws Exception {
	    // URL에서 BufferedImage 로드
	    BufferedImage originalImage = ImageIO.read(url);

	    // 원본 이미지 크기 확인
	    int originalWidth = originalImage.getWidth();
	    int originalHeight = originalImage.getHeight();

	    // 정사각형으로 변환
	    int size = Math.min(originalWidth, originalHeight); // 최소 크기로 정사각형 자르기
	    BufferedImage squareImage = originalImage.getSubimage(
	        (originalWidth - size) / 2, 
	        (originalHeight - size) / 2, 
	        size, 
	        size
	    );

	    // 고품질로 크기 조정
	    BufferedImage resizedImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = resizedImage.createGraphics();
	    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2d.drawImage(squareImage, 0, 0, diameter, diameter, null);
	    g2d.dispose();

	    // 원형 마스크 적용
	    BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = circularImage.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    // 원형 클리핑
	    g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, diameter, diameter));
	    g2.drawImage(resizedImage, 0, 0, diameter, diameter, null);
	    g2.dispose();

	    return new ImageIcon(circularImage);
	}

}
