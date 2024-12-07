package states;

import controllers.BookController;
import models.Book;

import java.util.List;

public class BookState {
    private List<Book> books;
    private BookController bookController;

    public BookState(int userId) {
        this.bookController = new BookController();
        this.books = bookController.getBooksForUser(userId);
    }

    // Kitap listesini döndür
    public List<Book> getBooks() {
        return books;
    }

    // Ödünç al işlemi
    public boolean loanBook(int userId, Book book) {
        if (bookController.loanBook(userId, book.getId())) {
            book.setLoaned(true);
            return true;
        }
        return false;
    }

    // İade et işlemi
    public boolean returnBook(int userId, Book book) {
        if (bookController.returnBook(userId, book.getId())) {
            book.setLoaned(false);
            return true;
        }
        return false;
    }
}