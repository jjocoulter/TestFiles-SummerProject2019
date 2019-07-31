import Supporting.Database;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;


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

    public void doRegister(ActionEvent actionEvent) throws IOException {
        Database database;
        ObjectMapper mapper = new ObjectMapper();
        try(InputStream fileStream = new FileInputStream("src\\database.json")) {
            database = mapper.readValue(fileStream, Database.class);
        }
        try (
                Connection con = DriverManager.getConnection("jdbc:mysql://" + database.url,
                        database.username, database.password);

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
