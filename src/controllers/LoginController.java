package controllers;

import views.LoginView;
import views.StudentMain;
import views.LibraryAdminPanelView;
import config.DatabaseConnection;
import models.User;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private LoginView loginView;
    private User loggedInUser; // Giriş yapan kullanıcıyı saklamak için

    public LoginController(LoginView loginView) {
        this.loginView = loginView;

        loginView.setLoginButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = loginView.getEmail();
                String password = loginView.getPassword();

                loggedInUser = authenticate(email, password);
                if (loggedInUser != null) {
                    JOptionPane.showMessageDialog(loginView, "Login successful!");

                    // Kullanıcının rolüne göre yönlendirme
                    if ("librarian".equalsIgnoreCase(loggedInUser.getRole())) {
                        // Kullanıcı adı ve profil resmi için gerekli parametreyi oluşturma
                        ImageIcon profileImage = new ImageIcon("resources/images/profile-placeholder.jpg"); // Profil resmi için geçici bir resim
                        new LibraryAdminPanelView(loggedInUser.getName(), profileImage);
                        loginView.dispose(); // Mevcut login ekranını kapat
                    } else {
                        new StudentMain(loggedInUser);
                        loginView.dispose(); // Mevcut login ekranını kapat
                    }
                } else {
                    JOptionPane.showMessageDialog(loginView, "Invalid email or password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private User authenticate(String email, String password) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT id, name, email, role FROM users WHERE email = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
