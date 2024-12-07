package models;

public class Book {
    private int id;
    private String title;
    private String author;
    private String status; // 'Available', 'Borrowed', 'Lost'
    private boolean loaned; // Ödünç alınıp alınmadığını tutar

    public Book(int id, String title, String author, String status, boolean loaned) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.status = status;
        this.loaned = loaned;
    }

    // Getter'lar ve Setter'lar
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getStatus() {
        return status;
    }

    public boolean isLoaned() {
        return loaned;
    }

    public void setLoaned(boolean loaned) {
        this.loaned = loaned;
    }
}