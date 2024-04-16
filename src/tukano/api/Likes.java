package tukano.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class Likes {

    @Id
    private String shortId;
    @Id
    private String userId;

    public Like(String shortId, String userId){
        this.shortId = shortId;
        this.userId = userId;
    }

    public String getShortId() {
        return shortId;
    }

    public String getUserId() {
        return userId;
    }
}
