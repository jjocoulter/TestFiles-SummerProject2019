package GameInfo;

import Supporting.Game;
import Supporting.GameData;
import Supporting.Methods;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by u0861925 on 08/07/2019.
 */
public class GameInfo extends Application {

    public Button btnSearch;
    public TextField tfSearch;
    public AnchorPane paneResults;
    public ListView<String> lvResults;
    public Button btnResultSelect;
    public AnchorPane paneGInfo;
    public ImageView ivCover;
    public Label lblTitle;
    public Label lblSummary;

    private int selectedGame;
    private Methods methods = new Methods();
    private List<Game> games;

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

        games = methods.findGames("games",
                "search \"" + tfSearch.getText() + "\"; fields name, id; limit 30;");

        ObservableList<String> gameList = FXCollections.observableArrayList();
        for (Game g : games) {
            gameList.add(g.getName());
        }

        lvResults.setItems(gameList);
        lvResults.getSelectionModel().select(0);
        lvResults.getFocusModel().focus(0);

        lvResults.setOnMouseClicked(event -> {
            selectedGame = lvResults.getSelectionModel().getSelectedIndex();
            System.out.println(selectedGame);
        });

    }


    public void doResultSelect(ActionEvent actionEvent) throws Exception {
        String selectedID = games.get(selectedGame).getId();
        System.out.println(selectedID);
        paneResults.setVisible(false);
        paneGInfo.setVisible(true);

        HttpResponse response = methods.searchDatabase("games", "fields cover, summary, name; where id="
                + selectedID + ";");

        InputStream ips = response.getEntity().getContent();
        BufferedReader buf = new BufferedReader(new InputStreamReader(ips, "UTF-8"));
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new Exception(response.getStatusLine().getReasonPhrase());
        }
        String s;
        StringBuilder sb = new StringBuilder();
        while (true) {
            s = buf.readLine();
            System.out.println(s);
            if (s == null || s.length() == 0)
                break;
            sb.append(s);
            sb.append("\n");
        }
        String jsonText = sb.toString();

        ObjectMapper mapper = new ObjectMapper();
        List<GameData> gameList = mapper.readValue(jsonText, new TypeReference<List<GameData>>(){});
        GameData game = gameList.get(0);
        buf.close();
        ips.close();
        lblSummary.setText(game.getSummary());
        lblTitle.setText(game.getTitle());

//        url = url.replace("thumb", "cover_big");
//        url = "https:" + url;
//        System.out.println(url);
//        ivCover.setFitHeight(187);
//        ivCover.setFitWidth(132);
//        ivCover.setImage(new Image(url));
//        ivCover.setVisible(true);

    }
}
