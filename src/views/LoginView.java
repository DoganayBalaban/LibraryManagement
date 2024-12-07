package views;

import controllers.RegisterController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    // Swing bileşenleri
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    // Constructor
    public LoginView() {
        setTitle("Giriş Yap");
        setSize(400, 250); // Pencere boyutunu artırdık
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel oluştur
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout()); // Daha esnek bir layout olan GridBagLayout kullanıldı
        GridBagConstraints gbc = new GridBagConstraints(); // Komponentlerin konumlandırılmasını sağlayacak

        // Email alanı
        gbc.gridx = 0; // X ekseninde 0. sütunda
        gbc.gridy = 0; // Y ekseninde 0. satırda
        gbc.insets = new Insets(10, 10, 5, 10); // Alanlar arasında boşluk (top, left, bottom, right)
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1; // X ekseninde 1. sütunda
        gbc.gridy = 0; // Y ekseninde 0. satırda
        gbc.insets = new Insets(10, 10, 5, 10);
        emailField = new JTextField(20); // Genişlik ayarlandı
        panel.add(emailField, gbc);

        // Şifre alanı
        gbc.gridx = 0; // X ekseninde 0. sütunda
        gbc.gridy = 1; // Y ekseninde 1. satırda
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(new JLabel("Şifre:"), gbc);

        gbc.gridx = 1; // X ekseninde 1. sütunda
        gbc.gridy = 1; // Y ekseninde 1. satırda
        gbc.insets = new Insets(5, 10, 5, 10);
        passwordField = new JPasswordField(20); // Genişlik ayarlandı
        panel.add(passwordField, gbc);

        // Login butonu
        gbc.gridx = 1; // X ekseninde 1. sütunda
        gbc.gridy = 2; // Y ekseninde 2. satırda
        gbc.insets = new Insets(10, 10, 10, 10);
        loginButton = new JButton("Giriş Yap");
        panel.add(loginButton, gbc);

        // Kayıt Ol butonu
        gbc.gridx = 1; // X ekseninde 1. sütunda
        gbc.gridy = 3; // Y ekseninde 3. satırda
        gbc.insets = new Insets(5, 10, 10, 10);
        registerButton = new JButton("Kayıt Ol");
        panel.add(registerButton, gbc);

        // Register butonuna action listener ekle
        registerButton.addActionListener(e -> {
                    RegisterView registerView = new RegisterView(); // RegisterView'yi aç
                    RegisterController registerController = new RegisterController(registerView); // RegisterController'ı başlat
                    registerView.setVisible(true);
                    this.setVisible(false);
                });

        // Paneli pencereye ekle
        add(panel);

        // Pencereyi görünür yap
        setVisible(true);
    }

    // Email ve Şifre alanlarına erişim için getter'lar
    public String getEmail() {
        return emailField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    // Login butonuna tıklama olayı için listener ekleme
    public void setLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }
}