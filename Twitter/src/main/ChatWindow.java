package main;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatWindow extends JFrame {
    private JPanel chatPanel;
    private JTextField messageField;
    private JButton sendButton;
    private Connection connection;
    private String userId;
    private String recipientId;
    private String recipientName;

    public ChatWindow(JFrame parent, Connection connection, String userId, String recipientId, String recipientName) {
        super("Chat with " + recipientName);
        this.connection = connection;
        this.userId = userId;
        this.recipientId = recipientId;
        this.recipientName = recipientName;

        setBounds(100, 100, 400, 600);
       setLocationRelativeTo(null);
       setResizable(false);
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       getContentPane().setBackground(Color.WHITE);
       getContentPane().setLayout(null);
       
       // setLayout(new BorderLayout());
        //setSize(400, 600);

        // 상단 패널 설정
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(106, 181, 249)); // 상단 패널 배경색 설정
        topPanel.setBounds(0, 0, 390, 60);
       topPanel.setLayout(null);
       ChatWindow.this.getContentPane().add(topPanel);
        //topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        //add(topPanel, BorderLayout.NORTH);

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
       
        JLabel textLabel = new JLabel(recipientName);
        textLabel.setFont(new Font("Arial", Font.BOLD, 20)); 
        textLabel.setForeground(Color.WHITE);
        textLabel.setBounds(60, 15, 150, 30);
        topPanel.add(textLabel);


        // 뒤로 가기 버튼 추가
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        backButton.setBounds(290, 20, 80, 26);
        backButton.setForeground(new Color(106, 181, 249)); // 글자 색상: 흰색
        backButton.setBackground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setOpaque(false); // 불투명 효과 제거
        backButton.setContentAreaFilled(false); // 버튼 배경 투명 처리
        topPanel.add(backButton);
        
     // back 버튼 클릭 이벤트
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

               
                SwingUtilities.invokeLater(() -> {
                    try {
                        DMList chatWindow = new DMList(userId);
                        chatWindow.setVisible(true);
                        ChatWindow.this.dispose(); // 현재 프레임 닫기 (필요 시 유지)
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
        

     // 모든 포스트를 담을 chat 패널
        chatPanel = new JPanel();
       chatPanel.setBackground(new Color(255, 255, 255));
       //containerPanel.setLayout(null); // 자유 배치
       chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS)); // BoxLayout으로 설정하여 자동 크기 조정containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS)); // BoxLayout으로 설정하여 자동 크기 조정
       chatPanel.setPreferredSize(new Dimension(200, 150)); // 초기 크기 설정??????

       // 스크롤 패널
       JScrollPane scrollPane = new JScrollPane(chatPanel);
       scrollPane.setBounds(0, 60, 386, 440);
       scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 스크롤 속도 조정
       // 세로 스크롤바 스타일 적용
       scrollPane.getVerticalScrollBar().setUI(new ScrollBarUI());
       scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); // 너비를 8로 설정 (이미지처럼 얇게)
       // 가로 스크롤바 숨기기 (필요 시 추가)
       scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
       scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤 비활성화
       scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); // 세로 스크롤 활성화
       scrollPane.getVerticalScrollBar().setBlockIncrement(50);
       getContentPane().add(scrollPane);

       //this.add(scrollPane, BorderLayout.CENTER);
       
        // 채팅 기록 불러오기
        loadChatHistory();
        

        
     // 하단 패널 생성
        JPanel sendMsgPanel = new JPanel(new BorderLayout());
        sendMsgPanel.setBackground(new Color(245, 245, 245)); // 배경색 설정
        sendMsgPanel.setBounds(0, 500, 390, 61); // 하단에 고정
        getContentPane().add(sendMsgPanel);

        // 메시지 입력 필드
        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sendMsgPanel.add(messageField, BorderLayout.CENTER);

        // 전송 버튼
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(106, 181, 249));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sendMsgPanel.add(sendButton, BorderLayout.EAST);
        
        sendButton.addActionListener(e -> {
            System.out.println("Send button clicked");
            sendMessage();
        });


   
        sendMsgPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(sendMsgPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    // 채팅 기록 불러오기
    private void loadChatHistory() {
        String query = "SELECT sender_id, message, created_at FROM dm " +
                "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                "ORDER BY created_at ASC";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, userId);
            ps.setString(2, recipientId);
            ps.setString(3, recipientId);
            ps.setString(4, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String sender = rs.getString("sender_id").equals(userId) ? "You" : recipientName;
                    String message = rs.getString("message");
                    String timestamp = rs.getString("created_at");
                    addMessageToChatPanel(sender, message, timestamp, rs.getString("sender_id").equals(userId));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "error:loading chatting history : " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 새 메시지 전송하기
    private void sendMessage() {
        String messageText = messageField.getText().trim();
        if (!messageText.isEmpty()) {
            String query = "INSERT INTO dm (sender_id, receiver_id, message, created_at) VALUES (?, ?, ?, DEFAULT)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, userId);
                ps.setString(2, recipientId);
                ps.setString(3, messageText);
                ps.executeUpdate();

                String currentTime = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                addMessageToChatPanel("You", messageText, currentTime, true);
                messageField.setText("");
                chatPanel.revalidate();
                chatPanel.repaint();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "메시지 전송에 실패했습니다: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "메시지를 입력하세요.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

 // 채팅 패널에 메시지를 추가하는 메서드
    private void addMessageToChatPanel(String sender, String message, String time, boolean isUserMessage) {
        // 메시지 텍스트 영역
        JTextPane messagePane = new JTextPane();
        messagePane.setContentType("text/html"); // HTML 형식으로 메시지 표시
        messagePane.setText(formatMessage(message)); // 메시지를 HTML 형식으로 변환
        messagePane.setEditable(false);
        messagePane.setFont(new Font("Arial", Font.PLAIN, 14));
        messagePane.setBackground(isUserMessage ? Color.WHITE : new Color(245, 245, 245));
        messagePane.setForeground(isUserMessage ? Color.WHITE : Color.BLACK);
        messagePane.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15), // 둥근 테두리
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // 내부 여백
        ));

        
        messagePane.setSize(new Dimension(100, Short.MAX_VALUE)); // 가로 고정, 세로 제한 해제
        int preferredHeight = messagePane.getPreferredSize().height; // 자동 계산된 높이
        messagePane.setPreferredSize(new Dimension(100, preferredHeight)); // 동적 세로 높이 적용

        // 메시지와 시간 표시를 위한 패널
        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.Y_AXIS));
        bubblePanel.setAlignmentX(isUserMessage ? Component.LEFT_ALIGNMENT : Component.RIGHT_ALIGNMENT);
        bubblePanel.setOpaque(false); // 투명 배경 설정
        bubblePanel.add(messagePane);

        // 시간 레이블 추가
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(Color.GRAY);
        timeLabel.setAlignmentX(isUserMessage ? Component.LEFT_ALIGNMENT : Component.RIGHT_ALIGNMENT);
        timeLabel.setHorizontalAlignment(isUserMessage ? SwingConstants.LEFT : SwingConstants.RIGHT);

        bubblePanel.add(timeLabel);

        // 채팅 패널에 추가
        chatPanel.add(bubblePanel);
        chatPanel.add(Box.createVerticalStrut(10)); // 메시지 간 간격 추가

     // Ensure the chat panel is updated and scrolled to the bottom
        SwingUtilities.invokeLater(() -> {
            chatPanel.revalidate();
            chatPanel.repaint();
            
            // Scroll to the bottom
            JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, chatPanel);
            if (scrollPane != null) {
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            }
        });
        
    }

/*

    // 채팅 패널을 감싸는 스크롤 설정 (초기화 시 설정)
    private void setupChatScrollPane() {
        JScrollPane scrollPane = new JScrollPane(chatPanel);
        scrollPane.getVerticalScrollBar().setUI(new ScrollBarUI());
       scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // 테두리 제거
        chatPanel.add(scrollPane, BorderLayout.CENTER); // 메인 프레임에 추가
    }

*/

        // HTML 형식으로 메시지 포맷팅
        private String formatMessage(String message) {
            StringBuffer formattedMessage = new StringBuffer("<html>");
            int maxLineLength = 30; // 최대 길이 설정
            for (int i = 0; i < message.length(); i += maxLineLength) {
                int end = Math.min(i + maxLineLength, message.length());
                formattedMessage.append(message.substring(i, end)).append("<br>");
            }
            formattedMessage.append("</html>");
            return formattedMessage.toString();
        }



    // 둥근 모서리를 위한 Border 클래스 정의
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 4, 4);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = 4;
            return insets;
        }
    }
}