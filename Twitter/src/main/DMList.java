package main;

import database.DatabaseConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DMList extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(DMList.class.getName());
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private JList<String[]> dmList;
    private DefaultListModel<String[]> listModel;
    private String userId;
    private Connection connection;
    private JPanel mainPanel;
    private String currentRecipientId;
    private String currentRecipientName;
    private JPanel newMessagePanel;

    public DMList(String userId) {
        this.userId = userId;

        try {
            this.connection = DatabaseConnection.getConnection();
            if (this.connection == null) {
                throw new SQLException("데이터베이스 연결 실패: connection 객체가 null입니다.");
            }
        } catch (SQLException e) {
            logError("데이터베이스 연결 실패: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "데이터베이스 연결 실패: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return; // 연결에 실패했으니까 더 이상 진행하지 않음
        }

        // 윈도우 설정
        setTitle("Twitter - Direct Messages");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        add(mainContainer);

        // DM 리스트 패널 설정
        mainPanel = new JPanel(new BorderLayout());
        //mainPanel.setPreferredSize(new Dimension(380, 250)); // 초기 크기 설정
        
        
        // 상단 패널 생성
	    JPanel topPanel = new JPanel();
	    topPanel.setBackground(new Color(106, 181, 249)); // 트위터와 비슷한 파란색
	    topPanel.setBounds(0, 0, 390, 60);
	    //topPanel.setLayout(null);
	    mainPanel.add(topPanel);


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
	    JLabel textLabel = new JLabel("Direct Messages");
	    textLabel.setFont(new Font("Arial", Font.BOLD, 20));
	    textLabel.setForeground(Color.WHITE);
	    textLabel.setBounds(60, 15, 200, 30);
	    topPanel.add(textLabel);
	    
	    
	    // + 버튼
        JButton addButton = new JButton("+");
        addButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        addButton.setBounds(320, 20, 45, 26);
        addButton.setForeground(new Color(106, 181, 249)); // 글자 색상: 흰색
        addButton.setBackground(Color.WHITE); // 배경 색상: 파란색
        addButton.setFocusPainted(false); // 클릭 시 포커스 효과 제거
        addButton.setBorderPainted(false); // 기본 테두리 제거
        addButton.setOpaque(false); // 불투명 효과 제거
        addButton.setContentAreaFilled(false); // 버튼 배경 투명 처리
        topPanel.add(addButton);
        
        
        // 둥근 버튼 모양 만들기
        addButton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 버튼 배경 채우기
                g2.setColor(addButton.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 30, 30); // 둥근 사각형: 30px radius

                super.paint(g, c);
            }
        });
        
        // 버튼 클릭 시 새 쪼지 패널로 전환하도록 이벤트 리스너 추가
        addButton.addActionListener(e -> switchToNewMessagePanel());

        topPanel.add(addButton);

        // DM 리스트 설정
        listModel = new DefaultListModel<>();
        dmList = new JList<>(listModel);
        dmList.setCellRenderer(new MessageRenderer()); // 메시지 렛더레 설정
        JScrollPane scrollPane = new JScrollPane(dmList);
        
       
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // DM 데이터 불러오기
        loadUserGroupedDirectMessages();

        // 리스트 선택 리스너 추가
        dmList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String[] selectedDm = dmList.getSelectedValue();
                if (selectedDm != null && !selectedDm[2].equals(userId)) {
                    currentRecipientId = selectedDm[2];
                    currentRecipientName = selectedDm[1];
                    switchToChatWindow(); // 선택한 사용자와의 채팅 창 전환
                } else {
                    JOptionPane.showMessageDialog(this, "자신에게 메시지를 보내지 않을 수 있습니다.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // 하단 버튼 3개 추가 (Profile, Home, DM)
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
        bottomPanel.setPreferredSize(new Dimension(400, 50));

        // 하단 버튼 생성 및 추가
        JButton profileButton = createBottomButton("Profile");
        JButton homeButton = createBottomButton("Home");
        JButton dmButton = createBottomButton("DM");

        bottomPanel.add(profileButton);
        bottomPanel.add(homeButton);
        bottomPanel.add(dmButton);
        
        // 홈 버튼 클릭 이벤트
        homeButton.addActionListener(new ActionListener() {
           @Override
            public void actionPerformed(ActionEvent e) {
        	   // allPost 클래스의 프레임 호출
        	   SwingUtilities.invokeLater(() -> {
        		   try {
        			   allPost allPostWindow = new allPost(userId);
        			   allPostWindow.setVisible(true);
        			   DMList.this.dispose(); // 현재 프레임 닫기 (필요 시 유지)
        		   } catch (Exception ex) {
        			   ex.printStackTrace();
        		   }
        	   });
           }
        });
        // 프로필 버튼 클릭 이벤트
        profileButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		// Profile 클래스의 프레임 호출
        		SwingUtilities.invokeLater(() -> {
        			try {
        				Profile profileWindow = new Profile(userId);
                        profileWindow.setVisible(true);
                        DMList.this.dispose(); // 현재 프레임 닫기 (필요 시 유지)
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
        		});
        	}
        });
       

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainContainer.add(mainPanel, "DMList");
        setLocationRelativeTo(null); // 화면 중앙에 위치시키기
        setVisible(true); // 창 보이기
        
        // 모든 포스트를 담을 컨테이너 패널
      
        mainPanel.setBackground(Color.WHITE);
        //containerPanel.setLayout(null); // 자유 배치
        mainPanel.setLayout(new BoxLayout( mainPanel, BoxLayout.Y_AXIS)); // BoxLayout으로 설정하여 자동 크기 조정containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS)); // BoxLayout으로 설정하여 자동 크기 조정
        mainPanel.setPreferredSize(new Dimension(380, 250)); // 초기 크기 설정
       
       
     
    }

    private JButton createBottomButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(106, 181, 249)); // 버튼 배경색 설정
        button.setForeground(Color.WHITE); // 버튼 텍스트 색상 설정
        button.setFont(new Font("Arial", Font.BOLD, 14)); // 버튼 폰트 설정
        return button;
    }


    // 메시지 렛더레 클래스 정의
    class MessageRenderer extends JPanel implements ListCellRenderer<String[]> {
        private JLabel nameLabel;
        private JLabel idLabel;
        private JLabel messagePreviewLabel;
        private JLabel timeLabel;
        private JLabel profileImageLabel;

        public MessageRenderer() {
            setLayout(new BorderLayout(10, 10));  // 간격 추가
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // 패널의 내부 여백 추가

            // 프로필 이미지
            profileImageLabel = new JLabel();
            profileImageLabel.setPreferredSize(new Dimension(50, 50));
            profileImageLabel.setOpaque(false); // 배경을 투명하게 설정하여 회색 네모가 보이지 않도록
            profileImageLabel.setBackground(Color.LIGHT_GRAY);  // 기본 배경색 (이미지가 없을 때)

            // 텍스트 정보를 담을 패널 생성
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BorderLayout());
            textPanel.setOpaque(false);

            // 이름과 ID, 시간 정보 패널
            JPanel nameAndIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            nameAndIdPanel.setOpaque(false);

            nameLabel = new JLabel();
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

            idLabel = new JLabel();
            idLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            idLabel.setForeground(Color.GRAY);

            nameAndIdPanel.add(nameLabel);
            nameAndIdPanel.add(idLabel);

            // 메시지 미리보기 레이블
            messagePreviewLabel = new JLabel();
            messagePreviewLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            messagePreviewLabel.setForeground(Color.GRAY);

            // 텍스트 패널에 이름/ID 패널과 메시지 미리보기 추가
            textPanel.add(nameAndIdPanel, BorderLayout.NORTH);
            textPanel.add(messagePreviewLabel, BorderLayout.CENTER);

            // 시간 레이블 설정
            timeLabel = new JLabel();
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            timeLabel.setForeground(Color.GRAY);

            // 시간 레이블을 오른쪽 끝에 배치하기 위해 새로운 JPanel 사용
            JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0)); // 오른쪽 정렬
            timePanel.setOpaque(false);
            timePanel.add(timeLabel);

            // 메시지 미리보기 레이블 밑에 시간 레이블 추가
            textPanel.add(timePanel, BorderLayout.SOUTH);

            // 전체 레이아웃에 추가
            add(profileImageLabel, BorderLayout.WEST);  // 프로필 이미지를 왼쪽에 추가
            add(textPanel, BorderLayout.CENTER);  // 텍스트 패널을 중앙에 추가
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String[]> list, String[] dm, int index, boolean isSelected, boolean cellHasFocus) {
            // 프로필 이미지 설정
            String profileImageUrl = dm[5];
            loadUserImage(profileImageLabel, profileImageUrl, 50);

            // 사용자 이름과 ID 설정
            nameLabel.setText(dm[1]);
            idLabel.setText("@" + dm[0]);

            // 메시지 미리보기 설정 (25글자까지만 보이고 "..."을 추가)
            String messagePreview = dm[3];
            if (messagePreview.length() > 25) {
                messagePreview = messagePreview.substring(0, 25) + "...";  // 메시지를 자르고 '...' 추가
            }
            messagePreviewLabel.setText(messagePreview);

            // 메시지 시간 설정
            timeLabel.setText(dm[4]);

            // 선택된 상태 강조
            if (isSelected) {
                setBackground(new Color(220, 230, 240));  // 선택된 배경색 설정
            } else {
                setBackground(Color.WHITE);  // 기본 배경색 설정
            }

            return this;
        }
    }

    // 공통 메서드: 사용자 프로필 이미지를 로드하는 메서드
    private void loadUserImage(JLabel label, String imageUrl, int diameter) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                URI uri = new URI(imageUrl);
                URL url = uri.toURL();
                ImageIcon circularIcon = createCircularImageIcon(url, diameter);
                if (circularIcon != null) {
                    label.setIcon(circularIcon);
                } else {
                    label.setIcon(getDefaultUserImageIcon(diameter));
                }
            } catch (Exception e) {
                // 예제가 발생할 경우 기본 이미지를 설정
                e.printStackTrace();
                label.setIcon(getDefaultUserImageIcon(diameter));
            }
        } else {
            // 이미지 URL이 없는 경우 기본 이미지를 설정
            label.setIcon(getDefaultUserImageIcon(diameter));
        }
    }

    private ImageIcon getDefaultUserImageIcon(int diameter) {
        try {
        	
            // 기본 이미지 로드 (로컬 리소스)
            URL defaultImageUrl = getClass().getResource("/images/defaultUserImage.jpeg");
            
            if (defaultImageUrl != null) {
            	return createCircularImageIcon(defaultImageUrl.toURI().toURL(), diameter);
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ImageIcon(new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB)); // 이미지가 없을 경우 빈 이미지 반환
    }
    
    // URL에서 이미지 아이콘을 생성하고 원형으로 만들는 메서드
    private ImageIcon createCircularImageIcon(URL url, int diameter) {
        try {
            // URL에서 BufferedImage 로드
            BufferedImage originalImage = ImageIO.read(url);
            if (originalImage == null) {
                return null; // 이미지를 로드할 수 없는 경우 null 반환
            }

            // 정사각형으로 변환
            int size = Math.min(originalImage.getWidth(), originalImage.getHeight());
            BufferedImage squareImage = originalImage.getSubimage(
                (originalImage.getWidth() - size) / 2,
                (originalImage.getHeight() - size) / 2,
                size,
                size
            );

            // 고평질로 크기 조정
            BufferedImage resizedImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(squareImage, 0, 0, diameter, diameter, null);
            g2d.dispose();

            // 원형 마스크 적용
            BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circularImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
            g2.drawImage(resizedImage, 0, 0, diameter, diameter, null);
            g2.dispose();

            return new ImageIcon(circularImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 데이터베이스에서 현재 유저가 받은 모든 DM을 사용자별으로 불러오는 메서드
    private void loadUserGroupedDirectMessages() {
        listModel.clear(); // 리스트 초기화

        String query = "WITH LatestMessages AS (" +
                       "    SELECT dm.sender_id, u1.user_name AS sender_name, dm.receiver_id, u2.user_name AS receiver_name, " +
                       "           dm.message, dm.created_at, " +
                       "           CASE WHEN dm.sender_id = ? THEN u2.image_url ELSE u1.image_url END AS image_url, " +
                       "           ROW_NUMBER() OVER (PARTITION BY " +
                       "                             CASE WHEN dm.sender_id = ? THEN dm.receiver_id ELSE dm.sender_id END " +
                       "                             ORDER BY dm.created_at DESC) AS rn " +
                       "    FROM DM dm " +
                       "    JOIN user u1 ON dm.sender_id = u1.user_id " +
                       "    JOIN user u2 ON dm.receiver_id = u2.user_id " +
                       "    WHERE dm.sender_id = ? OR dm.receiver_id = ? " +
                       ") " +
                       "SELECT sender_id, sender_name, receiver_id, receiver_name, message, created_at, image_url " +
                       "FROM LatestMessages " +
                       "WHERE rn = 1 " +
                       "ORDER BY created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, userId);
            ps.setString(2, userId);
            ps.setString(3, userId);
            ps.setString(4, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String senderId = rs.getString("sender_id");
                    String senderName = rs.getString("sender_name");
                    String receiverId = rs.getString("receiver_id");
                    String receiverName = rs.getString("receiver_name");
                    String latestMessage = rs.getString("message");
                    String latestCreatedAt = rs.getString("created_at");
                    String imageUrl = rs.getString("image_url");

                    // 자신이 보내는 경우 받는 사람 표시, 자신이 받은 경우 보낸 사람 표시
                    String key = senderId.equals(userId) ? receiverId : senderId;
                    String name = senderId.equals(userId) ? receiverName : senderName;

                    // 리스트에 추가
                    listModel.addElement(new String[]{senderId.equals(userId) ? receiverId : senderId, name, key, latestMessage, latestCreatedAt, imageUrl});
                }
            }
        } catch (SQLException ex) {
            logError("데이터베이스 접근 중 오류가 발생했습니다: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "데이터베이스 접근 중 오류가 발생했습니다: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // 선택한 사용자와의 채팅 기록 불러오기
    private void switchToChatWindow() {
        // 기존에 CardLayout에서 JPanel에 추가하는 방식이 아니고 JFrame을 사용하여 새 창을 띄우음
        new ChatWindow(this, connection, userId, currentRecipientId, currentRecipientName);
        this.setVisible(false); // 현재 창을 숨기기
    }

    private void switchToNewMessagePanel() {
        if (newMessagePanel == null) {
            newMessagePanel = new NewMessageDialog(mainContainer, cardLayout, connection, userId);
        }
        mainContainer.add(newMessagePanel, "NewMessagePanel");
        cardLayout.show(mainContainer, "NewMessagePanel");
    }


    // 로그 파일에 오류 기록하기
    private void logError(String message) {
        LOGGER.log(Level.SEVERE, message);
        try (FileWriter fw = new FileWriter("error.log", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(message);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "로그 파일에 기록 중 오류 발생: " + e.getMessage());
        }
    }


    // 정적 메서드에서 로그 파일에 오류 기록하기
    private static void logStaticError(String message) {
        try (FileWriter fw = new FileWriter("error.log", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(message);
        } catch (IOException e) {
            Logger.getLogger(DMList.class.getName()).log(Level.SEVERE, "로그 파일에 기록 중 오류 발생: " + e.getMessage());
        }
    }
}