/*
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
*/
package views;

import controllers.BookController;
import models.Book;
import models.User;
import states.BookState;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentMain extends JFrame {
    private BookState bookState;
    private User loggedInUser;
    private JPanel booksPanel; // Kitapları göstermek için ana panel
    private JTextField txtSearch; // Arama çubuğu

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

        // Arama çubuğu
        JPanel searchPanel = new JPanel(new FlowLayout());
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Ara");
        searchPanel.add(new JLabel("Kitap Ara:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // Kitapları gösterecek ana panel
        booksPanel = new JPanel();
        booksPanel.setLayout(new GridLayout(0, 2, 10, 10)); // 2 sütunlu grid düzeni
        loadBooks(bookState.getBooks()); // Tüm kitapları yükle

        // Arama butonuna aksiyon ekle
        btnSearch.addActionListener(e -> searchBooks());

        JScrollPane scrollPane = new JScrollPane(booksPanel);

        // Layout düzenlemesi
        setLayout(new BorderLayout());
        add(welcomeLabel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // Kitapları yüklemek için bir metod
    private void loadBooks(List<Book> books) {
        booksPanel.removeAll(); // Mevcut kitapları temizle

        for (Book book : books) {
            // Kitap kartını oluştur
            JPanel bookCard = createBookCard(book);
            booksPanel.add(bookCard);
        }

        booksPanel.revalidate();
        booksPanel.repaint();
    }

    // Kitap kartı oluşturma metodu
    private JPanel createBookCard(Book book) {
        JPanel bookCard = new JPanel();
        bookCard.setLayout(new BoxLayout(bookCard, BoxLayout.Y_AXIS));
        bookCard.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        bookCard.setPreferredSize(new Dimension(250, 200));

        JLabel titleLabel = new JLabel(book.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel authorLabel = new JLabel("Author: " + book.getAuthor());
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel("Status: " + book.getStatus());
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton actionButton = new JButton(book.isLoaned() ? "İade Et" : "Ödünç Al");
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionButton.addActionListener(e -> handleBookAction(book, actionButton));

        bookCard.add(titleLabel);
        bookCard.add(authorLabel);
        bookCard.add(statusLabel);
        bookCard.add(actionButton);

        return bookCard;
    }

    // Kitap işlemlerini yönetmek için metod
    private void handleBookAction(Book book, JButton actionButton) {
        if (book.isLoaned()) {
            if (bookState.returnBook(loggedInUser.getId(), book)) {
                JOptionPane.showMessageDialog(this, "Kitap başarıyla iade edildi.");
                actionButton.setText("Ödünç Al");
            } else {
                JOptionPane.showMessageDialog(this, "Kitap iade edilemedi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (bookState.loanBook(loggedInUser.getId(), book)) {
                JOptionPane.showMessageDialog(this, "Kitap başarıyla ödünç alındı.");
                actionButton.setText("İade Et");
            } else {
                JOptionPane.showMessageDialog(this, "Kitap ödünç alınamadı.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Arama işlemi
    private void searchBooks() {
        String keyword = txtSearch.getText().trim();
        BookController bookController = new BookController();

        // Eğer anahtar kelime boşsa, tüm kitapları al
        List<Book> books;
        if (keyword.isEmpty()) {
            books = bookController.getBooksForUser(loggedInUser.getId()); // Tüm kitapları al
        } else {
            // Anahtar kelime ile arama yap
            books = bookController.searchBooks(keyword);
        }

        loadBooks(books); // Sonuçları yükle
    }
}