package GameRequests;

import Supporting.BCrypt;
import Supporting.Methods;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.*;
import java.util.*;

/**
 * Created by u0861925 on 21/07/2019.
 */
public class GameRequests extends Application{
    public AnchorPane paneInitial;
    public Button btnMakeRequest;
    public Button btnShowRequests;
    public AnchorPane paneMakeRequest;
    public AnchorPane paneShowRequests;
    public TextField tfUser;
    public TextField tfGame;
    public Button btnSearch;
    public Button btnRequest;
    public ListView lvGames;
    public TableView tvRequests;
    public Button btnFillRequest;
    public TextField tfFillUser;
    public TableColumn tcRequester;
    public TableColumn tcGame;
    public TableColumn tcDate;

    private HashMap<String, String> games = new HashMap();
    private ObservableList<String> results = FXCollections.observableArrayList();
    private String selectedGame;
    private ObservableList data = FXCollections.observableArrayList();
    private Methods methods = new Methods();

    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("GameRequests.fxml"));
        primaryStage.setTitle("Requests");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setResizable(false);

        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

    }

    public void MakeRequest(ActionEvent actionEvent) {
        paneInitial.setVisible(false);
        paneShowRequests.setVisible(false);
        paneMakeRequest.setVisible(true);
    }

    public void ShowRequests(ActionEvent actionEvent) throws Exception {
        paneInitial.setVisible(false);
        paneShowRequests.setVisible(true);
        paneMakeRequest.setVisible(false);

        try (
                Connection con = DriverManager.getConnection("jdbc:mysql://selene.hud.ac.uk:3306/u0861925",
                        "u0861925", "02jan90");

                Statement stmt = con.createStatement()
        ) {
            ResultSet rset = stmt.executeQuery("SELECT Requester, Game, Date FROM Requests");

            while (rset.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rset.getMetaData().getColumnCount(); i++) {
                    row.add(rset.getString(i));
                }
                data.add(row);
                System.out.println(data.toString());

            }
            tvRequests.setItems(data);
        }
    }

    public void DoSearch(ActionEvent actionEvent) throws Exception {

        results.clear();
        games.clear();

        games = methods.findGames("games", "search \"" + tfGame.getText() +
                "\"; fields name, id; limit 30;");

        results.addAll(games.keySet());

        lvGames.setItems(results);
        lvGames.getSelectionModel().select(0);
        lvGames.getFocusModel().focus(0);

        lvGames.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                selectedGame = String.valueOf(lvGames.getSelectionModel().getSelectedItem());
                System.out.println(selectedGame);
            }
        });
    }

    public void RequestAction(ActionEvent actionEvent) throws SQLException {
        try (
                Connection con = DriverManager.getConnection("jdbc:mysql://selene.hud.ac.uk:3306/u0861925",
                        "u0861925", "02jan90");

                Statement stmt = con.createStatement()
        ) {
            String strSelect = "SELECT * FROM Users WHERE ID = \"" + tfUser.getText() + "\"";
            ResultSet result = stmt.executeQuery(strSelect);

            if (!result.next()){
                alertWarning("Invalid", "There is no ID match in the database.");
            } else {
                String getUser = tfUser.getText();
                String getGame = games.get(selectedGame);
                stmt.executeUpdate("INSERT INTO Requests(Requester, Game, Date) " + "VALUES ('" + getUser + "', '"
                        + getGame + "', CURDATE())");
            }
        }
    }

    public void FillRequest(ActionEvent actionEvent) {
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
