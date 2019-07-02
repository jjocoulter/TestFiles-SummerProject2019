import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;

import static javafx.application.Application.launch;

/**
 * Created by u0861925 on 02/07/2019.
 */
public class RegisterTest extends Application {
    public TextField tfName;
    public TextField tfNick;
    public TextField tfEmail;
    public Button btnRegister;

    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("RegisterTest.fxml"));
        primaryStage.setTitle("Register");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false);

        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

    }

    public void doRegister(ActionEvent actionEvent) {
        try (
                Connection con = DriverManager.getConnection("jdbc:mysql://selene.hud.ac.uk:3306/u0861925",
                        "u0861925", "02jan90");

                Statement stmt = con.createStatement()
        ) {
            String getName = tfName.getText();
            String getNick = tfNick.getText();
            String getEmail = tfEmail.getText();
            stmt.executeUpdate("INSERT INTO TEST_users(name, nick, email) " + "VALUES ('" + getName + "', '" +
                    getNick + "', '" + getEmail + "')");
            String strSelect = "select id, name, nick, email from TEST_users";
            System.out.println("The SQL statement is: " + strSelect + "\n");

            ResultSet rset = stmt.executeQuery(strSelect);

            System.out.println("The records selected are:");
            int rowCount = 0;
            while (rset.next()) {
                int id = rset.getInt("id");
                String name = rset.getString("name");
                String nick = rset.getString("nick");
                String email = rset.getString("email");
                System.out.println(id + ", " + name + ", " + nick + ", " + email);
                ++rowCount;
            }
            System.out.println("Total number of record = " + rowCount);

        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

}
