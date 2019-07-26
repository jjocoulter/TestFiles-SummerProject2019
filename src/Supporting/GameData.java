package Supporting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)

/**
 * Created by u0861925 on 26/07/2019.
 */
public class GameData {
    private final String title;
    private final String summary;
//    private final String url;

    @JsonCreator
    public GameData(@JsonProperty("name") String title,
                @JsonProperty("summary") String summary) {
        this.title = title;
        this.summary = summary;
//        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

//    public String getUrl() {
////        return url;
//    }
}
