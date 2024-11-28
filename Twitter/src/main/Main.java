/*
package main;

import database.DatabaseConnection;
import java.sql.Connection;
import model.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserModel userModel = new UserModel();
        PostModel postModel = new PostModel();
        CommentModel commentModel = new CommentModel();
        LikeModel likeModel = new LikeModel();
        FollowModel followModel = new FollowModel();
        DirectMessageModel dmModel = new DirectMessageModel();

        String loggedInUser = null;
        
     // DatabaseConnection 테스트
        try {
            Connection con = DatabaseConnection.getConnection();
            if (con != null) {
                System.out.println("Database connection successful!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            if (loggedInUser == null) { // 로그인하지 않은 상태
            	System.out.println("\n=== Twitter Clone ===");
                System.out.println("1: Login");
                System.out.println("2: Sign Up");
                System.out.println("3: Change Password");
                System.out.println("4: Exit");
                System.out.print("Choose an option: ");
                int option = scanner.nextInt();
                scanner.nextLine(); // 버퍼 비우기

                if (option == 1) { // 로그인
                    System.out.print("\nEnter ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Enter Password: ");
                    String password = scanner.nextLine();

                    loggedInUser = userModel.login(id, password);
                    if (loggedInUser != null) {
                        System.out.println("Welcome, " + loggedInUser + "!");
                    }
                } else if (option == 2) { // 회원가입
                    System.out.print("\nEnter ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Enter Password: ");
                    String password = scanner.nextLine();
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Phone Number: ");
                    String phone = scanner.nextLine();
                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine();

                    boolean signupSuccess = userModel.signup(id, password, name, phone, email);
                    if (signupSuccess) {
                        System.out.println("Sign up successful! You can now log in.");
                    } else {
                        System.out.println("Sign up failed. Please try again.");
                    }
                } else if (option == 3) { // 비밀번호 변경
                    System.out.print("\nEnter your ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Enter your current password: ");
                    String currentPassword = scanner.nextLine();
                    System.out.print("Enter your new password: ");
                    String newPassword = scanner.nextLine();

                    boolean passwordChanged = userModel.changePassword(id, currentPassword, newPassword);
                    if (passwordChanged) {
                        System.out.println("\nPassword changed successfully!");
                    } else {
                        System.out.println("Failed to change password. Please try again.");
                    }
                } else if (option == 4) { // 종료
                    System.out.println("\nTwitter has been shut down.");
                    break;
                } else {
                    System.out.println("\nInvalid option. Try again.");
                }
            } else { // 로그인한 상태
                System.out.println("\n=== Main Menu ===");
                System.out.println("1: Create Post");
                System.out.println("2: View Post");
                System.out.println("3: Comment on Post");
                System.out.println("4: Like Post");
                System.out.println("5: Like Comment");
                System.out.println("6: Follow User");
                System.out.println("7: View Following List");
                System.out.println("8: View Follower List");
                System.out.println("9: Send DM");
                System.out.println("10: View Conversation");
                System.out.println("11: Logout");
                System.out.print("Choose an option: ");
                int option = scanner.nextInt();
                scanner.nextLine(); // 버퍼 비우기

                if (option == 1) { // 게시글 작성
                    System.out.print("\nEnter your post content: ");
                    String message = scanner.nextLine();
                    System.out.print("Enter photo URL (or leave blank): ");
                    String photoUrl = scanner.nextLine();

                    boolean postCreated = postModel.createPost(loggedInUser, message, photoUrl.isEmpty() ? null : photoUrl);
                    if (postCreated) {
                        System.out.println("Post created successfully!");
                    } else {
                        System.out.println("Failed to create post.");
                    }
                } else if (option == 2) { // 게시글 조회
                    System.out.print("\nEnter Post ID to view: ");
                    int postId = scanner.nextInt();
                    scanner.nextLine(); // 버퍼 비우기

                    String postDetails = postModel.getPost(postId);
                    if (postDetails != null) {
                        System.out.println("\n" + postDetails);
                    } else {
                        System.out.println("Post not found.");
                    }
                } else if (option == 3) { // 댓글 작성
                    System.out.print("\nEnter Post ID to comment on: ");
                    int postId = scanner.nextInt();
                    scanner.nextLine(); // 버퍼 비우기
                    System.out.print("Enter your comment: ");
                    String comment = scanner.nextLine();
                    System.out.print("Enter photo URL (or leave blank): ");
                    String photoUrl = scanner.nextLine();

                    boolean commentAdded = commentModel.addComment(loggedInUser, postId, comment, photoUrl.isEmpty() ? null : photoUrl);
                    if (commentAdded) {
                        System.out.println("\nComment added successfully!");
                    } else {
                        System.out.println("\nFailed to add comment.");
                    }
                } else if (option == 4) { // 게시글 좋아요
                    System.out.print("\nEnter Post ID to like: ");
                    int postId = scanner.nextInt();
                    scanner.nextLine(); // 버퍼 비우기

                    boolean liked = likeModel.likePost(loggedInUser, postId);
                    if (liked) {
                        System.out.println("\nPost liked successfully!");
                    } else {
                        System.out.println("\nFailed to like post.");
                    }
                } else if (option == 5) { // 댓글 좋아요
                    System.out.print("\nEnter Comment ID to like: ");
                    int commentId = scanner.nextInt();
                    scanner.nextLine(); // 버퍼 비우기

                    boolean liked = likeModel.likeComment(loggedInUser, commentId);
                    if (liked) {
                        System.out.println("\nComment liked successfully!");
                    } else {
                        System.out.println("\nFailed to like comment.");
                    }
                } else if (option == 6) { // 팔로우
                    System.out.print("\nEnter User ID to follow: ");
                    String followUserId = scanner.nextLine();

                    boolean followed = followModel.followUser(loggedInUser, followUserId);
                    if (followed) {
                        System.out.println("\nYou are now following " + followUserId + "!");
                    } else {
                        System.out.println("\nFailed to follow user.");
                    }
                } else if (option == 7) { // 팔로잉 목록 조회
                    String followingList = followModel.getFollowingList(loggedInUser);
                    if (followingList != null) {
                        System.out.println("\nFollowing:\n" + followingList);
                    } else {
                        System.out.println("You are not following anyone.");
                    }
                } else if (option == 8) { // 팔로워 목록 조회
                    String followerList = followModel.getFollowerList(loggedInUser);
                    if (followerList != null) {
                        System.out.println("\nFollowers:\n" + followerList);
                    } else {
                        System.out.println("You have no followers.");
                    }
                } else if (option == 9) { // DM 전송
                    System.out.print("\nEnter Receiver ID: ");
                    String receiverId = scanner.nextLine();
                    System.out.print("Enter your message: ");
                    String message = scanner.nextLine();

                    boolean messageSent = dmModel.sendMessage(loggedInUser, receiverId, message);
                    if (messageSent) {
                        System.out.println("\nDM sent successfully!");
                    } else {
                        System.out.println("\nFailed to send DM.");
                    }
                } else if (option == 10) { // 대화 조회
                    System.out.print("\nEnter User ID to view conversation: ");
                    String userId = scanner.nextLine();

                    String conversation = dmModel.getConversation(loggedInUser, userId);
                    if (conversation != null) {
                        System.out.println("\nConversation:\n" + conversation);
                    } else {
                        System.out.println("\nNo conversation found.");
                    }
                } else if (option == 11) { // 로그아웃
                    loggedInUser = null;
                    System.out.println("\nLogged out successfully.");
                } else {
                    System.out.println("\nInvalid option. Try again.");
                }
            }
        }

        scanner.close();
    }
}
*/