package tukano.impl.rest.clients;

import java.net.URI;
import java.util.List;


import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Users;
import tukano.api.rest.RestUsers;

public class RestUsersClient extends RestClient implements Users {

	final WebTarget target;
	
	public RestUsersClient(URI serverURI) {
		super(serverURI);
		target = client.target( serverURI ).path( RestUsers.PATH );
	}
		
	@Override
	public Result<String> createUser(User user) {
		return super.reTry(() -> super.toJavaResult(
				target
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(user, MediaType.APPLICATION_JSON)), String.class));
	}

	@Override
	public Result<User> getUser(String userId, String pwd) {
		return super.reTry(() -> super.toJavaResult(
				target
				.path(userId)
				.queryParam(RestUsers.PWD, pwd)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get(),User.class));

	}
	public Result<Void> checkPassword(String name, String pwd) {
		return super.reTry(() -> super.toJavaResultVoid(
				target
				.path( name ).path("/check")
						.queryParam(RestUsers.PWD, pwd).request()
						.get()));
	}

	@Override
	public Result<User> updateUser(String userId, String password, User user) {
		return super.reTry(() -> super.reTry(() -> super.toJavaResult(
				target
				.path( userId )
				.queryParam(RestUsers.PWD, password).request()
				.put(Entity.entity(user, MediaType.APPLICATION_JSON)), User.class)));

	}

	@Override
	public Result<User> deleteUser(String userId, String password) {
		return super.reTry(() -> super.toJavaResult(
						target
						.path( userId )
						.queryParam(RestUsers.PWD, password).request()
						.accept(MediaType.APPLICATION_JSON)
						.delete(), User.class));
	}

	@Override
	public Result<List<User>> searchUsers(String userId) {
		return super.reTry(() -> super.toJavaResultList(
				target
				.queryParam(RestUsers.QUERY, userId)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get()));
	}


}
