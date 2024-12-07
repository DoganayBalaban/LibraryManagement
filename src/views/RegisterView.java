package views;

import controllers.LoginController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegisterView extends JFrame {
    // Swing bileşenleri
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox studentCheckBox;
    private JCheckBox librarianCheckBox;
    private JButton kayitBtn;
    private JButton loginBtn;

    // Constructor
    public RegisterView() {
        setTitle("Kayıt Ol");
        setSize(400, 350); // Pencere boyutunu biraz büyüttük
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel oluştur
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Dikey sıralama

        // Email ve Şifre alanlarını ekle
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Sol tarafa hizalama
        namePanel.add(new JLabel("İsim:"));
        nameField = new JTextField(20);// Genişliği belirledik
        namePanel.add(nameField);
        panel.add(namePanel);

        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Sol tarafa hizalama
        emailPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20); // Genişliği belirledik
        emailPanel.add(emailField);
        panel.add(emailPanel);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Sol tarafa hizalama
        passwordPanel.add(new JLabel("Şifre:"));
        passwordField = new JPasswordField(20); // Genişliği belirledik
        passwordPanel.add(passwordField);
        panel.add(passwordPanel);

        JPanel typePanel = new JPanel();
        typePanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Sol tarafa hizalama
        typePanel.add(new JLabel("Tür:"));

        // "Student" ve "Librarian" seçeneklerini eklemek için checkbox'lar
        studentCheckBox = new JCheckBox("Student");
        librarianCheckBox = new JCheckBox("Librarian");

        // Sadece birini seçebilmek için listener ekleyelim
        studentCheckBox.addActionListener(e -> {
            if (studentCheckBox.isSelected()) {
                librarianCheckBox.setSelected(false);
            }
        });

        librarianCheckBox.addActionListener(e -> {
            if (librarianCheckBox.isSelected()) {
                studentCheckBox.setSelected(false);
            }
        });

        typePanel.add(studentCheckBox);
        typePanel.add(librarianCheckBox);
        panel.add(typePanel);

        kayitBtn = new JButton("Kayıt Ol");
        panel.add(kayitBtn);
        loginBtn = new JButton("Giriş Yap");
        panel.add(loginBtn);

        loginBtn.addActionListener(e -> {
            LoginView loginView = new LoginView();
            LoginController loginController = new LoginController(loginView);
            loginView.setVisible(true);
            this.setVisible(false);
        });


                    // Paneli pencereye ekle
        add(panel);

        // Pencereyi görünür yap
        setVisible(true);
    }

    // Email ve Şifre alanlarına erişim için getter'lar
    public String getUsername() {
        return nameField.getText();
    }
    public String getEmail() {
        return emailField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    // Kullanıcının seçtiği türü döndürme
    public String getUserType() {
        if (studentCheckBox.isSelected()) {
            return "Student";
        } else if (librarianCheckBox.isSelected()) {
            return "Librarian";
        } else {
            return ""; // Herhangi biri seçilmediyse boş string döner
        }
    }

    // Register butonuna tıklama olayı için listener ekleme
    public void setRegisterButtonListener(ActionListener listener) {
        kayitBtn.addActionListener(listener);
    }
}