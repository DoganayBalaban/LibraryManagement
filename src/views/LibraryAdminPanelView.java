package views;

import config.DatabaseConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LibraryAdminPanelView extends JFrame {

    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JPanel mainContentPanel;

    public LibraryAdminPanelView(String loggedInUserName, ImageIcon profileImage) {
        setTitle("Kütüphaneci Yönetim Paneli");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Profil Paneli
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel welcomeLabel = new JLabel("Hoşgeldiniz, " + loggedInUserName);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Profil fotoğrafını boyutlandırma
        Image profileImageScaled = profileImage.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        profileImage = new ImageIcon(profileImageScaled);

        JLabel profilePicLabel = new JLabel(profileImage);
        profilePanel.add(profilePicLabel);
        profilePanel.add(welcomeLabel);

        // Sol Menü Butonları
        JPanel leftMenuPanel = new JPanel();
        leftMenuPanel.setLayout(new GridLayout(6, 1, 10, 10));
        leftMenuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnBookManagement = new JButton("Kitap Yönetimi");
        JButton btnStudentManagement = new JButton("Öğrenci Yönetimi");
        JButton btnUserAuthorization = new JButton("Kullanıcı Yetkilendirme");
        JButton btnNotifications = new JButton("Bildirimler");

        leftMenuPanel.add(btnBookManagement);
        leftMenuPanel.add(btnStudentManagement);
        leftMenuPanel.add(btnUserAuthorization);
        leftMenuPanel.add(btnNotifications);

        // Ana İçerik Paneli
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(createBookManagementPanel(), BorderLayout.CENTER);

        // JSplitPane ile sol menü sabitlenir
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftMenuPanel, mainContentPanel);
        splitPane.setDividerLocation(200); // Sol menünün genişliği
        splitPane.setDividerSize(2); // Bölme çizgisi boyutu
        splitPane.setEnabled(false); // Bölme çizgisi taşınamaz
        add(profilePanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Buton İşlevsellikleri
        btnBookManagement.addActionListener(e -> switchPanel(createBookManagementPanel()));
        btnStudentManagement.addActionListener(e -> switchPanel(createStudentManagementPanel()));
        btnUserAuthorization.addActionListener(e -> switchPanel(createUserAuthorizationPanel()));
        btnNotifications.addActionListener(e -> switchPanel(createNotificationsPanel()));

        setVisible(true);
    }

    private JPanel createBookManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Başlık Ekle
        JLabel titleLabel = new JLabel("Kitap Yönetimi", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setPreferredSize(new Dimension(panel.getWidth(), 40));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Kitap Adı", "Yazar", "Durum", "Tür"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(bookTable);

        // Veritabanından kitapları çek
        loadBooksFromDatabase();

        panel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Ekle");
        JButton btnEdit = new JButton("Düzenle");
        JButton btnDelete = new JButton("Sil");

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);

        panel.add(actionPanel, BorderLayout.SOUTH);

        // Kitap ekleme butonunun işlevi
        btnAdd.addActionListener(e -> addBook());

        // Kitap düzenleme butonunun işlevi
        btnEdit.addActionListener(e -> editBook());

        // Kitap silme butonunun işlevi
        btnDelete.addActionListener(e -> deleteBook());

        return panel;
    }

    private void loadBooksFromDatabase() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT * FROM books";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String status = resultSet.getString("status");
                String tur = resultSet.getString("tur");

                // Tabloya ekle
                tableModel.addRow(new Object[]{id, title, author, status, tur});
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kitaplar alınırken bir hata oluştu! Hata: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBook() {
        JTextField txtTitle = new JTextField(15);
        JTextField txtAuthor = new JTextField(15);
        String[] statuses = {"Rafta", "Ödünçte", "Kayıp"};
        JComboBox<String> cmbStatus = new JComboBox<>(statuses);
        JTextField txtTur = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Kitap Adı:"));
        panel.add(txtTitle);
        panel.add(new JLabel("Yazar:"));
        panel.add(txtAuthor);
        panel.add(new JLabel("Durum:"));
        panel.add(cmbStatus);
        panel.add(new JLabel("Tür:"));
        panel.add(txtTur);

        int result = JOptionPane.showConfirmDialog(this, panel, "Yeni Kitap Ekle", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String title = txtTitle.getText();
            String author = txtAuthor.getText();
            String status = (String) cmbStatus.getSelectedItem();
            String tur = txtTur.getText();

            try {
                Connection connection = DatabaseConnection.getInstance().getConnection();
                String query = "INSERT INTO books (title, author, status, tur) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, title);
                statement.setString(2, author);
                statement.setString(3, status);
                statement.setString(4, tur);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Kitap başarıyla eklendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, title, author, status, tur});
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Kitap eklenirken bir hata oluştu! Hata: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String title = (String) tableModel.getValueAt(selectedRow, 1);
            String author = (String) tableModel.getValueAt(selectedRow, 2);
            String status = (String) tableModel.getValueAt(selectedRow, 3);
            String tur = (String) tableModel.getValueAt(selectedRow, 4);

            JTextField txtTitle = new JTextField(title, 15);
            JTextField txtAuthor = new JTextField(author, 15);
            String[] statuses = {"Rafta", "Ödünçte", "Kayıp"};
            JComboBox<String> cmbStatus = new JComboBox<>(statuses);
            cmbStatus.setSelectedItem(status);
            JTextField txtTur = new JTextField(tur, 10);

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.add(new JLabel("Kitap Adı:"));
            panel.add(txtTitle);
            panel.add(new JLabel("Yazar:"));
            panel.add(txtAuthor);
            panel.add(new JLabel("Durum:"));
            panel.add(cmbStatus);
            panel.add(new JLabel("Tür:"));
            panel.add(txtTur);

            int result = JOptionPane.showConfirmDialog(this, panel, "Kitap Düzenle", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String newTitle = txtTitle.getText();
                String newAuthor = txtAuthor.getText();
                String newStatus = (String) cmbStatus.getSelectedItem();
                String newTur = txtTur.getText();
                

                try {
                    Connection connection = DatabaseConnection.getInstance().getConnection();
                    String query = "UPDATE books SET title = ?, author = ?, status = ?, tur = ? WHERE id = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, newTitle);
                    statement.setString(2, newAuthor);
                    statement.setString(3, newStatus);
                    statement.setString(4, newTur);
                    statement.setInt(5, id);

                    int rowsUpdated = statement.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(this, "Kitap başarıyla güncellendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                        tableModel.setValueAt(newTitle, selectedRow, 1);
                        tableModel.setValueAt(newAuthor, selectedRow, 2);
                        tableModel.setValueAt(newStatus, selectedRow, 3);
                        tableModel.setValueAt(newTur, selectedRow, 4);
                    }
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Kitap güncellenirken bir hata oluştu! Hata: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen düzenlemek istediğiniz kitabı seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);

            int result = JOptionPane.showConfirmDialog(this, "Seçilen kitabı silmek istediğinizden emin misiniz?", "Kitap Sil", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    Connection connection = DatabaseConnection.getInstance().getConnection();
                    String query = "DELETE FROM books WHERE id = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setInt(1, id);

                    int rowsDeleted = statement.executeUpdate();
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(this, "Kitap başarıyla silindi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                        tableModel.removeRow(selectedRow);
                    }
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Kitap silinirken bir hata oluştu! Hata: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen silmek istediğiniz kitabı seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

private JPanel createStudentManagementPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    // Başlık Ekle
    JLabel titleLabel = new JLabel("Kullanıcı Yönetimi", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titleLabel.setPreferredSize(new Dimension(panel.getWidth(), 40));
    panel.add(titleLabel, BorderLayout.NORTH);

    String[] columnNames = {"ID", "Ad", "Rol", "Email", "Şifre"};
    DefaultTableModel studentTableModel = new DefaultTableModel(columnNames, 0);
    JTable studentTable = new JTable(studentTableModel);
    JScrollPane tableScrollPane = new JScrollPane(studentTable);

    // Veritabanından kullanıcıları çek
    loadStudentsFromDatabase(studentTableModel);

    panel.add(tableScrollPane, BorderLayout.CENTER);

    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton btnAdd = new JButton("Ekle");
    JButton btnEdit = new JButton("Düzenle");
    JButton btnDelete = new JButton("Sil");

    actionPanel.add(btnAdd);
    actionPanel.add(btnEdit);
    actionPanel.add(btnDelete);

    panel.add(actionPanel, BorderLayout.SOUTH);

    // Kullanıcı ekleme işlevi
    btnAdd.addActionListener(e -> addStudent(studentTableModel));

    // Kullanıcı düzenleme işlevi
    btnEdit.addActionListener(e -> editStudent(studentTableModel, studentTable));

    // Kullanıcı silme işlevi
    btnDelete.addActionListener(e -> deleteStudent(studentTableModel, studentTable));

    return panel;
}

private void loadStudentsFromDatabase(DefaultTableModel studentTableModel) {
    try {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        // Update the query to only fetch users with the role "student"
        String query = "SELECT id, name, role, email, password FROM users WHERE role = 'student'";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String role = resultSet.getString("role");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password"); // Mask the password
            String maskedPassword = "*".repeat(password.length());

            studentTableModel.addRow(new Object[]{id, name, role, email, maskedPassword});
        }

        connection.close();
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Kullanıcılar alınırken bir hata oluştu! Hata: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
    }
}

private void addStudent(DefaultTableModel studentTableModel) {
    // Form elemanları
    JTextField txtId = new JTextField(15); // Kullanıcıdan ID alınıyor
    JTextField txtName = new JTextField(15);
    JTextField txtRole = new JTextField("student"); // Varsayılan "student" rolü
    txtRole.setEditable(false); // Rol alanı düzenlenemez
    JTextField txtEmail = new JTextField(15);
    JTextField txtPassword = new JPasswordField(15); // Şifre için JPasswordField kullanımı

    // Form düzeni
    JPanel panel = new JPanel(new GridLayout(5, 2));
    panel.add(new JLabel("ID:"));
    panel.add(txtId);
    panel.add(new JLabel("Ad:"));
    panel.add(txtName);
    panel.add(new JLabel("Rol:"));
    panel.add(txtRole);
    panel.add(new JLabel("Email:"));
    panel.add(txtEmail);
    panel.add(new JLabel("Şifre:"));
    panel.add(txtPassword);

    // Kullanıcı onayı
    int result = JOptionPane.showConfirmDialog(null, panel, "Yeni Kullanıcı Ekle", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String role = txtRole.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(((JPasswordField) txtPassword).getPassword()).trim();

        // Form doğrulama
        if (id.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Veritabanı bağlantısı
            Connection connection = DatabaseConnection.getInstance().getConnection();

            // Sorgu
            String query = "INSERT INTO users (id, name, role, email, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            // Parametrelerin atanması
            statement.setString(1, id);
            statement.setString(2, name);
            statement.setString(3, role);
            statement.setString(4, email);
            statement.setString(5, password); // Şifreyi hashlemelisiniz (örneğin bcrypt)

            // Sorgunun çalıştırılması
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Kullanıcı başarıyla eklendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                studentTableModel.addRow(new Object[]{id, name, role, email, "*".repeat(password.length())});
            }

            // Kaynakların kapatılması
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Kullanıcı eklenirken bir hata oluştu! Hata: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}





private void editStudent(DefaultTableModel studentTableModel, JTable studentTable) {
    int selectedRow = studentTable.getSelectedRow();
    if (selectedRow != -1) {
        int id = (int) studentTableModel.getValueAt(selectedRow, 0);
        String name = (String) studentTableModel.getValueAt(selectedRow, 1);
        String role = (String) studentTableModel.getValueAt(selectedRow, 2);
        String email = (String) studentTableModel.getValueAt(selectedRow, 3);

        JTextField txtName = new JTextField(name, 15);
        JTextField txtRole = new JTextField(role); // Set to the current role
        txtRole.setEditable(false); // Make the role field non-editable
        JTextField txtEmail = new JTextField(email, 15);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Ad:"));
        panel.add(txtName);
        panel.add(new JLabel("Rol:"));
        panel.add(txtRole);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);

        int result = JOptionPane.showConfirmDialog(null, panel, "Kullanıcı Düzenle", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newName = txtName.getText();
            String newEmail = txtEmail.getText();

            try {
                Connection connection = DatabaseConnection.getInstance().getConnection();
                String query = "UPDATE users SET name = ?, role = ?, email = ? WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, newName);
                statement.setString(2, role); // Keep the original role as "student"
                statement.setString(3, newEmail);
                statement.setInt(4, id);

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(null, "Kullanıcı başarıyla güncellendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    studentTableModel.setValueAt(newName, selectedRow, 1);
                    studentTableModel.setValueAt(newEmail, selectedRow, 3);
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Kullanıcı güncellenirken bir hata oluştu! Hata: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    } else {
        JOptionPane.showMessageDialog(null, "Lütfen düzenlemek istediğiniz kullanıcıyı seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
    }
}

private void deleteStudent(DefaultTableModel studentTableModel, JTable studentTable) {
    int selectedRow = studentTable.getSelectedRow();
    if (selectedRow != -1) {
        int id = (int) studentTableModel.getValueAt(selectedRow, 0);

        int result = JOptionPane.showConfirmDialog(null, "Seçilen kullanıcıyı silmek istediğinizden emin misiniz?", "Kullanıcı Sil", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            try {
                Connection connection = DatabaseConnection.getInstance().getConnection();
                String query = "DELETE FROM users WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, id);

                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, "Kullanıcı başarıyla silindi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    studentTableModel.removeRow(selectedRow);
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Kullanıcı silinirken bir hata oluştu! Hata: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    } else {
        JOptionPane.showMessageDialog(null, "Lütfen silmek istediğiniz kullanıcıyı seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
    }
}



private JPanel createUserAuthorizationPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    // Başlık Ekle
    JLabel titleLabel = new JLabel("Kullanıcı Yetkilendirme", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titleLabel.setPreferredSize(new Dimension(panel.getWidth(), 40));
    panel.add(titleLabel, BorderLayout.NORTH);

    // Kullanıcı Listesi Tablosu
    String[] columnNames = {"ID", "Ad", "Rol", "Email"};
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
    JTable userTable = new JTable(tableModel);
    JScrollPane scrollPane = new JScrollPane(userTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Rol Güncelleme Formu
    JPanel updatePanel = new JPanel(new GridLayout(3, 2, 10, 10));
    JTextField txtUserId = new JTextField();
    txtUserId.setEditable(false); // ID alanını sadece okunabilir yap
    JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"librarian", "student"});
    JButton btnUpdateRole = new JButton("Rolü Güncelle");

    updatePanel.add(new JLabel("Kullanıcı ID:"));
    updatePanel.add(txtUserId);
    updatePanel.add(new JLabel("Yeni Rol:"));
    updatePanel.add(roleComboBox);
    updatePanel.add(new JLabel(""));
    updatePanel.add(btnUpdateRole);

    panel.add(updatePanel, BorderLayout.SOUTH);

    // Tabloyu Güncelle
    loadUsersFromDatabase(tableModel);

    // Tablo Seçim İşlemi
    userTable.getSelectionModel().addListSelectionListener(e -> {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String userId = userTable.getValueAt(selectedRow, 0).toString();
            String currentRole = userTable.getValueAt(selectedRow, 2).toString();
            txtUserId.setText(userId);
            roleComboBox.setSelectedItem(currentRole);
        }
    });

    // Rol Güncelleme İşlemi
    btnUpdateRole.addActionListener(e -> {
        String userId = txtUserId.getText().trim();
        String newRole = roleComboBox.getSelectedItem().toString();

        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Lütfen bir kullanıcı seçin!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String query = "UPDATE users SET role = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, newRole);
            statement.setString(2, userId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(panel, "Kullanıcı rolü başarıyla güncellendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                loadUsersFromDatabase(tableModel); // Tabloyu güncelle
            } else {
                JOptionPane.showMessageDialog(panel, "Kullanıcı bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Rol güncelleme işleminde bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    });

    return panel;
}

// Veritabanından Kullanıcıları Tabloya Yükleme
private void loadUsersFromDatabase(DefaultTableModel tableModel) {
    tableModel.setRowCount(0); // Tabloyu sıfırla
    try {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        String query = "SELECT id, name, role, email FROM users";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String name = resultSet.getString("name");
            String role = resultSet.getString("role");
            String email = resultSet.getString("email");
            tableModel.addRow(new Object[]{id, name, role, email});
        }

        resultSet.close();
        statement.close();
        connection.close();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Kullanıcıları yüklerken bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
    }
}



    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Başlık Ekle
        JLabel titleLabel = new JLabel("Bildirimler", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setPreferredSize(new Dimension(panel.getWidth(), 40));
        panel.add(titleLabel, BorderLayout.NORTH);

        panel.add(new JLabel("Bildirimler Paneli"));
        return panel;
    }

    private void switchPanel(JPanel newPanel) {
        mainContentPanel.removeAll();
        mainContentPanel.add(newPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryAdminPanelView("Kütüphaneci Adı Soyadı", new ImageIcon("resources/images/profile-placeholder.jpg")));
    }
}
