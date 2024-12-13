package factory;

import models.Book;
import models.Roman;
import models.Bilim;

public class BookFactory {

    public static Book createBook(int id, String title, String author, String status, boolean loaned, String type) {
        switch (type.toLowerCase()) {
            case "roman":
                return new Roman(id, title, author, status, loaned);
            case "bilim":
                return new Bilim(id, title, author, status, loaned);
            default:
                throw new IllegalArgumentException("Geçersiz kitap türü: " + type);
        }
    }
}