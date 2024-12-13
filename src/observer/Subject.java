package observer;

import observer.Observer;

public interface Subject {
    void addObserver(Observer observer);  // Observer (kullanıcı) ekleme
    void removeObserver(Observer observer); // Observer (kullanıcı) çıkarma
    void notifyObservers(String message); // Tüm dinleyicilere bildirim gönderme
}