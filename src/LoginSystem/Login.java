package LoginSystem;

import Supporting.BCrypt;
import Supporting.Database;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

/**
 * Created by u0861925 on 07/07/2019.
 */
public class Login extends Application {
    public Button btnLogin;
    public PasswordField pfPass;
    public TextField tfUser;

    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 300, 300));
        primaryStage.setResizable(false);

        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

    }

    public void Login(ActionEvent actionEvent) throws SQLException, IOException {
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
            String strSelect = "SELECT Password FROM Users WHERE Username = \"" + tfUser.getText() + "\"";
            System.out.println("The SQL statement is: " + strSelect + "\n");
            ResultSet result = stmt.executeQuery(strSelect);

            if (!result.next()){
                alertWarning("Invalid", "There is no username match in the database.");
            } else {
                String password = result.getString("Password");
                if (BCrypt.checkpw(pfPass.getText(), password))
                    System.out.println("It matches");
                else
                    System.out.println("It does not match");

            }
        }


    }

    public void alertWarning(String header, String content){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        ButtonType okButton = new ButtonType("OK");
        alert.getButtonTypes().setAll(okButton);
        alert.showAndWait();
    }
}
