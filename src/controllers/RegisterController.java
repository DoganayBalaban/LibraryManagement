package controllers;

import config.DatabaseConnection;
import views.RegisterView;
import views.StudentMain;
import models.User;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterController {
    private RegisterView registerView;

    public RegisterController(RegisterView registerView) {
        this.registerView = registerView;

        // Register butonuna listener ekle
        this.registerView.setRegisterButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = registerView.getUsername();
                String email = registerView.getEmail();
                String password = registerView.getPassword();
                String role = registerView.getUserType();

                User newUser = register(username, role, email, password);
                if (newUser != null) {
                    JOptionPane.showMessageDialog(registerView, "Kayıt Olundu!");

                    // Öğrenci ana ekranına geç
                    new StudentMain(newUser);
                    registerView.dispose();
                } else {
                    JOptionPane.showMessageDialog(registerView, "Bir sorun var", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Kayıt işlemi
    private User register(String username, String role, String email, String password) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Veritabanı bağlantısı al
            connection = DatabaseConnection.getInstance().getConnection();

            // Kullanıcıyı veritabanına ekle
            String query = "INSERT INTO users (name, role, email, password) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, username);
            statement.setString(2, role);
            statement.setString(3, email);
            statement.setString(4, password);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // Yeni eklenen kullanıcıyı almak için oluşturulan ID'yi al
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    return new User(userId, username, email, role); // User nesnesi oluştur
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}