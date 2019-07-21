package GameRequests;

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
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        HttpPost post = new HttpPost("https://api-v3.igdb.com/games/");
        post.setHeader("user-key", "ba566df70dd39c35dc2edeea0cbd7838");
        post.setEntity(new StringEntity("search \"" + tfGame.getText() + "\"; fields name, id; limit 30;"));

        HttpClient client = HttpClientBuilder.create().build();

        HttpResponse response = client.execute(post);


        InputStream ips  = response.getEntity().getContent();
        BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
        if(response.getStatusLine().getStatusCode()!= HttpStatus.SC_OK)
        {
            throw new Exception(response.getStatusLine().getReasonPhrase());
        }

        results.clear();
        games.clear();
        String s;
        String id = "";
        while(true) {

            s = buf.readLine();
            System.out.println(s);
            if(s==null || s.length()==0)
                break;
            if (s.contains("name")){
                String[] parts = s.split(":", 2);
                String name = parts[1];
                name = name.substring(2);
                name = name.substring(0, (name.length() - 1));
                games.put(name, id);
            } else if (s.contains("id")){
                String[] parts = s.split(":");
                parts[1] = parts[1].replaceAll("\\s", "");
                parts[1] = parts[1].replaceAll(",", "");
                id = parts[1];
            }
        }
        buf.close();
        ips.close();
        System.out.println(games.toString());

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
            String getUser = tfUser.getText();
            String getGame = games.get(selectedGame);
            stmt.executeUpdate("INSERT INTO Requests(Requester, Game, Date) " + "VALUES ('" + getUser + "', '" +
                    getGame + "', CURDATE())");
        }
    }

    public void FillRequest(ActionEvent actionEvent) {
    }
}
