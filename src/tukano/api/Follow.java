package tukano.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.ArrayList;

@Entity
public class Follow {


    @Id
    private String followedUser;
    @Id
    private String followerUser;

    public Follow(String followedUser, String followerUser) {
        this.followedUser = followedUser;
        this.followerUser = followerUser;
    }

    public String getFollowedUser() {
        return followedUser;
    }

    public String getFollowerUser() {
        return followerUser;
    }
}
