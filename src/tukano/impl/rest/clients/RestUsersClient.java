package tukano.impl.rest.clients;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;
import tukano.api.java.Users;
import tukano.api.rest.RestUsers;
import utils.Sleep;

public class RestUsersClient extends RestClient implements Users {

	protected static final int MAX_RETRIES = 10;
	protected static final int RETRY_SLEEP = 1000;
	final URI serverURI;
	final Client client;
	final ClientConfig config;

	final WebTarget target;
	
	public RestUsersClient( URI serverURI ) {
		this.serverURI = serverURI;
		this.config = new ClientConfig();
		this.client = ClientBuilder.newClient(config);

		target = client.target( serverURI ).path( RestUsers.PATH );
	}
		
	@Override
	public Result<String> createUser(User user) {
		return super.reTry(() -> super.toJavaResult(
				client.target( serverURI ).path( RestUsers.PATH )
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(user, MediaType.APPLICATION_JSON)), String.class));
	}

	@Override
	public Result<User> getUser(String name, String pwd) {
		return super.reTry(() -> super.toJavaResult(
		client.target(serverURI).path(RestUsers.PATH)
		.path(name)
				.queryParam(RestUsers.PWD, pwd).request()
				.accept(MediaType.APPLICATION_JSON)
				.get(),User.class));

	}
	public Result<Void> checkPassword(String name, String pwd) {
		WebTarget target = client.target(serverURI).path(RestUsers.PATH);
		for (int i = 0; i < MAX_RETRIES; i++)
			try {
				Response r = target.path( name ).path("/check")
						.queryParam(RestUsers.PWD, pwd).request()
						.get();

				if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
					// SUCCESS
					return Result.ok();
				else {
					return Result.error(getErrorCodeFrom(r.getStatus()));
				}
			} catch (ProcessingException x) {
				Sleep.ms(RETRY_SLEEP);
			}
		return null; // Report failure
	}

	@Override
	public Result<User> updateUser(String userId, String password, User user) {
		return super.reTry(() -> super.reTry(() -> super.toJavaResult(client.target(serverURI).path(RestUsers.PATH)
		.path( userId )
				.queryParam(RestUsers.PWD, password).request()
				.put(Entity.entity(user, MediaType.APPLICATION_JSON)), User.class)));

	}

	@Override
	public Result<User> deleteUser(String userId, String password) {
		return super.reTry(() -> super.toJavaResult(client.target(serverURI).path(RestUsers.PATH)
		.path( userId )
						.queryParam(RestUsers.PWD, password).request()
						.accept(MediaType.APPLICATION_JSON)
						.delete(), User.class));
	}

	@Override
	public Result<List<User>> searchUsers(String userId) {
		WebTarget target = client.target(serverURI).path(RestUsers.PATH);
		for (int i = 0; i < MAX_RETRIES; i++)
			try {
				Response r = target
						.queryParam(RestUsers.QUERY, userId)
						.request()
						.accept(MediaType.APPLICATION_JSON)
						.get();

				if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
					// SUCCESS
					return Result.ok(r.readEntity(new GenericType<List<User>>() {}));
				else {
					return Result.error(getErrorCodeFrom(r.getStatus()));
				}
			} catch (ProcessingException x) {
				Sleep.ms(RETRY_SLEEP);
			}
		return null; // Report failure
	}


}
