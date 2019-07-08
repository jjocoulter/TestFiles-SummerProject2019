package GameInfo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by u0861925 on 08/07/2019.
 */
public class GameInfo extends Application {

    public Button btnSearch;
    public TextField tfSearch;
    public AnchorPane paneMain;
    public ListView lvResults;
    public Button btnResultSelect;

    private HashMap<String, String> games = new HashMap();
    private ObservableList<String> results = FXCollections.observableArrayList();
    private String selectedGame;

    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("GameInfo.fxml"));
        primaryStage.setTitle("Register");
        primaryStage.setScene(new Scene(root, 600, 800));
        primaryStage.setResizable(false);

        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

    }


    public void doSearch(ActionEvent actionEvent) throws Exception {
        results.clear();
        games.clear();
        search(tfSearch.getText());
        results.addAll(games.keySet());

        lvResults.setItems(results);
        lvResults.getSelectionModel().select(0);
        lvResults.getFocusModel().focus(0);

        lvResults.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                selectedGame = String.valueOf(lvResults.getSelectionModel().getSelectedItem());
                System.out.println(selectedGame);
            }
        });

    }

    private void search(String searchTerm) throws Exception{
        HttpClient client = HttpClientBuilder.create().build();

        HttpPost post = new HttpPost("https://api-v3.igdb.com/games/");
        post.setHeader("user-key", "ba566df70dd39c35dc2edeea0cbd7838");
        post.setEntity(new StringEntity("search \"" + searchTerm + "\"; fields name, id, popularity; limit 30;"));

        HttpResponse response = client.execute(post);

        InputStream ips  = response.getEntity().getContent();
        BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
        if(response.getStatusLine().getStatusCode()!= HttpStatus.SC_OK)
        {
            throw new Exception(response.getStatusLine().getReasonPhrase());
        }
        String s;
        String id = "";
        while(true) {

            s = buf.readLine();
            System.out.println(s);
            if(s==null || s.length()==0)
                break;
            if (s.contains("name")){
                String[] parts = s.split(":", 2);
                parts[1] = parts[1].replace("\"", "");
                parts[1] = parts[1].replaceFirst("\\s", "");
                String name = parts[1];
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
    }

    public void doResultSelect(ActionEvent actionEvent) {
        String getID = games.get(selectedGame);
        System.out.println(getID);
    }
}
