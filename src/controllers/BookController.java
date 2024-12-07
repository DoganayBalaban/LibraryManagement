package controllers;

import dao.BookDAO;
import models.Book;

import java.util.List;

public class BookController {
    private BookDAO bookDAO;

    public BookController() {
        this.bookDAO = new BookDAO();
    }

    // Kitapları getir
    public List<Book> getBooksForUser(int userId) {
        return bookDAO.getAllBooksWithLoanStatus(userId);
    }

    // Kitap ödünç alma işlemi
    public boolean loanBook(int userId, int bookId) {
        return bookDAO.loanBook(userId, bookId);
    }

    // Kitap iade etme işlemi
    public boolean returnBook(int userId, int bookId) {
        return bookDAO.returnBook(userId, bookId);
    }
}