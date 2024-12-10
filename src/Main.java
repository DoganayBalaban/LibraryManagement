import controllers.LoginController;
import views.LoginView;

public class Main {
    public static void main(String[] args) {

        LoginView loginView = new LoginView();

        // LoginController'ı oluştur ve LoginView'i ona aktar
        LoginController loginController = new LoginController(loginView);
    }
}