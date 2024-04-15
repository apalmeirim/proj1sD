package tukano.api;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.ArrayList;

@Entity
public class User {
	
	private String pwd;
	private String email;
	@Id
	private String userId;
	private String displayName;
	@ElementCollection
	private ArrayList<String> followers;
	@ElementCollection
	private ArrayList<String> following;

	public User() {}
	
	public User(String userId, String pwd, String email, String displayName) {
		this.pwd = pwd;
		this.email = email;
		this.userId = userId;
		this.displayName = displayName;
		this.followers = new ArrayList<String>();
		this.following = new ArrayList<String>();
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void addFollower(String userId) {
		followers.add(userId);
	}

	public void removeFollower(String userId) {
		followers.remove(userId);
	}

	public void addFollowing(String userId) {
		following.add(userId);
	}

	public void removeFollowing(String userId) {
		following.remove(userId);
	}
	
	public String userId() {
		return userId;
	}
	
	public String pwd() {
		return pwd;
	}
	
	public String email() {
		return email;
	}
	
	public String displayName() {
		return displayName;
	}

	public ArrayList<String> getFollowers() {
		return this.followers;
	}

	public ArrayList<String> getFollowing() {
		return this.following;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", pwd=" + pwd + ", email=" + email + ", displayName=" + displayName + "]";
	}
}
