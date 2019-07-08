package GameInfo;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
import java.util.Set;

/**
 * Created by u0861925 on 08/07/2019.
 */
public class GameInfo extends Application {

    public Button btnSearch;
    public TextField tfSearch;
    public Pane paneMain;
    public TextArea taSearch;

    HashMap<String, String> games = new HashMap();

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
        search(tfSearch.getText());
        String[] results = games.keySet().toArray(new String[games.size()]);
        for (int i = 0; i < results.length; i++){
            taSearch.appendText(results[i]);
            taSearch.appendText("\n");
        }
        taSearch.setVisible(true);
    }

    public void search(String searchTerm) throws Exception{
        HttpClient client = HttpClientBuilder.create().build();
        //change the end part of the url for different endpoints
        HttpPost post = new HttpPost("https://api-v3.igdb.com/games/");

        post.setHeader("user-key", "ba566df70dd39c35dc2edeea0cbd7838");
        //the search queries
        post.setEntity(new StringEntity("search \"" + searchTerm + "\"; fields name, id; limit 15;"));

        //get the response and check the response code. 200 is a successful response.
        HttpResponse response = client.execute(post);
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        //take the response and start to build it in to a readable string.
        InputStream ips  = response.getEntity().getContent();
        BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
        if(response.getStatusLine().getStatusCode()!= HttpStatus.SC_OK)
        {
            throw new Exception(response.getStatusLine().getReasonPhrase());
        }
        String s;
        int i = 0;
        String name = "";
        String id = "";
        //loop through the response until it is empty and build a string.
        while(true) {

            s = buf.readLine();
            System.out.println(s);
            if(s==null || s.length()==0)
                break;
            if (s.contains("name")){
                String[] parts = s.split(":", 2);
                parts[1] = parts[1].replace("\"", "");
                parts[1] = parts[1].replaceFirst("\\s", "");
                name = parts[1];
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
}
