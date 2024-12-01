package main;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
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

        setLayout(new BorderLayout());
        setSize(400, 600);

        // 상단 패널 설정
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(106, 181, 249)); // 상단 패널 배경색 설정
        topPanel.setPreferredSize(new Dimension(400, 60));
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        add(topPanel, BorderLayout.NORTH);

        JLabel textLabel = new JLabel("Chat with " + recipientName);
        textLabel.setFont(new Font("Arial", Font.BOLD, 20)); // 상단 제목 설정
        textLabel.setForeground(Color.WHITE);
        topPanel.add(textLabel);

        // 뒤로 가기 버튼 추가
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 15));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(106, 181, 249));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> {
            parent.setVisible(true);
            this.dispose();
        });
        topPanel.add(backButton);

        // 채팅 내용을 보여주는 영역
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE); // 배경색 설정
        JScrollPane chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.getViewport().setBackground(Color.WHITE); // 스크롤 패널의 배경색 설정
        add(chatScrollPane, BorderLayout.CENTER);

        // 채팅 기록 불러오기
        loadChatHistory();

        // 메시지 입력 및 전송 버튼
        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(106, 181, 249)); // send 버튼
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(e -> sendMessage());

        JPanel sendPanel = new JPanel(new BorderLayout());
        sendPanel.add(messageField, BorderLayout.CENTER);
        sendPanel.add(sendButton, BorderLayout.EAST);
        sendPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(sendPanel, BorderLayout.SOUTH);

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
            JOptionPane.showMessageDialog(this, "채팅 기록을 불러오는 중 오류가 발생했습니다: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
        JPanel messagePanel = new JPanel();
        
        // 상대방 메시지 왼쪽 정렬, 내 메시지 오른쪽 정렬
        FlowLayout layout = isUserMessage ? new FlowLayout(FlowLayout.RIGHT) : new FlowLayout(FlowLayout.LEFT);
        messagePanel.setLayout(layout);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 여백 설정
        messagePanel.setBackground(Color.WHITE);
        
        // 메시지 내용 길이에 맞춰서 줄바꿈 처리
        StringBuffer formattedMessage = new StringBuffer("<html>");
        int maxLineLength = 30; // 최대 길이 설정
        for (int i = 0; i < message.length(); i += maxLineLength) {
            int end = Math.min(i + maxLineLength, message.length());
            formattedMessage.append(message.substring(i, end)).append("<br>");
        }
        formattedMessage.append("</html>");

        
        JLabel messageLabel = new JLabel(formattedMessage.toString());
        messageLabel.setOpaque(true);
        messageLabel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15), // 모서리를 둥글게 만듦
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setBackground(isUserMessage ? new Color(106, 181, 249) : new Color(245, 245, 245)); // 유저 메시지는 파란색, 상대방 메시지는 회색
        messageLabel.setForeground(isUserMessage ? Color.WHITE : Color.BLACK); // 유저 메시지는 흰색 글자, 상대방 메시지는 검정 글자

        // 시간 레이블 추가
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(Color.GRAY);
        
        // messageLabel과 timeLabel을 수직으로 배치하기 위해 BoxLayout 사용
        BoxLayout boxLayout = new BoxLayout(messagePanel, BoxLayout.Y_AXIS);
        messagePanel.setLayout(boxLayout);
        messagePanel.add(messageLabel);
        messagePanel.add(timeLabel); // 시간은 메시지 아래에 배치

        // 상대방 메시지인 경우 왼쪽 정렬, 내 메시지인 경우 오른쪽 정렬
        chatPanel.add(messagePanel);
        chatPanel.add(Box.createVerticalStrut(10)); // 메시지 간의 간격 추가
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