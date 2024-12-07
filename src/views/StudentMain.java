package views;

import models.Book;
import models.User;
import states.BookState;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentMain extends JFrame {
    private BookState bookState;
    private User loggedInUser;

    public StudentMain(User user) {
        this.loggedInUser = user;
        this.bookState = new BookState(user.getId());

        setTitle("Student Main");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Hoş geldiniz mesajı
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName() + " (" + user.getRole() + ")");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Kitapları yükle
        List<Book> books = bookState.getBooks();

        // Panel içinde kitap kartları göstereceğiz
        JPanel booksPanel = new JPanel();
        booksPanel.setLayout(new GridLayout(0, 2, 10, 10));  // 2 sütunlu grid düzeni

        for (Book book : books) {
            // Kart paneli
            JPanel bookCard = new JPanel();
            bookCard.setLayout(new BoxLayout(bookCard, BoxLayout.Y_AXIS));
            bookCard.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            bookCard.setPreferredSize(new Dimension(250, 200));

            // Kitap başlığı
            JLabel titleLabel = new JLabel(book.getTitle());
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Kitap yazarı
            JLabel authorLabel = new JLabel("Author: " + book.getAuthor());
            authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Kitap durumu
            JLabel statusLabel = new JLabel("Status: " + book.getStatus());
            statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Aksiyon butonu
            JButton actionButton = new JButton(book.isLoaned() ? "İade Et" : "Ödünç Al");
            actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            actionButton.addActionListener(e -> {
                if (book.isLoaned()) {
                    if (bookState.returnBook(user.getId(), book)) {
                        JOptionPane.showMessageDialog(this, "Kitap başarıyla iade edildi.");
                        actionButton.setText("Ödünç Al");
                    } else {
                        JOptionPane.showMessageDialog(this, "Kitap iade edilemedi.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    if (bookState.loanBook(user.getId(), book)) {
                        JOptionPane.showMessageDialog(this, "Kitap başarıyla ödünç alındı.");
                        actionButton.setText("İade Et");
                    } else {
                        JOptionPane.showMessageDialog(this, "Kitap ödünç alınamadı.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Kartı oluştur ve ekle
            bookCard.add(titleLabel);
            bookCard.add(authorLabel);
            bookCard.add(statusLabel);
            bookCard.add(actionButton);

            // Kitap kartını ana paneldeki grid'e ekle
            booksPanel.add(bookCard);
        }

        JScrollPane scrollPane = new JScrollPane(booksPanel);

        // Layout düzenlemesi
        setLayout(new BorderLayout());
        add(welcomeLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

}