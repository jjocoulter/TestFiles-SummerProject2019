import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;


/**
 * Created by u0861925 on 01/07/2019.
 */
public class APITest {

    public static void main(String[] args) throws Exception{
        HttpClient client = HttpClientBuilder.create().build();
        //change the end part of the url for different endpoints
        HttpPost post = new HttpPost("https://api-v3.igdb.com/games/");

        post.setHeader("user-key", "ba566df70dd39c35dc2edeea0cbd7838");
        //the search queries
        post.setEntity(new StringEntity("fields name, platforms, rating, genres; where id = 1942;"));

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

        StringBuilder sb = new StringBuilder();
        String s;
        //loop through the response until it is empty and build a string.
        while(true )
        {
            s = buf.readLine();
            if(s==null || s.length()==0)
                break;
            //Gets the genre names and game name and formats them correctly for use.
            if (s.contains("name")){
                String[] parts = s.split(":", 2);
                parts[1] = parts[1].replace("\"", "");
                System.out.println(parts[1]);
            }
            sb.append(s);
            sb.append("\n");
        }

        //JSON test
        String jsonText = sb.toString();
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> data =
                mapper.readValue(jsonText , new TypeReference<List<Map<String, Object>>>(){});

        buf.close();
        ips.close();
//        System.out.println(sb.toString());
        System.out.println(jsonText);
        System.out.println(data.toString());

        for (int i = 0; i < data.size(); i++){
            System.out.println(data.get(i).get("name"));
        }


    }
}
