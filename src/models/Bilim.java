package models;

public class Bilim extends Book {

    public Bilim(int id, String title, String author, String status, boolean loaned) {
        super(id, title, author, status, loaned);
    }

    @Override
    public String getType() {
        return "Bilim";
    }
}