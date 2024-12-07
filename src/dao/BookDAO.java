package dao;

import config.DatabaseConnection;
import models.Book;

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
                SELECT b.id, b.title, b.author, b.status, 
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

                books.add(new Book(id, title, author, status, loaned));
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
}