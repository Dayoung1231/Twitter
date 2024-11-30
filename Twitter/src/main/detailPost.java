package main;

import javax.swing.*;

import model.BookmarkModel;
import model.CommentModel;
import model.LikeModel;
import model.RetweetModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class detailPost extends JFrame {

    // MySQL 연결 정보
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Twitter";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "ekdud0412?";

    private final JPanel postPanel = new JPanel();
    
    private String userID;
    private int postPosition; // 포스트 위치가 어디까지인지

    public detailPost(int postId, String currentUser) {
    	this.userID = currentUser;
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
	    scrollPane.setBounds(0, 0, 386, 563);
	    scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 스크롤 속도 조정
	    // 세로 스크롤바 스타일 적용
	    scrollPane.getVerticalScrollBar().setUI(new ScrollBarUI());
	    scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); // 너비를 8로 설정 (이미지처럼 얇게)
	    // 가로 스크롤바 숨기기 (필요 시 추가)
	    scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
	    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤 비활성화
	    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); // 세로 스크롤 활성화
	    detailPost.this.getContentPane().add(scrollPane);
	    
	    
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
            messageArea.setBounds(10, 80, 360, 100);
            postPanel.add(messageArea);


            
            // 이미지 URL 확인 및 표시
            int createdAtYPosition;
            if (postDetails.length > 5 && postDetails[5] != null && !postDetails[5].isEmpty()) {
            	JLabel imageLabel = new JLabel();
                loadPostImage(imageLabel, postDetails[5]); // 이미지 로드

                // 이미지 위치 및 크기 동적 설정
                imageLabel.setBounds(10, 180, 360, 200); // 세로 크기는 200, 가로 크기는 비율에 따라 자동
                postPanel.add(imageLabel);
                
                // 생성일 위치 설정
                createdAtYPosition = 385; // 이미지 아래에 생성일 표시
            } else {
                // 생성일 위치 설정
                createdAtYPosition = 185; // 메시지 바로 아래 생성일 표시
            }

            // 생성일 추가
            JLabel createdAtLabel = createCreatedAtLabel(postDetails[2], createdAtYPosition);
            postPanel.add(createdAtLabel);
            
            
            // 댓글 버튼
	        JButton commentButton = new JButton();
	        commentButton.setBounds(50, createdAtYPosition + 25, 20, 20); // 크기를 아이콘에 맞게 조정
	        commentButton.setContentAreaFilled(false); // 배경 제거
	        commentButton.setFocusPainted(false); // 포커스 테두리 제거
	        commentButton.setBorderPainted(false); // 버튼 테두리 제거
	        commentButton.setOpaque(false); // 불투명도 제거
	        postPanel.add(commentButton);

	        JLabel commentLabel = new JLabel(String.valueOf(postDetails[7]));
            commentLabel.setBounds(80, createdAtYPosition + 25, 50, 20);
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
	                commentFrame.getContentPane().setLayout(new FlowLayout());

	                // 입력 필드 생성
	                JTextField commentField = new JTextField(20);
	                JLabel messageLabel = new JLabel("Comment:");
	                JButton submitButton = new JButton("save");

	                commentFrame.getContentPane().add(messageLabel);
	                commentFrame.getContentPane().add(commentField);
	                commentFrame.getContentPane().add(submitButton);

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
	                        boolean success = commentModel.addComment(userID, postId, comment, null);

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
	        likeButton.setBounds(120, createdAtYPosition + 25, 20, 20); // 크기를 아이콘에 맞게 조정
	        likeButton.setFont(new Font("맑은 고딕", Font.PLAIN, 8));
	        likeButton.setContentAreaFilled(false); // 버튼 배경 제거
	        likeButton.setFocusPainted(false); // 포커스 테두리 제거
	        likeButton.setBorderPainted(false); // 버튼 테두리 제거
	        likeButton.setOpaque(false); // 버튼 불투명도 제거
	        postPanel.add(likeButton);

	        JLabel likeLabel = new JLabel(postDetails[6]); // 좋아요 수 표시
	        likeLabel.setBounds(150, createdAtYPosition + 25, 50, 20);
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
	                boolean success = likeModel.likePost(userID, postId);

	                if (success) {
	                    int newLikes = Integer.parseInt(likeLabel.getText()) + 1; // 좋아요 수 증가
	                    likeLabel.setText(String.valueOf(newLikes)); // UI 업데이트
	                    JOptionPane.showMessageDialog(detailPost.this, "Successfully liked!", "알림", JOptionPane.INFORMATION_MESSAGE);
	                } else {
	                    JOptionPane.showMessageDialog(detailPost.this, "Already liked.", "알림", JOptionPane.WARNING_MESSAGE);
	                }
	            }
	        });


	        // 리트윗 버튼
	        JButton retweetButton = new JButton();
	        retweetButton.setBounds(200, createdAtYPosition + 25, 20, 20); // 크기를 아이콘에 맞게 조정
	        retweetButton.setContentAreaFilled(false); // 배경 제거
	        retweetButton.setFocusPainted(false); // 포커스 테두리 제거
	        retweetButton.setBorderPainted(false); // 버튼 테두리 제거
	        retweetButton.setOpaque(false); // 불투명도 제거
	        postPanel.add(retweetButton);
	        
	        JLabel retweetLabel = new JLabel(String.valueOf(postDetails[8]));
            retweetLabel.setBounds(230, createdAtYPosition + 25, 50, 20);
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
	                boolean success = retweetModel.addRetweet(userID, postId); 

	                if (success) {
	                	int newRetweet = Integer.parseInt(retweetLabel.getText()) + 1; // 리트윗 수 증가
	                    retweetLabel.setText(String.valueOf(newRetweet)); // UI 업데이트
	                    JOptionPane.showMessageDialog(detailPost.this, "Successfully retweet!", "알림", JOptionPane.INFORMATION_MESSAGE);
	                } else {
	                    JOptionPane.showMessageDialog(detailPost.this, "Already retweet.", "알림", JOptionPane.WARNING_MESSAGE);
	                }
	            }
	        });

	        // 북마크 버튼
	        JButton bookmarkButton = new JButton();
	        bookmarkButton.setBounds(265, createdAtYPosition + 25, 20, 20); // 크기를 아이콘에 맞게 조정
	        bookmarkButton.setContentAreaFilled(false); // 배경 제거
	        bookmarkButton.setFocusPainted(false); // 포커스 테두리 제거
	        bookmarkButton.setBorderPainted(false); // 버튼 테두리 제거
	        bookmarkButton.setOpaque(false); // 불투명도 제거
	        postPanel.add(bookmarkButton);

	        JLabel bookmarkLabel = new JLabel(String.valueOf(postDetails[9]));
	        bookmarkLabel.setBounds(295, createdAtYPosition + 25, 50, 20);
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
	                boolean success = bookmarkModel.addBookmark(userID, postId);

	                if (success) {
	                	int newBookmark = Integer.parseInt(bookmarkLabel.getText()) + 1; // 북마크 수 증가
	                    bookmarkLabel.setText(String.valueOf(newBookmark)); // UI 업데이트
	                    JOptionPane.showMessageDialog(detailPost.this, "Successfully bookmarked", "알림", JOptionPane.INFORMATION_MESSAGE);
	                } else {
	                    JOptionPane.showMessageDialog(detailPost.this, "Already bookmarked.", "알림", JOptionPane.WARNING_MESSAGE);
	                }
	            }
	        });
	        
	        JLabel postEndLabel = new JLabel();
	        postEndLabel.setBounds(10, createdAtYPosition + 50, 360, 5);
	        postEndLabel.setBackground(new Color(245, 245, 245));
	        postEndLabel.setBorder(null);
	        postEndLabel.setOpaque(true);
            postPanel.add(postEndLabel);
            
            
            // 패널 크기 조정 (생성일 위치까지만 포함)
            postPanel.setPreferredSize(new Dimension(380, createdAtYPosition + 30 + 20));
            postPosition = createdAtYPosition + 30 + 20;
            
            
            // Back 버튼
            JButton backButton = new JButton("Back");
            backButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            backButton.setBounds(260, 30, 84, 26);
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
	                        allPost allPostWindow = new allPost(userID);
	                        allPostWindow.setVisible(true);
	                        detailPost.this.dispose(); // 현재 프레임 닫기 (필요 시 유지)
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
            
            
        } else {
            JLabel errorLabel = new JLabel("Failed to load post details.");
            errorLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            errorLabel.setForeground(Color.RED);
            errorLabel.setBounds(10, 10, 360, 30);
            postPanel.add(errorLabel);
        }
        
        
        // 모든 댓글 가져오기
	    java.util.List<String[]> allComments = loadAllComments(postId);

	    // 각 댓글 정보를 표시할 패널 생성 및 추가
	    int yOffset = postPosition; // 첫 댓글 간격
	    int panelHeight = 150; // 각 패널의 높이
	    for (String[] comment : allComments) {
	        JPanel commentPanel = new JPanel();
	        commentPanel.setLayout(null);
	        commentPanel.setBounds(10, yOffset, 360, panelHeight); // 패널 위치와 크기 설정
	        commentPanel.setBackground(new Color(255, 255, 255)); // 패널 배경 색상
	        commentPanel.setBorder(BorderFactory.createLineBorder(new Color(235, 235, 235), 2)); // 회색 테두리, 두께 2


	        // 유저 사진 표시
	        JButton userImageBtn = new JButton();
	        userImageBtn.setBounds(10, 10, 50, 50);
	        userImageBtn.setContentAreaFilled(false);
	        userImageBtn.setBorderPainted(false);
	        userImageBtn.setFocusPainted(false);
	        userImageBtn.setOpaque(false);
	        commentPanel.add(userImageBtn);

	        // 유저 사진 로드
	        if (comment[6] != null && !comment[6].isEmpty()) {
	            try {
	                URI uri = new URI(comment[6]);
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
	        nameLabel.setText("<html><span style='font-size:12px;'>" + comment[5] + "</span> " +
	                          "<span style='font-size:10px; color:gray;'>@" + comment[2] + "</span></html>");
	        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12)); // 기본 폰트 설정 (HTML 내부 스타일로 크기 조정 가능)
	        nameLabel.setBounds(70, 13, 280, 23); // 크기와 위치 설정
	        commentPanel.add(nameLabel);

	        
	        // 포스트 메시지 표시
	        JTextArea messageArea = new JTextArea();
	        messageArea.setText(comment[1]); // 메시지 내용
	        messageArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
	        messageArea.setBackground(new Color(245, 245, 245));
	        messageArea.setLineWrap(true);
	        messageArea.setWrapStyleWord(true);
	        messageArea.setEditable(false);
	        messageArea.setBounds(70, 40, 280, 60);
	        commentPanel.add(messageArea);

	        // 생성일 표시
	        JTextField createdAtField = new JTextField();
	        createdAtField.setText("Created at: " + comment[4]); // 생성일
	        createdAtField.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
	        createdAtField.setBackground(new Color(245, 245, 245));
	        createdAtField.setBorder(null);
	        createdAtField.setEditable(false);
	        createdAtField.setBounds(70, 104, 280, 20);
	        commentPanel.add(createdAtField);

	        
	        

	        // 모든 child 댓글 가져오기
	        java.util.List<String[]> allChildComments = loadAllChildComments(comment[0]);

	        // 첫 번째 child 댓글 가져오기 (리스트가 비어 있지 않은 경우)
	        String[] childComment = allChildComments.isEmpty() ? null : allChildComments.get(0);

	        // child 댓글 버튼
	        JButton childCommentButton = new JButton();
	        childCommentButton.setBounds(70, 125, 20, 20); // 크기를 아이콘에 맞게 조정
	        childCommentButton.setContentAreaFilled(false); // 배경 제거
	        childCommentButton.setFocusPainted(false); // 포커스 테두리 제거
	        childCommentButton.setBorderPainted(false); // 버튼 테두리 제거
	        childCommentButton.setOpaque(false); // 불투명도 제거
	        commentPanel.add(childCommentButton);

	        // child 댓글 수 라벨
	        String childCommentCount = childComment != null ? childComment[7] : "0"; // child 댓글 수 표시
	        JLabel childCommentLabel = new JLabel(childCommentCount);
	        childCommentLabel.setBounds(100, 125, 50, 20);
	        childCommentLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
	        childCommentLabel.setBackground(new Color(245, 245, 245));
	        commentPanel.add(childCommentLabel);

	        
	        // 댓글 아이콘 설정
	        try {
	            URL childCommentIconUrl = getClass().getResource("/images/Comment.png");
	            if (childCommentIconUrl != null) {
	                ImageIcon childCommentIcon = new ImageIcon(childCommentIconUrl);
	                Image scaledCommentIcon = childCommentIcon.getImage().getScaledInstance(19, 19, Image.SCALE_SMOOTH);
	                childCommentButton.setIcon(new ImageIcon(scaledCommentIcon));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        childCommentButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                // 댓글 입력 창 생성
	                JFrame commentFrame = new JFrame("Child Comment");
	                commentFrame.setBounds(100, 100, 300, 200);
	                commentFrame.setLocationRelativeTo(null);
	                commentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	                commentFrame.setLayout(new FlowLayout());

	                // 입력 필드 생성
	                JTextField commentField = new JTextField(20);
	                JLabel messageLabel = new JLabel("Child Comment:");
	                JButton submitButton = new JButton("save");

	                commentFrame.add(messageLabel);
	                commentFrame.add(commentField);
	                commentFrame.add(submitButton);

	                commentFrame.setVisible(true);

	                // child 댓글 작성 버튼 클릭 이벤트
	                submitButton.addActionListener(new ActionListener() {
	                    @Override
	                    public void actionPerformed(ActionEvent ev) {
	                        String comment = commentField.getText();
	                        if (comment.isEmpty()) {
	                            JOptionPane.showMessageDialog(commentFrame, "Enter the child comment!", "경고", JOptionPane.WARNING_MESSAGE);
	                            return;
	                        }

	                        // child 댓글 저장
	                        CommentModel commentModel = new CommentModel();
	                        boolean success = commentModel.addComment(userID, Integer.parseInt(childComment[5]), comment, null); // userId: post[4], postId: post[5]

	                        if (success) {
	                        	int newComment = Integer.parseInt(childCommentLabel.getText()) + 1; // child 댓글 수 증가
	    	                    childCommentLabel.setText(String.valueOf(newComment)); // UI 업데이트
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
	        likeButton.setBounds(140, 125, 20, 20); // 크기를 아이콘에 맞게 조정
	        likeButton.setFont(new Font("맑은 고딕", Font.PLAIN, 8));
	        likeButton.setContentAreaFilled(false); // 버튼 배경 제거
	        likeButton.setFocusPainted(false); // 포커스 테두리 제거
	        likeButton.setBorderPainted(false); // 버튼 테두리 제거
	        likeButton.setOpaque(false); // 버튼 불투명도 제거
	        commentPanel.add(likeButton);
	        
	        JLabel likeLabel = new JLabel(comment[6]); // 좋아요 수 표시
	        likeLabel.setBounds(170, 125, 50, 20);
	        likeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
	        likeLabel.setBackground(new Color(245, 245, 245));
	        commentPanel.add(likeLabel);

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
	                boolean success = likeModel.likePost(userID, Integer.parseInt(comment[0])); // currentUser: user_id, post[5]: post_id

	                if (success) {
	                    int newLikes = Integer.parseInt(likeLabel.getText()) + 1; // 좋아요 수 증가
	                    likeLabel.setText(String.valueOf(newLikes)); // UI 업데이트
	                    JOptionPane.showMessageDialog(detailPost.this, "Successfully liked!", "알림", JOptionPane.INFORMATION_MESSAGE);
	                } else {
	                    JOptionPane.showMessageDialog(detailPost.this, "Already liked.", "알림", JOptionPane.WARNING_MESSAGE);
	                }
	            }
	        });
	        
	        
	        postPanel.add(commentPanel);

	        // 다음 패널의 Y축 위치 계산
	        yOffset += panelHeight + 10;
	    }

	    // postPanel의 크기를 모든 포스트 수에 맞게 조정
	    postPanel.setPreferredSize(new Dimension(380, yOffset));
	    
	    // 스크롤바 위치 초기화 (맨 위로 설정)
	    SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }
    

    
    private java.util.List<String[]> loadAllChildComments(String commentId) {
        java.util.List<String[]> childCommentDetailsList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // SQL 쿼리: 해당 comment의 모든 child comment와 작성자 정보를 가져오기
            String query = "SELECT cc.cmt_id, cc.message, cc.user_id, cc.created_at, " +
                           "u.user_name, u.image_url, " +
                           "IFNULL((SELECT COUNT(*) FROM COMMENT_LIKE cl WHERE cl.cmt_id = cc.cmt_id), 0) AS num_of_likes, " +
                           "IFNULL((SELECT COUNT(*) FROM CHILD_COMMENT ccc WHERE ccc.parent_cmt_id = cc.cmt_id), 0) AS num_of_child_comments " +
                           "FROM CHILD_COMMENT cc " +
                           "JOIN USER u ON cc.user_id = u.user_id " +
                           "WHERE cc.parent_cmt_id = ? " +
                           "ORDER BY cc.created_at ASC";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, commentId); // parent comment ID로 필터링

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String[] childCommentDetails = new String[8];
                childCommentDetails[0] = rs.getString("cmt_id");               // Child comment ID
                childCommentDetails[1] = rs.getString("message");              // Child comment 내용
                childCommentDetails[2] = rs.getString("user_id");              // 작성자 ID
                childCommentDetails[3] = rs.getTimestamp("created_at").toString(); // 생성 날짜
                childCommentDetails[4] = rs.getString("user_name");            // 작성자 이름
                childCommentDetails[5] = rs.getString("image_url");            // 작성자 프로필 이미지 URL
                childCommentDetails[6] = rs.getString("num_of_likes");         // 좋아요 수
                childCommentDetails[7] = rs.getString("num_of_child_comments"); // Child 댓글 수
                childCommentDetailsList.add(childCommentDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return childCommentDetailsList;
    }


    
    
    private java.util.List<String[]> loadAllComments(int postId) {
	    java.util.List<String[]> comments = new ArrayList<>();

	    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
	    	// SQL 쿼리: 해당 포스트의 모든 댓글 정보와 댓글 작성자 정보를 가져오기
	        String query = "SELECT c.comment_id, c.message, c.user_id, c.num_of_likes, c.created_at, " +
                    "u.user_id AS user_id, u.user_name, u.image_url " +
                    "FROM COMMENT c " +
                    "JOIN USER u ON c.user_id = u.user_id " +
                    "WHERE c.post_id = ? " +
                    "ORDER BY c.created_at ASC";
	        
	        PreparedStatement pstmt = conn.prepareStatement(query);
	        pstmt.setInt(1, postId); // 해당 포스트 ID로 필터링

	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	String[] commentDetails = new String[8];
	            commentDetails[0] = rs.getString("comment_id");   // 댓글 ID
	            commentDetails[1] = rs.getString("message");      // 댓글 메시지
	            commentDetails[2] = rs.getString("user_id");      // 댓글 작성자 ID
	            commentDetails[3] = rs.getString("num_of_likes"); // 댓글 좋아요 수
	            commentDetails[4] = rs.getTimestamp("created_at").toString(); // 댓글 생성일
	            commentDetails[5] = rs.getString("user_name");    // 댓글 작성자 이름
	            commentDetails[6] = rs.getString("image_url");    // 댓글 작성자 프로필 이미지 URL
	            comments.add(commentDetails);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return comments;
	}
    
  
    private JLabel createCreatedAtLabel(String createdAtText, int yPosition) {
        JLabel createdAtLabel = new JLabel("Created at: " + createdAtText);
        createdAtLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        createdAtLabel.setBackground(new Color(245, 245, 245));
        createdAtLabel.setBounds(10, yPosition, 360, 20);
        createdAtLabel.setBorder(null);
        createdAtLabel.setOpaque(true);
        return createdAtLabel;
    }
    
    
    private String[] loadPostDetails(int postId) {
        String[] postDetails = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 수정된 SQL 쿼리: 게시물의 댓글 수, 리트윗 수, 북마크 수도 가져오기
            String query = "SELECT u.user_name, p.message, p.created_at, u.image_url, u.user_id, p.photo_url, p.num_of_likes, " +
                    "(SELECT COUNT(*) FROM COMMENT WHERE post_id = p.post_id) AS comment_count, " +
                    "(SELECT COUNT(*) FROM RETWEET WHERE post_id = p.post_id) AS retweet_count, " +
                    "(SELECT COUNT(*) FROM BOOKMARK WHERE post_id = p.post_id) AS bookmark_count " +
                    "FROM POSTS p " +
                    "JOIN USER u ON p.writer_id = u.user_id " +
                    "WHERE p.post_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, postId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                postDetails = new String[10];
                postDetails[0] = rs.getString("user_name");  // 사용자 이름
                postDetails[1] = rs.getString("message");    // 메시지
                postDetails[2] = rs.getTimestamp("created_at").toString(); // 생성일
                postDetails[3] = rs.getString("image_url");  // 사용자 이미지 URL
                postDetails[4] = rs.getString("user_id");    // 사용자 ID
                postDetails[5] = rs.getString("photo_url");  // 게시물 이미지 URL
                postDetails[6] = rs.getString("num_of_likes"); // 좋아요 수
                postDetails[7] = String.valueOf(rs.getInt("comment_count")); // 댓글 수
                postDetails[8] = String.valueOf(rs.getInt("retweet_count")); // 리트윗 수
                postDetails[9] = String.valueOf(rs.getInt("bookmark_count")); // 북마크 수
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
                int newHeight = 200; // 세로 크기 고정
                int newWidth = originalImage.getWidth() * newHeight / originalImage.getHeight(); // 가로 크기 비율 계산

                // 이미지 크기 조정
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