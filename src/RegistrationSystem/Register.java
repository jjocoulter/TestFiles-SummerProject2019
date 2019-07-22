package RegistrationSystem;

import Supporting.BCrypt;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by u0861925 on 07/07/2019.
 */
public class Register extends Application {
    public TextField tfName;
    public TextField tfUsername;
    public TextField tfEmail;
    public PasswordField pfPass1;
    public PasswordField pfPass2;
    public Button btnRegister;

    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
        primaryStage.setTitle("Register");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false);

        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

    }

    public void Register(ActionEvent actionEvent) throws SQLException {
        if (VerifyInformation()){
            try (
                    Connection con = DriverManager.getConnection("jdbc:mysql://selene.hud.ac.uk:3306/u0861925",
                            "u0861925", "02jan90");

                    Statement stmt = con.createStatement()
            ) {
                String getName = tfName.getText();
                String getNick = tfUsername.getText();
                String getEmail = tfEmail.getText();
                String getPassword = BCrypt.hashpw(pfPass1.getText(), BCrypt.gensalt(12));
                stmt.executeUpdate("INSERT INTO Users(Name, Username, Email, Password) " + "VALUES ('" + getName +
                        "', '" + getNick + "', '" + getEmail + "', '" + getPassword + "')");

            }
            tfName.clear();
            tfUsername.clear();
            tfEmail.clear();
            pfPass1.clear();
            pfPass2.clear();
        }
    }

    public boolean VerifyInformation(){
        if (tfName.getText().isEmpty() || tfUsername.getText().isEmpty() || tfEmail.getText().isEmpty() ||
                pfPass1.getText().isEmpty() || pfPass2.getText().isEmpty()){
            alertWarning("Empty field.", "Please ensure all fields are filled in.");
            return false;
        } else if (!tfName.getText().matches("^[\\p{L} .'-]+$")){
            alertWarning("Invalid name", "Please enter a full, valid name.");
            return false;
        } else if (!tfUsername.getText().matches("^[\\p{L} .'-]+$")){
            alertWarning("Invalid username", "Please enter a valid username.");
            return false;
        } else if (!validateEmailAddress(tfEmail.getText())){
            alertWarning("Invalid e-mail", "Please enter a valid e-mail address.");
            return false;
        } else if(pfPass1.getText().length() < 6){
            alertWarning("Invalid password.", "Password must be at least 6 characters.");
            return false;
        } else if(!pfPass1.getText().equals(pfPass2.getText())) {
            alertWarning("Password error", "Passwords do not match.");
            return false;
        } else {
            return true;
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

    private boolean validateEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
