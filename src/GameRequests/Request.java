package GameRequests;

import java.util.Date;

/**
 * Created by u0861925 on 22/07/2019.
 */
public class Request {
    private Integer requestID = 0;
    private Integer requester = 0;
    private String game = null;
    private Date date = null;

    public Request() {
    }

    public Request(int requestID, int requester, String game, Date date) {
        this.requestID = requestID;
        this.requester = requester;
        this.game = game;
        this.date = date;
    }

    public Integer getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getRequester() {
        return requester;
    }

    public void setRequester(int requester) {
        this.requester = requester;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
