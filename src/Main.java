import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import views.LoginView;
import controllers.LoginController;
import controllers.RegisterController;
import views.RegisterView;

public class Main {
    public static void main(String[] args) {

        LoginView loginView = new LoginView();

        // LoginController'ı oluştur ve LoginView'i ona aktar
        LoginController loginController = new LoginController(loginView);
    }
}