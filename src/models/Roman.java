package models;

public class Roman extends Book {

    public Roman(int id, String title, String author, String status, boolean loaned) {
        super(id, title, author, status, loaned);
    }

    @Override
    public String getType() {
        return "Roman";
    }
}