package dao;

import config.DatabaseConnection;
import factory.BookFactory;
import models.Bilim;
import models.Book;
import models.Roman;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public List<Book> getAllBooksWithLoanStatus(int userId) {
        List<Book> books = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            String query = """
                SELECT b.id, b.title, b.author, b.status,b.tur,
                       CASE WHEN l.id IS NOT NULL THEN TRUE ELSE FALSE END AS loaned
                FROM books b
                LEFT JOIN loans l ON b.id = l.book_id AND l.user_id = ?
            """;
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String status = resultSet.getString("status");
                boolean loaned = resultSet.getBoolean("loaned");
                String tur = resultSet.getString("tur"); // Kitap t

                Book book;
                if (tur.equals("Roman")) {
                    book = new Roman(id, title, author, status, loaned);  // Roman türü
                } else if (tur.equals("Bilim")) {
                    book = new Bilim(id, title, author, status, loaned);  // Bilim türü
                } else {
                    book = new Book(id, title, author, status, loaned) {
                        @Override
                        public String getType() {
                            return tur;
                        }
                    };  // Standart Book, gerekli değilse bu satır kaldırılabilir
                }

                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return books;
    }

    public boolean loanBook(int userId, int bookId) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();

            // Bağlantı açık mı diye kontrol et
            if (connection == null || connection.isClosed()) {
                System.out.println("Veritabanı bağlantısı kapalı, yenisi açılıyor.");
                connection = DatabaseConnection.getInstance().getConnection();  // Yeniden bağlantıyı al
            }

            String query = "INSERT INTO loans (user_id, book_id, loan_date) VALUES (?, ?, NOW())";
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setInt(2, bookId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean returnBook(int userId, int bookId) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();

            // Bağlantı açık mı diye kontrol et
            if (connection == null || connection.isClosed()) {
                System.out.println("Veritabanı bağlantısı kapalı, yenisi açılıyor.");
                connection = DatabaseConnection.getInstance().getConnection();  // Yeniden bağlantıyı al
            }

            String query = "DELETE FROM loans WHERE user_id = ? AND book_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setInt(2, bookId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> fetchBookTypes() {
        List<String> bookTypes = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT DISTINCT tur FROM books"; // "tur" kolonunu alıyoruz
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                bookTypes.add(resultSet.getString("tur"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bookTypes;
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();

            // Anahtar kelime kontrolü
            String query;
            if (keyword == null || keyword.trim().isEmpty()) {
                // Tüm kitapları döndüren sorgu
                query = """
                SELECT b.id, b.title, b.author, b.status, b.tur
                FROM books b
            """;
                statement = connection.prepareStatement(query);
            } else {
                // Anahtar kelime ile arama yapan sorgu
                query = """
                SELECT b.id, b.title, b.author, b.status, b.tur
                FROM books b
                WHERE b.title LIKE ?
            """;
                statement = connection.prepareStatement(query);
                statement.setString(1, "%" + keyword.trim() + "%");
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String status = resultSet.getString("status");
                String tur = resultSet.getString("tur");

                Book book;
                if ("Roman".equals(tur)) {
                    book = new Roman(id, title, author, status, false);
                } else if ("Bilim".equals(tur)) {
                    book = new Bilim(id, title, author, status, false);
                } else {
                    book = new Book(id, title, author, status, false) {
                        @Override
                        public String getType() {
                            return tur;
                        }
                    };
                }

                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return books;
    }
}