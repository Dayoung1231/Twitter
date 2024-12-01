package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewMessageDialog extends JPanel {
    private JTextField recipientField;
    private JTextArea messageArea;
    private DefaultListModel<String[]> userModel;
    private Connection connection;
    private String userId;

    public NewMessageDialog(JPanel parent, CardLayout cardLayout, Connection connection, String userId) {
        this.connection = connection;
        this.userId = userId;

        setLayout(new BorderLayout());

        // 통합 상단 패널 - 제목, Back 버튼, 받는 사람 필드 포함
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(106, 181, 249));

        // 첫 번째 행 - 제목과 Back 버튼
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(106, 181, 249));
        titlePanel.setPreferredSize(new Dimension(400, 50));

        JLabel titleLabel = new JLabel("  New Chat", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Back 버튼을 추가하고 더 명확하게 보이도록 설정합니다.
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(106, 181, 249));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setPreferredSize(new Dimension(80, 30)); // 크기 지정
        backButton.addActionListener(e -> cardLayout.show(parent, "DMList"));
        titlePanel.add(backButton, BorderLayout.EAST); // 버튼을 오른쪽에 배치

        
        // 두 번째 행 - 받는 사람 선택
        JPanel recipientPanel = new JPanel(new BorderLayout());
        recipientPanel.setPreferredSize(new Dimension(400, 40));
        recipientPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        recipientField = new JTextField();
        recipientPanel.add(new JLabel("receiver:"), BorderLayout.WEST);
        recipientPanel.add(recipientField, BorderLayout.CENTER);

        // 상단 패널에 두 개의 행 추가
        topPanel.add(titlePanel);
        topPanel.add(recipientPanel);

        // 사용자 목록 - 쪽지 보낼 사용자를 검색하고 선택할 수 있음
        userModel = new DefaultListModel<>();
        JList<String[]> userList = new JList<>(userModel);
        userList.setCellRenderer(new UserListRenderer());
        JScrollPane userScrollPane = new JScrollPane(userList);

        // 데이터베이스에서 사용자 목록 가져오기
        loadUsers("");

        recipientField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadUsers(recipientField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadUsers(recipientField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadUsers(recipientField.getText());
            }
        });

        // 사용자가 리스트에서 유저를 선택하면 recipientField에 자동으로 반영
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String[] selectedUser = userList.getSelectedValue();
                if (selectedUser != null) {
                    recipientField.setText(selectedUser[1]); // userId를 recipientField에 설정
                }
            }
        });

        // 하단 패널 - 메시지 입력과 전송 버튼
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        messageArea = new JTextArea(4, 20);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);

        JButton sendButton = new JButton("send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(106, 181, 249));
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(e -> sendMessage(parent, cardLayout));

        bottomPanel.add(messageScrollPane, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // 각 컴포넌트 추가
        add(topPanel, BorderLayout.NORTH);        // 통합 상단 패널 추가
        add(userScrollPane, BorderLayout.CENTER); // 사용자 목록 추가
        add(bottomPanel, BorderLayout.SOUTH);     // 하단 메시지 패널 추가
    }

    private void loadUsers(String filterText) {
        userModel.clear();
        String query = "SELECT user_id, user_name, image_url " +
                       "FROM user " +
                       "WHERE user_name LIKE ? AND user_id != ?"; // 현재 사용자를 제외
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, "%" + filterText + "%");
            ps.setString(2, userId); // 현재 사용자의 ID를 조건에 추가
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String userName = rs.getString("user_name");
                String userId = rs.getString("user_id");
                String imageUrl = rs.getString("image_url");
                userModel.addElement(new String[]{userName, userId, imageUrl});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터베이스 접근 중 오류가 발생했습니다: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void sendMessage(JPanel parent, CardLayout cardLayout) {
        String recipient = recipientField.getText().trim();
        String messageText = messageArea.getText().trim();
        if (!recipient.isEmpty() && !messageText.isEmpty()) {
            String query = "INSERT INTO dm (sender_id, receiver_id, message, created_at) VALUES (?, ?, ?, DEFAULT)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, userId);
                ps.setString(2, recipient);
                ps.setString(3, messageText);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Message sent successfully.");
                cardLayout.show(parent, "DMList"); // DM 리스트 화면으로 전환
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to send message: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Enter the receiver and the message", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    // 사용자 목록 렌더러 클래스 정의
    public class UserListRenderer extends JPanel implements ListCellRenderer<String[]> {
        private JLabel nameLabel;
        private JLabel profileImageLabel;

        public UserListRenderer() {
            setLayout(new BorderLayout(5, 5));
            profileImageLabel = new JLabel();
            profileImageLabel.setPreferredSize(new Dimension(40, 40));
            nameLabel = new JLabel();
            add(profileImageLabel, BorderLayout.WEST);
            add(nameLabel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String[]> list, String[] user, int index, boolean isSelected, boolean cellHasFocus) {
            String userName = user[0];
            String userId = user[1];
            String imageUrl = user[2];

            nameLabel.setText(userName + " (@" + userId + ")");
            loadUserImage(profileImageLabel, imageUrl, 40);

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }
    }

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
                // 예외가 발생할 경우 기본 이미지를 설정
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

    private ImageIcon createCircularImageIcon(URL url, int diameter) {
        try {
            BufferedImage originalImage = ImageIO.read(url);
            if (originalImage == null) {
                return null;
            }
            int size = Math.min(originalImage.getWidth(), originalImage.getHeight());
            BufferedImage squareImage = originalImage.getSubimage(
                (originalImage.getWidth() - size) / 2,
                (originalImage.getHeight() - size) / 2,
                size,
                size
            );

            BufferedImage resizedImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(squareImage, 0, 0, diameter, diameter, null);
            g2d.dispose();

            BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circularImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
            g2.drawImage(resizedImage, 0, 0, diameter, diameter, null);
            g2.dispose();

            return new ImageIcon(circularImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}