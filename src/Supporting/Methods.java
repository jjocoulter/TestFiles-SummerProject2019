package Supporting;

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

/**
 * Created by u0861925 on 22/07/2019.
 */
public class Methods {

    private HttpClient client = HttpClientBuilder.create().build();
    private HashMap<String, String> games = new HashMap();

    public HttpResponse searchDatabase(String endpoint, String query) throws IOException {
        HttpPost post = new HttpPost("https://api-v3.igdb.com/" + endpoint + "/");
        post.setHeader("user-key", "ba566df70dd39c35dc2edeea0cbd7838");
        post.setEntity(new StringEntity(query));

        return client.execute(post);
    }

    public HashMap<String, String> findGames(String endpoint, String query) throws Exception{
        HttpResponse response = searchDatabase(endpoint, query);

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
            if (s.contains("id")){
                String[] parts = s.split(":");
                parts[1] = parts[1].replaceAll("\\s", "");
                parts[1] = parts[1].replaceAll(",", "");
                id = parts[1];
            } else if (s.contains("name")){
                String[] parts = s.split(":", 2);
                String name = parts[1];
                name = name.substring(2);
                name = name.substring(0, (name.length() - 1));
                games.put(name, id);
            }
        }
        buf.close();
        ips.close();
        System.out.println(games.toString());

        return games;
    }
}
