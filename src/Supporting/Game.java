package Supporting;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by u0861925 on 26/07/2019.
 */
public class Game {
    public final String name;
    public final String id;

    @JsonCreator
    public Game(@JsonProperty("name") String name,
                  @JsonProperty("id") String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
