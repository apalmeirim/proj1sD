package tukano.impl.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;
import tukano.api.User;
import tukano.api.java.Users;

public class JavaUsers implements Users {
	private final Map<String,User> users = new HashMap<>();

	private static Logger Log = Logger.getLogger(JavaUsers.class.getName());


	@Override
	public Result<String> createUser(User user) {
		Log.info("createUser : " + user);
		
		// Check if user data is valid
		if(user.getUserId() == null || user.getPwd() == null || user.getDisplayName() == null || user.getEmail() == null) {
			Log.info("User object invalid.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}
		
		// Insert user, checking if name already exists
		if( users.putIfAbsent(user.getUserId(), user) != null ) {
			Log.info("User already exists.");
			return Result.error( ErrorCode.CONFLICT);
		}
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
		
		User user = users.get(userId);			
		// Check if user exists 
		if( user == null ) {
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
		//Check if the password is correct
		if( !user.getPwd().equals( pwd)) {
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

		// Check if user exists
		User lastUser = users.get(userId);
		if( lastUser == null ) {
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}

		//Check if the password is correct
		if( !lastUser.getPwd().equals( pwd)) {
			Log.info("Password is incorrect.");
			return Result.error( ErrorCode.FORBIDDEN);
		}
		users.replace(lastUser.getUserId(), user);
		Log.info("UpdateUser : user = " + userId + "; pwd = " + pwd);
		return Result.ok(user);
	}

	@Override
	public Result<User> deleteUser(String userId, String pwd) {
		if(userId == null || pwd == null) {
			Log.info("Name or Password null.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}

		User user = users.get(userId);
		// Check if user exists
		if( user == null ) {
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}

		//Check if the password is correct
		if( !user.getPwd().equals( pwd)) {
			Log.info("Password is incorrect.");
			return Result.error(ErrorCode.FORBIDDEN);
		}
		users.remove(userId);
		Log.info("DeleteUser : user = " + userId + "; pwd = " + pwd);
		return Result.ok(user);
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		return Result.error( ErrorCode.NOT_IMPLEMENTED);
	}
}
