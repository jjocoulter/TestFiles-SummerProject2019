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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.util.HashMap;

/**
 * Created by u0861925 on 08/07/2019.
 */
public class GameInfo extends Application {

    public Button btnSearch;
    public TextField tfSearch;
    public AnchorPane paneResults;
    public ListView lvResults;
    public Button btnResultSelect;
    public AnchorPane paneGInfo;
    public ImageView ivCover;
    public Label lblTitle;
    public Label lblSummary;

    private HashMap<String, String> games = new HashMap();
    private ObservableList<String> results = FXCollections.observableArrayList();
    private String selectedGame;
    private HttpClient client = HttpClientBuilder.create().build();

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
        paneGInfo.setVisible(false);
        paneResults.setVisible(true);
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

        HttpResponse response = searchDatabase("games", "search \"" + searchTerm +
                "\"; fields name, id; limit 30;");


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
                String name = parts[1];
                name = name.substring(2);
                name = name.substring(0, (name.length() - 2));
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

    public void doResultSelect(ActionEvent actionEvent) throws Exception {
        String selectedID = games.get(selectedGame);
        System.out.println(selectedID);
        paneResults.setVisible(false);
        paneGInfo.setVisible(true);

        HttpResponse response = searchDatabase("games", "fields cover, summary; where id=" + selectedID + ";");

//        HttpResponse response = searchDatabase("covers", "fields url; where id=" + selectedID + ";");
        InputStream ips  = response.getEntity().getContent();
        BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
        if(response.getStatusLine().getStatusCode()!= HttpStatus.SC_OK)
        {
            throw new Exception(response.getStatusLine().getReasonPhrase());
        }
        String s;
        String url = "";
        while(true) {

            s = buf.readLine();
            System.out.println(s);
            if (s == null || s.length() == 0)
                break;
            if (s.contains("url")){
                String[] parts = s.split(":", 2);
                url = parts[1];
                url = url.substring(2);
                url = url.substring(0, (url.length() - 1));
            }
        }
        buf.close();
        ips.close();
        System.out.println(url);

        url = url.replace("thumb", "cover_big");
        url = "https:" + url;
        System.out.println(url);
        ivCover.setFitHeight(187);
        ivCover.setFitWidth(132);
        ivCover.setImage(new Image(url));
        ivCover.setVisible(true);

    }

    private HttpResponse searchDatabase(String endpoint, String query) throws IOException{
        HttpPost post = new HttpPost("https://api-v3.igdb.com/" + endpoint + "/");
        post.setHeader("user-key", "ba566df70dd39c35dc2edeea0cbd7838");
        post.setEntity(new StringEntity(query));

        return client.execute(post);
    }
}
