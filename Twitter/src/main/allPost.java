package main;

import java.sql.Connection;
import java.util.ArrayList;
import java.sql.*;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.*;

public class allPost {

	private JFrame frame;

	// MySQL 연결 정보
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Twitter";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "ekdud0412?";
    

    
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					allPost window = new allPost();
					window.setVisible(true);
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


	public allPost() {
		initialize();
	}


	private void initialize() {
		
		// 현재 로그인된 유저
		String currentUser = "user1"; // 실제 로그인 로직에 따라 변경 필요
		
	    frame = new JFrame();
	    frame.setBounds(100, 100, 400, 600);
	    frame.setLocationRelativeTo(null);
	    frame.setResizable(false);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().setBackground(Color.WHITE);
	    frame.getContentPane().setLayout(null);

	    
	    // 상단 패널 생성
	    JPanel topPanel = new JPanel();
	    topPanel.setBackground(new Color(106, 181, 249)); // 트위터와 비슷한 파란색
	    topPanel.setBounds(0, 0, 390, 60);
	    topPanel.setLayout(null);
	    frame.getContentPane().add(topPanel);


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
	    JLabel textLabel = new JLabel("Tweet");
	    textLabel.setFont(new Font("Arial", Font.BOLD, 20));
	    textLabel.setForeground(Color.WHITE);
	    textLabel.setBounds(60, 15, 99, 30);
	    topPanel.add(textLabel);
	    
	    
	    // Create 버튼
        JButton createButton = new JButton("create");
        createButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        createButton.setBounds(290, 20, 80, 26);
        createButton.setForeground(new Color(106, 181, 249)); // 글자 색상: 흰색
        createButton.setBackground(Color.WHITE); // 배경 색상: 파란색
        createButton.setFocusPainted(false); // 클릭 시 포커스 효과 제거
        createButton.setBorderPainted(false); // 기본 테두리 제거
        createButton.setOpaque(false); // 불투명 효과 제거
        createButton.setContentAreaFilled(false); // 버튼 배경 투명 처리
        topPanel.add(createButton);
        
        // Create 버튼 클릭 이벤트
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // createPost 클래스의 프레임 호출
                SwingUtilities.invokeLater(() -> {
                    try {
                        createPost createPostWindow = new createPost();
                        createPostWindow.setVisible(true);
                        frame.dispose(); // 현재 프레임 닫기 (필요 시 유지)
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
        
        // 둥근 버튼 모양 만들기
        createButton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 버튼 배경 채우기
                g2.setColor(createButton.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 30, 30); // 둥근 사각형: 30px radius

                super.paint(g, c);
            }
        });
	   
	    
	    // 하단 패널 생성
	    JPanel bottomPanel = new JPanel();
	    bottomPanel.setBackground(new Color(106, 181, 249));
	    bottomPanel.setBounds(0, 519, 390, 44); // 하단에 고정
	    bottomPanel.setLayout(new GridLayout(1, 3)); // 버튼을 균등하게 배치
	    frame.getContentPane().add(bottomPanel);

	    // 프로필 버튼
	    JButton profileButton = new JButton("Profile");
	    profileButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
	    profileButton.setForeground(Color.WHITE);
	    profileButton.setFocusPainted(false);
	    profileButton.setBackground(new Color(106, 181, 249)); // 밝은 파란색
	    bottomPanel.add(profileButton);

	    // 홈 버튼
	    JButton homeButton = new JButton("Home");
	    homeButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
	    homeButton.setForeground(Color.WHITE);
	    homeButton.setFocusPainted(false);
	    homeButton.setBackground(new Color(106, 181, 249));
	    bottomPanel.add(homeButton);

	    // DM 버튼
	    JButton dmButton = new JButton("DM");
	    dmButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
	    dmButton.setForeground(Color.WHITE);
	    dmButton.setFocusPainted(false);
	    dmButton.setBackground(new Color(106, 181, 249));
	    bottomPanel.add(dmButton);

	    
	    
	    // 모든 포스트를 담을 컨테이너 패널
	    JPanel containerPanel = new JPanel();
	    containerPanel.setBackground(new Color(255, 255, 255));
	    //containerPanel.setLayout(null); // 자유 배치
	    containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS)); // BoxLayout으로 설정하여 자동 크기 조정containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS)); // BoxLayout으로 설정하여 자동 크기 조정
	    containerPanel.setPreferredSize(new Dimension(380, 250)); // 초기 크기 설정

	    // 스크롤 패널
	    JScrollPane scrollPane = new JScrollPane(containerPanel);
	    scrollPane.setBounds(0, 60, 386, 460);
	    scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 스크롤 속도 조정
	    // 세로 스크롤바 스타일 적용
	    scrollPane.getVerticalScrollBar().setUI(new ScrollBarUI());
	    scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); // 너비를 8로 설정 (이미지처럼 얇게)
	    // 가로 스크롤바 숨기기 (필요 시 추가)
	    scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
	    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤 비활성화
	    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); // 세로 스크롤 활성화
	    frame.getContentPane().add(scrollPane);

	    // 모든 포스트 가져오기
	    java.util.List<String[]> allPosts = loadAllPostsWithImages();

	    // 각 포스트 정보를 표시할 패널 생성 및 추가
	    int yOffset = 10; // 첫 포스트 간격
	    int panelHeight = 150; // 각 패널의 높이
	    for (String[] post : allPosts) {
	        JPanel postPanel = new JPanel();
	        postPanel.setLayout(null);
	        postPanel.setBounds(10, yOffset, 360, panelHeight); // 패널 위치와 크기 설정
	        postPanel.setBackground(new Color(255, 255, 255)); // 패널 배경 색상
	        postPanel.setBorder(BorderFactory.createLineBorder(new Color(235, 235, 235), 2)); // 회색 테두리, 두께 2


	        // 유저 사진 표시
	        JButton userImageBtn = new JButton();
	        userImageBtn.setBounds(10, 10, 50, 50);
	        userImageBtn.setContentAreaFilled(false);
	        userImageBtn.setBorderPainted(false);
	        userImageBtn.setFocusPainted(false);
	        userImageBtn.setOpaque(false);
	        postPanel.add(userImageBtn);

	        // 유저 사진 로드
	        if (post[3] != null && !post[3].isEmpty()) {
	            try {
	                URI uri = new URI(post[3]);
	                URL url = uri.toURL();
	                ImageIcon circularIcon = createCircularImageIcon(url, 50); // 지름 50px로 생성
	                userImageBtn.setIcon(circularIcon);
	            } catch (Exception e) {
	                e.printStackTrace();
	                userImageBtn.setIcon(getDefaultUserImageIcon(50)); // 기본 이미지
	            }
	        } else {
	            userImageBtn.setIcon(getDefaultUserImageIcon(50)); // 기본 이미지
	        }

	        
	        // 유저 이름과 아이디 표시 (HTML로 스타일 적용)
	        JLabel nameLabel = new JLabel();
	        nameLabel.setText("<html><span style='font-size:12px;'>" + post[0] + "</span> " +
	                          "<span style='font-size:10px; color:gray;'>@" + post[4] + "</span></html>");
	        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12)); // 기본 폰트 설정 (HTML 내부 스타일로 크기 조정 가능)
	        nameLabel.setBounds(70, 13, 280, 23); // 크기와 위치 설정
	        postPanel.add(nameLabel);

	        
	        // 포스트 메시지 표시
	        JTextArea messageArea = new JTextArea();
	        messageArea.setText(post[1]); // 메시지 내용
	        messageArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
	        messageArea.setBackground(new Color(245, 245, 245));
	        messageArea.setLineWrap(true);
	        messageArea.setWrapStyleWord(true);
	        messageArea.setEditable(false);
	        messageArea.setBounds(70, 40, 280, 60);
	        postPanel.add(messageArea);

	        // 생성일 표시
	        JTextField createdAtField = new JTextField();
	        createdAtField.setText("Created at: " + post[2]); // 생성일
	        createdAtField.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
	        createdAtField.setBackground(new Color(245, 245, 245));
	        createdAtField.setBorder(null);
	        createdAtField.setEditable(false);
	        createdAtField.setBounds(70, 104, 280, 20);
	        postPanel.add(createdAtField);
	        
	        
	        // 댓글 버튼
	        JButton commentButton = new JButton();
	        commentButton.setBounds(70, 130, 20, 20); // 크기를 아이콘에 맞게 조정
	        commentButton.setContentAreaFilled(false); // 배경 제거
	        commentButton.setFocusPainted(false); // 포커스 테두리 제거
	        commentButton.setBorderPainted(false); // 버튼 테두리 제거
	        commentButton.setOpaque(false); // 불투명도 제거
	        postPanel.add(commentButton);

	        JLabel commentLabel = new JLabel(post[7]); // 댓글 수 표시
	        commentLabel.setBounds(100, 130, 50, 20);
	        commentLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
	        commentLabel.setBackground(new Color(245, 245, 245));
	        postPanel.add(commentLabel);
	        
	        // 댓글 아이콘 설정
	        try {
	            URL commentIconUrl = getClass().getResource("/images/Comment.png");
	            if (commentIconUrl != null) {
	                ImageIcon commentIcon = new ImageIcon(commentIconUrl);
	                Image scaledCommentIcon = commentIcon.getImage().getScaledInstance(19, 19, Image.SCALE_SMOOTH);
	                commentButton.setIcon(new ImageIcon(scaledCommentIcon));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        commentButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                // 댓글 입력 창 생성
	                JFrame commentFrame = new JFrame("Comment");
	                commentFrame.setBounds(100, 100, 300, 200);
	                commentFrame.setLocationRelativeTo(null);
	                commentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	                commentFrame.setLayout(new FlowLayout());

	                // 입력 필드 생성
	                JTextField commentField = new JTextField(20);
	                JLabel messageLabel = new JLabel("Comment:");
	                JButton submitButton = new JButton("save");

	                commentFrame.add(messageLabel);
	                commentFrame.add(commentField);
	                commentFrame.add(submitButton);

	                commentFrame.setVisible(true);

	                // 댓글 작성 버튼 클릭 이벤트
	                submitButton.addActionListener(new ActionListener() {
	                    @Override
	                    public void actionPerformed(ActionEvent ev) {
	                        String comment = commentField.getText();
	                        if (comment.isEmpty()) {
	                            JOptionPane.showMessageDialog(commentFrame, "Enter the comment!", "경고", JOptionPane.WARNING_MESSAGE);
	                            return;
	                        }

	                        // 댓글 저장
	                        CommentModel commentModel = new CommentModel();
	                        boolean success = commentModel.addComment(currentUser, Integer.parseInt(post[5]), comment, null); // userId: post[4], postId: post[5]

	                        if (success) {
	                        	int newComment = Integer.parseInt(commentLabel.getText()) + 1; // 댓글 수 증가
	    	                    commentLabel.setText(String.valueOf(newComment)); // UI 업데이트
	                            JOptionPane.showMessageDialog(commentFrame, "saved!", "성공", JOptionPane.INFORMATION_MESSAGE);
	                            commentFrame.dispose();
	                        } else {
	                            JOptionPane.showMessageDialog(commentFrame, "failed.", "실패", JOptionPane.ERROR_MESSAGE);
	                        }
	                    }
	                });
	            }
	        });

	        
	        // 좋아요 버튼
	        JButton likeButton = new JButton();
	        likeButton.setBounds(140, 130, 20, 20); // 크기를 아이콘에 맞게 조정
	        likeButton.setFont(new Font("맑은 고딕", Font.PLAIN, 8));
	        likeButton.setContentAreaFilled(false); // 버튼 배경 제거
	        likeButton.setFocusPainted(false); // 포커스 테두리 제거
	        likeButton.setBorderPainted(false); // 버튼 테두리 제거
	        likeButton.setOpaque(false); // 버튼 불투명도 제거
	        postPanel.add(likeButton);
	        
	        JLabel likeLabel = new JLabel(post[6]); // 좋아요 수 표시
	        likeLabel.setBounds(170, 130, 50, 20);
	        likeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
	        likeLabel.setBackground(new Color(245, 245, 245));
	        postPanel.add(likeLabel);

	        // 좋아요 아이콘 설정
	        try {
	            URL likeIconUrl = getClass().getResource("/images/Likes.png");
	            if (likeIconUrl != null) {
	                ImageIcon likeIcon = new ImageIcon(likeIconUrl);
	                Image scaledIcon = likeIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH);
	                likeButton.setIcon(new ImageIcon(scaledIcon));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }


	        likeButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                LikeModel likeModel = new LikeModel();
	                boolean success = likeModel.likePost(currentUser, Integer.parseInt(post[5])); // currentUser: user_id, post[5]: post_id

	                if (success) {
	                    int newLikes = Integer.parseInt(likeLabel.getText()) + 1; // 좋아요 수 증가
	                    likeLabel.setText(String.valueOf(newLikes)); // UI 업데이트
	                    JOptionPane.showMessageDialog(frame, "Successfully liked!", "알림", JOptionPane.INFORMATION_MESSAGE);
	                } else {
	                    JOptionPane.showMessageDialog(frame, "Already liked.", "알림", JOptionPane.WARNING_MESSAGE);
	                }
	            }
	        });


	        // 리트윗 버튼
	        JButton retweetButton = new JButton();
	        retweetButton.setBounds(220, 130, 20, 20); // 크기를 아이콘에 맞게 조정
	        retweetButton.setContentAreaFilled(false); // 배경 제거
	        retweetButton.setFocusPainted(false); // 포커스 테두리 제거
	        retweetButton.setBorderPainted(false); // 버튼 테두리 제거
	        retweetButton.setOpaque(false); // 불투명도 제거
	        postPanel.add(retweetButton);

	        JLabel retweetLabel = new JLabel(post[8]); // 리트윗 수 표시
	        retweetLabel.setBounds(250, 130, 50, 20);
	        retweetLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
	        retweetLabel.setBackground(new Color(245, 245, 245));
	        postPanel.add(retweetLabel);

	        
	        // 리트윗 아이콘 설정
	        try {
	            URL retweetIconUrl = getClass().getResource("/images/Retweet.png");
	            if (retweetIconUrl != null) {
	                ImageIcon retweetIcon = new ImageIcon(retweetIconUrl);
	                Image scaledRetweetIcon = retweetIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
	                retweetButton.setIcon(new ImageIcon(scaledRetweetIcon));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        // 리트윗 버튼 클릭 이벤트
	        retweetButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                RetweetModel retweetModel = new RetweetModel();
	                boolean success = retweetModel.addRetweet(currentUser, Integer.parseInt(post[5])); // currentUser: user_id, post[5]: post_id

	                if (success) {
	                	int newRetweet = Integer.parseInt(retweetLabel.getText()) + 1; // 리트윗 수 증가
	                    retweetLabel.setText(String.valueOf(newRetweet)); // UI 업데이트
	                    JOptionPane.showMessageDialog(frame, "Successfully retweet!", "알림", JOptionPane.INFORMATION_MESSAGE);
	                } else {
	                    JOptionPane.showMessageDialog(frame, "Already retweet.", "알림", JOptionPane.WARNING_MESSAGE);
	                }
	            }
	        });

	        // 북마크 버튼
	        JButton bookmarkButton = new JButton();
	        bookmarkButton.setBounds(285, 130, 20, 20); // 크기를 아이콘에 맞게 조정
	        bookmarkButton.setContentAreaFilled(false); // 배경 제거
	        bookmarkButton.setFocusPainted(false); // 포커스 테두리 제거
	        bookmarkButton.setBorderPainted(false); // 버튼 테두리 제거
	        bookmarkButton.setOpaque(false); // 불투명도 제거
	        postPanel.add(bookmarkButton);
	        
	        JLabel bookmarkLabel = new JLabel(post[9]); // 리트윗 수 표시
	        bookmarkLabel.setBounds(315, 130, 50, 20);
	        bookmarkLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
	        bookmarkLabel.setBackground(new Color(245, 245, 245));
	        postPanel.add(bookmarkLabel);

	        // 북마크 아이콘 설정
	        try {
	            URL bookmarkIconUrl = getClass().getResource("/images/Bookmark.png");
	            if (bookmarkIconUrl != null) {
	                ImageIcon bookmarkIcon = new ImageIcon(bookmarkIconUrl);
	                Image scaledBookmarkIcon = bookmarkIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
	                bookmarkButton.setIcon(new ImageIcon(scaledBookmarkIcon));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        // 북마크 버튼 클릭 이벤트
	        bookmarkButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                BookmarkModel bookmarkModel = new BookmarkModel();
	                boolean success = bookmarkModel.addBookmark(currentUser, Integer.parseInt(post[5])); // currentUser: user_id, post[5]: post_id

	                if (success) {
	                	int newBookmark = Integer.parseInt(bookmarkLabel.getText()) + 1; // 북마크 수 증가
	                    bookmarkLabel.setText(String.valueOf(newBookmark)); // UI 업데이트
	                    JOptionPane.showMessageDialog(frame, "Successfully bookmarked", "알림", JOptionPane.INFORMATION_MESSAGE);
	                } else {
	                    JOptionPane.showMessageDialog(frame, "Already bookmarked.", "알림", JOptionPane.WARNING_MESSAGE);
	                }
	            }
	        });
	        
	        
	        // Detail
	        JButton detailButton = new JButton("details");
	        detailButton.setBounds(295, 13, 55, 23); // 버튼 위치 및 크기 설정
	        detailButton.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
	        detailButton.setContentAreaFilled(false); // 배경 제거
	        //detailButton.setFocusPainted(false); // 포커스 테두리 제거
	        detailButton.setBorder(BorderFactory.createLineBorder((new Color(245, 245, 245)), 2)); // 버튼 테두리
	        detailButton.setOpaque(false); // 불투명도 제거
	        postPanel.add(detailButton);

	        // Detail 버튼 클릭 이벤트
	        detailButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                // 현재 포스트의 ID를 가져와 Detail 화면에 전달
	                int postId = Integer.parseInt(post[5]); // post[5]에 현재 포스트의 ID가 저장되어 있음

	                // detailPost 클래스의 프레임 호출
	                SwingUtilities.invokeLater(() -> {
	                    try {
	                        detailPost detailWindow = new detailPost(postId); // postId를 전달
	                        detailWindow.setVisible(true);
	                        frame.dispose(); // 현재 프레임 닫기 (필요 시 유지)
	                    } catch (Exception ex) {
	                        ex.printStackTrace();
	                    }
	                });
	            }
	        });

	        

	        // postPanel을 containerPanel에 추가
	        containerPanel.add(postPanel);

	        // 다음 패널의 Y축 위치 계산
	        yOffset += panelHeight + 10;
	    }

	    // containerPanel의 크기를 모든 포스트 수에 맞게 조정
	    containerPanel.setPreferredSize(new Dimension(380, yOffset));
	    
	    // 스크롤바 위치 초기화 (맨 위로 설정)
	    SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
	}

	private java.util.List<String[]> loadAllPostsWithImages() {
	    java.util.List<String[]> posts = new ArrayList<>();

	    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
	        // 모든 포스트와 유저 이미지를 가져오는 SQL 쿼리
	        String query = "SELECT u.user_name, p.message, p.created_at, u.image_url, u.user_id, p.post_id, p.num_of_likes, " +
	                "(SELECT COUNT(*) FROM COMMENT WHERE post_id = p.post_id) AS comment_count, " +
	                "(SELECT COUNT(*) FROM RETWEET WHERE post_id = p.post_id) AS retweet_count, " +
	                "(SELECT COUNT(*) FROM BOOKMARK WHERE post_id = p.post_id) AS bookmark_count " +
	                "FROM POSTS p JOIN USER u ON p.writer_id = u.user_id " +
	                "ORDER BY p.created_at ASC";
	        PreparedStatement pstmt = conn.prepareStatement(query);

	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            String[] postDetails = new String[10]; // 배열 크기를 10으로 확장
	            postDetails[0] = rs.getString("user_name"); // 유저 이름
	            postDetails[1] = rs.getString("message");   // 메시지
	            postDetails[2] = rs.getTimestamp("created_at").toString(); // 생성일
	            postDetails[3] = rs.getString("image_url"); // 유저 이미지 URL
	            postDetails[4] = rs.getString("user_id");   // 유저 아이디
	            postDetails[5] = rs.getString("post_id");   // postId 추가
	            postDetails[6] = rs.getString("num_of_likes"); // 좋아요 수
	            postDetails[7] = String.valueOf(rs.getInt("comment_count")); // 댓글 수
	            postDetails[8] = String.valueOf(rs.getInt("retweet_count")); // 리트윗 수
	            postDetails[9] = String.valueOf(rs.getInt("bookmark_count")); // 북마크 수
	            posts.add(postDetails);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return posts;
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
