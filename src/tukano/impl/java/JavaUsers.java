package tukano.impl.java;

import java.util.*;
import java.util.logging.Logger;

import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;
import tukano.api.User;
import tukano.api.java.Shorts;
import tukano.api.java.Users;
import tukano.impl.ShortsClientFactory;
import tukano.persistence.Hibernate;

public class JavaUsers implements Users {

	private static Logger Log = Logger.getLogger(JavaUsers.class.getName());

	@Override
	public Result<String> createUser(User user) {

		Log.info("PostUser = " + user);
		
		// Check if user data is valid
		if(user.getUserId() == null || user.getPwd() == null || user.getDisplayName() == null || user.getEmail() == null) {
			Log.info("User object invalid.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}

		// Insert user, checking if name already exists
		if(!Hibernate.getInstance().sql("SELECT * From User WHERE userId LIKE '" + user.getUserId() + "'", User.class).isEmpty()) {
			Log.info("User already exists.");
			return Result.error( ErrorCode.CONFLICT);
		}

		Hibernate.getInstance().persist(user);
		return Result.ok( user.getUserId() );
	}

	@Override
	public Result<User> getUser(String userId, String pwd) {
		Log.info("getUser : user = " + userId + "; pwd = " + pwd);
		
		// Check if user is valid
		if(userId == null || pwd == null) {
			Log.info("Name or Password null.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}
		
		var res = Hibernate.getInstance().sql("SELECT * FROM User WHERE userId LIKE '" + userId + "'", User.class);
		// Check if user exists 
		if(res.isEmpty()) {
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}

		User user = res.get(0);
		//Check if the password is correct
		if( !user.getPwd().equals(pwd)) {
			Log.info("Password is incorrect.");
			return Result.error( ErrorCode.FORBIDDEN);
		}
		
		return Result.ok(user);
	}

	@Override
	public Result<User> updateUser(String userId, String pwd, User user) {
		if(userId == null || pwd == null) { 
			Log.info("Name or Password null.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}
		if(user.getUserId() == null || user.getUserId().equals(userId)) {
			// Check if user exists
			Result<User> res = getUser(userId, pwd);
			if (!res.isOK()) return Result.error(res.error());
			User lastUser = res.value();
			if (user.getPwd() != null) lastUser.setPwd(user.getPwd());
			if (user.getEmail() != null) lastUser.setEmail(user.getEmail());
			if (user.getDisplayName() != null) lastUser.setDisplayName(user.getDisplayName());
			Hibernate.getInstance().update(lastUser);
			Log.info("UpdateUser : user = " + userId + "; pwd = " + pwd);
			return Result.ok(lastUser);
		}
		return Result.error(ErrorCode.BAD_REQUEST);
	}

	@Override
	public Result<User> deleteUser(String userId, String pwd) {
		if(userId == null || pwd == null) {
			Log.info("Name or Password null.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}

		Result<User> res = getUser(userId, pwd);
		if(!res.isOK()) return Result.error(res.error());
		User user = res.value();
		// Check if user exists
		Shorts shorts = ShortsClientFactory.getClients();

		List<User> allUsers = Hibernate.getInstance().sql("SELECT * FROM User", User.class);
		Iterator<User> itAllUsers = allUsers.iterator();
		while(itAllUsers.hasNext()) {
			User u = itAllUsers.next();
            var resAllShorts = shorts.getShorts(u.getUserId());
			if(resAllShorts.isOK()) {
				Iterator<String> itAllShorts = resAllShorts.value().iterator();
				while (itAllShorts.hasNext()) {
					String sh = itAllShorts.next();
					List<String> allLikes = shorts.likes(sh, u.getPwd()).value();
					if (allLikes.contains(userId)) shorts.like(sh, userId, false, pwd);
				}
			}
		}

        Result<List<String>> resShorts = shorts.getShorts(userId);
		if(resShorts.isOK()){
			Iterator<String> it = resShorts.value().iterator();
			while (it.hasNext()) {
				String s = it.next();
				shorts.deleteShort(s, pwd);
			}
		}
		Hibernate.getInstance().delete(user);
		Log.info("DeleteUser : user = " + userId + "; pwd = " + pwd);
		return Result.ok(user);
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		List<User> users = Hibernate.getInstance().sql("SELECT * FROM User", User.class);
		if(pattern == null || pattern.trim().isEmpty())
			return Result.ok(Collections.emptyList());
		List<User> res = new ArrayList<>();
		Iterator<User> usersIt = users.iterator();
		while(usersIt.hasNext()) {
			User user = usersIt.next();
			if(user.getUserId().toLowerCase().contains(pattern.toLowerCase()))
				res.add(user);
		}
		return Result.ok(res);
	}

}
