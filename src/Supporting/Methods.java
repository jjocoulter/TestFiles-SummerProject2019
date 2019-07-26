package Supporting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by u0861925 on 22/07/2019.
 */
public class Methods {

    private HttpClient client = HttpClientBuilder.create().build();

    public HttpResponse searchDatabase(String endpoint, String query) throws IOException {
        HttpPost post = new HttpPost("https://api-v3.igdb.com/" + endpoint + "/");
        post.setHeader("user-key", "ba566df70dd39c35dc2edeea0cbd7838");
        post.setEntity(new StringEntity(query));

        return client.execute(post);
    }

    public List<Game> findGames(String endpoint, String query) throws Exception{
        HttpResponse response = searchDatabase(endpoint, query);

        InputStream ips  = response.getEntity().getContent();
        BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
        if(response.getStatusLine().getStatusCode()!= HttpStatus.SC_OK)
        {
            throw new Exception(response.getStatusLine().getReasonPhrase());
        }

        StringBuilder sb = new StringBuilder();
        String s;
        while(true )
        {
            s = buf.readLine();
            if(s==null || s.length()==0)
                break;
            sb.append(s);
            sb.append("\n");
        }

        String jsonText = sb.toString();

        ObjectMapper mapper = new ObjectMapper();
        List<Game> games = mapper.readValue(jsonText, new TypeReference<List<Game>>(){});
        buf.close();
        ips.close();
        System.out.println(games.toString());

        return games;
    }

    public String getTitle(int gameID) throws Exception{
        String name = null;
        HttpResponse response = searchDatabase("games", "fields name; where id=" + gameID + ";");

        InputStream ips  = response.getEntity().getContent();
        BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
        if(response.getStatusLine().getStatusCode()!= HttpStatus.SC_OK)
        {
            throw new Exception(response.getStatusLine().getReasonPhrase());
        }

        String s;
        while(true) {

            s = buf.readLine();
            if(s==null || s.length()==0)
                break;
            if (s.contains("name")){
                String[] parts = s.split(":", 2);
                name = parts[1];
                name = name.substring(2);
                name = name.substring(0, (name.length() - 1));
            }
        }
        buf.close();
        ips.close();

        return name;
    }
}
